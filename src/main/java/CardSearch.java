import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.*;
import java.text.Collator;
import java.util.*;

public class CardSearch {
    private HashMap<String, HashMap<String,String>> texts;
    private final String IMG_URL = "https://ygoprodeck.com/pics/";
    private final String CARDINFO_URL = "https://raw.githubusercontent.com/NaimSantos/DataEditorX/master/DataEditorX/data/cardinfo_english.txt";
    private final String[] CARDSDB_URL = {
            "https://github.com/ProjectIgnis/BabelCDB/raw/master/cards.cdb", // Anglais
            "https://github.com/ProjectIgnis/BabelCDB/raw/master/prerelease.cdb", // Nouvelles Anglais
            "https://github.com/Team13fr/IgnisMulti/raw/master/Fran%C3%A7ais/cards.cdb", // Français
            "https://github.com/Team13fr/IgnisMulti/raw/master/Fran%C3%A7ais/prerelease.cdb" // Nouvelles Français
    };
    private final String[] DBFILENAME = {
            "cards_en.cdb",
            "release_en.cdb",
            "cards_fr.cdb",
            "release_fr.cdb"
    };

    public CardSearch() throws Exception {
        for (int i = 0; i < CARDSDB_URL.length; i++) {
            URL url = new URL(CARDSDB_URL[i]);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(DBFILENAME[i]);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        }
        texts = new HashMap<>();
        URL url2 = new URL(CARDINFO_URL);
        Scanner scan = new Scanner(url2.openStream());
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
            recherche = recherche.replace("'", "''");
            ArrayList<Integer> ids = new ArrayList<>();
            Collection<String> sugg = new TreeSet<>(Collator.getInstance());
            for (String db : DBFILENAME) {
                Connection connection = null;
                try {
                    // create a database connection
                    connection = DriverManager.getConnection("jdbc:sqlite:" + db);
                    Statement statement = connection.createStatement();
                    statement.setQueryTimeout(30);  // set timeout to 30 sec.
                    ResultSet rs = statement.executeQuery("SELECT id FROM texts WHERE LOWER(name)=LOWER('" + recherche + "')");
                    //On ajoute toute les ids trouvés à la liste
                    while (rs.next()) {
                        if (!ids.contains(rs.getInt("id"))) {
                            ids.add(rs.getInt("id"));
                        }
                    }
                    //On ajoute des suggestion au cas ou aucune id n'est trouvée
                    if (ids.isEmpty()) {
                        rs = statement.executeQuery("SELECT name FROM texts WHERE LOWER(name) LIKE LOWER('%" + recherche + "%')");
                        while (rs.next()) {
                            sugg.add(rs.getString("name"));
                        }
                    }
                } catch (SQLException e) {
                    // if the error message is "out of memory",
                    // it probably means no database file is found
                    System.err.println(e.getMessage());
                } finally {
                    try {
                        if (connection != null)
                            connection.close();
                    } catch (SQLException e) {
                        // connection close failed.
                        System.err.println(e.getMessage());
                    }
                }
            }
            //On affiche les images si on a trouvé un résultat
            if (!ids.isEmpty()){
                for (int id: ids) {
                    msg.append(IMG_URL).append(id).append(".jpg\n");
                }
            } else {
                //Sinon on affiche les 1O première suggestions
                msg.append("D\u00e9sol\u00e9, je n'ai pas trouv\u00e9 cette carte.");
                if(!sugg.isEmpty()){
                    msg.append("\nTu cherchais peut-\u00eatre celles-ci :");
                    for (int i = 0; i< 10 && i < sugg.size(); i++){
                        msg.append("\n").append(sugg.toArray()[i]);
                    }
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

    public String random() {
        return "";
    }
}
