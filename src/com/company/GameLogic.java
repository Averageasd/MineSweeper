package com.company;

import java.awt.*;
import java.util.Random;

/**
 * Logic for the game.
 */
public class GameLogic {

    private final Node[][] nodes;
    protected int flagCounter;
    protected boolean isGameStart;
    protected boolean isMineClicked;
    private int minesExplored;
    private int nodesOpened;

    public GameLogic(){
        nodes = new Node[Constant.ROW.getNum()][Constant.COL.getNum()];
        flagCounter = 0;
        minesExplored = 0;
        nodesOpened = 0;
        isGameStart = false;
        isMineClicked = false;
        initializeBoard();
    }

    /**
     * initialize the game board.
     */
    private void initializeBoard(){
        for (int i = 0; i<nodes.length;i++){
            for (int j = 0; j<nodes.length;j++){

                // each cell initially has a color of black.
                nodes[i][j] = new Node();
                nodes[i][j].x = j;
                nodes[i][j].y = i;
                nodes[i][j].cellSize = Constant.UNIT_SIZE.getNum();
                nodes[i][j].reveal = false;
                nodes[i][j].isMine = false;
                nodes[i][j].setFlag = false;
                nodes[i][j].color = Color.BLACK;
            }
        }

        // call methods to generate random mines and calculate number of surrounding mines for each cell.
        generateMines();
        generateNumForCells();
    }

    /**
     * generate 10 mines that are randomly located on the board.
     */
    private void generateMines(){
        final int limit = 10;

        int mines = 0;
        int randX;
        int randY;

        Random random = new Random();
        while (mines<limit){
            randX = random.nextInt(nodes.length);
            randY = random.nextInt(nodes.length);
            Node node = nodes[randX][randY];
            if (!node.isMine){
                node.isMine = true;
                nodes[randX][randY] = node;
                mines++;
            }
        }
    }


    /**
     * calculate number of mines for each cell.
     */
    private void generateNumForCells(){
        for (int i = 0; i<nodes.length;i++){
            for (int j = 0;j<nodes.length;j++){
                if (!nodes[i][j].isMine) {
                    nodes[i][j].numMines = getNumMines(i, j);
                }
            }
        }
    }

    /**
     * helper method that calculate number of mines for each cell.
     * @param i row of cell
     * @param j col of cell
     *
     * @return number of surrounding mines for each cell
     */
    private int getNumMines(int i, int j) {
        final int[][] dirs = new int[][]{{0,-1},{-1,-1},{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1}};
        int surroundMines = 0;
        for (int[] dir: dirs){
            int updatedX = dir[0] + i;
            int updatedY = dir[1] + j;
            if (!isOutOfBound(updatedX,updatedY)){
                Node node = nodes[updatedX][updatedY];
                if (node.isMine){
                    surroundMines++;
                }
            }
        }
        return surroundMines;
    }

    /**
     * check if row and col positions are still in bound.
     *
     * @param i row position
     * @param j col position
     *
     * @return true if col and row positions are out of bound.
     */
    private boolean isOutOfBound(int i, int j){
        return (i>=nodes.length || i<0 || j>=nodes[0].length || j<0);
    }

    /**
     * check if there is a flag set at (i,j)
     *
     * @param i row position of cell
     * @param j col position of cell
     *
     * @return true if there is a flag set at (i,j)
     */
    public boolean isFlagSet(int i, int j){
        return nodes[i][j].setFlag;
    }

    /**
     * check if the node at (i,j) is revealed
     *
     * @param i row position of cell
     * @param j col position of cell
     *
     * @return true if node at (i,j) is revealed
     */
    public boolean isNodeOpen(int i, int j){
        return nodes[i][j].reveal;
    }

