import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack {
    private class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) {
                return value.equals("A") ? 11 : 10;
            }
            return Integer.parseInt(value);
        }

        public boolean isAce() {
            return value.equals("A");
        }

        public String getImagePath() {
            return "./cards/" + toString() + ".png";
        }
    }

    // Game constants
    private static final int BOARD_WIDTH = 800;
    private static final int BOARD_HEIGHT = 700; // Yükseklik biraz daha arttırıldı
    private static final int CARD_WIDTH = 110; // Kart boyutu küçültüldü
    private static final int CARD_HEIGHT = 154;
    private static final Color TABLE_COLOR = new Color(53, 101, 77);

    // Game components
    private ArrayList<Card> deck;
    private Random random = new Random();

    // Scores
    private int playerScore = 0;
    private int dealerScore = 0;

    // Dealer components
    private Card hiddenCard;
    private ArrayList<Card> dealerHand;
    private int dealerSum;
    private int dealerAceCount;

    // Player components
    private ArrayList<Card> playerHand;
    private int playerSum;
    private int playerAceCount;

    // UI components
    private JFrame frame = new JFrame("Black Jack");
    private JPanel gamePanel = new GamePanel();
    private JPanel buttonPanel = new JPanel();
    private JButton hitButton = new JButton("Hit");
    private JButton stayButton = new JButton("Stay");
    private JButton restartButton = new JButton("Tekrar Başla");

    public BlackJack() {
        initializeGame();
        setupUI();
    }

    private void initializeGame() {
        buildDeck();
        shuffleDeck();
        resetGame();
    }

    private void setupUI() {
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(TABLE_COLOR);
        frame.add(gamePanel);

        setupButtons();
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void setupButtons() {
        hitButton.setFocusable(false);
        stayButton.setFocusable(false);
        restartButton.setFocusable(false);
        
        buttonPanel.add(hitButton);
        buttonPanel.add(stayButton);
        buttonPanel.add(restartButton);

        hitButton.addActionListener(e -> {
            Card card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
            
            if (reducePlayerAce() >= 21) {
                disableButtons();
                updateScores();
            }
            gamePanel.repaint();
        });

        stayButton.addActionListener(e -> {
            disableButtons();
            dealerTurn();
            updateScores();
            gamePanel.repaint();
        });

        restartButton.addActionListener(e -> {
            initializeGame();
            gamePanel.repaint();
        });
    }

    private void updateScores() {
        if (playerSum > 21) {
            dealerScore++;
        } else if (dealerSum > 21) {
            playerScore++;
        } else if (playerSum > dealerSum) {
            playerScore++;
        } else if (playerSum < dealerSum) {
            dealerScore++;
        }
    }

    private void disableButtons() {
        hitButton.setEnabled(false);
        stayButton.setEnabled(false);
    }

    private void enableButtons() {
        hitButton.setEnabled(true);
        stayButton.setEnabled(true);
    }

    private void dealerTurn() {
        while (dealerSum < 17) {
            Card card = deck.remove(deck.size() - 1);
            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1 : 0;
            dealerHand.add(card);
        }
        reduceDealerAce(); // Dealer'ın as değerini kontrol et
    }

    private void resetGame() {
        // Reset dealer
        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        // Reset player
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        enableButtons();
    }

    private void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                deck.add(new Card(value, type));
            }
        }
    }

    private void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
        }
    }

    private int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount--;
        }
        return playerSum;
    }

    private int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount--;
        }
        return dealerSum;
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            try {
                // Draw scores at the top
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.WHITE);
                g.drawString("Oyuncu: " + playerScore, 30, 30);
                g.drawString("Bilgisayar: " + dealerScore, BOARD_WIDTH - 150, 30);

                // Draw dealer's cards and total
                int dealerX = 20;
                int dealerY = 60;
                
                // Draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if (!stayButton.isEnabled()) {
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg, dealerX, dealerY, CARD_WIDTH, CARD_HEIGHT, null);

                // Draw dealer's hand
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, dealerX + CARD_WIDTH + 10 + (CARD_WIDTH + 5) * i, dealerY, CARD_WIDTH, CARD_HEIGHT, null);
                }

                // Draw dealer's total
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                String dealerTotalText = stayButton.isEnabled() ? "?" : String.valueOf(dealerSum);
                g.drawString("Toplam: " + dealerTotalText, dealerX, dealerY + CARD_HEIGHT + 20);

                // Draw player's cards and total
                int playerX = 20;
                int playerY = dealerY + CARD_HEIGHT + 50;
                
                // Draw player's hand
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, playerX + (CARD_WIDTH + 5) * i, playerY, CARD_WIDTH, CARD_HEIGHT, null);
                }

                // Draw player's total
                g.drawString("Toplam: " + playerSum, playerX, playerY + CARD_HEIGHT + 20);

                // Draw game result if game ended
                if (!stayButton.isEnabled()) {
                    String message = determineWinner();
                    displayResult(g, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String determineWinner() {
            if (playerSum > 21) return "Kaybettiniz!";
            if (dealerSum > 21) return "Kazandınız!";
            if (playerSum == dealerSum) return "Berabere!";
            return playerSum > dealerSum ? "Kazandınız!" : "Kaybettiniz!";
        }

        private void displayResult(Graphics g, String message) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.YELLOW);
            
            // Center the message
            FontMetrics fm = g.getFontMetrics();
            int messageWidth = fm.stringWidth(message);
            int x = (BOARD_WIDTH - messageWidth) / 2;
            int y = 250;
            
            g.drawString(message, x, y);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BlackJack());
    }
}