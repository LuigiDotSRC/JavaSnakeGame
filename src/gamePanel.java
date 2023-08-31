import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Array;
import java.util.Random;
import java.util.ArrayList;

public class gamePanel extends JPanel implements ActionListener {

    final int WINDOW_LENGTH = 600;
    final int WINDOW_HEIGHT = 600;
    final int TILE_SCALE = 25; // each tile will be 25 by 25px
    final int DELAY = 75; //75 is good num

    // BODY2 BODY1 HEAD
    final static int[] SNAKE_INIT_POS_HEAD = {6,4};
    final static int[] SNAKE_INIT_POS_BODY1 = {5,4};
    final static int[] SNAKE_INIT_POS_BODY2 = {4,4};
    final static int SNAKE_INIT_DIRECTION = 4;

    final Color DEFAULT_BOARD_COLOR1 = new Color(162, 209, 73);
    final Color DEFAULT_BOARD_COLOR2 = new Color(153, 194, 79);
    final Color DEFAULT_BODRER_COLOR = new Color(50, 105, 0);
    final Color SNAKE_HEAD_COLOR = new Color(0, 132, 208);
    final Color SNAKE_BODY_COLOR1 = new Color(0, 81, 122);
    final Color SNAKE_BODY_COLOR2 = new Color(0, 53, 122);
    final Color APPLE_COLOR = new Color(255,0,0);

    Random RNG;
    Timer timer;

    static boolean isRunning = false;
    static ArrayList<int []> snakeBody;
    static int direction;
    static int highscore = 0;
    int[] apple = new int[2];

