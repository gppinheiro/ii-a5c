import com.a5c.DB.dbConnect;

import java.sql.SQLException;

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

    public static void main(final String[] args) {
        System.out.println(getTFdb());
    }
}
