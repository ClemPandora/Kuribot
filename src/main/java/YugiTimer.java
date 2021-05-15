import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;

public class YugiTimer extends Thread {
    public Member owner;
    private Message msg;
    private List<Role> roles;
    private List<User> users;
    private int time;
    private boolean timing;
    private boolean paused;
    private long remaining;
    private LocalTime endTime;

    public YugiTimer(Message ms, List<Role> rl, List<User> ur, Member ow, int tm){
        msg = ms;
        roles = rl;
        users = ur;
        owner = ow;
        time = tm;
        timing = true;
        paused = false;
    }

    public void stopTime(){
        timing = false;
        paused = false;
        //Petite pause pour laisser la boucle se finir
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //On efface le message du timer
        msg.delete().queue();
        //On mentionne tous les rôles
        StringBuilder str = new StringBuilder();
        for (Role rl: roles) {
            str.append(" ");
            str.append(rl.getAsMention());
        }
        for (User ur: users) {
            str.append(" ");
            str.append(ur.getAsMention());
        }
        msg.getChannel().sendMessage(Main.TEXT.get("stopTimer")+str.toString()).queue();
    }

    public void pauseTime() {
        paused = true;
        //Petite pause pour laisser la boucle se finir
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //On récupère le temps restant
        remaining = NANOS.between(LocalTime.now(), endTime);
        //On met à jour l'affichage
        long sec = LocalTime.now().until(endTime, SECONDS);
        Date date = new Date(sec*1000);
        msg.editMessage("> "+new SimpleDateFormat("mm:ss").format(date)+" (pause)").queue();
        msg.clearReactions((String) Main.TEXT.get("pauseEmoji")).queue();
        msg.addReaction((String) Main.TEXT.get("playEmoji")).queue();
        //On mentionne tous les rôles
        StringBuilder str = new StringBuilder();
        for (Role rl: roles) {
            str.append(" ");
            str.append(rl.getAsMention());
        }
        for (User ur: users) {
            str.append(" ");
            str.append(ur.getAsMention());
        }
        msg.getChannel().sendMessage(Main.TEXT.get("pauseTimer")+str.toString()).queue();
    }

    public void playTime(){
        //On actualise le temps de fin du timer
        endTime = LocalTime.now().plusNanos(remaining);
        //On met à jour l'affichage
        long sec = LocalTime.now().until(endTime, SECONDS);
        Date date = new Date(sec*1000);
        msg.editMessage("> "+new SimpleDateFormat("mm:ss").format(date)).queue();
        msg.clearReactions((String) Main.TEXT.get("playEmoji")).queue();
        msg.addReaction((String) Main.TEXT.get("pauseEmoji")).queue();
        //On mentionne tous les rôles
        StringBuilder str = new StringBuilder();
        for (Role rl: roles) {
            str.append(" ");
            str.append(rl.getAsMention());
        }
        for (User ur: users) {
            str.append(" ");
            str.append(ur.getAsMention());
        }
        msg.getChannel().sendMessage(Main.TEXT.get("endPauseTimer")+str.toString()).queue();
        paused = false;
    }

    @Override
    public void run() {
        endTime = LocalTime.now().plusMinutes(time);
        long lastSecond = 0;
        while (endTime.compareTo(LocalTime.now()) > 0 && timing){
            //Boucle infini pour garder la boucle en cours pendant la pause
            while (paused){
            }
            long sec = LocalTime.now().until(endTime, SECONDS);
            //Sur un multiple de 5 seconde
            if(sec != lastSecond && sec%5 == 0){
                //On met à a jour le timer
                lastSecond = sec;
                Date date = new Date(sec*1000);
                msg.editMessage("> "+new SimpleDateFormat("mm:ss").format(date)).queue();
            }
        }
    }
}
