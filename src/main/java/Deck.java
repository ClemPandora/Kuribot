import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Deck {
    private final int WIDTH = 168;
    private final int HEIGHT = 246;
    private String nom;
    private JSONArray main;
    private JSONArray extra;
    private JSONArray side;
    private ArrayList<String>[] topDeck;

    public Deck(String nom) {
        this.nom = nom;
        main = new JSONArray();
        extra = new JSONArray();
        side = new JSONArray();
    }

    public Deck(String nom, URL url, CardSearch cards) throws IOException {
        this(nom);
        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
        Scanner scan = new Scanner(httpcon.getInputStream());
        scan.nextLine();
        scan.nextLine();
        String line = scan.nextLine();
        while(!line.equals("#extra")) {
            addMain(cards.searchById(line));
            line = scan.nextLine();
        }
        line = scan.nextLine();
        while(!line.equals("!side")){
            addExtra(cards.searchById(line));
            line = scan.nextLine();
        }
        while(scan.hasNextLine()){
            addSide(cards.searchById(scan.nextLine()));
        }
    }

    public Deck(String nom, JSONArray main, JSONArray extra, JSONArray side) {
        this.nom = nom;
        this.main = main;
        this.extra = extra;
        this.side = side;
    }

    public void addMain(JSONObject card){
        if(main.length() < 60 && card != null) {
            main.put(card);
        }
    }

    public void addExtra(JSONObject card){
        if(extra.length() < 15 && card != null) {
            extra.put(card);
        }
    }

    public void addSide(JSONObject card){
        if(side.length() < 60 && card != null) {
            side.put(card);
        }
    }

    public String getNom(){
        return nom;
    }

    public JSONArray getMain(){
        return main;
    }

    public JSONArray getExtra() {
        return extra;
    }

    public JSONArray getSide() {
        return side;
    }

    public File toImage() {
        int width = 15 * WIDTH;
        int height = (main.length()/15+5) * HEIGHT;
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.BLACK);
        g2.fillRect(0, 0, width, height);
        g2.setColor(oldColor);
        int i = 0;
        for (Object json : main) {
            JSONObject card = (JSONObject) json;
            card = (JSONObject) card.getJSONArray("card_images").get(0);
            try {
                BufferedImage img = ImageIO.read(new URL(card.getString("image_url_small")));
                g2.drawImage(img, null, (i%15) * WIDTH, (i/15) * HEIGHT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
        i=0;
        for (Object json : extra) {
            JSONObject card = (JSONObject) json;
            card = (JSONObject) card.getJSONArray("card_images").get(0);
            try {
                BufferedImage img = ImageIO.read(new URL(card.getString("image_url_small")));
                g2.drawImage(img, null, (i%15) * WIDTH, (main.length()/15+2) * HEIGHT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
        i=0;
        for (Object json : side) {
            JSONObject card = (JSONObject) json;
            card = (JSONObject) card.getJSONArray("card_images").get(0);
            try {
                BufferedImage img = ImageIO.read(new URL(card.getString("image_url_small")));
                g2.drawImage(img, null, (i%15) * WIDTH, (main.length()/15+4) * HEIGHT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }

        g2.dispose();
        File image = new File("img.png");
        try {
            ImageIO.write(newImage, "png", image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(nom+"\nMain:");
        for(Object json : main) {
            JSONObject card = (JSONObject) json;
            str.append("\n- ");
            str.append(card.getString("name"));
        }
        str.append("\nExtra:");
        for(Object json : extra) {
            JSONObject card = (JSONObject) json;
            str.append("\n- ");
            str.append(card.getString("name"));
        }
        str.append("\nSide:");
        for(Object json : side) {
            JSONObject card = (JSONObject) json;
            str.append("\n- ");
            str.append(card.getString("name"));
        }
        return str.toString();
    }
}
