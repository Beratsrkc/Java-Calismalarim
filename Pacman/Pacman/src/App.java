import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        int rowCount = 21;
        int columnCount = 19;
        int titleSize = 32;
        int boardWidth = columnCount * titleSize;
        int boardHeight = rowCount * titleSize;

        JFrame frame = new JFrame("Pac Man");
        
        try {
            // İkonu yükle (dosya yolunu kendi projenize göre ayarlayın)
            Image icon = new ImageIcon(App.class.getResource("/img/pacmanicon.jpg")).getImage();
            frame.setIconImage(icon);
        } catch (Exception e) {
            System.out.println("İkon yüklenirken hata oluştu: " + e.getMessage());
        }

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack();
        
        frame.setVisible(true);
        pacmanGame.requestFocus();
    }
}