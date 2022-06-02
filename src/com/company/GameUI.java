package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * handle user's inputs from mouse clicks or keyboard.
 * Displays UI of the game.
 */
public class GameUI extends JPanel implements Runnable, MouseListener, KeyListener {

    GameLogic gameLogic;
    private int posX;
    private int posY;
    private boolean mousePressed;
    private boolean exploreCell;
    private boolean setFlag;

    /**
     * Initialize everything.
     *
     * @param gameLogic contains the data and logic for the game.
     */
    public GameUI(GameLogic gameLogic){
        this.gameLogic = gameLogic;
        posX = 0;
        posY = 0;
        mousePressed = false;
        exploreCell = false;
        setFlag = false;
        setPreferredSize(new Dimension(Constant.WIDTH.getNum(),Constant.HEIGHT.getNum()));
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
        setFont(new Font("SansSerif",Font.BOLD,30));
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Game loop. UI is updated here.
     */
    @Override
    public void run() {

        // draw interval. every 1B nanoseconds, we draw 60 frames.
        // It means that it takes aprox 16M ns to draw 1 frame.
        double drawInterval = 1000000000.0/60.0;

        // the time we start drawing.
        long startTime = System.nanoTime();

        // difference between start time and current time.
        double deltaTime = 0;

        // game loop.
        while (true) {
            long curTime = System.nanoTime();

            // add up the difference between beginning time and current time.
            deltaTime += (curTime - startTime);

            // update beginning time. assign the current time to it.
            startTime = curTime;

            // once the difference time is greater than or equal to 16M ns, we draw a frame.
            if (deltaTime >= drawInterval) {
                repaint();
                if (gameLogic.isGameStart) {
                    update();
                }

                // subtract drawInterval because we don't want to draw more than 60 frames per 1 sec.
                deltaTime -= drawInterval;
            }

        }
    }

    /**
     * handle user's key inputs.
     */
    private void update(){
        handleKeyPressed();

    }

    /**
     * contains logic for handing user's key inputs.
     */
    private void handleKeyPressed(){

        // only if the game runs normally, we update the game UI and states.
        if (gameLogic.isGameStart){
            if (gameLogic.isGameDone() || gameLogic.isMineClicked()) {
                gameLogic.reset();
            }
            handleMousePressed();
        }

        // once the user finishes the game or clicks on a mine, start a new game.
        if (gameLogic.isGameDone() || gameLogic.isMineClicked()){
            gameLogic.isGameStart = false;
        }
    }

    /**
     * logic for handling mouse presses from user.
     */
    private void handleMousePressed(){
        if (!mousePressed){
            return;
        }

        // user left-clicks to explore a cell.
        if (exploreCell) {

            // the cell that user clicks on to explore must not
            // have a flag.
            if (!gameLogic.isFlagSet(posX, posY)) {
                gameLogic.explore(posX, posY);
                mousePressed = false;
                exploreCell = false;
            }
        }

        // user right-clicks a cell to set a flag.
        if (setFlag){
            if (!gameLogic.isNodeOpen(posX,posY)){
                gameLogic.setFlag(posX,posY);
                mousePressed = false;
                setFlag = false;
            }
        }
    }

    // draw the game UI.
    public void paintComponent(Graphics graphics){
        Graphics2D graphics2D = (Graphics2D) graphics;
        gameLogic.draw(graphics2D);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * handles user left and right clicks.
     * the state variables (boolean variables) are updated according
     * to how user performs clicking.
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameLogic.isGameStart){
            return;
        }

        // get positions on screen where user clicks.
        posX = e.getY() / Constant.UNIT_SIZE.getNum();
        posY = e.getX() / Constant.UNIT_SIZE.getNum();
        if (e.getButton() == MouseEvent.BUTTON1) {
            exploreCell = true;
            mousePressed = true;
        }
        else if (e.getButton() == MouseEvent.BUTTON3){
            setFlag = true;
            mousePressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * handle user key presses. User presses 'S' to start/pause/resume the game.
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S){
            if (gameLogic.isGameStart){
                gameLogic.isGameStart = false;
            }
            else{
                gameLogic.isGameStart = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
