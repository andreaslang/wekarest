package wekarest.config

class ConfigBuilder extends BuilderSupport {

    final config

    def stack = [] as LinkedList

    ConfigBuilder() {
        this([:])
    }

    ConfigBuilder(Map config) {
        this.config = config
        stack.push(config)
    }

    @Override
    protected void setParent(Object parent, Object child) {

    }

    @Override
    protected Object createNode(Object name) {
        return createNode(name, [:])
    }

    @Override
    protected Object createNode(Object name, Object value) {
        throw new UnsupportedOperationException('Cannot create node with value - only map syntax supported.')
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        def currentNode = stack.getLast()
        currentNode[name] = attributes
        stack << attributes
        return attributes
    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        throw new UnsupportedOperationException('Cannot create node with value - only map syntax supported.')
    }

    @Override
    protected void nodeCompleted(Object parent, Object node) {
        super.nodeCompleted(parent, node)
        stack.removeLast()
    }
}