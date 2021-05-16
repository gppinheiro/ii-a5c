package com.a5c.UDP;

import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;
import com.a5c.DB.dbConnect;
import com.a5c.OPC_UA.clientOPC_UA;
import com.a5c.OPC_UA.readOPC;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;

public class receiveUDP implements Runnable {
    private final clientUDP client;
    public dbConnect db;
    private InetAddress addressServer;
    private int portServer;
    private DatagramPacket packUDP;
    private Thread thrUDP;
    private final long initTime;
    private int[][] ttExcepted;
    public boolean receivedT;
    private final readOPC opcR;

    public receiveUDP(clientUDP cl, dbConnect db, clientOPC_UA op) {
        this.client = cl;
        this.db = db;
        this.initTime = System.currentTimeMillis();
        this.receivedT = false;
        this.opcR = new readOPC(op);
        try {
            this.ttExcepted = db.getAllExceptedTransformationTime();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void start() {
        if(thrUDP==null) {
            thrUDP = new Thread(this);
            thrUDP.start();
        }
    }

    @Override
    public void run() {
        try {
            // Fill with 0s -  Byte Array
            byte[] buffer = new byte[65536];
            Arrays.fill(buffer , (byte) 0);

            // DatagramPacker
            packUDP = new DatagramPacket(buffer, 0, buffer.length);

            while(true) {
                // Receive what ERP sent to us
                client.socket.receive(packUDP);

                // SocketAddress - This value are need to send back to server some information, if only necessary.
                SocketAddress SocketAddr = packUDP.getSocketAddress();
                // Address + Port
                String aux = SocketAddr.toString();
                // Address
                String address = aux.substring(1, aux.indexOf(":"));
                // Port
                portServer = Integer.parseInt(aux.substring(aux.indexOf(":") + 1));
                // InetAddress with the before result
                addressServer = InetAddress.getByName(address);

                // Receive Orders XML
                byte[] buffer2 = Arrays.copyOfRange(packUDP.getData(), 0, packUDP.getLength());
                Files.write(Paths.get("receiveOrdersXML.xml"), buffer2);

                readXML();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readXML() {
        try {
            // Create the document
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("receiveOrdersXML.xml");

            // Search on document
            // <Request_Orders/>
            NodeList requestOrdersList = doc.getElementsByTagName("Request_Orders");
            // <Request_Stores/>
            NodeList requestStoresList = doc.getElementsByTagName("Request_Stores");
            // <Order/>
            NodeList orderList = doc.getElementsByTagName("Order");

            if(requestOrdersList.getLength()!=0) {
                writeOrderSchedule();
                sendXML("sendRequestOrders.xml");
            }

            if(requestStoresList.getLength()!=0) {
                writeCurrentStores();
                sendXML("sendCurrentStores.xml");
            }

            // Lets search on order
            for (int i=0; i< orderList.getLength(); i++) {
                this.receivedT = true;
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
                                tf.setTimeMES((int) ( System.currentTimeMillis()-initTime)/1000 );
                                tf.setExceptedTT(this.ttExcepted[from][to]);

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

    public void writeCurrentStores() {
        try {
            // Create the document
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            //<Current_Stores> - Quantity of each piece
            Element CSelem = doc.createElement("Current_Stores");
            doc.appendChild(CSelem);

            // P1 to P9
            //<WorkPiece type="Px" quantity="XX"/>
            Integer[] npieces = db.getCurrentStores();
            for(int i=1; i<10; i++) {
                //<WorkPiece/>
                Element WPelem = doc.createElement("WorkPiece");
                CSelem.appendChild(WPelem);

                //type
                Attr attr = doc.createAttribute("type");
                attr.setValue("P"+i);
                WPelem.setAttributeNode(attr);

                //quantity
                attr = doc.createAttribute("quantity");
                attr.setValue(npieces[i].toString());
                WPelem.setAttributeNode(attr);
            }

            //Create file XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("sendCurrentStores.xml"));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | SQLException | TransformerException e) {
            e.printStackTrace();
        }

    }

    public void writeOrderSchedule() {
        try {
            // Create the document
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            //<Order_Schedule>
            Element CSelem = doc.createElement("Order_Schedule");
            doc.appendChild(CSelem);

            Transform[] tfTODO = db.getTransform();
            Transform[] tfDOING = db.getElapseTransform();
            Transform[] tfDONE = db.getEndTransform();

            // pi = Penalty Incurred or Excepted
            int pi;

            for (int i=0; i< tfTODO.length ; i++) {
                //<Order/>
                Element OrderElem = doc.createElement("Order");
                CSelem.appendChild(OrderElem);

                //number
                Attr attr = doc.createAttribute("Number");
                attr.setValue(String.valueOf(tfTODO[i].getOrderNumber()));
                OrderElem.setAttributeNode(attr);

                //from
                attr = doc.createAttribute("From");
                attr.setValue("P" + tfTODO[i].getFrom());
                OrderElem.setAttributeNode(attr);

                //to
                attr = doc.createAttribute("To");
                attr.setValue("P" + tfTODO[i].getTo());
                OrderElem.setAttributeNode(attr);

                //quantity
                attr = doc.createAttribute("Quantity");
                attr.setValue(String.valueOf(tfTODO[i].getQuantity()));
                OrderElem.setAttributeNode(attr);

                //quantity1
                attr = doc.createAttribute("Quantity1");
                attr.setValue(String.valueOf(0));
                OrderElem.setAttributeNode(attr);

                //quantity2
                attr = doc.createAttribute("Quantity2");
                attr.setValue(String.valueOf(0));
                OrderElem.setAttributeNode(attr);

                //quantity3
                attr = doc.createAttribute("Quantity3");
                attr.setValue(String.valueOf(tfTODO[i].getQuantity()));
                OrderElem.setAttributeNode(attr);

                //time
                attr = doc.createAttribute("Time");
                attr.setValue(String.valueOf(tfTODO[i].getTime()));
                OrderElem.setAttributeNode(attr);

                //time1
                attr = doc.createAttribute("Time1");
                attr.setValue(String.valueOf(tfTODO[i].getTimeMES()));
                OrderElem.setAttributeNode(attr);

                //maxdelay
                attr = doc.createAttribute("MaxDelay");
                attr.setValue(String.valueOf(tfTODO[i].getMaxDelay()));
                OrderElem.setAttributeNode(attr);

                //penalty
                attr = doc.createAttribute("Penalty");
                attr.setValue(String.valueOf(tfTODO[i].getInitPenalty()));
                OrderElem.setAttributeNode(attr);

                pi=0;
                for(int a=i; a>=i; a--){
                    pi += db.getPenaltyExcepted(tfTODO[a].getFrom(),tfTODO[a].getTo(),tfTODO[a].getQuantity(), tfTODO[a].getMaxDelay());
                }

                //start excepted
                attr = doc.createAttribute("Start");
                attr.setValue(String.valueOf( (pi-db.getPenaltyExcepted(tfTODO[i].getFrom(),tfTODO[i].getTo(),tfTODO[i].getQuantity(), tfTODO[i].getMaxDelay()))*50 ));
                OrderElem.setAttributeNode(attr);

                //end excepted
                attr = doc.createAttribute("End");
                attr.setValue(String.valueOf(pi*50));
                OrderElem.setAttributeNode(attr);

                //penalty excepted
                attr = doc.createAttribute("PenaltyIncurred");
                attr.setValue(String.valueOf(pi));
                OrderElem.setAttributeNode(attr);

            }

            for (int i=0; i< tfDOING.length ; i++) {
                //<Order/>
                Element OrderElem = doc.createElement("Order");
                CSelem.appendChild(OrderElem);

                //number
                Attr attr = doc.createAttribute("Number");
                attr.setValue(String.valueOf(tfDOING[i].getOrderNumber()));
                OrderElem.setAttributeNode(attr);

                //from
                attr = doc.createAttribute("From");
                attr.setValue("P" + tfDOING[i].getFrom());
                OrderElem.setAttributeNode(attr);

                //to
                attr = doc.createAttribute("To");
                attr.setValue("P" + tfDOING[i].getTo());
                OrderElem.setAttributeNode(attr);

                //quantity
                attr = doc.createAttribute("Quantity");
                attr.setValue(String.valueOf(tfDOING[i].getQuantity()));
                OrderElem.setAttributeNode(attr);

                //quantity1
                attr = doc.createAttribute("Quantity1");
                if(tfDOING[i].getSide().equals("right")) {
                    attr.setValue(String.valueOf(opcR.getCountRightProd()));
                }
                else if(tfDOING[i].getSide().equals("left")) {
                    attr.setValue(String.valueOf(opcR.getCountLeftProd()));
                }
                else {
                    attr.setValue(String.valueOf(0));
                }
                OrderElem.setAttributeNode(attr);

                //quantity2
                attr = doc.createAttribute("Quantity2");
                if(tfDOING[i].getSide().equals("right")) {
                    attr.setValue(String.valueOf(tfDOING[i].getQuantity() - opcR.getCountRightProd() - opcR.getCountRightPorProd()));
                }
                else if(tfDOING[i].getSide().equals("left")) {
                    attr.setValue(String.valueOf(tfDOING[i].getQuantity() - opcR.getCountLeftProd() - opcR.getCountLeftPorProd()));
                }
                else {
                    attr.setValue(String.valueOf(0));
                }
                OrderElem.setAttributeNode(attr);

                //quantity3
                attr = doc.createAttribute("Quantity3");
                if(tfDOING[i].getSide().equals("right")) {
                    attr.setValue(String.valueOf(opcR.getCountRightPorProd()));
                }
                else if(tfDOING[i].getSide().equals("left")) {
                    attr.setValue(String.valueOf(opcR.getCountLeftPorProd()));
                }
                else {
                    attr.setValue(String.valueOf(0));
                }
                attr.setValue(String.valueOf(0));
                OrderElem.setAttributeNode(attr);

                //time
                attr = doc.createAttribute("Time");
                attr.setValue(String.valueOf(tfDOING[i].getTime()));
                OrderElem.setAttributeNode(attr);

                //time1
                attr = doc.createAttribute("Time1");
                attr.setValue(String.valueOf(tfDOING[i].getTimeMES()));
                OrderElem.setAttributeNode(attr);

                //maxdelay
                attr = doc.createAttribute("MaxDelay");
                attr.setValue(String.valueOf(tfDOING[i].getMaxDelay()));
                OrderElem.setAttributeNode(attr);

                //penalty
                attr = doc.createAttribute("Penalty");
                attr.setValue(String.valueOf(tfDOING[i].getInitPenalty()));
                OrderElem.setAttributeNode(attr);

                pi=0;
                for(int a=i; a>=i; a--){
                    pi += db.getPenaltyExcepted(tfDOING[a].getFrom(),tfDOING[a].getTo(),tfDOING[a].getQuantity(),tfDOING[a].getMaxDelay());
                }

                //start
                attr = doc.createAttribute("Start");
                attr.setValue(String.valueOf(tfDOING[i].getST()));
                OrderElem.setAttributeNode(attr);

                //end excepted
                attr = doc.createAttribute("End");
                attr.setValue(String.valueOf(pi*50));
                OrderElem.setAttributeNode(attr);

                //penalty excepted
                attr = doc.createAttribute("PenaltyIncurred");
                attr.setValue(String.valueOf(pi));
                OrderElem.setAttributeNode(attr);

            }

            for (Transform transform : tfDONE) {
                //<Order/>
                Element OrderElem = doc.createElement("Order");
                CSelem.appendChild(OrderElem);

                //number
                Attr attr = doc.createAttribute("Number");
                attr.setValue(String.valueOf(transform.getOrderNumber()));
                OrderElem.setAttributeNode(attr);

                //from
                attr = doc.createAttribute("From");
                attr.setValue("P" + transform.getFrom());
                OrderElem.setAttributeNode(attr);

                //to
                attr = doc.createAttribute("To");
                attr.setValue("P" + transform.getTo());
                OrderElem.setAttributeNode(attr);

                //quantity
                attr = doc.createAttribute("Quantity");
                attr.setValue(String.valueOf(transform.getQuantity()));
                OrderElem.setAttributeNode(attr);

                //quantity1
                attr = doc.createAttribute("Quantity1");
                attr.setValue(String.valueOf(transform.getQuantity()));
                OrderElem.setAttributeNode(attr);

                //quantity2
                attr = doc.createAttribute("Quantity2");
                attr.setValue(String.valueOf(0));
                OrderElem.setAttributeNode(attr);

                //quantity3
                attr = doc.createAttribute("Quantity3");
                attr.setValue(String.valueOf(0));
                OrderElem.setAttributeNode(attr);

                //time
                attr = doc.createAttribute("Time");
                attr.setValue(String.valueOf(transform.getTime()));
                OrderElem.setAttributeNode(attr);

                //time1
                attr = doc.createAttribute("Time1");
                attr.setValue(String.valueOf(transform.getTimeMES()));
                OrderElem.setAttributeNode(attr);

                //maxdelay
                attr = doc.createAttribute("MaxDelay");
                attr.setValue(String.valueOf(transform.getMaxDelay()));
                OrderElem.setAttributeNode(attr);

                //penalty
                attr = doc.createAttribute("Penalty");
                attr.setValue(String.valueOf(transform.getInitPenalty()));
                OrderElem.setAttributeNode(attr);

                //start
                attr = doc.createAttribute("Start");
                attr.setValue(String.valueOf(transform.getST()));
                OrderElem.setAttributeNode(attr);

                //end
                attr = doc.createAttribute("End");
                attr.setValue(String.valueOf(transform.getET()));
                OrderElem.setAttributeNode(attr);

                //penalty incurred
                attr = doc.createAttribute("PenaltyIncurred");
                attr.setValue(String.valueOf(transform.getPenalty()));
                OrderElem.setAttributeNode(attr);

            }

            //Create file XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("sendCurrentStores.xml"));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | SQLException | TransformerException e) {
            e.printStackTrace();
        }

    }

    public void sendXML(String file) {
        try {
            byte[] buffer = Files.readAllBytes(Paths.get(file));
            packUDP = new DatagramPacket(buffer, buffer.length, addressServer, portServer);
            client.socket.send(packUDP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}