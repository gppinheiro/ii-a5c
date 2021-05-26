package com.a5c.DB;

import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;
import com.a5c.OPC_UA.readOPC;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

/**
 * Class dbConnect is used to: communicate with Heroku DataBase and create a Prepared Statements to send to the DB.
 * Date: March-3-2021.
 * @author Group A5_C.
 */
public class dbConnect {
    /**
     * Store Heroku URI: User + Password + Host + Port + Database.
     * Private - Nobody needs to know what URI we are using.
     * Static and Final - Never changes.
     */
    private static final String heroku_url="postgres://vkgvpttoqwwjti:241f4c5e49e1ff17e84ecab4bbe8c63ead0a80d1684405a3c5fa8542f36de5c0@ec2-54-74-35-87.eu-west-1.compute.amazonaws.com:5432/d2j57fljq86oa0";
    /**
     * Store DB Connection.
     * Private - Nobody needs to know the db connection.
     */
    private Connection conn = null;
    private final Timestamp initTime;
    private final readOPC opcR;

    public boolean reading;

    public Connection getConn() {
        return conn;
    }

    /**
     * Constructor that creates connection with DataBase.
     */
    public dbConnect(readOPC opc) {
        this.initTime = new Timestamp(System.currentTimeMillis());
        this.reading = false;
        this.opcR = opc;
        try {
            this.conn = getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to make first connection.
     * @return DB Connection.
     * @throws URISyntaxException if occurs an URI error in syntax.
     * @throws SQLException if occurs an error in DB.
     */
    private static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI(heroku_url);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        return DriverManager.getConnection(dbUrl, username, password);
    }

    public void addTransform(Transform tf) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("INSERT INTO ii.\"Transform\" VALUES (?,?,?,?,?,?,?,?,?,?);");
        s.setInt(1,tf.getOrderNumber());
        s.setInt(2,tf.getFrom());
        s.setInt(3,tf.getTo());
        s.setInt(4,tf.getQuantity());
        s.setInt(5,tf.getTime());
        s.setInt(6,tf.getMaxDelay());
        s.setInt(7,tf.getPenalty());
        s.setTimestamp(8,new Timestamp(System.currentTimeMillis()));
        s.setInt(9,tf.getTimeMES());
        s.setInt(10,tf.getExceptedTT());
        s.executeUpdate();
    }

    public Transform addElapseTransform(Transform tf, String side) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("INSERT INTO ii.\"ElapseTransform\" VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
        s.setInt(1,tf.getOrderNumber());
        s.setInt(2,tf.getFrom());
        s.setInt(3,tf.getTo());
        s.setInt(4,tf.getQuantity());
        s.setInt(5,tf.getMaxDelay());
        s.setInt(6,tf.getPenalty());
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        tf.setST((int) (ts.getTime()-initTime.getTime())/1000);
        s.setTimestamp(7,ts);
        s.setString(8,side);
        s.setInt(9,tf.getTime());
        s.setInt(10,tf.getST());
        s.setInt(11,tf.getTimeMES());
        s.setInt(12,tf.getQuantity());
        s.executeUpdate();

        return tf;
    }

