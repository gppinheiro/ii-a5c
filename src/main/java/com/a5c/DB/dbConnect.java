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

    /**
     * Method to get DB Connection.
     * @return DB Connection.
     */
    public Connection getConn() {
        return this.conn;
    }

    public void searchPath() throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("SET search_path TO ii;");
        s.executeUpdate();
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

    public void addUnload(Unload un) throws SQLException {
        PreparedStatement s = this.conn.prepareStatement("INSERT INTO ii.\"Unload\" VALUES (?,?,?,?,?);");
        s.setInt(1,un.getOrderNumber());
        s.setInt(2,un.getType());
        s.setInt(3,un.getDestination());
        s.setInt(4,un.getQuantity());
        s.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
        s.executeUpdate();
    }

}
