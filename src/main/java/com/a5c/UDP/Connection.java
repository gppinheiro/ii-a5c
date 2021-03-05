package com.a5c.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Connection {
    private final InetAddress addr;
    private final int port;
    private int id;
    private final DatagramSocket clientSocket;

    /**
     * Create a new server connection with the given details.
     */
    public Connection(DatagramSocket socket, InetAddress addr, int port, int id) {
        this.addr = addr;
        this.port = port;
        this.clientSocket = socket;
        this.setId(id);
    }

    /**
     * Send some data on this server connection
     */
    public void send(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);

        try {
            clientSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive data on this connection
     */
    public byte[] receive() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            clientSocket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return packet.getData();
    }

    /**
     * Get the port number of the server
     * @return port number
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Get the address of this connection
     */
    public InetAddress getAddress() {
        return this.addr;
    }

    /**
     * Close the connection to the server.
     */
    public void close() {
        new Thread(() -> {
            synchronized(clientSocket) {
                clientSocket.close();
            }
        }).start();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
