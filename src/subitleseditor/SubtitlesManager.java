package subitleseditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author isacv
 */
public class SubtitlesManager {
    private final List<Subtitle> subtitles;
    private final List<Notifier> notifiers;    
    private String filePath;
    private String encoding;
    
    public SubtitlesManager(){
        subtitles = new ArrayList<>();
        notifiers = new ArrayList<>();
    }
    
    /**
     * Adds a new notifier to this class's notifiers list. All the notifiers get notified for each 
     * generated action. There are 2 types of notifications: Soft and Hard, that allow the
     * notified ones to give different implementations for both if they deem necessary.
     * @param n The notifier to be added to the list
     */
    public void addNotifier(Notifier n){
        if (!notifiers.contains(n)){
            notifiers.add(n);
        }
    }
    
    /**
     * Sets the file path
     * @param newFilePath The new file path 
     */
    public void setFilePath(String newFilePath){
        filePath = newFilePath;
    }
    
    /**
     * Gets the file path
     * @return The current file path
     */
    public String getFilePath(){
        return filePath;
    }
    
    /**
     * Sets the current subtitle manager encoding. 
     * This encoding will only be used on the next file action (load/save).
     * More often than not it is advisable to load from file again so that the load 
     * is already done with the correct encoding.
     * @param newEncoding New Encoding to be used as a String
     */
    public void setEncoding(String newEncoding){
        encoding = newEncoding;
    }
    
