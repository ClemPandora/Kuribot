import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.Console;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Main extends ListenerAdapter {
    private final String CODE = "```";
    private static CardSearch cardSearch;
    private static String cmdChar;
    private static HashMap<String, YugiTimer> timers;
    public static JSONObject TEXT;
    public static EmbedBuilder helpMessage;
    public final static String[] numbers = {"0️⃣","1️⃣","2️⃣"};

    public static void main(String[] args) throws Exception{
        timers = new HashMap<>();
        JSONParser parser = new JSONParser();
        TEXT = (JSONObject) parser.parse(new FileReader("textFR.json"));
        CardSearchAPI cardSearchAPI = new CardSearchAPI();
        //cardSearch = new CardSearch();
        // On attribut le caractère de commande
        cmdChar = "+";

        helpMessage = new EmbedBuilder();
        helpMessage.setColor(new Color(70,190,100));
        helpMessage.setAuthor("Commandes du Kuribot :");
        helpMessage.addField("+aide","Si tu lis ce message, tu dois savoir à quoi ça sert", false);
        helpMessage.addField("+c <nom de carte (anglais ou français)>", "Affiche l'image d'une carte",false);
        helpMessage.addField("+arch <nom d'archétype (anglais)>", "Vous ajoute un rôle correspondant à l'archétype choisi", false);
        helpMessage.addField("+time <temps en minutes> <Rôle ou personne à mentionner>","Lance un timer qui mentionnera les rôles ou personnes indiqués à la fin du temps. Il est possible de mentionner autant de rôles/personnes que vous le souhaiter, il suffit de tous les mettre dans le message de la commande. Si le temps n'est pas précisé, il sera de 40 minutes par défaut. Il est possible de mettre en pause ou de stopper le timer grâce aux émotes sur celui-ci (seulement pour la personne ayant lancé le timer ou un administrateur du serveur).", false);

        // On lance l'api JDA
        String token = System.getenv("DISCORD_API_TOKEN");
        JDABuilder.createDefault(token)
                .setActivity(Activity.playing(cmdChar+TEXT.get("statusMessage")))
                .addEventListeners(new Main())
                .build();
    }

    // Méthode appelée à chaque message reçu par le bot
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // On vérifie que l'auteur du message n'est pas un bot, et que celui-ci n'est pas vide
        if(!event.getAuthor().isBot() && !event.getMessage().getContentDisplay().equals("")) {
            // On récupère le contenu du message et on regarde si le premier caractère est le caractère de commande
            String msg = event.getMessage().getContentDisplay();
            if(msg.substring(0,1).equals(cmdChar)) {
                String cmd;
                String arg = "";
                // Si un espace se trouve dans le message, alors on sépare la commande et ses arguments, sinon on récupère juste la commande
                if(msg.contains(" ")) {
                    cmd = msg.substring(1, msg.indexOf(" "));
                    arg = msg.substring(msg.indexOf(" ")+1);
                } else {
                    cmd = msg.substring(1);
                }
                // On applique la commande
                ApplyCommand(cmd, event, arg);
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        //Si la réaction ne vient pas d'un bot
        if(!event.getUser().isBot()){
            //Si le message est dans la liste des timer et que l'emote est un emoji
            if(timers.containsKey(event.getMessageId()) && event.getReactionEmote().isEmoji()){
                //Si la réaction viens de celui qui a lancé le timer ou d'un admin
                if(event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getMember().equals(timers.get(event.getMessageId()).owner)){
                    String emoji = event.getReactionEmote().getEmoji();
                    if(emoji.equals(TEXT.get("pauseEmoji"))) {
                        timers.get(event.getMessageId()).pauseTime();
                    } else if(emoji.equals(TEXT.get("stopEmoji"))) {
                        timers.get(event.getMessageId()).stopTime();
                    } else if(emoji.equals(TEXT.get("playEmoji"))) {
                        timers.get(event.getMessageId()).playTime();
                    }
                }
            }
        }
    }

    // Méthode appliquant les commandes
    public void ApplyCommand(String cmd, MessageReceivedEvent event, String arg){
        switch (cmd) {
            case "aide":
                event.getChannel().sendMessage(helpMessage.build()).queue();
                break;
            case "c":
                //event.getChannel().sendMessage(cardSearch.search(arg)).queue();
                event.getChannel().sendMessage((String) TEXT.get("maintenance")).queue();
                break;
            case "d":
                /*List<Message.Attachment> list = event.getMessage().getAttachments();
                if(!list.isEmpty()) {
                    Message.Attachment file = list.get(0);
                    if (file.getFileName().substring(file.getFileName().lastIndexOf(".")).equals(".ydk")) {
                        try {
                            URL url = new URL(file.getUrl());
                            Deck deck = new Deck(file.getFileName().substring(0, file.getFileName().lastIndexOf(".")), url, cardSearch);
//                                        event.getChannel().sendMessage(db.saveDeck(deck, event.getAuthor().getId())).queue();
                            //---En attendant la db
                            event.getChannel().sendMessage("Deck "+arg+" charg\u00e9 ! G\u00e9n\u00e9ration de l'image...").queue();
                            event.getChannel().sendFile(deck.toImage()).queue();
                            event.getChannel().sendMessage(CODE + deck.toString() + CODE).queue();
                            //---
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    event.getChannel().sendMessage("Tu dois ajouter le fichier ydk \u00e0 la commande !").queue();
                }*/
                //TODO Image deck
                break;
            case "a":
                //event.getChannel().sendMessage(cardSearch.random()).queue();
                //TODO Recherche aléatoire
                break;
            case "arch":
                List<Role> rolelist = event.getGuild().getRolesByName("+"+arg,true);
                Member member = event.getMember();
                if(rolelist.isEmpty()) {
                    String arch = cardSearch.searchArch(arg);
                    if(!arch.equals("")){
                        event.getGuild().createRole().setName("+"+arch).setColor(0xff9933).setMentionable(true).complete();
                        rolelist = event.getGuild().getRolesByName("+"+arch,true);
                        assert member != null;
                        event.getGuild().addRoleToMember(member, rolelist.get(0)).queue();
                        event.getChannel().sendMessage((String) TEXT.get("archAdded")).queue();
                    } else {
                        event.getChannel().sendMessage((String) TEXT.get("archNotFound")).queue();
                    }
                } else {
                    assert member != null;
                    if(member.getRoles().contains(rolelist.get(0))){
                        event.getGuild().removeRoleFromMember(member, rolelist.get(0)).queue();
                        event.getChannel().sendMessage((String) TEXT.get("archRemoved")).queue();
                    } else {
                        event.getGuild().addRoleToMember(member, rolelist.get(0)).queue();
                        event.getChannel().sendMessage((String) TEXT.get("archAdded")).queue();
                    }
                }
                break;
            case "time":
                //On sépare les arguments de la commande pour récupérer le temps
                String[] args = arg.split(" ");
                int time;
                //Si le temps est valide, on le fixe à cette valeur, sinon il est fixé à 40min
                try{
                    time = Integer.parseInt(args[0]);
                } catch (NumberFormatException e){
                    time = 40;
                }
                //On récupère toutes les mentions contenues dans la commande
                List<Role> rl = event.getMessage().getMentionedRoles();
                List<User> ur = event.getMessage().getMentionedUsers();
                //On envoi le message du timer
                event.getChannel().sendMessage((String) TEXT.get("startTimer")).queue();
                Date date = new Date(time * 60000L);
                Message message = event.getChannel().sendMessage("> "+new SimpleDateFormat("hh:mm:ss").format(date)).complete();
                message.addReaction((String) TEXT.get("stopEmoji")).queue();
                message.addReaction((String) TEXT.get("pauseEmoji")).queue();
                //On créé un timer
                YugiTimer timer = new YugiTimer(message, rl, ur, event.getMember(),time);
                timer.start();
                //On ajoute le message dans la liste des timers
                timers.put(message.getId(),timer);
                break;
            default:
                break;
        }
    }
}
