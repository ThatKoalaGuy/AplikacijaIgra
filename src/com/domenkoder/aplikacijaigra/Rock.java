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
public class Rock extends JLabel{
    int speed = 2;

    public Rock(int x) {
        setIcon(new ImageIcon(getClass().getResource("images/rock.png")));
        setBounds(x, -80, 80, 80);
    }

    public void move() {
        setLocation(getX(), getY() + speed);
    }
}
