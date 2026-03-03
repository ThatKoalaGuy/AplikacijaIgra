package com.domenkoder.aplikacijaigra;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.Timer;

public class FadingLabel extends JLabel {

    private float alpha = 1.0f;
    private Timer flashTimer;

    public void setAlpha(float alpha) {
        this.alpha = Math.max(0f, Math.min(1f, alpha));
        repaint();
    }

    public void startFlash() {
        if (flashTimer != null && flashTimer.isRunning()) {
            flashTimer.stop();
        }

        final int[] count = {0};
        final int maxFlashes = 6;

        flashTimer = new Timer(80, e -> {
            count[0]++;
            if (count[0] % 2 == 1) {
                setAlpha(0.2f);
            } else {
                setAlpha(1.0f);
            }

            if (count[0] >= maxFlashes) {
                flashTimer.stop();
                setAlpha(1.0f);
            }
        });

        flashTimer.setRepeats(true);
        flashTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        super.paintComponent(g2);
        g2.dispose();
    }
}
