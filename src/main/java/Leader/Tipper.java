/**
  File: Tipper.java
  Author: Amy Ma
  Description: A class extended from Node acting as one of the servers that the TC communicates with to retrieve information
  
*/


package Leader;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;

public class Tipper extends Node {

    private JSONArray tipList = new JSONArray();
    private boolean isReady;
    private int pt;

    public Tipper(int port) {
        super(port);
        pt = port;
        tipList.put("DEFAULT: Be patient and persistent.");
        tipList.put("DEFAULT: Luck comes from hard work.");
        tipList.put("DEFAULT: Life's good, but it's not fair.");
        System.out.println("Default tips initialized");
        isReady = true;
    }

    public JSONObject ready() {
        JSONObject ret = new JSONObject();
        if (isReady) {
            ret.put("method", "ready");
            ret.put("ready", true);
            System.out.println("Confirmed 'I'm ready' to TC");
        } else {
            // System.out.println("i'm not ready error..");
            ret = error("I'm not ready");
        }
        return ret;
    }

    public synchronized JSONObject add(String tip) {
        isReady = false;
        JSONObject ret = new JSONObject();
        ret.put("method", "add");
        ret.put("data", tip);
        ret.put("fromPort", pt);
        ret.put("complete", true);
        tipList.put(tip);
        isReady = true;
        System.out.println("Added to my database.  'Complete' sent to TC");
        return ret;
    }

    public synchronized JSONObject list() {
        isReady = false;
        System.out.println("List (Commit) received");
        JSONObject ret = new JSONObject();
        ret.put("method", "list");
        ret.put("size", tipList.length());
        ret.put("data", tipList);
        ret.put("complete", true);
        ret.put("fromPort", pt);
        isReady = true;
        System.out.println("'Complete' sent to TC");
        return ret;
    }

    public JSONObject error(String error) {
        JSONObject ret = new JSONObject();
        ret.put("error", error);
        return ret;
    }

    @Override
    public void run() {
        // separated so the finally can clean up the connection
        ServerSocket socket = null;
        try {
            // create the listening socket
            socket = new ServerSocket(pt);
            while (true) { // handle connections indefinitely
                Socket conn = null;
                try {
                    // listen for connection
                    conn = socket.accept();
                    System.out.println("->Received a ping from " + conn.getInetAddress() + " @ " + conn.getLocalPort());

                    // read in a message
                    JSONObject root = NetworkUtils.read(conn);
                    JSONObject ret = error("");

                    if (root.has("method")) {
                        switch (root.getString("method")) {
                        case ("add"):
                            System.out.println("Add (Commit) request received");
                            ret = add(root.getString("data"));
                            break;
                        case ("list"):
                            ret = list();
                            break;
                        case ("prepare"):
                            System.out.println("Prepare request received");
                            ret = ready();
                            break;
                        /*
                         * case("ready"): ret = ready(); break;
                         */
                        }
                    }

                    NetworkUtils.respond(conn, ret);

                    // cleanup
                    conn.close();
                } catch (SocketException | EOFException e) {
                    // expected on timeout
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    // cleanup, just in case
                    if (conn != null)
                        try {
                            conn.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // cleanup, just in case
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void main(String[] args) {
        new Tipper(Integer.valueOf(args[0])).run();
    }
}