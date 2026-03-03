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
public class Explosion extends JLabel {
    public Explosion(int x, int y) {
        setIcon(new ImageIcon(getClass().getResource(
                "/com/domenkoder/aplikacijaigra/images/explosion.gif")));

        setBounds(x, y, 110, 110);
    }
}
