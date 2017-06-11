/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subitleseditor;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 *
 * @author isacv
 */
public class Main {
    public static void main(String[] args){
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame uiwindow = new MainUI();
           
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                double screenwidth = screenSize.getWidth();
                double screenheight = screenSize.getHeight();

                uiwindow.setLocation((int)((screenwidth-uiwindow.getWidth())/2), (int)((screenheight-uiwindow.getHeight())/2));
                uiwindow.setTitle("Subtitles Editor");
                uiwindow.setVisible(true);
                //new MainUI().setVisible(true);
            }
        });
    }
}
