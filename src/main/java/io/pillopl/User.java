package io.pillopl;

import com.google.common.collect.ImmutableList;
import javaslang.API;
import javaslang.Predicates;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static javaslang.API.Case;
import static javaslang.collection.List.ofAll;

public class User {

    private String nickname = "";

    public List<DomainEvent> getChanges() {
        return ImmutableList.copyOf(changes);
    }

    public void flushChanges() {
        changes.clear();
    }

    public static User recreateFrom(UUID uuid, List<DomainEvent> domainEvents) {
        return ofAll(domainEvents).foldLeft(new User(uuid), User::handleEvent);
    }

    User handleEvent(DomainEvent event) {
        return API.Match(event).of(
                Case(Predicates.instanceOf(UserActivated.class), this::userActivated),
                Case(Predicates.instanceOf(UserDeactivated.class), this::userDeactivated),
                Case(Predicates.instanceOf(UserNameChanged.class), this::userNameChanged)

        );
    }

    enum UserState {
        INITIALIZED, ACTIVATED, DEACTIVATED
    }

    private final UUID uuid;

    private UserState state = UserState.INITIALIZED;

    private List<DomainEvent> changes = new ArrayList<>();

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    void activate() { //behaviour
        if(isActivated()) { //invariant
            throw new IllegalStateException(); //NACK
        }
        //ACK
        userActivated(new UserActivated(Instant.now()));
    }

    private User userActivated(UserActivated userActivated) {
        state = UserState.ACTIVATED; //state change
        changes.add(userActivated);
        return this;
    }

    void deactivate() {
        if(isDeactivated()) {
            throw new IllegalStateException();
        }
        userDeactivated(new UserDeactivated(Instant.now()));
    }

    private User userDeactivated(UserDeactivated userDeactivated) {
        state = UserState.DEACTIVATED;
        changes.add(userDeactivated);
        return this;
    }

    void changeNicknameTo(String newNickName) {
        if(isDeactivated()) {
            throw new IllegalStateException();
        }
        userNameChanged(new UserNameChanged(newNickName, Instant.now()));
    }

    private User userNameChanged(UserNameChanged userNameChanged) {
        nickname = userNameChanged.getNewNickName();
        changes.add(userNameChanged);
        return this;
    }

    boolean isActivated() {
        return state.equals(UserState.ACTIVATED);
    }

    boolean isDeactivated() {
        return state.equals(UserState.DEACTIVATED);
    }

    String getNickname() {
        return nickname;
    }

    public UUID getUuid() {
        return uuid;
    }
}
