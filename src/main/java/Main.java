

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class Main extends ListenerAdapter {
    private final String COMMANDS_LIST =
            "Salut ! Je suis L'ultime tout-puissant Kuribot aux yeux marrons et rouges brillants des t\u00e9n\u00e8bres absolues !" +
                    "\nVoici la liste des commandes que je peux exécuter :" +
                    "\n\n+aide : Si tu lis ce message, tu dois savoir \u00e0 quoi \u00e7a sert" +
                    "\n\n+c <nom de carte (anglais)> : Affiche l'image d'une carte" +
                    "\n\n+d <fichier .ydk> : Affiche le contenu d'un deck sous forme d'image et de liste (les fichiers " +
                    "ydk sont trouvable dans le dossier \"deck\" des jeux ygopro ou t\u00e9l\u00e9chargeable sur certains sites)" +
//                    "\n\n+d <fichier .ydk> : Enregistre un deck dans la base de donn\u00e9e" +
//                    "\n\n+dl : Affiche la liste des deck enregistr\u00e9s" +
//                    "\n\n+dl <nom_du_deck> : Affiche une image du deck correspondant" +
                    "\n\n+a : Affiche une carte al\u00e9atoire" +
                    "\n\n+arch <nom d'arch\u00e9type (anglais)> : Vous ajoute un r\u00f4le correspondant \u00e0 l'arch\u00e9type choisi";

    private final String CODE = "```";
    private static CardSearch cardSearch;
    private static String cmdChar;
    private YugiTimer timer;

    public static void main(String[] args) throws Exception{
        cardSearch = new CardSearch();
        cardSearch.search("kur");
        // On attribut le caractère de commande
        cmdChar = "+";

        // On lance l'api JDA
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        String token = "NTU3NTQ4MzcwODk4NDUyNDgy.D3J5Xg.pXd6ZMmtd6nb9oyP9HHeT_xpk_s";
        builder.setToken(token);
        builder.setActivity(Activity.playing(cmdChar+"aide pour voir les commandes"));
        builder.addEventListeners(new Main());
        builder.build();
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
                String arg;
                // Si un espace se trouve dans le message, alors on sépare la commande et ses arguments, sinon on récupère juste la commande
                if(msg.contains(" ")) {
                    cmd = msg.substring(1, msg.indexOf(" "));
                    arg = msg.substring(msg.indexOf(" ")+1);
                } else {
                    cmd = msg.substring(1);
                    arg = "";
                }
                // On applique la commande
                ApplyCommand(cmd, event, arg);
            }
        }
    }


    // Méthode appliquant les commandes
    public void ApplyCommand(String cmd, MessageReceivedEvent event, String arg){
        switch (cmd) {
            case "aide":
                event.getChannel().sendMessage(CODE + COMMANDS_LIST + CODE).queue();
                break;
            case "c":
                event.getChannel().sendMessage(cardSearch.search(arg)).queue();
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
                /*List<Role> rolelist = event.getGuild().getRolesByName("+"+arg,true);
                Member member = event.getGuild().getMember(event.getAuthor());
                if(rolelist.isEmpty()) {
                    String arch = cardSearch.searchArch(arg);
                    if(!arch.equals("")){
                        event.getGuild().createRole().setName("+"+arch).setColor(0xff9933).setMentionable(true).complete();
                        rolelist = event.getGuild().getRolesByName("+"+arch,true);
                        assert member != null;
                        event.getGuild().addRoleToMember(member, rolelist.get(0)).queue();
                        event.getChannel().sendMessage("Arch\u00e9type ajout\u00e9 !").queue();
                    } else {
                        event.getChannel().sendMessage("Arch\u00e9type introuvable !").queue();
                    }
                } else {
                    assert member != null;
                    if(member.getRoles().contains(rolelist.get(0))){
                        event.getGuild().removeRoleFromMember(member, rolelist.get(0)).queue();
                        event.getChannel().sendMessage("Arch\u00e9type retir\u00e9 !").queue();
                    } else {
                        event.getGuild().addRoleToMember(member, rolelist.get(0)).queue();
                        event.getChannel().sendMessage("Arch\u00e9type ajout\u00e9 !").queue();
                    }
                }*/
                //TODO Ajout role archétype
                break;
            case "time":
                if(timer != null){
                    timer.stopTime();
                }
                List<Role> rl = event.getMessage().getMentionedRoles();
                if(!rl.isEmpty()){
                    event.getChannel().sendMessage("Timer actif !").queue();
                    Message message = event.getChannel().sendMessage("> 40:00").complete();
                    message.editMessage("> 40:00").queue();
                    timer = new YugiTimer(message, rl.get(0));
                    timer.start();
                } else {
                    event.getChannel().sendMessage("Merci de pr\u00e9ciser le r\u00f4le \u00e0 mentionner.").queue();
                }
                break;
            case "stop":
                if (timer != null){
                    timer.stopTime();
                }
                break;
            default:
                break;
        }
    }
}