    /**
     * recursively explores surrounding cells of a cell at position (i,j)
     *
     * @param i row position of cell
     * @param j col position of cell
     */
    public void explore(int i, int j){

        // base cases. if one of these base cases is reached, stop calling
        // recursive function.

        // out of bound.
        if (isOutOfBound(i,j)){
            return;
        }

        // node is already revealed.
        if (nodes[i][j].reveal){
            return;
        }

        // node is mine.
        if (nodes[i][j].isMine){
            nodes[i][j].reveal = true;
            nodes[i][j].color = Color.RED;
            isMineClicked = true;
            return;
        }

        // node has at least one surrounding mines.
        if (nodes[i][j].numMines>0){
            nodesOpened++;
            nodes[i][j].color = Color.WHITE;
            if (nodes[i][j].setFlag) {
                nodes[i][j].setFlag = false;
                flagCounter--;
            }
            nodes[i][j].reveal = true;
            return;
        }

        // open node. set color of node to white.
        nodes[i][j].color = Color.WHITE;

        // if at this position there is a flag, remove that flag and decrement
        // the number of flags set.
        if (nodes[i][j].setFlag) {
            nodes[i][j].setFlag = false;
            flagCounter--;
        }
        nodes[i][j].reveal = true;
        nodesOpened++;

        // explore all neighboring cells of current cell.
        explore(i,j-1);
        explore(i-1,j-1);
        explore(i-1,j);
        explore(i-1,j+1);
        explore(i,j+1);
        explore(i+1,j+1);
        explore(i+1,j);
        explore(i+1,j-1);
    }

    /**
     * handle logic for setting flags.
     *
     * @param i row position of cell
     * @param j col position fo cell
     */
    public void setFlag(int i, int j) {

        // if there is a flag already, remove it.
        if (isFlagSet(i,j)){
            nodes[i][j].setFlag = false;
            flagCounter-=1;
            return;
        }

        // number of flags cannot exceed 10.
        if (flagCounter == 10){
            return;
        }
        nodes[i][j].setFlag = true;
        if (nodes[i][j].isMine){
            minesExplored++;
        }
        flagCounter+=1;
    }

    /**
     * draw UI of game.
     * @param graphics2D invoke various methods on object of type Graphics2D to draw rectangles and string.s
     */
    public void draw(Graphics2D graphics2D){
        for (int i = 0; i<nodes.length;i++){
            for (int j = 0; j<nodes[i].length;j++){
                Node node = nodes[i][j];
                graphics2D.setColor(node.color);
                graphics2D.fillRect(node.x*node.cellSize,node.y*node.cellSize,node.cellSize,node.cellSize);
                if (node.reveal && !node.isMine && node.numMines>0){
                    graphics2D.setColor(Color.BLACK);
                    graphics2D.drawString(String.valueOf(node.numMines),node.x*node.cellSize+node.cellSize/2,
                            node.y*node.cellSize + node.cellSize/2);
                }

                if (node.setFlag){
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.drawString("F",node.x*node.cellSize+node.cellSize/2,
                            node.y*node.cellSize + node.cellSize/2);
                }
            }
        }

        // if game has not started or already overs(user wins), display this string.
        if (!isGameStart && !isMineClicked){
            graphics2D.setColor(Color.BLUE);
            graphics2D.drawString("PRESS S TO START GAME",70,250);
        }

        // user clicks on a mine.
        if (isMineClicked){
            graphics2D.setColor(Color.BLUE);
            graphics2D.drawString("YOU STEP ON MINE.",70,250);
        }

        // draw vertical lines.
        for (int i = 0; i<nodes.length;i++){
            graphics2D.setColor(Color.LIGHT_GRAY);
            graphics2D.setStroke(new BasicStroke(2));
            graphics2D.drawLine(i*Constant.UNIT_SIZE.getNum(),0,i*Constant.UNIT_SIZE.getNum(),Constant.HEIGHT.getNum());
        }

        // draw horizontal lines.
        for (int i = 0; i<nodes.length;i++){
            graphics2D.setColor(Color.LIGHT_GRAY);
            graphics2D.setStroke(new BasicStroke(2));
            graphics2D.drawLine(0,i * Constant.UNIT_SIZE.getNum(),Constant.WIDTH.getNum(), i*Constant.UNIT_SIZE.getNum());
        }
    }


    /**
     * check if game is done. Game is done when all 90 non-mine cells are explored or
     * user has set flag on 10 mines.
     *
     * @return true if game is over
     */
    public boolean isGameDone(){
        return minesExplored == 10 || nodesOpened == 90;
    }

    /**
     * check if user clicks on a mine.
     * @return true if user clicks on a mine
     */
    public boolean isMineClicked(){
        return isMineClicked;
    }

    /**
     * reset when game is over.
     */
    public void reset(){
        initializeBoard();
        flagCounter = 0;
        minesExplored = 0;
        nodesOpened = 0;
        isMineClicked = false;
    }
}
