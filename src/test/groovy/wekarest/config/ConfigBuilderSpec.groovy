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
        println(config)

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
        println(config)

        expect:
        config.test.hello.world == 'yes'
        config.test2.hello.world == 'yes'
    }
}
