import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GameUI {
    // --- PALET WARNA MODERN (DARK THEME) ---
    private final Color BG_COLOR = new Color(30, 30, 35);
    private final Color PANEL_COLOR = new Color(45, 45, 50);
    private final Color ACCENT_COLOR = new Color(0, 150, 255);
    
    // --- KOMPONEN UI ---
    private JFrame frame;
    private JTextArea gameLog;
    private JLabel labelP1, labelP2, labelManaP1, labelManaP2;
    private JButton btnPassTurn, btnDrawCard;
    private JPanel p1HandPanel, p2HandPanel;
    
    // --- DATA GAME ---
    private Player p1, p2, currentPlayer, enemy;

    public static void main(String[] args) {
        new GameUI(); 
    }

    public GameUI() {
        showMainMenu();
    }

    // ==========================================
    // 1. MAIN MENU
    // ==========================================
    private void showMainMenu() {
        JFrame menuFrame = new JFrame("Java BattleCards Final Project");
        menuFrame.setSize(400, 350);
        menuFrame.getContentPane().setBackground(BG_COLOR);
        menuFrame.setLayout(new GridBagLayout());
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_COLOR);

        JLabel title = new JLabel("TRADING CARD GAME");
        title.setFont(new Font("Impact", Font.PLAIN, 32));
        title.setForeground(ACCENT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton btnStart = createStyledButton("START DUEL", new Color(0, 120, 0));
        btnStart.addActionListener(e -> {
            String name1 = JOptionPane.showInputDialog(menuFrame, "Nama Player 1:", "Player 1");
            if (name1 == null || name1.isEmpty()) name1 = "Player 1";
            String name2 = JOptionPane.showInputDialog(menuFrame, "Nama Player 2:", "Player 2");
            if (name2 == null || name2.isEmpty()) name2 = "Player 2";
            menuFrame.dispose();
            startGame(name1, name2);
        });
        
        JButton btnRules = createStyledButton("Rules", new Color(0, 120, 0));
        btnRules.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRules.addActionListener(e -> showRules());

        JButton btnExit = createStyledButton("Exit", new Color(0, 120, 0));
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExit.addActionListener(e -> System.exit(0));

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(btnStart);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnRules);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnExit);
        menuFrame.add(panel);
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
    }
    
    private void showRules() {
        String rules = "=== ATURAN STRATEGI ===\n\n" +
                       "1. AWAL: Start dengan 0 kartu.\n" +
                       "2. MANA: +2 Mana setiap ganti giliran (Akumulasi).\n" +
                       "3. DRAW: Klik tombol 'DRAW' untuk ambil kartu (Biaya: 1 Mana).\n" +
                       "4. ATTACK: Klik kartu di tangan untuk summon & serang.\n" +
                       "5. SKIP: Klik 'SKIP TURN' untuk menabung mana.";
        JOptionPane.showMessageDialog(null, rules);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40)); 
        return btn;
    }

    // ==========================================
    // 2. GAMEPLAY UI SETUP
    // ==========================================
    private void startGame(String name1, String name2) {
        p1 = new Player(name1);
        p2 = new Player(name2);
        setupDeck(p1); setupDeck(p2);
        currentPlayer = p1; enemy = p2;
        p1.addManaTurn(); 
        makeGameFrame();
        refreshUI();
    }

    private void makeGameFrame() {
        frame = new JFrame("Arena: " + p1.getName() + " vs " + p2.getName());
        frame.setSize(1000, 720);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG_COLOR);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- TOP: STATS ---
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.setBackground(PANEL_COLOR);
        topPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Player 1 Stat
        JPanel p1Stat = new JPanel(new GridLayout(2, 1));
        p1Stat.setBackground(PANEL_COLOR);
        labelP1 = new JLabel(p1.getName());
        labelP1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        labelP1.setForeground(new Color(100, 200, 255)); // Biru
        labelManaP1 = new JLabel("Mana: 0");
        labelManaP1.setForeground(Color.WHITE);
        p1Stat.add(labelP1); p1Stat.add(labelManaP1);

        // Player 2 Stat
        JPanel p2Stat = new JPanel(new GridLayout(2, 1));
        p2Stat.setBackground(PANEL_COLOR);
        labelP2 = new JLabel(p2.getName());
        labelP2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        labelP2.setForeground(new Color(255, 100, 100)); // Merah
        labelP2.setHorizontalAlignment(SwingConstants.RIGHT);
        labelManaP2 = new JLabel("Mana: 0");
        labelManaP2.setForeground(Color.WHITE);
        labelManaP2.setHorizontalAlignment(SwingConstants.RIGHT);
        p2Stat.add(labelP2); p2Stat.add(labelManaP2);

        topPanel.add(p1Stat); topPanel.add(p2Stat);

        // --- MIDDLE: LOG & BUTTONS ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_COLOR);

        gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setBackground(new Color(20, 20, 20)); // Hitam banget
        gameLog.setForeground(new Color(0, 255, 100)); // Hijau Hacker
        gameLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollLog = new JScrollPane(gameLog);
        scrollLog.setBorder(new LineBorder(Color.DARK_GRAY));
        
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(BG_COLOR);
        
        btnDrawCard = new JButton("DRAW (-1 Mana)");
        btnDrawCard.setBackground(new Color(0, 100, 200));
        btnDrawCard.setForeground(Color.WHITE);
        btnDrawCard.addActionListener(e -> performDraw());
        
        btnPassTurn = new JButton("SKIP TURN >>");
        btnPassTurn.setBackground(new Color(200, 50, 0));
        btnPassTurn.setForeground(Color.WHITE);
        btnPassTurn.addActionListener(e -> switchTurn());

        actionPanel.add(btnDrawCard); actionPanel.add(btnPassTurn);
        
        centerPanel.add(scrollLog, BorderLayout.CENTER);
        centerPanel.add(actionPanel, BorderLayout.SOUTH);

        // --- BOTTOM: CARDS ---
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.setBackground(BG_COLOR);

        p1HandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p1HandPanel.setBackground(new Color(40, 40, 45));
        JScrollPane scrollP1 = setupScroll(p1HandPanel, "Hand: " + p1.getName());

        p2HandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p2HandPanel.setBackground(new Color(45, 40, 40));
        JScrollPane scrollP2 = setupScroll(p2HandPanel, "Hand: " + p2.getName());

        bottomPanel.add(scrollP1); bottomPanel.add(scrollP2);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JScrollPane setupScroll(JPanel panel, String title) {
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createTitledBorder(null, title, 0, 0, new Font("Arial", Font.BOLD, 12), Color.GRAY));
        return scroll;
    }

    // ==========================================
    // 3. RENDER KARTU
    // ==========================================
    private void renderHandButtons(Player player, JPanel panel) {
        panel.removeAll();
        ArrayList<Card> hand = player.getHand();

        if(hand.isEmpty()) {
            JLabel empty = new JLabel("(Kosong)");
            empty.setForeground(Color.GRAY);
            panel.add(empty);
        }

        for (int i = 0; i < hand.size(); i++) {
            Card c = hand.get(i);
            
            String typeLabel = "";
            String statLabel = "";
            
            if (c instanceof Minion) {
                Minion m = (Minion) c;
                typeLabel = "MINION";
                statLabel = "ATK: " + m.getAttack(); 
            } else if (c instanceof Spell) {
                Spell s = (Spell) c;
                typeLabel = "SPELL";
                statLabel = "DMG: " + s.getDamage();
            }

            // HTML Layout untuk tombol
            String html = "<html><center>" +
                          "<div style='font-size:9px; color:#DDDDDD; margin-bottom:5px;'>" + typeLabel + "</div>" +
                          "<div style='font-size:12px; font-weight:bold; color:white; margin-bottom:5px;'>" + c.getName() + "</div>" +
                          "<div style='font-size:10px; color:#FFFF00;'>Cost: " + c.getManaCost() + "</div>" +
                          "<div style='font-size:11px; font-weight:bold; color:#FF9999; margin-top:5px;'>" + statLabel + "</div>" +
                          "</center></html>";

            JButton btn = new JButton(html);
            btn.setPreferredSize(new Dimension(110, 160));
            
            Color cardColor = getRarityColor(c.getName());
            btn.setBackground(cardColor);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            
            // Logika Klik
            int idx = i;
            btn.addActionListener(e -> {
                if (player == currentPlayer) {
                    if (player.playCard(idx, enemy)) {
                        log(">> " + player.getName() + " mainkan " + c.getName());
                        checkWin();
                        refreshUI();
                    } else {
                        log("[!] Mana kurang!");
                    }
                } else {
                    log("[!] Bukan giliranmu!");
                }
            });

            if (player != currentPlayer) {
                btn.setEnabled(false);
                btn.setBackground(new Color(30, 30, 30));
            }

            JPanel wrapper = new JPanel();
            wrapper.setBackground(new Color(40, 40, 45));
            wrapper.add(btn);
            panel.add(wrapper);
        }
    }

    private Color getRarityColor(String name) {
        if (name.contains("[SSR]")) return new Color(210, 140, 0);   // Emas Gelap (Legend)
        if (name.contains("[E]"))   return new Color(100, 0, 150);   // Ungu Tua (Epic)
        if (name.contains("[R]"))   return new Color(0, 80, 180);    // Biru Tua (Rare)
        return new Color(106, 106, 106);                             // Abu-abu (Common)
    }

    // ==========================================
    // 4. GAME LOGIC UTILS
    // ==========================================
    private void performDraw() {
        if(currentPlayer.getMana() >= 1) {
            currentPlayer.payMana(1);
            currentPlayer.drawCard();
            log(currentPlayer.getName() + " draw kartu.");
            refreshUI();
        } else {
            JOptionPane.showMessageDialog(frame, "Mana tidak cukup!");
        }
    }

    private void switchTurn() {
        if(currentPlayer == p1) { currentPlayer = p2; enemy = p1; }
        else { currentPlayer = p1; enemy = p2; }
        currentPlayer.addManaTurn();
        log("\n=== GILIRAN " + currentPlayer.getName() + " ===");
        refreshUI();
    }

    private void refreshUI() {
        labelP1.setText(p1.getName() + " (HP: " + p1.getHealth() + ")");
        labelP2.setText(p2.getName() + " (HP: " + p2.getHealth() + ")");
        labelManaP1.setText("Mana: " + p1.getMana());
        labelManaP2.setText("Mana: " + p2.getMana());

        renderHandButtons(p1, p1HandPanel);
        renderHandButtons(p2, p2HandPanel);
        
        if(currentPlayer.getMana() >= 1) {
            btnDrawCard.setEnabled(true);
            btnDrawCard.setBackground(new Color(0, 100, 200));
        } else {
            btnDrawCard.setEnabled(false);
            btnDrawCard.setBackground(Color.GRAY);
        }

        frame.revalidate(); frame.repaint();
    }

    private void checkWin() {
        if (!p1.isAlive()) gameOver(p2.getName());
        else if (!p2.isAlive()) gameOver(p1.getName());
    }

    private void gameOver(String winner) {
        JOptionPane.showMessageDialog(frame, "GAME OVER! Pemenang: " + winner);
        frame.dispose(); showMainMenu();
    }

    private void log(String text) {
        gameLog.append(text + "\n");
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }

    private void setupDeck(Player p) {
        for(int i=0; i<25; i++) p.addCardToDeck(getGachaCard());
    }

    private Card getGachaCard() {
        double rng = Math.random();
        if (rng < 0.50) { 
            int pick = (int)(Math.random()*3);
            if(pick==0) return new Minion("Goblin [C]", 1, 2);
            if(pick==1) return new Spell("Arrow [C]", 1, 2);
            return new Minion("Wolf [C]", 2, 3);
        } else if (rng < 0.80) {
            int pick = (int)(Math.random()*3);
            if(pick==0) return new Minion("Knight [R]", 3, 5);
            if(pick==1) return new Spell("Fireball [R]", 3, 5);
            return new Minion("Orc [R]", 3, 6);
        } else if (rng < 0.95) {
            int pick = (int)(Math.random()*2);
            if(pick==0) return new Minion("Golem [E]", 5, 8);
            return new Spell("Lightning [E]", 5, 9);
        } else {
            int pick = (int)(Math.random()*2);
            if(pick==0) return new Minion("Dragon [SSR]", 7, 12);
            return new Spell("Meteor [SSR]", 8, 15);
        }
    }
}
