package wekarest.service

import org.codehaus.groovy.control.CompilerConfiguration
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.stereotype.Service
import wekarest.config.ConfigBuilder
import wekarest.config.ConfigScript

@Service
class ConfigurationService implements InitializingBean {

    private Map config

    private resourceLoader = new DefaultResourceLoader()

    Map getClassifierConfig(String name) {
        return config.classifiers[name]
    }

    String getInstanceLoaderClassName(String type) {
        return config.instanceLoaders[type]
    }

    @Override
    void afterPropertiesSet() throws Exception {
        def compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = ConfigScript.class.name
        def shell = new GroovyShell(this.class.classLoader, new Binding(), compilerConfiguration)
        def stream = resourceLoader.getResource('classpath:DefaultConfig.groovy').getInputStream()
        def builder = new ConfigBuilder()
        def script = shell.parse(new InputStreamReader(stream)) as ConfigScript
        script.setDelegate(builder)
        script.run()
        config = builder.config
    }
}
