package com.domenkoder.aplikacijaigra;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.*;

public class GameManager {

    protected JFrame frame;
    protected FadingLabel spaceshipLabel;
    protected JLabel bgLabel;
    protected JLabel jLabelHeart1;
    protected JLabel jLabelHeart2;
    protected JLabel jLabelHeart3;
    protected JLabel jLabel3; // timer
    protected JLabel jLabel4; // score

    protected ArrayList<Rock> rocks = new ArrayList<>();
    protected ArrayList<Bullet> bullets = new ArrayList<>();

    protected int lives = 3;
    protected boolean gameOver = false;

    protected Timer spawnTimer;
    protected Timer moveTimer;
    protected Timer levelTimer;

    protected int playerX = 490;
    protected int playerY = 500;

    private final int spawnInterval;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean spacePressed = false;

    private int shootCooldown = 0;
    private final int shootCooldownMax = 24;

    private int score = 0;

    public GameManager(JFrame frame,
            FadingLabel spaceshipLabel,
            JLabel bgLabel,
            JLabel heart1,
            JLabel heart2,
            JLabel heart3,
            JLabel levelLabel,
            JLabel scoreLabel,
            int spawnInterval) {

        this.frame = frame;
        this.spaceshipLabel = spaceshipLabel;
        this.bgLabel = bgLabel;
        this.jLabelHeart1 = heart1;
        this.jLabelHeart2 = heart2;
        this.jLabelHeart3 = heart3;
        this.jLabel3 = levelLabel;
        this.jLabel4 = scoreLabel;
        this.spawnInterval = spawnInterval;

        score = 0;
        jLabel4.setText("Score: " + score);

        setupUI();
        updateHearts();
        setupSpawnTimer();
        setupMoveTimer();
        startLevelTimer();

        // KEYS NA KONCU - po vseh timerjih!
        setupInputBindings();

        spaceshipLabel.setLocation(playerX, playerY);
        frame.requestFocus();  // Focus na koncu
    }

