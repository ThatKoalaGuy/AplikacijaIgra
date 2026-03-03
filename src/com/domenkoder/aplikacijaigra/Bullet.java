/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.domenkoder.aplikacijaigra;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author domen
 */
public class Bullet extends JLabel {

    public Bullet(int x, int y) {
        setIcon(new ImageIcon(
                getClass().getResource("/com/domenkoder/aplikacijaigra/images/bullet.gif")
        ));
        setBounds(x, y, 20, 40);
    }

    public void move() {
        setLocation(getX(), getY() - 10); // gre gor
    }
}
