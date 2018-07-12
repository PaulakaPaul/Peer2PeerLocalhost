package com.etheral;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerThread extends Thread {

    public static final int SERVER_PORT = 1432;
    public static final String colour = Colours.ANSI_RED;

    private ServerSocket serverSocket;
    private Map<String, InetAddress> addresses = new HashMap<>();
    private Map<String, Integer> ports = new HashMap<>();
    private Map<String, List<String>> cachedMessages = new HashMap<>();

    @Override
    public void run() {

        Socket inputSocket = null;
        Socket outputSocket = null;

        DataInputStream inputStream = null;
        DataOutputStream dataOutputStream = null;


        try {
            serverSocket = new ServerSocket(SERVER_PORT);

            while (true) {

                inputSocket = serverSocket.accept();
                System.out.println(colour + "server: client connected");
                System.out.println(colour + "server: Known people with their ips");
                for (String people : addresses.keySet())
                    System.out.println(colour + people + ": " + addresses.get(people) + ":" + ports.get(people));

                inputStream = new DataInputStream(inputSocket.getInputStream());

                String jsonStringInputData = inputStream.readUTF();
                JSONObject jsonInputData = new JSONObject(jsonStringInputData);

                //cache sender ip
                addresses.put(jsonInputData.getJSONObject(Consts.METADATA).getString(Consts.PERSON_FROM),
                        InetAddress.getByName(jsonInputData.getJSONObject(Consts.METADATA).getString(Consts.IP)));

                //cache port
                ports.put(jsonInputData.getJSONObject(Consts.METADATA).getString(Consts.PERSON_FROM),
                        jsonInputData.getJSONObject(Consts.METADATA).getInt(Consts.PORT_CHAT_FROM));

                String receiver = jsonInputData.getJSONObject(Consts.METADATA).getString(Consts.PERSON_TO);
               // if (!receiver.equals(Main.yourName)) {
                    if (addresses.containsKey(receiver) && ports.containsKey(receiver) && ports.get(receiver) != ChatListenerThread.PORT_NOT_BINDED) {
                        System.out.println(colour + "server: sending message");
                        outputSocket = new Socket(addresses.get(receiver), ports.get(receiver));
                        dataOutputStream = new DataOutputStream(
                                outputSocket.getOutputStream());

                        //now that the path is created send the data
                        //first try to send the cached messages
                        if (cachedMessages.containsKey(receiver)) {
                            for (String m : cachedMessages.get(receiver))
                                dataOutputStream.writeUTF(m);

                            cachedMessages.get(receiver).clear();
                        }

                        //send the new data
                        dataOutputStream.writeUTF(jsonStringInputData);
                        dataOutputStream.writeUTF(Consts.EOF);

                    } else { // cache the message
                        System.out.println(colour + "server: caching message");
                        if (cachedMessages.containsKey(receiver))
                            cachedMessages.get(receiver).add(jsonStringInputData);
                        else {
                            ArrayList<String> messages = new ArrayList<String>();
                            messages.add(jsonStringInputData);
                            cachedMessages.put(receiver, messages);
                        }
                    }
             //   } else {
              //      System.out.println(ChatListenerThread.colour + jsonInputData.getJSONObject(Consts.METADATA).getString(Consts.PERSON_FROM)
              //              + ": " + jsonInputData.getString(Consts.MESSAGE));
              //  }
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

            if (outputSocket != null) {
                try {
                    outputSocket.close();
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

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void AddAddress() {
        try {
            addresses.put("A", InetAddress.getByName("127.0.0.1"));
            addresses.put("B", InetAddress.getByName("127.0.0.1"));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
