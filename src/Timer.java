import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * Klasa odmieżacza czasu (nie używane)
 */
public class Timer extends Thread {

    private GameLogic gameLogicRef;

    private long limit;
    private long delta;
    private long timeLeft;

    long start;
    long end;

    public Timer(GameLogic gR, int limitSec) {
        gameLogicRef = gR;
        limit = limitSec*1000;
        timeLeft = limit;

        start = 0;
        end = 0;
    }

    @Override
    public void run() {
        while(true) {
            start = Calendar.getInstance().getTimeInMillis();

            //System.out.println("start: " + start);
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            end = Calendar.getInstance().getTimeInMillis();
           // System.out.println("end: " + end);
            delta = end - start;
            timeLeft -= delta;

            //System.out.println("Delta: "+ delta);
            System.out.println("Time Left: "+ timeLeft);

        }
    }

    public void resetTimer() {
        timeLeft = limit;
        delta = 0;
        start = 0;
        end = 0;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public String getTimeLeftString() {
        long tl = (long) Math.ceil(timeLeft/1000);
        if(tl < 10) {
            return "00:0"+tl;
        }
        else {
            return "00:"+tl;
        }
    }


}
