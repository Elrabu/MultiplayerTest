package org.game;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class PlayerSprite extends Application {

    private double x, y;
    private int s, id;
    private Color c;
    private Rectangle r;


    public PlayerSprite(double a,double b, int size, Color color, Rectangle rectangle) {
        x = a;
        y = b;
        s = size;
        c = color;
        r = rectangle;

    }

    public void createRectangle(int s, Color c) {
        r = new Rectangle(s,s, c);
    }

    public Rectangle getR() {
        return r;
    }

    public void setX(double n) {

        x = n;
    }

    public void setY(double n){
        y = n;
    }

    public double getX() {

        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
