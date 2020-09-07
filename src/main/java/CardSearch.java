import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class CardSearch {
    public CardSearch() throws Exception {
        URL url = new URL("https://github.com/ProjectIgnis/BabelCDB/raw/master/cards.cdb");
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream("cards.cdb");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    public String search(String recherche){

        StringBuilder msg = new StringBuilder();
        if(!recherche.equals("")){
            Connection connection = null;
            try
            {
                // create a database connection
                connection = DriverManager.getConnection("jdbc:sqlite:cards.cdb");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
                ResultSet rs = statement.executeQuery("SELECT id FROM texts WHERE LOWER(name)=LOWER('"+recherche+"')");
                if(rs.next()){
                    msg.append("https://ygoprodeck.com/pics/").append(rs.getInt("id")).append(".jpg");
                    while (rs.next()){
                        msg.append("\n").append("https://ygoprodeck.com/pics/").append(rs.getInt("id")).append(".jpg");
                    }
                } else {
                    msg.append("D\u00e9sol\u00e9, je n'ai pas trouv\u00e9 cette carte.");
                    msg.append("\nTu cherchais peut-\u00eatre celles-ci :");
                    rs = statement.executeQuery("SELECT id,name FROM texts WHERE LOWER(name) LIKE LOWER('%"+recherche+"%')");
                    while (rs.next()){
                        msg.append("\n").append(rs.getString("name"));
                    }
                }
            }
            catch(SQLException e)
            {
                // if the error message is "out of memory",
                // it probably means no database file is found
                System.err.println(e.getMessage());
            }
            finally
            {
                try
                {
                    if(connection != null)
                        connection.close();
                }
                catch(SQLException e)
                {
                    // connection close failed.
                    System.err.println(e.getMessage());
                }
            }
        } else {
            msg.append("Je ne peux pas chercher une carte sans nom.");
        }

        return msg.toString();
    }

    public String searchArch(String arch) {
        String res = "";
        return res;
    }

    public JSONObject searchById(String id) {
        return null;
    }

    public String random() {
        return "";
    }
}
