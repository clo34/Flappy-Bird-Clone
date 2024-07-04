import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 360;
    int boardHeight = 640;

    //images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird
    int birdX = boardWidth/8; //where we place our bird on x position
    int birdY = boardHeight/2; //where we place our bird on the y position
    int birdWidth = 34; //the width of the bird
    int birdHeight = 24; //the height of the bird

    /**
     * The Bird class
     * */
    public class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;  //scaled by 1/6
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false; // use for keeping track of score

        Pipe(Image img){
            this.img = img;
        }

    }

    //game logic
    Bird bird;
    int velocityX = -4; //moves pipes to hte left speed (simulates bird moving right)
    int velocityY = 0; //moving downwards = positive velocity (since (0,0)), upwards = negative
    int gravity = 1;

    ArrayList<Pipe> pipes;

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false; // when true = birds falls out of screen or bird collides
    double score = 0;



    /**
    * Constructor for FlappyBird class
    * */
    public FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.cyan);
        setFocusable(true); //Make sure our JPanel will be the one that takes in the keyevents
        addKeyListener(this); // Make sure we check the 3 Key functions

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();
        Random random = new Random();

        //place pipes timer
        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipeTimer.start();

        //game timer -> continuously draw the background 60 times a second
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

    }

    public void placePipes(){
        // (0~1) * pipeHeight/2 -> 256
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        //invoke the function from JPanel since we inherited it
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //draw background
        // top left corner = (0,0), bottom right corner = (boardWidth, boardHeight)
        // basically (360, 640), we always start from top left corner when drawing
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //draw bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        //draw pipes
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 32));
        if(gameOver){
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
            g.setColor(Color.yellow);
            g.setFont(new Font("Times New Roman", Font.PLAIN, 22));
            g.drawString("Press Space to Restart the Game", boardWidth/10, boardHeight/2);
        }
        else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); //limits bird in the frame

        //pipes
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5; //0.5 because there are 2 pipes! so 0.5*2 = 1, 1 for each set of pipes
            }
            if(collision(bird, pipe)){
                gameOver = true;
            }
        }

        if(bird.y > boardHeight){
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width &&           //a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x &&          //a's top right corner passes b's top left corner
                a.y < b.y + b.height &&         //a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y;           //a's bottom left corner passes b's top left corner
    }

    /**
     * The action that is performed every 60 milliseconds
     * */
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint(); //will call the paint component
        if(gameOver){
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //When you type on a key that has a character
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //Similar to key type but it could be any key (We won't need this)
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;
            if (gameOver) {
                //restart the game by resetting the conditions
                bird.y = birdY;
                velocityY= 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //When you press a key, let it go and the key goes back up (we won't need this)
    }
}