    public void addEndTransform(Transform tf, String side, int ft) throws SQLException {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        tf.setET((int) (ts.getTime()-initTime.getTime())/1000);
        tf.setPenalty(Math.max((tf.getET()-tf.getTimeMES()-tf.getMaxDelay())/50 * tf.getInitPenalty(), 0));

        PreparedStatement s = this.conn.prepareStatement("INSERT INTO ii.\"EndTransform\" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
        s.setInt(1,tf.getOrderNumber());
        s.setInt(2,tf.getFrom());
        s.setInt(3,tf.getTo());
        s.setInt(4,tf.getQuantity());
        s.setInt(5,tf.getPenalty());
        s.setInt(6,ft);
        s.setTimestamp(7,ts);
        s.setString(8,side);
        s.setInt(9,tf.getTime());
        s.setInt(10,tf.getMaxDelay());
        s.setInt(11,tf.getInitPenalty());
        s.setInt(12,tf.getST());
        s.setInt(13,tf.getET());
        s.setInt(14,tf.getTimeMES());
        s.executeUpdate();
    }

    public int TransformLength() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"Transform\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public int ElapseTransformLength() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"ElapseTransform\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public int HowManyAreDoing(String side) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"ElapseTransform\" WHERE side=?;");
        s.setString(1,side);
        ResultSet rs = s.executeQuery();
        rs.next();
        System.out.println("side: "+side+" val: "+rs.getInt(1));
        return rs.getInt(1);
    }

    public Transform[] getTransform() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"Transform\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        Transform[] Transforms = new Transform[rs.getInt(1)];

        s = this.conn.prepareStatement("SELECT \"OrderNumber\", \"from\", \"to\", quantity, time, \"MaxDelay\", penalty, \"timeMES\", \"TransformTimeExcepted\" FROM ii.\"Transform\";");
        rs = s.executeQuery();

        int i=0;
        while (rs.next() && i<Transforms.length) {
            Transforms[i] = new Transform(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getInt(7));
            Transforms[i].setTimeMES(rs.getInt(8));
            Transforms[i].setExceptedTT(rs.getInt(9));
            i++;
        }

        return Transforms;
    }

    public Transform[] getElapseTransform() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"ElapseTransform\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        Transform[] Transforms = new Transform[rs.getInt(1)];

        s = this.conn.prepareStatement("SELECT \"OrderNumber\", \"from\", \"to\", quantity, time, \"MaxDelay\", penalty, st, \"timeMES\", \"side\", \"PorProd\" FROM ii.\"ElapseTransform\";");
        rs = s.executeQuery();

        int i=0;
        while (rs.next() && i<Transforms.length) {
            Transforms[i] = new Transform(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getInt(7));
            Transforms[i].setST(rs.getInt(8));
            Transforms[i].setTimeMES(rs.getInt(9));
            Transforms[i].setSide(rs.getString(10));
            Transforms[i].setPorProd(rs.getInt(11));
            i++;
        }

        return Transforms;
    }

    public Transform getElapseTransform(int number_order, String side) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT \"OrderNumber\", \"from\", \"to\", quantity, time, \"MaxDelay\", penalty, st, \"timeMES\", \"PorProd\"  FROM ii.\"ElapseTransform\" WHERE \"OrderNumber\"=? AND side=?;");
        s.setInt(1,number_order);
        s.setString(2,side);

        ResultSet rs = s.executeQuery();
        if (rs.next()) {
            Transform tfs = new Transform(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getInt(7));
            tfs.setST(rs.getInt(8));
            tfs.setTimeMES(rs.getInt(9));
            tfs.setPorProd(rs.getInt(10));
            tfs.setSide(side);

            return tfs;
        }
        else return null;
    }

    public void updateElapseTransform(int nOrder, int val) throws SQLException {
        PreparedStatement s =  this.conn.prepareStatement("UPDATE ii.\"ElapseTransform\" SET \"PorProd\"=? WHERE \"OrderNumber\"=?;");
        s.setInt(1,val);
        s.setInt(2,nOrder);
        s.executeUpdate();
    }

    public Transform[] getEndTransform() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"EndTransform\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        Transform[] Transforms = new Transform[rs.getInt(1)];

        s = this.conn.prepareStatement("SELECT \"OrderNumber\", \"from\", \"to\", quantity, time, \"MaxDelay\", penalty, \"InitialPenalty\", st, et, \"timeMES\"  FROM ii.\"EndTransform\";");
        rs = s.executeQuery();

        int i=0;
        while (rs.next()) {
            Transforms[i] = new Transform(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getInt(7));
            Transforms[i].setInitPenalty(rs.getInt(8));
            Transforms[i].setST(rs.getInt(9));
            Transforms[i].setET(rs.getInt(10));
            Transforms[i].setTimeMES(rs.getInt(11));
            i++;
        }

        return Transforms;
    }

    public void deleteTransform(Transform tf, String table_name) throws SQLException {
        PreparedStatement s = null;
        if (table_name.equals("Transform")) {
            s = this.conn.prepareStatement("DELETE FROM ii.\"Transform\" WHERE \"OrderNumber\"="+tf.getOrderNumber()+";");
        }
        else if (table_name.equals("ElapseTransform")) {
            s = this.conn.prepareStatement("DELETE FROM ii.\"ElapseTransform\" WHERE \"OrderNumber\"="+tf.getOrderNumber()+";");
        }
        assert s != null;
        s.executeUpdate();
    }

    public void addUnload(Unload un) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("INSERT INTO ii.\"Unload\" VALUES (?,?,?,?,?);");
        s.setInt(1,un.getOrderNumber());
        s.setInt(2,un.getType());
        s.setInt(3,un.getDestination());
        s.setInt(4,un.getQuantity());
        s.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
        s.executeUpdate();
    }

    public void addEndUnload(Unload un) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("INSERT INTO ii.\"EndUnload\" VALUES (?,?,?,?,?);");
        s.setInt(1,un.getOrderNumber());
        s.setInt(2,un.getType());
        s.setInt(3,un.getDestination());
        s.setInt(4,un.getQuantity());
        s.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
        s.executeUpdate();
    }

    public int UnloadLength() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"Unload\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public Unload[] getUnload() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"Unload\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        Unload[] Unloads = new Unload[rs.getInt(1)];

        s = this.conn.prepareStatement("SELECT \"OrderNumber\", type, destination, quantity FROM ii.\"Unload\";");
        rs = s.executeQuery();

        int i=0;
        while (rs.next()) {
            Unloads[i] = new Unload(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4));
            i++;
        }

        return Unloads;
    }

    public Integer[] getCurrentStores() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT type, quantity FROM ii.\"CurrentStores\";");
        ResultSet rs = s.executeQuery();

        Integer[] pieces = new Integer[10];
        pieces[0]=0;
        while (rs.next()) {
            pieces[rs.getInt(1)] = rs.getInt(2);
        }

        return pieces;
    }

    public void deleteUnload(Unload un) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("DELETE FROM ii.\"Unload\" WHERE \"OrderNumber\"="+un.getOrderNumber()+";");
        s.executeUpdate();
    }

    public void updateCurrentStores(int[] wv) throws SQLException {
        PreparedStatement s;
        for(int i=0; i<wv.length; i++) {
            s = this.conn.prepareStatement("UPDATE ii.\"CurrentStores\" SET quantity=? WHERE \"type\"="+(i+1)+";");
            s.setInt(1,wv[i]);
            s.executeUpdate();
        }
    }

    public void updateCurrentStores() throws SQLException {
        int[] wv = opcR.getWareHouse();
        PreparedStatement s;
        for(int i=0; i<wv.length; i++) {
            s = this.conn.prepareStatement("UPDATE ii.\"CurrentStores\" SET quantity=? WHERE \"type\"="+(i+1)+";");
            s.setInt(1,wv[i]);
            s.executeUpdate();
        }
    }

    // T1: 15 15
    // T2: P2-P3 15
    // T3: P3-P4 15
    // T4: P4-P5 15
    // T5: P5-P6 30
    // T6: P6-P7 30
    // T7: P6-P8 15
    // T8: P5-P9 30
    public void updateMachinesStatistic (int id, int[] values) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("UPDATE ii.\"MachinesStatistic\" SET t1=?,t2=?,t3=?,t4=?,t5=?,t6=?,t7=?,t8=?,total=? WHERE machine=?;");
        s.setInt(1,values[0]);
        s.setInt(2,values[1]);
        s.setInt(3,values[2]);
        s.setInt(4,values[3]);
        s.setInt(5,values[4]);
        s.setInt(6,values[5]);
        s.setInt(7,values[6]);
        s.setInt(8,values[7]);
        s.setInt(9,values[0]+values[1]+values[2]+values[3]+values[4]+values[5]+values[6]+values[7]);

        String str = null;
        if (id==1) { str="LM1"; }
        else if (id==2) { str="LM2"; }
        else if (id==3) { str="LM3"; }
        else if (id==4) { str="LM4"; }
        else if (id==5) { str="RM1"; }
        else if (id==6) { str="RM2"; }
        else if (id==7) { str="RM3"; }
        else if (id==8) { str="RM4"; }
        s.setString(10,str);

        s.executeUpdate();

        s = this.conn.prepareStatement("UPDATE ii.\"MachinesTimes\" SET t1=?,t2=?,t3=?,t4=?,t5=?,t6=?,t7=?,t8=?,total=? WHERE machine=?;");
        s.setInt(1,values[0]*15);
        s.setInt(2,values[1]*15);
        s.setInt(3,values[2]*15);
        s.setInt(4,values[3]*15);
        s.setInt(5,values[4]*30);
        s.setInt(6,values[5]*30);
        s.setInt(7,values[6]*15);
        s.setInt(8,values[7]*30);
        s.setInt(9,values[0]*15+values[1]*15+values[2]*15+values[3]*15+values[4]*30+values[5]*30+values[6]*15+values[7]*30);
        s.setString(10,str);

        s.executeUpdate();
    }

    public void resetMachinesStatistic() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("UPDATE ii.\"MachinesStatistic\" SET t1=0,t2=0,t3=0,t4=0,t5=0,t6=0,t7=0,t8=0,total=0 WHERE machine=?;");
        PreparedStatement ss = this.conn.prepareStatement("UPDATE ii.\"MachinesTimes\" SET t1=0,t2=0,t3=0,t4=0,t5=0,t6=0,t7=0,t8=0,total=0 WHERE machine=?;");
        String str = null;
        for(int id=1; id<9; id++) {
            if (id==1) { str="LM1"; }
            else if (id==2) { str="LM2"; }
            else if (id==3) { str="LM3"; }
            else if (id==4) { str="LM4"; }
            else if (id==5) { str="RM1"; }
            else if (id==6) { str="RM2"; }
            else if (id==7) { str="RM3"; }
            else if (id==8) { str="RM4"; }
            s.setString(1,str);
            ss.setString(1,str);
            s.executeUpdate();
            ss.executeUpdate();
        }
    }

    public void updatePushersStatistic (int id, int[] values) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("UPDATE ii.\"PushersStatistic\" SET p1=?,p2=?,p3=?,p4=?,p5=?,p6=?,p7=?,p8=?,p9=?,total=? WHERE pusher=?;");
        s.setInt(1,values[0]);
        s.setInt(2,values[1]);
        s.setInt(3,values[2]);
        s.setInt(4,values[3]);
        s.setInt(5,values[4]);
        s.setInt(6,values[5]);
        s.setInt(7,values[6]);
        s.setInt(8,values[7]);
        s.setInt(9,values[8]);
        s.setInt(10,values[0]+values[1]+values[2]+values[3]+values[4]+values[5]+values[6]+values[7]+values[8]);

        String str = null;
        if (id==1) { str="Pusher1"; }
        else if (id==2) { str="Pusher2"; }
        else if (id==3) { str="Pusher3"; }
        s.setString(11,str);

        s.executeUpdate();
    }

    public void resetPushersStatistic () throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("UPDATE ii.\"PushersStatistic\" SET p1=0,p2=0,p3=0,p4=0,p5=0,p6=0,p7=0,p8=0,p9=0,total=0 WHERE pusher=?;");

        String str = null;
        for (int id=1; id<4; id++) {
            if (id==1) { str="Pusher1"; }
            else if (id==2) { str="Pusher2"; }
            else if (id==3) { str="Pusher3"; }
            s.setString(1,str);
            s.executeUpdate();
        }
    }

    public int getPenaltyExcepted(int from, int to, int quantity, int maxDelay) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT tt FROM \"ExceptedTT\" WHERE \"from\"=? AND \"to\"=?;");
        s.setInt(1,from);
        s.setInt(2,to);

        ResultSet rs = s.executeQuery();
        rs.next();

        int tt = rs.getInt(1);

        int res;
        if (quantity>=5)
            res = ( (quantity/4)*(tt+2*3) + ( tt + 2*(quantity%4) ) - maxDelay)/50;
        else
            res = ((tt+2*(quantity-1) ) - maxDelay)/50;

        return Math.max(res,0);
    }

    public int getPiecesProd(int from, int to, int quantity, long time) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT tt FROM \"ExceptedTT\" WHERE \"from\"=? AND \"to\"=?;");
        s.setInt(1,from);
        s.setInt(2,to);

        ResultSet rs = s.executeQuery();
        rs.next();
        int tt = rs.getInt(1);

        int eTT;
        for(int i=0; i<quantity; i++) {
            if (i>=5)
                eTT = (i/4)*(tt+2*3) + tt + 2*(quantity%4);
            else
                eTT = tt + 2*(i-1);

            if(time < eTT ) { return i; }
        }

        return quantity;
    }

    public int[][] getAllExceptedTransformationTime() throws SQLException {
        int[][] ep = new int[7][10];

        PreparedStatement s = this.conn.prepareStatement("SELECT \"from\", \"to\", tt FROM \"ExceptedTT\";");
        ResultSet rs = s.executeQuery();

        while (rs.next()) {
            ep[rs.getInt(1)][rs.getInt(2)]=rs.getInt(3);
        }

        return ep;
    }

}
