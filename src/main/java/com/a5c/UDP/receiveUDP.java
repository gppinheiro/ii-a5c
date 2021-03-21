package com.a5c.UDP;

import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;
import com.a5c.DB.dbConnect;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;

public class receiveUDP implements Runnable {
    private clientUDP client;
    public dbConnect db;
    private InetAddress address;
    private Thread thrUDP;

    public receiveUDP(clientUDP cl, dbConnect db) {
        this.client = cl;
        this.db = db;
    }

    @Override
    public void run() {
        try {
            // Fill with 0s -  Byte Array
            byte[] buffer = new byte[65536];
            Arrays.fill(buffer , (byte) 0);

            // DatagramPacker
            DatagramPacket packUDP = new DatagramPacket(buffer, 0, buffer.length);

            while(true) {
                // Receive what ERP sent to us
                client.socket.receive(packUDP);

                // SocketAddress - I don't know if it is localhost or not. So we must play on the safe side.
                /*SocketAddress SocketAddr = packUDP.getSocketAddress();
                // Address + Port
                String aux = SocketAddr.toString();
                // Address
                String addressServer = aux.substring(1, aux.indexOf(":"));
                // Port
                int portServer = Integer.parseInt(aux.substring(aux.indexOf(":") + 1));
                // InetAddress with the before result
                address = InetAddress.getByName(addressServer);*/

                // Receive Orders XML
                byte[] buffer2 = Arrays.copyOfRange(packUDP.getData(), 0, packUDP.getLength());
                Files.write(Paths.get("receiveOrdersXML.xml"), buffer2);

                readXML();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if(thrUDP==null) {
            thrUDP = new Thread(this);
            thrUDP.start();
        }
    }

    public void readXML() {
        try {
            // Create the document
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("receiveOrdersXML.xml");

            // Search on document
            // TODO: WriteXML
            // <Request_Orders/>
            NodeList requestOrdersList = doc.getElementsByTagName("Request_Orders");
            // <Request_Stores/>
            NodeList requestStoresList = doc.getElementsByTagName("Request_Stores");
            // <Order/>
            NodeList orderList = doc.getElementsByTagName("Order");

            // Lets search on order
            for (int i=0; i< orderList.getLength(); i++) {
                // Search for nodes like a binary tree
                Node nod = orderList.item(i);

                // I made a research and the guys say that it if is important, more efficient. So why not?
                if (nod.getNodeType() == Node.ELEMENT_NODE) {
                    // Search for elements on this node
                    Element element = (Element) nod;
                    // Order Number
                    int orderNumber = Integer.parseInt(element.getAttribute("Number"));
                    // Get Child Nodes
                    NodeList orderList2 = element.getChildNodes();

                    for (int j=0; j<orderList2.getLength(); j++) {
                        // Same shit as before
                        Node nod2 = orderList2.item(j);

                        if (nod2.getNodeType() == Node.ELEMENT_NODE) {
                            // All elements - We are looking for Transform and Unload
                            Element element2 = (Element) nod2;

                            // TRANSFORM
                            if (element2.getTagName().equals("Transform")) {
                                // From - Px - We don't want the 'P' so we must transform the x into a integer
                                int from = Character.getNumericValue(((Element) nod2).getAttribute("From").charAt(1));
                                // To
                                int to = Character.getNumericValue(((Element) nod2).getAttribute("To").charAt(1));
                                // Quantity
                                int quantity = Integer.parseInt(((Element) nod2).getAttribute("Quantity"));
                                // Time
                                int time = Integer.parseInt(((Element) nod2).getAttribute("Time"));
                                // MaxDelay
                                int maxDelay = Integer.parseInt(((Element) nod2).getAttribute("MaxDelay"));
                                // Penalty
                                int penalty = Integer.parseInt(((Element) nod2).getAttribute("Penalty"));

                                // New Transform Class
                                Transform tf = new Transform(orderNumber,from,to,quantity,time,maxDelay,penalty);

                                // Add to DB
                                db.addTransform(tf);
                            }

                            // UNLOAD
                            else if (element2.getTagName().equals("Unload")) {
                                // Type
                                int type = Character.getNumericValue(((Element) nod2).getAttribute("Type").charAt(1));
                                // Destination
                                int destination = Character.getNumericValue(((Element) nod2).getAttribute("Destination").charAt(1));
                                // Quantity
                                int quantity = Integer.parseInt(((Element) nod2).getAttribute("Quantity"));

                                // New Unload Class
                                Unload un = new Unload(orderNumber,type,destination,quantity);

                                // Add to DB
                                db.addUnload(un);
                            }
                        }
                    }

                }
            }

        } catch (ParserConfigurationException | SAXException | IOException | SQLException e) {
            e.printStackTrace();
        }

    }

}
