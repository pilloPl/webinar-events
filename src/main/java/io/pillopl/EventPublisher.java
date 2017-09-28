package io.pillopl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.Publisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventPublisher {

    @Publisher(channel = Source.OUTPUT)
    public DomainEvent sendEvent(DomainEvent event) {
        log.info("about to send: " + event);
        return event;

    }


}
