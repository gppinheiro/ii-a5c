package com.a5c.UDP;

import java.net.*;

public class clientUDP {
    public final int port = 54321;
    //public final String ip = "localhost";
    public final String ip = "193.136.33.135";
    public DatagramSocket socket;
    //public InetAddress address;

    /**
     * Create a new clientUDP.
     */
    public clientUDP() {
        try {
            socket = new DatagramSocket(port);
            //address = InetAddress.getByName(ip);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

}
