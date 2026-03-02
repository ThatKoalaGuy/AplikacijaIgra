package com.domenkoder.aplikacijaigra;

import java.awt.event.ActionEvent;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.*;

public class GameManager {

    protected JFrame frame;
    protected FadingLabel spaceshipLabel;
    protected JLabel bgLabel;
    protected JLabel jLabelHeart1;
    protected JLabel jLabelHeart2;
    protected JLabel jLabelHeart3;

    protected ArrayList<Rock> rocks = new ArrayList<>();
    protected ArrayList<Bullet> bullets = new ArrayList<>();

    protected int lives = 3;
    protected boolean gameOver = false;

    protected Timer spawnTimer;
    protected Timer moveTimer;

    // Use the designer position (Level1 has 490,500)
    protected int playerX = 490;
    protected int playerY = 500;

    private final int spawnInterval;

    public GameManager(JFrame frame,
                       FadingLabel spaceshipLabel,
                       JLabel bgLabel,
                       JLabel heart1,
                       JLabel heart2,
                       JLabel heart3,
                       int spawnInterval) {

        this.frame = frame;
        this.spaceshipLabel = spaceshipLabel;
        this.bgLabel = bgLabel;
        this.jLabelHeart1 = heart1;
        this.jLabelHeart2 = heart2;
        this.jLabelHeart3 = heart3;
        this.spawnInterval = spawnInterval;

        setupUI();
        updateHearts();
        setupInputBindings();
        setupSpawnTimer();
        setupMoveTimer();

        // Force initial position
        spaceshipLabel.setLocation(playerX, playerY);
    }

    private void setupUI() {
        frame.getContentPane().setComponentZOrder(bgLabel, frame.getContentPane().getComponentCount() - 1);
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }

    private void updateHearts() {
        jLabelHeart1.setVisible(lives >= 1);
        jLabelHeart2.setVisible(lives >= 2);
        jLabelHeart3.setVisible(lives >= 3);
    }

    private void setupInputBindings() {
        JComponent root = frame.getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke("LEFT"), "left");
        am.put("left", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { movePlayer(-10); }
        });

        im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        am.put("right", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { movePlayer(10); }
        });

        im.put(KeyStroke.getKeyStroke("SPACE"), "shoot");
        am.put("shoot", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { shootBullet(); }
        });
    }

    private void clampPlayerPosition() {
        if (playerX < 0) playerX = 0;
        if (playerX > 900) playerX = 900;  // 1200-300
    }

    private void syncShipPosition() {
        // ABSOLUTNO NEPREMIČNA POZICIJA - NIČ TE NE BO TELEPORTIRALO!
        spaceshipLabel.setBounds(playerX, playerY, spaceshipLabel.getWidth(), spaceshipLabel.getHeight());
    }

    private void movePlayer(int deltaX) {
        playerX += deltaX;
        clampPlayerPosition();
        syncShipPosition();
    }

    private void shootBullet() {
        int bulletX = playerX + spaceshipLabel.getWidth() / 2 - 10;
        int bulletY = playerY;

        Bullet b = new Bullet(bulletX, bulletY);
        bullets.add(b);

        frame.getContentPane().add(b, new org.netbeans.lib.awtextra.AbsoluteConstraints(bulletX, bulletY, 20, 40));
        frame.getContentPane().setComponentZOrder(b, 0);
    }

    private void setupSpawnTimer() {
        spawnTimer = new Timer(spawnInterval, e -> {
            int screenWidth = frame.getContentPane().getWidth();
            int randomX = (int) (Math.random() * (screenWidth - 80));

            Rock r = new Rock(randomX);
            rocks.add(r);

            frame.getContentPane().add(r, new org.netbeans.lib.awtextra.AbsoluteConstraints(randomX, -80, 80, 80));
            frame.getContentPane().setComponentZOrder(r, 0);
        });
        spawnTimer.start();
    }

    private void setupMoveTimer() {
        moveTimer = new Timer(16, (ActionEvent e) -> {
            // *** TOLE JE TVOJ WHILE(TRUE) - VSAK FRAME PRISILNO SINHRONIZIRA POZICIJO ***
            syncShipPosition();
            
            ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
            ArrayList<Rock> rocksToRemove = new ArrayList<>();

            // Bullets
            for (Bullet b : bullets) {
                b.move();

                if (b.getY() < -50) {
                    frame.getContentPane().remove(b);
                    bulletsToRemove.add(b);
                    continue;
                }

                for (Rock r : rocks) {
                    if (b.getBounds().intersects(r.getBounds())) {
                        frame.getContentPane().remove(b);
                        frame.getContentPane().remove(r);
                        bulletsToRemove.add(b);
                        rocksToRemove.add(r);
                        break;
                    }
                }
            }
            bullets.removeAll(bulletsToRemove);

            // Rocks + Collision
            for (Rock r : rocks) {
                r.move();

                if (r.getY() > frame.getContentPane().getHeight()) {
                    frame.getContentPane().remove(r);
                    rocksToRemove.add(r);
                    continue;
                }

                // PRISILNA SINHRONIZACIJA PRED KOLIZIJO
                syncShipPosition();
                
                if (!gameOver && r.getBounds().intersects(spaceshipLabel.getBounds())) {
                    lives--;
                    updateHearts();

                    frame.getContentPane().remove(r);
                    rocksToRemove.add(r);

                    // ŠE ENA PRISILNA SINHRONIZACIJA PO KOLIZIJI
                    syncShipPosition();

                    Timer flashTimer = new Timer(100, null);
                    final int[] count = {0};
                    flashTimer.addActionListener(ev -> {
                        float newAlpha = (count[0] % 2 == 0) ? 0f : 1f;
                        spaceshipLabel.setAlpha(newAlpha);
                        count[0]++;
                        if (count[0] >= 6) {
                            spaceshipLabel.setAlpha(1f);
                            flashTimer.stop();
                        }
                    });
                    flashTimer.start();

                    if (lives <= 0) {
                        gameOver = true;
                        spawnTimer.stop();
                        moveTimer.stop();
                        System.out.println("GAME OVER 💀");
                    }
                }
            }

            rocks.removeAll(rocksToRemove);
            frame.getContentPane().repaint();
        });

        moveTimer.start();
    }
}