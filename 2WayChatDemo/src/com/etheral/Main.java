package com.etheral;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class Main {

    public static final String color = Colours.ANSI_GREEN;

    public static String yourName;
    public static String hisName;

    public static final String SERVER_IP = "192.168.0.10"; //TODO change this

    /**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * READ ME
     * ** 1 ** change SERVER_IP with your own
     * ** 2 ** firstly start the server or it wont work
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     **/

    public static void main(String[] args) {

        try {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

                String input = "";
                do {
                    System.out.println(color + "Start server or connect to server? S/C");
                    input = br.readLine();

                    if (input.toUpperCase().equals("S")) {
                        new ServerThread().start();

                        System.out.println(color + "Enter your name:");
                        yourName = br.readLine();
                        System.out.println(color + "Enter his name:");
                        hisName = br.readLine();

                        System.out.println(color + "Chat listener is starting...");
                        ChatListenerThread chatThread = new ChatListenerThread(yourName);
                        chatThread.start();

                        // send a connection message
                        try {
                            Thread.sleep(200);
                            new Messenger("connected", hisName,  yourName, InetAddress.getByName(SERVER_IP), chatThread).sendMessage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        while (true) {
                            System.out.println(color + "Enter message");
                            input = br.readLine();
                            new Messenger(input, hisName,  yourName, InetAddress.getByName(SERVER_IP), chatThread).sendMessage();
                        }

                    } else if (input.toUpperCase().equals("C")) {

                        System.out.println(color + "Enter your name:");
                        yourName = br.readLine();
                        System.out.println(color + "Enter his name:");
                        hisName = br.readLine();

                        System.out.println(color + "Chat listener is starting...");
                        ChatListenerThread chatThread = new ChatListenerThread(yourName);
                        chatThread.start();

                        // send a connection message
                        try {
                            Thread.sleep(200);
                            new Messenger("connected", hisName,  yourName, InetAddress.getByName(SERVER_IP), chatThread).sendMessage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        while (true) {
                            System.out.println(color + "Enter message");
                            input = br.readLine();
                            new Messenger(input, hisName, yourName, InetAddress.getByName(SERVER_IP), chatThread).sendMessage();
                        }

                    }
                }while (!input.toUpperCase().equals("S") && !input.toUpperCase().equals("C"));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
