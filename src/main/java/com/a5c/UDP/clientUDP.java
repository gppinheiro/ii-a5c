package com.a5c.UDP;

import java.net.*;

public class clientUDP {
    public final int port = 54321;
    public DatagramSocket socket;
    public InetAddress address;

    /**
     * Create a new clientUDP.
     */
    public clientUDP() {
        try {
            socket = new DatagramSocket(port);
            address = InetAddress.getByName("localhost");
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
