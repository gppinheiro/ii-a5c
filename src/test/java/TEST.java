import com.a5c.DATA.Transform;
import com.a5c.DB.dbConnect;

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

    public static void main(final String[] args) {
        dbConnect db = new dbConnect();
        try {
            Transform tf_test = new Transform(7,2,8,2,0,0,100);
            Transform tf_test2 = new Transform(8,1,6,4,0,0,100);
            Transform tf_test3 = new Transform(9,1,2,1,0,0,100);
            db.addTransform(tf_test);
            db.addTransform(tf_test2);
            db.addTransform(tf_test3);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(TestIfGoodOrNot(db));
    }
}