    /**
     * Writes the current subtitles to file if there is a file path selected
     */
    public void writeToFile(){
        if (filePath != null){
            try(PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), encoding))) {
                for (Subtitle s : subtitles){
                    pw.print(s);
                }
                
                pw.flush();
                pw.close();
                
                notifyAction("File saved sucessfuly");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SubtitlesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else {
            notifyAction("No file selected, unable to save");
        }
    }
    
    /**
     * A soft notification to all registered notified parties
     * @param message the notification message
     */
    private void notifyAction(String message){
        for (Notifier n: notifiers){
            n.notifyAction(message);
        }
    }
    
    /**
     * A hard notification to all registered notified parties
     * @param message the notification message
     */
    private void severeNotifyAction(String message){
        for (Notifier n: notifiers){
            n.severeNotifyAction(message);
        }
    }
    
    /**
     * Updates the current reading type according to a subtitle file structure
     * @param readingType The reading type
     * @return 
     */
    private ReadingType updateCurrentRead(ReadingType readingType){
        switch(readingType){
            case number: return ReadingType.time;
            case time: return ReadingType.text;
            case text: return ReadingType.emptyline;
            case emptyline: return ReadingType.number;
        }
        
        return null;
    }
    
    /**
     * Reads a subtitle from the currently selected file path
     */
    public void readFromFile(){
        ReadingType currRead = ReadingType.number;
        BufferedReader br = null;
        
        try {
            FileInputStream fileStream = new FileInputStream(new File(filePath));
            br = new BufferedReader(new InputStreamReader(fileStream,encoding));
            notifyAction("Loading file...");
            String textLine = br.readLine();
            subtitles.clear();
            Subtitle currentSubtitle = null;

            while (textLine != null){
                switch(currRead){
                    case number:
                        if (currentSubtitle!= null){
                            subtitles.add(currentSubtitle);
                        }

                        currentSubtitle = new Subtitle();
                        currentSubtitle.number = Integer.parseInt(textLine);
                        currRead = updateCurrentRead(currRead);
                        break;
                    case time:
                        String[] blocks = textLine.split(" --> ");

                        currentSubtitle.parseStartTime(blocks[0]);
                        currentSubtitle.parseEndTime(blocks[1]);

                        currRead = updateCurrentRead(currRead);                            
                        break;
                    case text:
                        if (textLine.equals("")){
                            currRead = ReadingType.number;
                        }
                        else {
                            currentSubtitle.addText(textLine);
                        }
                        break;
                }

                textLine = br.readLine(); 
            }
            subtitles.add(currentSubtitle);

            notifyAction("File loaded sucessfuly");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            severeNotifyAction("The selected file does not exist");
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            severeNotifyAction("Unexpected error while loading from the file");
        } finally {
            try {
                if (br != null){
                    br.close();    
                }                
            } catch (IOException ex) {
                Logger.getLogger(SubtitlesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Updates all the subtitles according to the given parameters.
     * Positive values move all the subtitles forward(later) whereas negative values move them backwards(earlier)
     * @param minutes minutes to advance/delay
     * @param seconds seconds to advance/delay
     * @param milliseconds milliseconds to advance/delay
     */
    public void updateAllSubs(int minutes, int seconds, int milliseconds){
        updateRangedSubs(minutes, seconds, milliseconds, 0, subtitles.size()-1);
    }
    
    /**
     * Updates the subtitles by moving the forward(later) or backwards (earlier) for a given range of subtitles.
     * Positive values move all the subtitles forward(later) whereas negative values move them backwards(earlier).
     * @param minutes minutes to advance/delay
     * @param seconds seconds to advance/delay
     * @param milliseconds milliseconds to advance/delay
     * @param min subtitle where the update starts (inclusive)
     * @param max subtitle where the update ends (inclusive)
     */
    public void updateRangedSubs(int minutes, int seconds, int milliseconds, int min, int max){
        for (Subtitle sub: subtitles){
            if (sub.number >= min && sub.number <= max){
                sub.updateTimes(minutes, seconds, milliseconds);    
            }            
        }
        
        String notifyStr = "Subtitles ";
        
        if (minutes > 0 || seconds > 0 || milliseconds > 0){
            notifyStr += " delayed for ";
        }
        else {
            notifyStr += " advanced for ";
        }
        
        if (minutes != 0){
            notifyStr += " " + Misc.plurify(Math.abs(minutes), "minute") + " ";
        }
        
        if (seconds != 0){
            notifyStr += " " + Misc.plurify(Math.abs(seconds), "second") + " ";
        }
        
        if (milliseconds != 0 ){
            notifyStr += " " + Misc.plurify(Math.abs(milliseconds), "millisecond") + " ";
        }
        
        notifyStr += " from subtitle " + min + " to " + max;
        notifyAction(notifyStr);
    }
    
    /**
     * Updates the subs based on the target subtitle. It rearranges the target subtitle to a specific time
     * and readjusts all previous times proportionally.
     * Positive values move the subtitle forward(later) whereas negative values move it backwards(earlier).
     * @param minutes minutes to advance/delay
     * @param seconds seconds to advance/delay
     * @param milliseconds milliseconds to advance/delay
     * @param targetSub target subtitle to adjust from towards the first subtitle
     */
    public void updateSubsProprortionally(int minutes, int seconds, int milliseconds, int targetSub){
        int totalChange = (minutes * 60000) + (seconds * 1000) + milliseconds;
        
        Integer subIndex = getSubtitleIndex(targetSub);
        
        if (subIndex == null){ //unexisting sub, unable to update
            severeNotifyAction("The subtitle selected for the update doesn't exist");
            return;
        }
        
        //sub index is the amount of elements - 1, which is what is neccessary for distributed update
        long perSubChangeBackward = totalChange / subIndex;
        
        int change = totalChange;
        
        //makes the proportional adjustment for all the subtitles up to the selected subtitle
        for (int i = subIndex - 1; i >= 1; --i){ //update all subs backwards with the proportional change
            subtitles.get(i).updateTimes(change);
            change -= perSubChangeBackward;
        }
        
        //updates all the remaining forward subtitles so that they keep the same distance 
        //among themselves maintaining the same subtitle sync
        for (int i = subIndex ; i < subtitles.size(); ++i){
            subtitles.get(i).updateTimes(totalChange);
        }
        
        String notifyStr = "Subtitle " + targetSub;
        
        if (minutes > 0 || seconds > 0 || milliseconds > 0){
            notifyStr += " delayed for ";
        }
        else {
            notifyStr += " advanced for ";
        }
        
        if (minutes != 0){
            notifyStr += " " + Misc.plurify(Math.abs(minutes), "minute") + " ";
        }
        
        if (seconds != 0){
            notifyStr += " " + Misc.plurify(Math.abs(seconds), "second") + " ";
        }
        
        if (milliseconds != 0 ){
            notifyStr += " " + Misc.plurify(Math.abs(milliseconds), "millisecond") + " ";
        }
        
        notifyAction(notifyStr + " and remaining subtitles adjusted proportionally");
    }
    
    /**
     * Gets the subtitle referring the passed number
     * @param subNumber The subtitle number
     * @return The subtitle for the given number
     */
    private Subtitle getSubtitle(int subNumber){
        for (Subtitle sub: subtitles){
            if (sub.number == subNumber){
                return sub;
            }
        }
        
        return null;
    }
    
    /**
     * Gets the subtitle index for a given subtitle number
     * @param subNumber The subtitle number to get the index
     * @return The index of the given subtitle
     */
    private Integer getSubtitleIndex(int subNumber){
        for (int i = 0; i < subtitles.size(); ++i){
            Subtitle sub = subtitles.get(i);
            
            if (sub.number == subNumber){
                return i;
            }
        }
        
        return null;
    }
    
    /**
     * Gets the text that represents all the subs and should match a perfect subtitle file
     * @return The String representation of all subs in order
     */
    public String subsToText(){
        StringBuilder sb = new StringBuilder(5000);
        
        for (Subtitle sub : subtitles){
            sb.append(sub.toString());
        }
        
        return sb.toString();
    }
    
    /**
     * Gets the first subtitle number
     * @return The first subtitle number
     */
    public int getFirstSubNumber(){
        if (subtitles != null){
            return subtitles.get(0).number;
        }
        
        return 0;
    }
    
    /**
     * Gets the last subtitle number
     * @return The last subtitle number
     */
    public int getLastSubNumber(){
        if (subtitles != null || subtitles.size() >= 1){
            return subtitles.get(subtitles.size() - 1).number;
        }
        
        return 0;
    }
}
