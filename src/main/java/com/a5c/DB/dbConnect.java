package com.a5c.DB;

import com.a5c.DATA.Transform;
import com.a5c.DATA.Unload;

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

    /**
     * Constructor that creates connection with DataBase.
     */
    public dbConnect() {
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
        PreparedStatement s = this.conn.prepareStatement("INSERT INTO ii.\"Transform\" VALUES (?,?,?,?,?,?,?,?);");
        s.setInt(1,tf.getOrderNumber());
        s.setInt(2,tf.getFrom());
        s.setInt(3,tf.getTo());
        s.setInt(4,tf.getQuantity());
        s.setInt(5,tf.getTime());
        s.setInt(6,tf.getMaxDelay());
        s.setInt(7,tf.getPenalty());
        s.setTimestamp(8,new Timestamp(System.currentTimeMillis()));
        s.executeUpdate();
    }

    public void addElapseTransform(Transform tf, String side) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("INSERT INTO ii.\"ElapseTransform\" VALUES (?,?,?,?,?,?,?,?,?);");
        s.setInt(1,tf.getOrderNumber());
        s.setInt(2,tf.getFrom());
        s.setInt(3,tf.getTo());
        s.setInt(4,tf.getQuantity());
        s.setInt(5,tf.getMaxDelay());
        s.setInt(6,tf.getPenalty());
        s.setTimestamp(7,new Timestamp(System.currentTimeMillis()));
        s.setString(8,side);
        s.setInt(9,tf.getTime());
        s.executeUpdate();
    }

    public void addEndTransform(Transform tf, String side, int ft) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("INSERT INTO ii.\"EndTransform\" VALUES (?,?,?,?,?,?,?,?,?,?,?);");
        s.setInt(1,tf.getOrderNumber());
        s.setInt(2,tf.getFrom());
        s.setInt(3,tf.getTo());
        s.setInt(4,tf.getQuantity());
        s.setInt(5,tf.getPenalty());
        s.setInt(6,ft);
        s.setTimestamp(7,new Timestamp(System.currentTimeMillis()));
        s.setString(8,side);
        s.setInt(9,tf.getTime());
        s.setInt(10,tf.getMaxDelay());
        s.setInt(11,tf.getInitPenalty());
        s.executeUpdate();
    }

    public int TransformLength() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"Transform\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public Transform[] getTransform() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"Transform\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        Transform[] Transforms = new Transform[rs.getInt(1)];

        s = this.conn.prepareStatement("SELECT \"OrderNumber\", \"from\", \"to\", quantity, time, \"MaxDelay\", penalty  FROM ii.\"Transform\";");
        rs = s.executeQuery();

        int i=0;
        while (rs.next()) {
            Transforms[i] = new Transform(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getInt(7));
            i++;
        }

        return Transforms;
    }

    public Transform[] getElapseTransform() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"ElapseTransform\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        Transform[] Transforms = new Transform[rs.getInt(1)];

        s = this.conn.prepareStatement("SELECT \"OrderNumber\", \"from\", \"to\", quantity, time, \"MaxDelay\", penalty  FROM ii.\"ElapseTransform\";");
        rs = s.executeQuery();

        int i=0;
        while (rs.next()) {
            Transforms[i] = new Transform(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getInt(7));
            i++;
        }

        return Transforms;
    }

    public Transform[] getEndTransform() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SELECT COUNT(*) FROM ii.\"EndTransform\";");
        ResultSet rs = s.executeQuery();
        rs.next();
        Transform[] Transforms = new Transform[rs.getInt(1)];

        s = this.conn.prepareStatement("SELECT \"OrderNumber\", \"from\", \"to\", quantity, time, \"MaxDelay\", penalty  FROM ii.\"EndTransform\";");
        rs = s.executeQuery();

        int i=0;
        while (rs.next()) {
            Transforms[i] = new Transform(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getInt(7));
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

}
