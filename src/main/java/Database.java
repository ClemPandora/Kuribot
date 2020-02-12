import net.dv8tion.jda.api.JDA;
import org.json.JSONArray;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private Connection conn;

    public Database() throws Exception {
        conn = getConnection();
    }

    private Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI("postgresql://cboqzqbofterrl:ba8c8babc103ef27f0d26229eb31d1ce177b341b8f4b4fbf87247eb6a6ccc6b6@ec2-54-228-243-238.eu-west-1.compute.amazonaws.com:5432/dd57uhvvq9ssdl");

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
    }

    public String saveDeck(Deck deck, String id) throws Exception {
        PreparedStatement check = conn.prepareStatement("SELECT * FROM decks WHERE nom = ?");
        check.setString(1,deck.getNom());
        ResultSet rs = check.executeQuery();
        int i = 0;
        while(rs.next()) {
            i++;
        }
        rs.close();
        check.close();
        if(i == 0) {
            PreparedStatement st = conn.prepareStatement("INSERT INTO decks VALUES (?,?,?,?,?)");
            st.setString(1, deck.getNom());
            st.setString(2, id);
            st.setString(3, deck.getMain().toString());
            st.setString(4, deck.getExtra().toString());
            st.setString(5, deck.getSide().toString());
            st.executeUpdate();
            st.close();
            return "Le deck "+deck.getNom()+" a \u00e9t\u00e9 ajout\u00e9 \u00e0 la base de donn\u00e9e !";
        } else {
            return "Ce nom de deck est d\u00e9j\u00e0 utilis\u00e9";
        }
    }

    public String showDecks(JDA jda) throws Exception{
        PreparedStatement check = conn.prepareStatement("SELECT * FROM decks");
        ResultSet rs = check.executeQuery();
        int maxLenNom = 3;
        int maxLenUser = 8;
        HashMap<String,String> table = new HashMap<>();
        while(rs.next()) {
            String nom = rs.getString(1);
            String user = rs.getString(2);
            maxLenNom = maxLenNom < nom.length() ? nom.length() : maxLenNom;
            maxLenUser = maxLenUser < user.length() ? user.length() : maxLenUser;
            table.put(nom,jda.getUserById(user).getName());
        }
        rs.close();
        check.close();
        maxLenNom+=3;
        maxLenUser+=3;
        StringBuilder res = new StringBuilder(
                "┌"+String.format(String.format("%-"+maxLenNom+"s","").replace(" ","─"))+
                "┬"+String.format(String.format("%-"+maxLenUser+"s","").replace(" ","─"))+"┐");
        res.append("\n│"+ String.format("%-"+maxLenNom+"s","Nom")+"│"+String.format("%-"+maxLenUser+"s","Cr\u00e9ateur")+"│");
        res.append(
                "\n├"+String.format(String.format("%-"+maxLenNom+"s","").replace(" ","─"))+
                "┼"+String.format(String.format("%-"+maxLenUser+"s","").replace(" ","─"))+"┤");
        for (Map.Entry<String, String> entry : table.entrySet()) {
            res.append("\n│"+String.format("%-"+maxLenNom+"s",entry.getKey())+ "│" + String.format("%-"+maxLenUser+"s",entry.getValue())+"│");
        }
        res.append(
                "\n└"+String.format(String.format("%-"+maxLenNom+"s","").replace(" ","─"))+
                "┴"+String.format(String.format("%-"+maxLenUser+"s","").replace(" ","─"))+"┘");
        return res.toString();
    }

    public Deck getDeck(String nom) throws Exception {
        PreparedStatement check = conn.prepareStatement("SELECT * FROM decks WHERE nom = ?");
        check.setString(1,nom);
        ResultSet rs = check.executeQuery();
        Deck deck = null;
        while(rs.next()) {
            deck = new Deck(rs.getString(1),
                   new JSONArray(rs.getString(3)),
                   new JSONArray(rs.getString(4)),
                   new JSONArray(rs.getString(5)));
        }
        rs.close();
        check.close();
        return deck;
    }
}
