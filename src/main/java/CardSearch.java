import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class CardSearch {
    private JSONArray cardList;

    public CardSearch() {
        try {
            FileReader reader = new FileReader("cardinfo.json");
            JSONTokener tokener = new JSONTokener(reader);
            cardList = new JSONArray(tokener);
            cardList = (JSONArray) cardList.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String search(String recherche) {
        StringBuilder msg = new StringBuilder();
        if(!recherche.equals("")) {
            ArrayList<String> list = new ArrayList<>();
            boolean trouve = false;
            for (Object json : cardList) {
                JSONObject card = (JSONObject) json;
                if (card.getString("name").equalsIgnoreCase(recherche)) {
                    trouve = true;

                    for (Object o : card.getJSONArray("card_images")) {
                        JSONObject img = (JSONObject) o;
                        if (!msg.toString().equals("")) {
                            msg.append("\n");
                        }
                        msg.append(img.getString("image_url"));
                    }
                } else if (!trouve && card.getString("name").toLowerCase().contains(recherche.toLowerCase())) {
                    list.add(card.getString("name"));
                }
            }
            if (!trouve) {
                msg.append("D\u00e9sol\u00e9, je n'ai pas trouv\u00e9 cette carte.");
                if (!list.isEmpty()) {
                    msg.append("\nTu cherchais peut-\u00eatre celles-ci :");
                    for (String name : list) {
                        msg.append("\n- ");
                        msg.append(name);
                    }
                }
            }
        } else {
            msg.append("Je ne peux pas chercher une carte sans nom.");
        }
        return msg.toString();
    }

    public String searchArch(String arch) {
        String res = "";
        if(!arch.equals("")) {
            for (Object json : cardList) {
                JSONObject card = (JSONObject) json;
                if (card.has("archetype") && card.getString("archetype").equalsIgnoreCase(arch)) {
                    res = card.getString("archetype");
                    break;
                }
            }
        }
        return res;
    }

    public JSONObject searchById(String id) {
        for(Object json : cardList) {
            JSONObject card = (JSONObject) json;
            if(card.getString("id").equals(id)) {
                return card;
            }
        }
        return null;
    }

    public String random() {
        Random r = new Random();
        int num = r.nextInt(cardList.length());
        StringBuilder msg = new StringBuilder();
        JSONArray card = ((JSONObject) cardList.get(num)).getJSONArray("card_images");
        for (Object o : card) {
            JSONObject img = (JSONObject) o;
            msg.append(img.getString("image_url"));
            msg.append("\n");
        }
        return msg.toString();
    }
}
