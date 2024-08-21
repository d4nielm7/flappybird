import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class flappybird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Bird class properties
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2; // Changed from boardWidth/2 to boardHeight/2
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

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game logic properties
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;
    ArrayList<Pipe> pipes;
    Random rand = new Random();
    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    double highScore = 0;

    public void checkHighScore() {
        if (score > highScore) {
            highScore = score;
        }
    }

    flappybird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // Create bird instance
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();
        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipeTimer.start();
        // Start game timer
        gameLoop = new Timer(1000 / 60, this); // Frame rate is set to 60 FPS
        gameLoop.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void draw(Graphics g) {
        // Draw background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        // Draw bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Draw pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Draw score and game over text
        g.setColor(Color.white);
        g.setFont(new Font("Raleway", Font.BOLD, 25));
        FontMetrics metrics = g.getFontMetrics(g.getFont());

        if (gameOver) {
            String gameOverText = "Game Over";
            int gameOverWidth = metrics.stringWidth(gameOverText);
            g.drawString(gameOverText, (boardWidth - gameOverWidth) / 2, boardHeight / 2 - 50);

            String scoreText = "Score: " + String.valueOf((int) score);
            int scoreWidth = metrics.stringWidth(scoreText);
            g.drawString(scoreText, (boardWidth - scoreWidth) / 2, boardHeight / 2);

            String highScoreText = "High Score: " + String.valueOf((int) highScore);
            int highScoreWidth = metrics.stringWidth(highScoreText);
            g.drawString(highScoreText, (boardWidth - highScoreWidth) / 2, boardHeight / 2 + 50);

            String continueText = "Press Space to Restart";
            int continueWidth = metrics.stringWidth(continueText);
            g.drawString(continueText, (boardWidth - continueWidth) / 2, boardHeight / 2 + 100);
        } else {
            String scoreText = String.valueOf((int) score);
            int scoreWidth = metrics.stringWidth(scoreText);
            g.drawString(scoreText, (boardWidth - scoreWidth) / 2, 35);
        }
    }

    public void move() {
        // Update bird's velocity and position
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); // Prevent the bird from moving above the screen

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
                sound.playSound("sfx_point.wav");
            }

            if (collision(bird, pipe)) {
                gameOver = true;
                sound.playSound("sfx_hit.wav");
                checkHighScore();
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
            sound.playSound("sfx_die.wav");
            checkHighScore();
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && // a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x && // a's top right corner passes b's top left corner
                a.y < b.y + b.height && // a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y; // a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                // Restart the game
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;  // Reset current score
                gameLoop.start();
                placePipeTimer.start();
            } else {
                // Make the bird jump
                velocityY = -9;
                sound.playSound("sfx_wing.wav");
            }
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
