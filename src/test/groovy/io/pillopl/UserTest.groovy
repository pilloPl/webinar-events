package io.pillopl

import spock.lang.Specification

class UserTest extends Specification {


    User user = new User(UUID.randomUUID())

    def 'deactivated user cannot change nickanme'() {
        given:
            user.deactivate()
        when:
            user.changeNicknameTo("Barry")
        then:
            thrown(IllegalStateException)
    }

    def 'activated user can change nickname'() {
        given:
            user.activate()
        when:
            user.changeNicknameTo("Barry")
        then:
            user.getNickname() == "Barry"
    }

    def 'new user can be activated'() {
        when:
            user.activate()
        then:
            user.isActivated()
    }

    def 'activated can be deactivated'() {
        given:
            user.activate()
        when:
            user.deactivate()
        then:
            user.isDeactivated()
    }

    def 'activated user cannot be activated'() {
        given:
            user.activate()
        when:
            user.activate()
        then:
            thrown(IllegalStateException)
    }

    def 'deactivated user cannot be deactivated'() {
        given:
            user.deactivate()
        when:
            user.deactivate()
        then:
            thrown(IllegalStateException)
    }

}
