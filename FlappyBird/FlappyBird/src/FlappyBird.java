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
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
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

    // pipes
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

    // game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    
    // Yeni eklenen özellikler
    boolean gameStarted = false;
    boolean gameOver = false;
    double score = 0;
    double highScore = 0;
    Font gameFont = new Font("Arial", Font.BOLD, 28);

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // load images
        backgroundImg = new ImageIcon(getClass().getResource("./img/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./img/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./img/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./img/bottompipe.png")).getImage();

        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        placePipesTimer = new Timer(1500, e -> placePipes());
        gameLoop = new Timer(1000 / 60, this);
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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        if (!gameStarted) {
            drawStartScreen(g);
            return;
        }

        // pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // score
        g.setColor(Color.WHITE);
        g.setFont(gameFont);
        g.drawString(String.valueOf((int) score), 20, 40);

        if (gameOver) {
            drawGameOverScreen(g);
        }
    }

    private void drawStartScreen(Graphics g) {
        // Yarı saydam arkaplan
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, boardWidth, boardHeight);
        
        // Başlık
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        String title = "FLAPPY BIRD";
        g.drawString(title, (boardWidth - g.getFontMetrics().stringWidth(title)) / 2, boardHeight / 2 - 30);
        
        // Talimat
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String instruction = "SPACE tuşuna basarak başla";
        g.drawString(instruction, (boardWidth - g.getFontMetrics().stringWidth(instruction)) / 2, boardHeight / 2 + 20);
    }

    private void drawGameOverScreen(Graphics g) {
        // Yarı saydam arkaplan
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, boardWidth, boardHeight);
        
        // Game Over metni
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String gameOverText = "GAME OVER";
        g.drawString(gameOverText, (boardWidth - g.getFontMetrics().stringWidth(gameOverText)) / 2, boardHeight / 2 - 60);
        
        // Skor
        g.setFont(new Font("Arial", Font.PLAIN, 28));
        String scoreText = "Skor: " + (int)score;
        g.drawString(scoreText, (boardWidth - g.getFontMetrics().stringWidth(scoreText)) / 2, boardHeight / 2);
        
        // En yüksek skor
        if (score > highScore) {
            highScore = score;
        }
        String highScoreText = "En İyi: " + (int)highScore;
        g.drawString(highScoreText, (boardWidth - g.getFontMetrics().stringWidth(highScoreText)) / 2, boardHeight / 2 + 40);
        
        // Yeniden başlatma talimatı
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String restartText = "SPACE - Yeniden Başlat";
        g.drawString(restartText, (boardWidth - g.getFontMetrics().stringWidth(restartText)) / 2, boardHeight / 2 + 80);
    }

    public void move() {
        if (!gameStarted || gameOver) return;

        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                gameStarted = true;
                gameLoop.start();
                placePipesTimer.start();
            } else if (!gameOver) {
                velocityY = -9;
            } else {
                resetGame();
            }
        }
    }

    private void resetGame() {
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        gameStarted = false;
        gameLoop.stop();
        placePipesTimer.stop();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}