package smartrockets;

import javax.swing.JFrame;

public class GameFrame extends JFrame{
    
    public GameFrame(){
        this.setSize(800, 600);
        this.setTitle("Smart Rockets");
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
    
}
