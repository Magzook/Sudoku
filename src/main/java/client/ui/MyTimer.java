package client.ui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import java.util.Timer;
import java.util.TimerTask;

public class MyTimer {
    private static Label labelTimer;
    private static Timer timer;
    private static int minutes = 0, seconds = 0;
    public static void setLabelTimer(Label labelTimer) {
        MyTimer.labelTimer = labelTimer;
    }
    public static String getTime() {
        return labelTimer.getText();
    }
    public static void setMinutes(int minutes) {MyTimer.minutes = minutes;}
    public static void setSeconds(int seconds) {MyTimer.seconds = seconds;}

    public static void start() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                seconds++;
                if (seconds == 60) {
                    minutes++;
                    seconds = 0;
                }
                String strMinutes, strSeconds;
                if (minutes < 10) strMinutes = "0" + minutes;
                else strMinutes = Integer.toString(minutes);
                if (seconds < 10) strSeconds = "0" + seconds;
                else strSeconds = Integer.toString(seconds);
                Platform.runLater(() -> labelTimer.setText(strMinutes + ":" + strSeconds));
            }
        }, 1000, 1000);
    }
    public static void pause() {
        timer.cancel();
    }
    public static void reset() {
        timer.cancel();
        minutes = 0;
        seconds = 0;
    }
}