    gamePanel(){
        this.setBackground(new Color(0,0,0));
        this.setPreferredSize(new Dimension(WINDOW_LENGTH,WINDOW_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(new keyboardInput());
        gameStart();
        System.out.println("Panel Created!");
    }

    public void gameStart(){
        isRunning = true;

        makeApple();

        snakeBody = new ArrayList<>();
        snakeBody.add(SNAKE_INIT_POS_HEAD);
        snakeBody.add(SNAKE_INIT_POS_BODY1);
        snakeBody.add(SNAKE_INIT_POS_BODY2);

        direction = SNAKE_INIT_DIRECTION;

        timer = new Timer(DELAY,this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(isRunning){
            move(direction);
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public static class keyboardInput extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){

            switch(e.getKeyCode()){
                case((int) 'W') -> {
                    if(direction != 1 && direction != 2){
                        direction = 1;
                    }
                    break;
                }

                case((int) 'A') -> {
                    if(direction != 3 && direction != 4){
                        direction = 3;
                    }
                    break;
                }

                case((int) 'S') -> {
                    if(direction != 1 && direction != 2){
                        direction = 2;
                    }
                    break;
                }

                case((int) 'D') -> {
                    if(direction != 3 && direction != 4){
                        direction = 4;
                    }
                    break;
                }
            }

            if(!isRunning && e.getKeyCode() == (int) 'R'){
                //reset
                reset();
            }
        }
    }

    public static void reset(){
        isRunning = true;
        snakeBody = new ArrayList<>();
        snakeBody.add(SNAKE_INIT_POS_HEAD);
        snakeBody.add(SNAKE_INIT_POS_BODY1);
        snakeBody.add(SNAKE_INIT_POS_BODY2);

        direction = SNAKE_INIT_DIRECTION;
    }

    public void move(int direction){ // 1: up, 2: down, 3: left, 4: right
        int[] nextHead = new int[2];
        switch (direction) {
            case 1 -> {
                nextHead[0] = snakeBody.get(0)[0];
                nextHead[1] = snakeBody.get(0)[1] - 1;
            }
            case 2 -> {
                nextHead[0] = snakeBody.get(0)[0];
                nextHead[1] = snakeBody.get(0)[1] + 1;
            }
            case 3 -> {
                nextHead[0] = snakeBody.get(0)[0] - 1;
                nextHead[1] = snakeBody.get(0)[1];
            }
            case 4 -> {
                nextHead[0] = snakeBody.get(0)[0] + 1;
                nextHead[1] = snakeBody.get(0)[1];
            }
        }
        snakeBody = moveAlgorithm(snakeBody,nextHead);
    }

    private ArrayList<int[]> moveAlgorithm(ArrayList<int[]> positions, int[] nextHead) {
        for (int i = positions.size() - 1; i > 0; i--) {
            positions.set(i, positions.get(i - 1));
        }

        positions.set(0, nextHead);
        return positions;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void checkCollisions(){
        //check collision with walls
        //if collided, running = false, set new highscore

        int[] headPos = snakeBody.get(0);
        if(headPos[0] == 0 || headPos[0] == 23 || headPos[1] == 0 || headPos[1] == 23){
            isRunning = false;
            if(highscore < snakeBody.size()-3){
                highscore = snakeBody.size()-3;
            }
        }
        /*
        for(int i=1; i<snakeBody.size(); i++){
            if(snakeBody.get(i)[0] == headPos[0] && snakeBody.get(i)[1] == headPos[1]){
                isRunning = false;
            }
        }
        */


    }

    public void makeApple(){
        RNG = new Random();
        int appleX = RNG.nextInt((WINDOW_LENGTH / TILE_SCALE) - 2) + 1;
        int appleY = RNG.nextInt((WINDOW_HEIGHT / TILE_SCALE) - 2) + 1;

        //System.out.println(appleX + "," + appleY);
        apple[0] = appleX;
        apple[1] = appleY;
    }

    public void checkApple(){
        if(snakeBody.get(0)[0] == apple[0] && snakeBody.get(0)[1] == apple[1]){

            makeApple();

            int[] lastPos = snakeBody.get(snakeBody.size()-1);
            int[] secondlastPos = snakeBody.get(snakeBody.size()-2);

            int[] difference = {lastPos[0]-secondlastPos[0], lastPos[1]-secondlastPos[1]};
            int[] newTail = {lastPos[0] + difference[0], lastPos[1] + difference[1]};
            snakeBody.add(newTail);
        }
    }

    public void draw(Graphics g){

        if(isRunning){
            //draw checker pattern
            for(int j=0; j<WINDOW_HEIGHT/TILE_SCALE; j++){
                for(int i=0; i<WINDOW_LENGTH/TILE_SCALE; i++){
                    if(j % 2 == 0){
                        if(i%2 == 0){
                            g.setColor(DEFAULT_BOARD_COLOR1);
                        }else{
                            g.setColor(DEFAULT_BOARD_COLOR2);
                        }
                    }else{
                        if(i%2 == 1){
                            g.setColor(DEFAULT_BOARD_COLOR1);
                        }else{
                            g.setColor(DEFAULT_BOARD_COLOR2);
                        }
                    }
                    g.fillRect(i*TILE_SCALE,j*TILE_SCALE,TILE_SCALE,TILE_SCALE);
                }
            }

            //draw border
            g.setColor(DEFAULT_BODRER_COLOR);
            for(int i=0; i<WINDOW_LENGTH/TILE_SCALE; i++) {
                g.fillRect(i * TILE_SCALE, 0, TILE_SCALE, TILE_SCALE);
                g.fillRect(i * TILE_SCALE, WINDOW_HEIGHT-TILE_SCALE, TILE_SCALE, TILE_SCALE);
            }
            for(int i=0; i<WINDOW_HEIGHT/TILE_SCALE; i++) {
                g.fillRect(0, i*TILE_SCALE, TILE_SCALE, TILE_SCALE);
                g.fillRect(WINDOW_LENGTH-TILE_SCALE, i*TILE_SCALE, TILE_SCALE, TILE_SCALE);
            }

            //draw score txt
            String txt = String.valueOf(snakeBody.size());
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(new Color(255,255,255));
            g.drawString(txt,50,20);

            //draw apple
            g.setColor(APPLE_COLOR);
            g.fillRect(apple[0]*TILE_SCALE,apple[1]*TILE_SCALE,TILE_SCALE,TILE_SCALE);

            //draw snake
            for(int i=0; i<snakeBody.size(); i++){
                if(i == 0){ //head
                    g.setColor(SNAKE_HEAD_COLOR);
                }

                else if(i % 2 == 0){
                    g.setColor(SNAKE_BODY_COLOR1);
                }else{
                    g.setColor(SNAKE_BODY_COLOR2);
                }
                g.fillRect(snakeBody.get(i)[0]*TILE_SCALE,snakeBody.get(i)[1]*TILE_SCALE,TILE_SCALE,TILE_SCALE);
            }

        }else{
            //gameover
            g.setColor(new Color(255,255,255));

            g.setFont(new Font("Arial", Font.BOLD, 70));
            FontMetrics fontmetric = g.getFontMetrics(g.getFont());
            String txt = "GAME OVER";
            g.drawString(txt,(WINDOW_LENGTH-fontmetric.stringWidth(txt))/2,(WINDOW_HEIGHT/2));

            g.setFont(new Font("Arial", Font.BOLD, 20));
            fontmetric = g.getFontMetrics(g.getFont());
            txt = "Press [R] to restart";
            g.drawString(txt,(WINDOW_LENGTH-fontmetric.stringWidth(txt))/2,(WINDOW_HEIGHT)-250);

            g.setFont(new Font("Arial",Font.PLAIN, 20));
            fontmetric = g.getFontMetrics(g.getFont());
            txt = "Apples Collected: " + (snakeBody.size()-3);
            g.drawString(txt,(WINDOW_LENGTH-fontmetric.stringWidth(txt))/2,(WINDOW_HEIGHT)-220);
            txt = "High Score: " + highscore;
            g.drawString(txt,(WINDOW_LENGTH-fontmetric.stringWidth(txt))/2,(WINDOW_HEIGHT)-200);
        }

    }


}
