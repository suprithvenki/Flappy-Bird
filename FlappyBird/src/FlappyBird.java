import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Bird
    int birdX = boardWidth / 8;// 1/8th position
    int birdY = boardHeight / 2; // half the screen height
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // game logic
    Bird bird;
    int velocityX = -4;// moves pipes to the left speed(simulates bird moving right)
    int velocityY = 0;// change of movement over time
    int gravity = 1;// for every frame bird slowes down by 1 px
    ArrayList<Pipe> pipes;
    Random random = new Random();

    // pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int X = pipeX;
        int Y = pipeY;
        int width = pipeWidth;
        int Height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // game timer
    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        setFocusable(true);
        addKeyListener(this);

        // setBackground(Color.green);

        // load images
        backgroundImg = new ImageIcon(getClass().getResource("./images/flappybirdbg.png")).getImage();

        birdImg = new ImageIcon(getClass().getResource("./images/flappybird.png")).getImage();

        topPipeImg = new ImageIcon(getClass().getResource("./images/toppipe.png")).getImage();

        bottomPipeImg = new ImageIcon(getClass().getResource("./images/bottompipe.png")).getImage();
        // bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        // game timer
        gameLoop = new Timer(1000 / 60, this);// this referes to action performed
        gameLoop.start();
        placePipesTimer.start();
    }

    public void placePipes() {
        // Math.random() * (pipeHeight / 2)=>(0-1)*pipeHeight/2 ->(0 to 256)
        // 128-0
        // 0- 128 - (0 to 256)

        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.Y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottompipe = new Pipe(bottomPipeImg);
        bottompipe.Y = topPipe.Y + pipeHeight + openingSpace;
        pipes.add(bottompipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        System.out.println("moving");

        // background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // draw pipes(top pipe)
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.X, pipe.Y, pipe.width, pipe.Height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        if (gameOver) {
            g.drawString("Game Over" + String.valueOf((int) score), 10, 35);
        } else {
            g.drawString((String.valueOf((int) score)), 10, 35);
        }
    }

    public void move() {
        // update all x and y positions
        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);// bird should stop when it reaches top

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.X += velocityX;

            if (collision(bird, pipe)) {
                gameOver = true;
            }

            if (!pipe.passed && bird.x > pipe.X + pipe.width) {
                pipe.passed = true;
                score += 0.5;// top=0.5 ,bottom=0.5,2 pipes will be passed in one move
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }

    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.X + b.width && // a's top left corner doesnt reach b's top right corner
                a.x + a.width > b.X && // a's top right corner passes b's top left corner
                a.y < b.Y + b.Height && // a's top left corner doesnt reach b's bottom left corner
                a.y + a.height > b.Y;// a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) {
                // restart game by reseting the condition
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
