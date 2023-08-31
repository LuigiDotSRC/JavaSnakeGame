import javax.swing.JFrame;

public class gameWindow extends JFrame{

    gameWindow(){
        this.add(new gamePanel());
        this.setTitle("Snake Game");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

}