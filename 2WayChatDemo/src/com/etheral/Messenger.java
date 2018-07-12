package com.etheral;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Messenger {

    private String message;
    private String personTo;
    private String personFrom;
    private InetAddress serverIpAddress;
    private IHasPort chatListener;

    public Messenger(String message, String personTo, String personFrom, InetAddress serverIpAddress, IHasPort chatListener) {
        this.message = message;
        this.personTo = personTo;
        this.personFrom = personFrom;
        this.serverIpAddress = serverIpAddress;
        this.chatListener = chatListener;
    }

    public void sendMessage() {
        Socket socket = null;
        DataOutputStream dataOutputStream = null;

        try {
            socket = new Socket(serverIpAddress, ServerThread.SERVER_PORT);

            dataOutputStream = new DataOutputStream(
                    socket.getOutputStream());

            //print data
            System.out.println(ChatListenerThread.colour + personFrom + ": " + message);
            //send the data as json
            dataOutputStream.writeUTF(createJson().toString());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            if (socket != null) {
                try {
                    socket.close();
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

    private JSONObject createJson() {
        JSONObject obj = new JSONObject();
        JSONObject metadata = new JSONObject();

        try {
            metadata.put(Consts.IP, InetAddress.getLocalHost().getHostAddress()); // your ip address
            metadata.put(Consts.PERSON_TO, personTo);
            metadata.put(Consts.PERSON_FROM, personFrom);
            metadata.put(Consts.PORT_CHAT_FROM, chatListener.getPort()); // your chat listener port

            obj.put(Consts.METADATA, metadata);
            obj.put(Consts.MESSAGE, message);
        } catch (JSONException | UnknownHostException e) {
            return obj;
        }
        return obj;
    }
}
