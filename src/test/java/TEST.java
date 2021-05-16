import com.a5c.DATA.Transform;
import com.a5c.DB.dbConnect;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class TEST {
    public static int getLeftTimer() {
        //String s = (String) client.getValue("|var|CODESYS Control Win V3 x64.Application.GVL.timer_l");
        String s = "T#12m12s";
        // T#||m||s||ms
        s = s.replace("T#","");
        String[] sparts = s.split("s");

        if (sparts[0].contains("m")) {
            String[] mparts = sparts[0].split("m");
            return Integer.parseInt(mparts[0])*60 + Integer.parseInt(mparts[1]);
        }
        else {
            return Integer.parseInt(sparts[0]);
        }

    }

    public static int getTFdb() {
        dbConnect db = new dbConnect();
        try {
            return db.getTransform().length;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 1000;
    }

    public static int TestIfGoodOrNot(dbConnect db) {
        try {
            Transform[] tfs = db.getTransform();

            Transform temp;
            for (int i = 1; i < tfs.length; i++) {
                for (int j = i; j > 0; j--) {
                    if ((tfs[j].getMaxDelay() < tfs[j - 1].getMaxDelay()) || (tfs[j].getMaxDelay() == tfs[j - 1].getMaxDelay() && tfs[j].getPenalty() > tfs[j - 1].getPenalty())) {
                        temp = tfs[j];
                        tfs[j] = tfs[j - 1];
                        tfs[j-1] = temp;
                    }
                }
            }

            ArrayList<Transform> tfsAL = new ArrayList<>(Arrays.asList(tfs).subList(0, tfs.length));

            boolean easy = false;
            int i=0;

            while(!easy && i<tfs.length) {
                temp = tfsAL.get(i);
                if ( !(temp.getFrom() == 1 && (temp.getTo() == 6 || temp.getTo() == 7 || temp.getTo() == 8 || temp.getTo() == 9)) && !( temp.getFrom() == 2 && (temp.getTo() == 7 || temp.getTo() == 8) ) ) {
                    easy=true;
                    tfs[0]=temp;
                }
                else {
                    tfsAL.remove(temp);
                    tfsAL.add(temp);
                }
                i+=1;
            }

            return tfs[0].getOrderNumber();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return 1000;
    }

    public static void writeOrderSchedule(dbConnect db) {
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

                //time1 - Only 1s slower
                attr = doc.createAttribute("Time1");
                attr.setValue(String.valueOf(tfTODO[i].getTime()+1));
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
                    pi += db.getPenaltyExcepted(tfTODO[a].getFrom(),tfTODO[a].getTo(),tfTODO[a].getQuantity(),tfTODO[a].getMaxDelay());
                }

                //start excepted
                attr = doc.createAttribute("Start");
                attr.setValue(String.valueOf( (pi-db.getPenaltyExcepted(tfTODO[i].getFrom(),tfTODO[i].getTo(),tfTODO[i].getQuantity(),tfTODO[i].getMaxDelay()))*50 ));
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
                attr.setValue(String.valueOf(0));
                OrderElem.setAttributeNode(attr);

                //quantity2
                attr = doc.createAttribute("Quantity2");
                attr.setValue(String.valueOf(tfDOING[i].getQuantity()));
                OrderElem.setAttributeNode(attr);

                //quantity3
                attr = doc.createAttribute("Quantity3");
                attr.setValue(String.valueOf(0));
                OrderElem.setAttributeNode(attr);

                //time
                attr = doc.createAttribute("Time");
                attr.setValue(String.valueOf(tfDOING[i].getTime()));
                OrderElem.setAttributeNode(attr);

                //time1 - Only 1s slower
                attr = doc.createAttribute("Time1");
                attr.setValue(String.valueOf(tfDOING[i].getTime()+1));
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

                //time1 - Only 1s slower
                attr = doc.createAttribute("Time1");
                attr.setValue(String.valueOf(transform.getTime() + 1));
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
            StreamResult result = new StreamResult(new File("sendRequestOrders.xml"));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | SQLException | TransformerException e) {
            e.printStackTrace();
        }

    }

    public static void getTransformOrderBy(dbConnect db) throws SQLException {
        PreparedStatement s = db.getConn().prepareStatement("SELECT COUNT(*) FROM ii.\"Transform\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        Transform[] Transforms = new Transform[rs.getInt(1)];

        s = db.getConn().prepareStatement("SELECT \"OrderNumber\", \"from\", \"to\", quantity, time, \"MaxDelay\", penalty, \"timeMES\"  FROM ii.\"Transform\" ORDER BY penalty DESC, \"MaxDelay\" ASC;");
        rs = s.executeQuery();

        int i=0;
        while (rs.next()) {
            Transforms[i] = new Transform(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getInt(7));
            Transforms[i].setTimeMES(rs.getInt(8));
            i++;
        }

    }

    public static void getTransformOld(dbConnect db) throws SQLException, InterruptedException {
        Transform[] tfs = db.getTransform();
        long initTime = System.currentTimeMillis();

        Thread.sleep(5000);

        long nowTime = System.currentTimeMillis();

        Transform temp;
        tfs[0].setRealMaxDelay((int) ( tfs[0].getMaxDelay() - ( nowTime - initTime )/1000 ) );
        for (int i = 1; i < tfs.length; i++) {
            tfs[i].setRealMaxDelay((int) ( tfs[i].getMaxDelay() - ( nowTime - initTime )/1000 ) );
            for (int j = i; j > 0; j--) {
                if ((tfs[j].getRealMaxDelay() < tfs[j - 1].getRealMaxDelay()) || (tfs[j].getRealMaxDelay() == tfs[j - 1].getRealMaxDelay() && tfs[j].getPenalty() > tfs[j - 1].getPenalty())) {
                    temp = tfs[j];
                    tfs[j] = tfs[j - 1];
                    tfs[j-1] = temp;
                }
            }
        }

        for (Transform tf: tfs) {
            System.out.println(tf.toString());
        }

    }

    public static void getExceptedPenalty(dbConnect db) throws SQLException {
        int[][] ep = new int[7][10];

        PreparedStatement s = db.getConn().prepareStatement("SELECT \"from\", \"to\", tt FROM ii.\"ExceptedPenalty\";");
        ResultSet rs = s.executeQuery();

        while (rs.next()) {
            ep[rs.getInt(1)][rs.getInt(2)]=rs.getInt(3);
        }

        for (int[] i: ep) {
            System.out.println(Arrays.toString(i));
        }
    }

    public static void main(final String[] args) {
        dbConnect db = new dbConnect();
        try {
            /*db.addTransform(new Transform(1,1,2,1,0,10,10));
            db.addTransform(new Transform(2,1,2,2,0,10,20));
            db.addTransform(new Transform(3,1,2,2,0,15,20));
            db.addTransform(new Transform(4,1,2,2,0,15,10));*/
            //db.addTransform(new Transform(5,1,2,2,0,30,30));
            //db.deleteTransform("Transform");
            //db.deleteTransform("ElapseTransform");
            //db.addUnload(new Unload(1,1,2,4));
            db.getTransform();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
