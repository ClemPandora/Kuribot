import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class CardSearch {
    private HashMap<String, HashMap<String,String>> texts;

    public CardSearch() throws Exception {
        URL url = new URL("https://github.com/ProjectIgnis/BabelCDB/raw/master/cards.cdb");
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream("cards.cdb");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        texts = new HashMap<>();
        URL url2 = new URL("https://raw.githubusercontent.com/NaimSantos/DataEditorX/master/DataEditorX/data/cardinfo_english.txt");
        Scanner scan = new Scanner(url2.openStream());
        // "0x" + Integer.toHexString(int)
        scan.nextLine();
        while (!scan.hasNext("#end") && scan.hasNext()){
            String category = scan.nextLine().substring(2);
            texts.put(category, new HashMap<>());
            while(!scan.hasNext("##.+") && scan.hasNext()){
                texts.get(category).put(scan.next().replaceAll("^0x", ""), scan.nextLine().trim());
            }
        }
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
        for (String value : texts.get("setname").values()) {
            if(value.equalsIgnoreCase(arch)){
                return value;
            }
        }
        return "";
    }

    public JSONObject searchById(String id) {
        return null;
    }

    public String random() {
        return "";
    }
}
