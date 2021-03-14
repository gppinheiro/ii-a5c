package com.a5c.OPC_UA;

// LINK: https://github.com/eclipse/milo

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.concurrent.CompletableFuture;

public class mainOPC implements ClientOPC_UA {

    private OpcUaClient clientOPC;

    // Probably we will need to change Node Identifier, endpointURL and nsIndex -> UA Expert shows
    private static final String Identifier = "|var|CODESYS Control Win V3 x64.Application.PLC_PRG.";
    private static final String endpointURL = "opc.tcp://localhost:4840";
    private static final int nsIndex = 4;

    @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        //synchronous connect
        client.connect().get();
        setClientOPC(client);
    }

    public void setClientOPC(OpcUaClient clientOPC) {
        this.clientOPC = clientOPC;
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

}
