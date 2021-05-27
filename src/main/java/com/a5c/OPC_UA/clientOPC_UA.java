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
    //private static final String Identifier = "|var|CODESYS Control Win V3 x64.Application.PLC_PRG.";
    private static final String endpointURL = "opc.tcp://localhost:4840";
    private static final int nsIndex = 4;

    /**
     * Constructor to create a new OPC Client connection.
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
     * @param Identifier - Identifier's Name.
     * @return an object - it can be int, boolean, String, etc. Depends on what we need.
     */
    public Object getValue(String Identifier) {
        NodeId nodeIDString = new NodeId(nsIndex, Identifier);
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
     */
    public void setValue(String Variable, boolean SetValue) {
        SET(Variable, new Variant(SetValue));
    }

    /**
     * Set Short INT Variable Value
     * @param Variable - Variable's Name
     * @param SetValue - Variable's Value
     */
    public void setValue(String Variable, int[] SetValue) {
        short[] SetValueShort = new short[SetValue.length];

        for(int j = 0; j < SetValue.length; j++) {
            SetValueShort[j] = (short) SetValue[j];
        }

        SET(Variable, new Variant(SetValueShort));
    }

    /**
     * SET Value
     * @param Identifier - Identifier's Name
     * @param var - Variant Class to Set
     */
    public void SET(String Identifier, Variant var) {
        NodeId nodeIDString = new NodeId(nsIndex, Identifier);
        DataValue dv = new DataValue(var);

        try {
            clientOPC.writeValue(nodeIDString, dv).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
