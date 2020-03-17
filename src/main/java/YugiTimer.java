import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import static java.time.temporal.ChronoUnit.SECONDS;

public class YugiTimer extends Thread {
    private Message msg;
    private Role role;
    private boolean timing;

    public YugiTimer(Message ms, Role rl){
        msg = ms;
        role = rl;
        timing = true;
    }

    public void stopTime(){
        timing = false;
    }

    @Override
    public void run() {
        LocalTime EndTime = LocalTime.now().plusMinutes(40);
        long lastSecond = 0;
        while (EndTime.compareTo(LocalTime.now()) > 0 && timing){
            long sec = LocalTime.now().until(EndTime, SECONDS);
            if(sec != lastSecond && sec%5 == 0){
                lastSecond = sec;
                Date date = new Date(sec*1000);
                msg.editMessage("> "+new SimpleDateFormat("mm:ss").format(date)).queue();
            }
        }
        msg.getChannel().sendMessage(role.getAsMention()+" Fin du temps !").queue();
    }
}
