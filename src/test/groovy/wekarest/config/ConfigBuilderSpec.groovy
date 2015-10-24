package wekarest.config

import spock.lang.Specification

class ConfigBuilderSpec extends Specification {

    def "should build two level map"() {
        given:
        def builder = new ConfigBuilder()
        builder.test {
            hello(world: 'yes')
        }
        def config = builder.config

        expect:
        config.test.hello.world == 'yes'
    }

    def "should build two root map"() {
        given:
        def builder = new ConfigBuilder()
        builder.test {
            hello(world: 'yes')
        }
        builder.test2 {
            hello(world: 'yes')
        }
        def config = builder.config

        expect:
        config.test.hello.world == 'yes'
        config.test2.hello.world == 'yes'
    }



    def "should assign value"() {
        given:
        def builder = new ConfigBuilder()
        builder.test {
            hello('world')
        }
        def config = builder.config as Map

        expect:
        config == [test: [hello: 'world']]
    }

    def "should merge nodes"() {
        given:
        def builder = new ConfigBuilder()
        builder.test {
            hello(world: 'yes')
            hello(world2: 'yes')
        }
        def config = builder.config as Map

        expect:
        config == [
                test: [hello: [world: 'yes', world2: 'yes']]
        ]
    }
}
