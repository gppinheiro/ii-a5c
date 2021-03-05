package com.a5c.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class UDPServer implements Runnable{

    /* Server information */
    private final int port;
    private DatagramSocket socket;
    private boolean running;

    /* Client relevant */
    public static ArrayList<Connection> CLIENTS = new ArrayList<>();


    /**
     * Construct a new instance of a multi-threaded UDP server.
     */
    public UDPServer(int port) {
        this.port = port;

        try {
            this.init();
        } catch (SocketException e) {
            System.err.println("Unable to initialise the server..." + e.getMessage());
        }
    }

    /**
     * Initialize the server
     */
    public void init() throws SocketException {
        this.socket = new DatagramSocket(this.port);
        Thread process = new Thread(this, "server_process");
        process.start();
    }

    /**
     * Get the port that the server is binded to
     * @return port
     */
    public int getPort() {
        return port;
    }


    /**
     * Send a packet to a client
     */
    public void send(final Packet packet) {
        /* Threads */
        Thread send = new Thread("send_thread") {
            public void run() {
                DatagramPacket dgpack = new DatagramPacket(
                        packet.getData(),
                        packet.getData().length,
                        packet.getAddr(),
                        packet.getPort()
                );

                try {
                    socket.send(dgpack);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        };

        send.start();
    }

    /**
     * Send a packet to all connected clients
     */
    public void broadcast(byte[] data) {
        for(Connection c : CLIENTS) {
            send(new Packet(data, c.getAddress(), c.getPort()));
        }
    }

    /**
     * Wait for input... and use a Handler.java to process the packet
     * @param handler The packet handler
     */
    public void receive(final Handler handler) {
        Thread receive = new Thread("receive_thread") {
            public void run() {
                while (running) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket dgpacket = new DatagramPacket(buffer, buffer.length);

                    try {
                        socket.receive(dgpacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    handler.process(new Packet(dgpacket.getData(), dgpacket.getAddress(), dgpacket.getPort()));
                }
            }
        };

        receive.start();
    }

    /**
     * The run method of this runnable thread object.
     */
    @Override
    public void run() {
        running = true;
        System.out.println("Server started on port " + port);
    }
}
