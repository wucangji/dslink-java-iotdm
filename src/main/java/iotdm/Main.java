package iotdm;

import org.dsa.iot.dslink.DSLink;
import org.dsa.iot.dslink.DSLinkFactory;
import org.dsa.iot.dslink.DSLinkHandler;
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.NodeBuilder;
import org.dsa.iot.dslink.node.NodeManager;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.node.value.ValueType;
import org.dsa.iot.dslink.serializer.Deserializer;
import org.dsa.iot.dslink.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class that starts the DSLink. Typically it extends
 * {@link DSLinkHandler} and the main method extends into it.
 */
public class Main extends DSLinkHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private IotdmLink iotdmLink;
    @Override
    public boolean isResponder() {
        return true;
    }

    @Override
    public void onResponderInitialized(DSLink link) {
        Node superRoot = link.getNodeManager().getSuperRoot();
        NodeBuilder builder = superRoot.createChild("example");
        builder.setDisplayName("Example");
        builder.setValueType(ValueType.STRING);
        builder.setValue(new Value("Hello world2"));
        builder.build();

        LOGGER.info("Initialized");
    }

    @Override
    public void onResponderConnected(DSLink link) {
        LOGGER.info("Connected");

        NodeManager manager = link.getNodeManager();
        Serializer copyser = new Serializer(manager);
        Deserializer copydeser = new Deserializer(manager);
        Node superRoot = manager.getNode("/").getNode();

        iotdmLink = IotdmLink.start(superRoot, copyser, copydeser);
    }

    public static void main(String[] args) {
        DSLinkFactory.start(args, new Main());
    }
}
