import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");

        try {
            // İkonu yükle (dosya yolunu kendi projenize göre ayarlayın)
            Image icon = new ImageIcon(App.class.getResource("/img/flappybird.png")).getImage();
            frame.setIconImage(icon);
        } catch (Exception e) {
            System.out.println("İkon yüklenirken hata oluştu: " + e.getMessage());
        }
        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
}
