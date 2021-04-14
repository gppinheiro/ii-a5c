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
        PreparedStatement s = this.conn.prepareStatement("INSERT INTO ii.\"ElapseTransform\" VALUES (?,?,?,?,?,?,?,?);");
        s.setInt(1,tf.getOrderNumber());
        s.setInt(2,tf.getFrom());
        s.setInt(3,tf.getTo());
        s.setInt(4,tf.getQuantity());
        s.setInt(5,tf.getMaxDelay());
        s.setInt(6,tf.getPenalty());
        s.setTimestamp(7,new Timestamp(System.currentTimeMillis()));
        s.setString(8,side);
        s.executeUpdate();
    }

    public void addEndTransform(Transform tf, String side, int ft) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("INSERT INTO ii.\"EndTransform\" VALUES (?,?,?,?,?,?,?,?);");
        s.setInt(1,tf.getOrderNumber());
        s.setInt(2,tf.getFrom());
        s.setInt(3,tf.getTo());
        s.setInt(4,tf.getQuantity());
        s.setInt(5,tf.getPenalty());
        s.setInt(6,ft);
        s.setTimestamp(7,new Timestamp(System.currentTimeMillis()));
        s.setString(8,side);
        s.executeUpdate();
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

}
