package com.company;

import java.awt.*;

/**
 * represents a cell in the game-board.
 */
public class Node {

    protected int x;
    protected int y;
    protected int numMines;
    protected int cellSize;
    protected boolean reveal;
    protected boolean isMine;
    protected boolean setFlag;
    protected Color color;
}
