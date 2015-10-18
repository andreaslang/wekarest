package wekarest.config

abstract class ConfigScript extends DelegatingScript {

    @Override
    Object invokeMethod(String name, Object args) {
        return delegate.invokeMethod(name, args)
    }

}
