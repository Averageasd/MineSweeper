package com.company;

/**
 * enum where game-board width, height, cell's size are defined.
 */
public enum Constant {
    ROW(10),
    COL(10),
    UNIT_SIZE(50),
    WIDTH(COL.getNum() * UNIT_SIZE.getNum()),
    HEIGHT(ROW.getNum() * UNIT_SIZE.getNum());


    private final int num;
    Constant(int num){
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
