package com.a5c.OPC_UA;

// LINK: https://github.com/eclipse/milo
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.concurrent.ExecutionException;

public class clientOPC_UA {

    private OpcUaClient clientOPC;

    // Probably we will need to change Node Identifier, endpointURL and nsIndex -> UA Expert shows
    private static final String Identifier = "|var|CODESYS Control Win V3 x64.Application.PLC_PRG.";
    private static final String endpointURL = "opc.tcp://localhost:4840";
    private static final int nsIndex = 4;

    /**
     * Constructor to create a new OPC Client connection
     */
    public clientOPC_UA() {
        try {
            this.clientOPC = OpcUaClient.create(endpointURL);
            clientOPC.connect().get();
        } catch (InterruptedException | ExecutionException | UaException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Variable Value
     * @param Variable - Variable's Name.
     * @return an object - it can be int, boolean, String, etc. Depends on what we need.
     */
    public Object getValue(String Variable) {
        NodeId nodeIDString = new NodeId(nsIndex, Identifier+Variable);
        DataValue value = null;

        try {
            value = clientOPC.readValue(0, TimestampsToReturn.Both, nodeIDString).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert value != null;
        return value.getValue().getValue();
    }

    /**
     * Set Boolean Variable Value
     * @param Variable - Variable's Name
     * @param SetValue - Variable's Value
     * @return true if sets ok
     */
    public boolean setValue(String Variable, boolean SetValue) {
        NodeId nodeIDString = new NodeId(nsIndex, Identifier+Variable);

        Variant var = new Variant(SetValue);
        DataValue dv = new DataValue(var);

        try {
            clientOPC.writeValue(nodeIDString, dv).get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Set Boolean Variable Value
     * @param Variable - Variable's Name
     * @param SetValue - Variable's Value
     * @return true if sets ok
     */
    public boolean setValue(String Variable, int SetValue) {
        NodeId nodeIDString = new NodeId(nsIndex, Identifier+Variable);

        Variant var = new Variant((short) SetValue);
        DataValue dv = new DataValue(var);

        try {
            clientOPC.writeValue(nodeIDString, dv).get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
