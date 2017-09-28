package io.pillopl;

import java.util.UUID;

public interface UserRepository {

    void save(User user);

    User find(UUID uuid);

}
