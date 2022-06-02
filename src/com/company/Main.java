package com.company;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
        JFrame jFrame = new JFrame();
        GameLogic gameLogic = new GameLogic();
        GameUI gameUI = new GameUI(gameLogic);
        jFrame.add(gameUI);
        jFrame.setResizable(false);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.pack();
    }
}
