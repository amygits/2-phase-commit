/*
File: Client.java
Author: Amy Ma
Description: Client class that communcicates with the TC, has 3 options for the user: Add tips, List tips, Quit client
*/

package Leader;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ResponseCache;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.Scanner;

import Leader.NetworkUtils;

public class Client {

    private static BufferedReader stdin;

    public static JSONObject addTip() {
        JSONObject request = new JSONObject();
        String strToSend = null;
        boolean valid = true;
        request.put("method", "add");
        do {
            valid = true;
            try {
                System.out.println("Please enter the tip you'd like to submit: ");
                strToSend = stdin.readLine();
                if (strToSend.toLowerCase().equals("quit") || strToSend.toLowerCase().equals("exit")) {
                    System.exit(0);
                }
            } catch (IOException e) {
                System.out.println("Invalid, please try again.");
                valid = false;
            }
        } while (!valid);
        request.put("data", strToSend);
        return request;
    }

    public static JSONObject listTip() {
        JSONObject request = new JSONObject();
        request.put("method", "list");
        return request;
    }

    public static JSONObject exitClient() {
        JSONObject request = new JSONObject();
        request.put("method", "quit");
        return request;
    }

    public static void main(String[] args) throws IOException {
        String host;
        int port;
        Socket sock;

        stdin = new BufferedReader(new InputStreamReader(System.in));

        try {
            if (args.length != 2) {
                System.out.println("gradle Client --args=8000 localhost");
                System.exit(0);
            }
            host = args[1];
            port = -1;
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.out.println("[Port] must be an integer");
                System.exit(2);
            }
            int choice;
            do {
                System.out.println("Welcome to 2TC");
                System.out.println("Please enter a valid option (1-2).  0 to disconnect the client");
                System.out.println("1. Add a tip - Adds a tip then displays it");
                System.out.println("2. List tips - Lists tip list");
                System.out.println("0. Quit");
                System.out.println();
                Scanner input = new Scanner(System.in);
                choice = input.nextInt();

                JSONObject response = null;
                switch (choice) {
                case (1):
                    response = NetworkUtils.send(host, port, addTip());
                    if (response.has("error")) {
                        System.out.println("Error from server: " + response.getString("error"));
                    }
                    if (response.has("complete")) {
                        System.out.println("The response from the server: ");
                        // System.out.println("method: " + response.getString("method"));
                        System.out.println("Added data: " + response.getString("data"));
                    } else {
                        // System.out.println("method: " + response.getString("method"));
                        System.out.println("something went wrong.");
                    }
                    System.out.println();
                    break;
                case (2):
                    response = NetworkUtils.send(host, port, listTip());
                    if (response.has("error")) {
                        System.out.println("Error from server: " + response.getString("error"));
                    }
                    if (response.has("complete")) {
                        System.out.println("The response from the server: ");
                        // System.out.println("method: " + response.getString("method"));
                        System.out.println("List data: " + response.getJSONArray("data"));
                    } else {
                        // System.out.println("method: " + response.getString("method"));
                        System.out.println("something went wrong.");
                    }
                    System.out.println();
                    break;
                case (0):
                    System.exit(0);
                    break;
                default:
                    System.out.println("Please select a valid option (0-2).");
                    break;
                }

            } while (true);

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Ran into an issue connecting to server, please check servers and try again.");
        }
    }

}