package org.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class PlayerFrame extends Application {

    private int width, height;
    private boolean up, down, left, right;

    private PlayerSprite me;
    private PlayerSprite enemy;

    private Rectangle square;

    private Socket socket;
    private int playerID;

    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private String hostIpAdress = "localhost";


    public PlayerFrame() {
        width = 640;  // Default width
        height = 480;
    }

    public PlayerFrame(int w, int h) {
        width = w;
        height = h;
    }

    @Override
    public void start(Stage stage) throws Exception {
        createSprites();
        Pane root = new Pane();
        square = me.getR();
        root.getChildren().add(square);

        Scene scene = new Scene(root, width, height);
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));

        stage.setTitle("Player #" + playerID);
        stage.setScene(scene);
        stage.show();

    }

    private void createSprites() {
        if(playerID == 1) {
            me = new PlayerSprite(100, 400, 50, Color.RED, null);
            me.createRectangle(50, Color.RED);
        } else {
            me = new PlayerSprite(400, 400, 50, Color.BLUE, null);
            me.createRectangle(50, Color.BLUE);
        }
    }

    private void handleKeyPress(javafx.scene.input.KeyCode code) {
        switch (code) {
            case UP:
                square.setY(square.getY() - 10);
                break;
            case DOWN:
                square.setY(square.getY() + 10);
                break;
            case LEFT:
                square.setX(square.getX() - 10);
                break;
            case RIGHT:
                square.setX(square.getX() + 10);
                break;
        }
    }

    private void connectToServer() {
        try {
            socket = new Socket(hostIpAdress, 45371);                //Connect to the Server
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            playerID = in.readInt();
            System.out.println("You are Player #" + playerID);
            if(playerID == 1) {
                System.out.println("Waiting for Player #2 to connect...");
            }
            rfsRunnable = new ReadFromServer(in);
            wtsRunnable = new WriteToServer(out);
            rfsRunnable.waitForStartMsg();
        }catch(IOException ex){
            System.out.println("IOEXCeption from connectServer()");
        }
    }


    private class ReadFromServer implements Runnable {
        private DataInputStream dataIn;

        public ReadFromServer(DataInputStream in){
            dataIn = in;
            System.out.println("RFS Runnable created");
        }

        public void run() {
            try {
                while(true) {
                    if(enemy != null) {
                        enemy.setX(dataIn.readDouble());
                        enemy.setY(dataIn.readDouble());
                    }
                }
            } catch(IOException ex) {
                System.out.println("IOException RFS run()");
            }
        }

        public void waitForStartMsg() {
            try {
                String startMsg = dataIn.readUTF();
                System.out.println("Message from server: "+ startMsg);
                Thread readThread = new Thread(rfsRunnable);
                Thread writeThread = new Thread(wtsRunnable);
                readThread.start();
                writeThread.start();

            } catch(IOException ex) {
                System.out.println("IOException from waitForStartMsg()");
            }
        }
    }

    private class WriteToServer implements Runnable {
        private DataOutputStream dataOut;

        public WriteToServer(DataOutputStream out){
            dataOut = out;
            System.out.println("WTS Runnable created");
        }

        public void run() {
            try {

                while (true){
                    if(me != null) {
                        dataOut.writeDouble(me.getX());
                        dataOut.writeDouble(me.getY());
                        dataOut.flush();
                    }
                    try {
                        Thread.sleep(25);
                    }catch (InterruptedException ex){
                        System.out.println("InterruptedException from WTS run()");
                    }
                }

            }catch (IOException ex) {
                System.out.println("IoException from WTS run(");
            }
        }

    }

    public static void main(String[] args) {
        PlayerFrame pf = new PlayerFrame(640, 480);
        pf.connectToServer();
        launch(args);
    }
}
