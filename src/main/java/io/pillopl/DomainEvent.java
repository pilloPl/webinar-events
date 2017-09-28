package io.pillopl;

import java.time.Instant;

public interface DomainEvent {

    Instant occuredAt();

}
