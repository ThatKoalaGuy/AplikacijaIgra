/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.domenkoder.aplikacijaigra;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import com.domenkoder.aplikacijaigra.GameManager;  // DODAJ TO!

/**
 *
 * @author domen
 */
public class Rock extends JLabel {

    int speed = 2;
    private boolean zigzag = false;
    private double zigzagTime = 0;

    public Rock(int x) {
        setIcon(new ImageIcon(getClass().getResource("images/rock.png")));
        setBounds(x, -80, 80, 80);

        //zazna level 2+!
        try {
            if (GameManager.currentLevel >= 2) {
                zigzag = true;
            }
        } catch (Exception e) {
            // Ni problema če GameManager.currentLevel ne obstaja
        }
    }

    public void move() {
        if (zigzag) {
            //Cikcak efekt!
            zigzagTime += 0.05;
            int zigzagOffset = (int) (Math.sin(zigzagTime * 2) * 6);
            setLocation(getX() + zigzagOffset, getY() + speed);
        } else {
            setLocation(getX(), getY() + speed);
        }
    }
}
