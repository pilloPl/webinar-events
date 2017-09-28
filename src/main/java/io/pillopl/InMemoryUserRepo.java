package io.pillopl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepo implements UserRepository {

    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getUuid(), user);
    }

    @Override
    public User find(UUID uuid) {
        return users.get(uuid);
    }
}
