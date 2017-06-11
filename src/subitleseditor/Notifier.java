/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subitleseditor;

/**
 *
 * @author isacv
 */
public interface Notifier {
    /**
     * Quiet notifications, that generally don't have much of an impact in the program.
     * Should be used more for something like status update
     * @param message Notification message
     */
    public void notifyAction(String message);
    
    /**
     * Notifies severely. Generally representing something that doesn't allow the program to procced to do what it 
     * is supposed to do, and normally shown in a an alert or something of the sort
     * @param message Severe notification message
     */
    public void severeNotifyAction(String message);
}
