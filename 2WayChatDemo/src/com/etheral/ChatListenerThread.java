package com.etheral;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatListenerThread extends Thread implements IHasPort{

    public static final String colour = Colours.ANSI_BLUE;
    public static final int PORT_NOT_BINDED = -1;

    private int chatListenerPort = 1000;
    private boolean bound = false;
    private String ownerName;
    private ServerSocket serverSocket;

    public ChatListenerThread(String receiverName) {
        this.ownerName = receiverName;
    }

    @Override
    public void run() {
        Socket inputSocket = null;
        DataInputStream inputStream = null;

        try {

            //find a free port for the socket
            while (!bound && chatListenerPort <= 9999) {
                try {
                    serverSocket = new ServerSocket(chatListenerPort);
                    System.out.println(colour +"chat listener: socket bound successfully");
                    bound = true;
                } catch (BindException e) {
                    System.out.println(colour +"chat listener: trying to bind again: " +  e.getMessage());
                    chatListenerPort++;
                }
            }

            while (true) {

                inputSocket = serverSocket.accept();
                System.out.println(colour + "chat listener: listener connected");
                inputStream = new DataInputStream(inputSocket.getInputStream());

                //print all the data
                String jsonStringData = null;
                while(!(jsonStringData = inputStream.readUTF()).equals(Consts.EOF)) {
                    JSONObject jsonData = new JSONObject(jsonStringData);
                    System.out.println(colour + jsonData.getJSONObject(Consts.METADATA).getString(Consts.PERSON_FROM) +
                    ": " + jsonData.getString(Consts.MESSAGE));
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {

            if (inputSocket != null) {
                try {
                    inputSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public int getPort() {
        if(bound)
            return chatListenerPort;

        return PORT_NOT_BINDED;
    }

    public String getPerson() {
        return ownerName;
    }

}
