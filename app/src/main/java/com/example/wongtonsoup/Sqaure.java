package com.example.wongtonsoup;

public class Sqaure extends Shape{
    private int side;
    public Sqaure(int x, int y) {
        super(x, y);
    }

    public Sqaure(int x, int y, int side) {
        super(x, y);
        this.side = side;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }
}
