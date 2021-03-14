package com.a5c.OPC_UA;

// LINK: https://github.com/eclipse/milo

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.client.UaStackClientConfig;
import org.eclipse.milo.opcua.stack.client.security.ClientCertificateValidator;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.channel.MessageLimits;
import org.eclipse.milo.opcua.stack.core.serialization.EncodingLimits;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public class mainOPC {

    private OpcUaClient clientOPC;

    // Probably we will need to change Node Identifier, endpointURL and nsIndex -> UA Expert shows
    private static final String Identifier = "|var|CODESYS Control Win V3 x64.Application.PLC_PRG.";
    private static final String endpointURL = "opc.tcp://localhost:4840";
    private static final int nsIndex = 4;


    public mainOPC() {
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

}
