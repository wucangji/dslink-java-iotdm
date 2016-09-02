package iotdm;


import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.Permission;
import org.dsa.iot.dslink.node.actions.Action;
import org.dsa.iot.dslink.node.actions.ActionResult;
import org.dsa.iot.dslink.node.actions.Parameter;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.node.value.ValueType;
import org.dsa.iot.dslink.serializer.Deserializer;
import org.dsa.iot.dslink.serializer.Serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by canwu on 8/24/15.
 */
public class IotdmLink {
    private final static Logger LOGGER;
    Node node;

    Serializer copySerializer;
    Deserializer copyDeserializer;

    private IotdmLink(Node node, Serializer ser, Deserializer deser) {
        this.node = node;
        this.copySerializer = ser;
        this.copyDeserializer = deser;

    }

    public static IotdmLink start (Node parent, Serializer copyser, Deserializer copydeser) {
        Node node = parent;
        final IotdmLink link= new IotdmLink(node, copyser, copydeser);
        link.init();
        return link;
    }

    static {
        LOGGER = LoggerFactory.getLogger(IotdmLink.class);
    }

    private void init() {
        //Action act = new Action(Permission.READ, new ConfigHandler());

        // this part will add a right click feature tells the costomer to enter the input
        Action act = new Action(Permission.READ, new AddAgentHandler());
        act.addParameter(new Parameter("Host", ValueType.STRING));
        act.addParameter(new Parameter("IP", ValueType.STRING));
        node.createChild("addAgent").setAction(act).build().setSerializable(false);
    }

    private class ConfigHandler implements Handler<ActionResult> {
        public void handle(ActionResult event) {
            boolean keepLoaded = event.getParameter("keep MIBs loaded", ValueType.BOOL).getBool();

            //mibnode.setAttribute("keep MIBs loaded", new Value(keepLoaded));

            Action act = new Action(Permission.READ, new ConfigHandler());
            act.addParameter(new Parameter("keep MIBs loaded", ValueType.BOOL, new Value(keepLoaded)));
            Node anode = node.getChild("options");
            if (anode != null) anode.setAction(act);
            else node.createChild("options").setAction(act).build().setSerializable(false);
        }
    }


    private class AddAgentHandler implements Handler<ActionResult> {
        public void handle(ActionResult event) {
            String comStr="N/A", secName="N/A", authProt="N/A", authPass="N/A",
                    privProt="N/A", privPass="N/A", engine="N/A", cEngine="N/A", cName="N/A";
            String ip = event.getParameter("IP", ValueType.STRING).getString() + "/"
                    + event.getParameter("Port", ValueType.STRING).getString();
            String name = event.getParameter("Name", ValueType.STRING).getString();
            long interval = (long) (1000*event.getParameter("Polling Interval", ValueType.NUMBER).getNumber().doubleValue());

            int retries = event.getParameter("Retries", ValueType.NUMBER).getNumber().intValue();
            long timeout = event.getParameter("Timeout", ValueType.NUMBER).getNumber().longValue();

            Node child = node.createChild(name).build();
            child.setAttribute("Polling Interval", new Value(interval));
            child.setAttribute("ip", new Value(ip));
            child.setAttribute("Community String", new Value(comStr));
            child.setAttribute("Security Name", new Value(secName));
            child.setAttribute("Auth Protocol", new Value(authProt));
            child.setAttribute("Auth Passphrase", new Value(authPass));
            child.setAttribute("Priv Protocol", new Value(privProt));
            child.setAttribute("Priv Passphrase", new Value(privPass));
            child.setAttribute("Engine ID", new Value(engine));
            child.setAttribute("Context Engine", new Value(cEngine));
            child.setAttribute("Context Name", new Value(cName));
            child.setAttribute("Retries", new Value(retries));
            child.setAttribute("Timeout", new Value(timeout));
            //new AgentNode(getMe(), child);
        }
    }
}