    private void setupUI() {
        // SAMO z-order ozadja - brez focus problema!
        frame.getContentPane().setComponentZOrder(bgLabel,
                frame.getContentPane().getComponentCount() - 1);
        // IZBRIŠČENO: frame.setFocusable(true); frame.requestFocusInWindow();
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

        im.put(KeyStroke.getKeyStroke("pressed LEFT"), "leftPressed");
        im.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
        am.put("leftPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                leftPressed = true;
            }
        });
        am.put("leftReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                leftPressed = false;
            }
        });

        im.put(KeyStroke.getKeyStroke("pressed RIGHT"), "rightPressed");
        im.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");
        am.put("rightPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                rightPressed = true;
            }
        });
        am.put("rightReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                rightPressed = false;
            }
        });

        im.put(KeyStroke.getKeyStroke("pressed SPACE"), "spacePressed");
        im.put(KeyStroke.getKeyStroke("released SPACE"), "spaceReleased");
        am.put("spacePressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                spacePressed = true;
            }
        });
        am.put("spaceReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                spacePressed = false;
            }
        });
    }

    private void clampPlayerPosition() {
        if (playerX < 0) {
            playerX = 0;
        }
        if (playerX > 900) {
            playerX = 900;
        }
    }

    private void syncShipPosition() {
        spaceshipLabel.setBounds(playerX, playerY,
                spaceshipLabel.getWidth(),
                spaceshipLabel.getHeight());
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

        frame.getContentPane().add(b,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(
                        bulletX, bulletY, 20, 40));
        frame.getContentPane().setComponentZOrder(b, 0);
    }

    private void spawnExplosion(int x, int y) {
        System.out.println("💥 EXPLOSION na (" + x + "," + y + ")");  // DEBUG
        Explosion explosion = new Explosion(x, y);
        frame.getContentPane().add(explosion);
        frame.getContentPane().setComponentZOrder(explosion, 0);

        Timer removeTimer = new Timer(600, e -> {
            System.out.println("🧹 EXPLOSION odstranjen");  // DEBUG
            frame.getContentPane().remove(explosion);
            frame.getContentPane().repaint();
        });
        removeTimer.setRepeats(false);
        removeTimer.start();
    }

    private void setupSpawnTimer() {
        spawnTimer = new Timer(spawnInterval, e -> {
            int screenWidth = frame.getContentPane().getWidth();
            int margin = 70;
            int rockWidth = 80;

            int randomX = margin + (int) (Math.random() * (screenWidth - 2 * margin - rockWidth));

            Rock r = new Rock(randomX);
            rocks.add(r);

            frame.getContentPane().add(r,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(
                            randomX, -80, rockWidth, rockWidth));
            frame.getContentPane().setComponentZOrder(r, 0);
        });
        spawnTimer.start();
    }

    private void setupMoveTimer() {
        moveTimer = new Timer(16, (ActionEvent e) -> {
            if (gameOver) {
                return;  // Early exit če je konec
            }
            if (leftPressed) {
                movePlayer(-8);
            }
            if (rightPressed) {
                movePlayer(8);
            }

            if (spacePressed && shootCooldown <= 0) {
                shootBullet();
                shootCooldown = shootCooldownMax;
            }
            if (shootCooldown > 0) {
                shootCooldown--;
            }

            ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
            ArrayList<Rock> rocksToRemove = new ArrayList<>();

            for (Bullet b : bullets) {
                b.move();

                if (b.getY() < -50) {
                    frame.getContentPane().remove(b);
                    bulletsToRemove.add(b);
                    continue;
                }

                for (Rock r : rocks) {
                    if (b.getBounds().intersects(r.getBounds())) {
                        spawnExplosion(r.getX(), r.getY());
                        frame.getContentPane().remove(b);
                        frame.getContentPane().remove(r);
                        bulletsToRemove.add(b);
                        rocksToRemove.add(r);
                        score++;
                        jLabel4.setText("Score: " + score);
                        break;
                    }
                }
            }

            bullets.removeAll(bulletsToRemove);

            for (Rock r : rocks) {
                r.move();

                if (r.getY() > frame.getContentPane().getHeight()) {
                    frame.getContentPane().remove(r);
                    rocksToRemove.add(r);
                    continue;
                }

                if (!gameOver && r.getBounds().intersects(spaceshipLabel.getBounds())) {
                    spawnExplosion(r.getX(), r.getY());
                    spaceshipLabel.startFlash();  // ✅ ADD THIS LINE
                    lives--;
                    updateHearts();
                    frame.getContentPane().remove(r);
                    rocksToRemove.add(r);

                    if (lives <= 0) {
                        System.out.println("GAME OVER 💀");
                        endGame();
                    }
                }
            }

            rocks.removeAll(rocksToRemove);
            frame.getContentPane().repaint();
        });
        moveTimer.start();
    }

    private void startLevelTimer() {
        int levelSeconds = 30;
        jLabel3.setText("Time: " + levelSeconds);

        levelTimer = new Timer(1000, e -> {
            if (gameOver) {
                levelTimer.stop();
                return;
            }

            int currentTime = Integer.parseInt(
                    jLabel3.getText().replace("Time: ", ""));
            currentTime--;

            if (currentTime <= 0) {
                jLabel3.setText("Time: 0");
                System.out.println("LEVEL COMPLETE 🎉");
                endGame();
            } else {
                jLabel3.setText("Time: " + currentTime);
            }
        });
        levelTimer.start();
    }

    private void endGame() {
        if (gameOver) {
            return;
        }
        gameOver = true;

        spawnTimer.stop();
        moveTimer.stop();
        if (levelTimer != null) {
            levelTimer.stop();
        }

        // SHRANI REZULTAT TRENUTNE RAVNI
        SaveManager.saveResult(LevelManager.getCurrentLevel(), score);

        // 2 second delay before showing results
        Timer delayTimer = new Timer(2000, e -> {
            new Scoreboard(score).setVisible(true);
            frame.dispose();
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }
}
