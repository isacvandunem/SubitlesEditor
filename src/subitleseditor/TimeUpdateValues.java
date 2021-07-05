package subitleseditor;

/**
 *
 * @author isacv
 */
public class TimeUpdateValues {
    private int minutes;
    private int seconds;
    private int milliseconds;

    public TimeUpdateValues(int minutes, int seconds, int milliseconds) {
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
    }
    
    public boolean allZeros(){
        return (seconds == 0 && milliseconds == 0 && minutes == 0);
    }
    
    public int getMinutes(){
        return minutes;
    }
    
    public int getSeconds() {
        return seconds;
    }

    public int getMilliseconds() {
        return milliseconds;
    }
    
    public void setNegativeTimes(){
        if (minutes > 0){
            minutes = -minutes;
        }
        
        if (seconds > 0){
            seconds = -seconds;
        }
        
        if (milliseconds > 0){
            milliseconds = -milliseconds;
        }
    }
    
    public void setPositiveTimes(){
        if (minutes < 0){
            minutes = -minutes;
        }
        
        if (seconds < 0){
            seconds = -seconds;
        }
        
        if (milliseconds < 0){
            milliseconds = -milliseconds;
        }
    }

}
