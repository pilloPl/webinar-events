package io.pillopl

import spock.lang.Specification

import java.time.Instant

class UserRepositoryTest extends Specification {

    EventSourcedUserRepository userRepository =  new EventSourcedUserRepository(eventPublisher)

    def 'should be able to save and load user'() {
        given:
            UUID uuid = UUID.randomUUID()
        and:
            User someUser = new User(uuid)
        and:
            someUser.activate()
        and:
            someUser.changeNicknameTo("Barry")
        when:
            userRepository.save(someUser)
        and:
            User loaded = userRepository.find(uuid)
        then:
            loaded.isActivated()
            loaded.getNickname() == "Barry"
    }

    def 'should be able to load state from a historic timestamp'() {
        given:
            UUID uuid = UUID.randomUUID()
        and:
            User someUser = new User(uuid)
        and:
            someUser.activate()
        and:
            someUser.changeNicknameTo("Barry")
        when:
            userRepository.save(someUser)
        and:
            sleep(1000L)
        and:
            someUser.changeNicknameTo("John")
        and:
            userRepository.save(someUser)
        then:
            userRepository.find(uuid).getNickname() == "John"
            userRepository.find(uuid, Instant.now().minusMillis(1005L)).getNickname() == "Barry"
    }

}
