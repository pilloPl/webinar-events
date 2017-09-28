package io.pillopl;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class EventSourcedUserRepository implements UserRepository {

    private final EventPublisher eventPublisher;
    private final Map<UUID, List<DomainEvent>> users = new ConcurrentHashMap<>();

    public EventSourcedUserRepository(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void save(User user) {
        List<DomainEvent> newChanges = user.getChanges();
        List<DomainEvent> currentChanges = users.getOrDefault(user.getUuid(), new ArrayList<>());
        currentChanges.addAll(newChanges);
        users.put(user.getUuid(), currentChanges);
        user.flushChanges();
        newChanges.forEach(eventPublisher::sendEvent);
    }

    @Override
    public User find(UUID uuid) {
        if(!users.containsKey(uuid)) {
            return null;
        }
        return User.recreateFrom(uuid, users.get(uuid));
    }

    public User find(UUID uuid, Instant timestamp) {
        if(!users.containsKey(uuid)) {
            return null;
        }
        List<DomainEvent> domainEvents = users.get(uuid)
                .stream()
                .filter(event -> !event.occuredAt().isAfter(timestamp))
                .collect(Collectors.toList());
        return User.recreateFrom(uuid, domainEvents);
    }
}
