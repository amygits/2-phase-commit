/**
  File: Node.java
  Author: Amy Ma
  Description: A superclass of Node that starts a Server and has abstract add/list methods
*/

package Leader;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONObject;

public abstract class Node implements Runnable {
  private int _port;

  public Node(int port) {
    _port = port;
  }

  public abstract JSONObject add(String tip);

  public abstract JSONObject list();

  public abstract JSONObject error(String error);

  @Override
  public void run() {
    // separated so the finally can clean up the connection
    ServerSocket socket = null;
    try {
      // create the listening socket
      socket = new ServerSocket(_port);
      while (true) { // handle connections indefinitely
        Socket conn = null;
        try {
          // listen for connection
          conn = socket.accept();
          System.out.println("Received ping from " + conn.getInetAddress() + " @ " + conn.getLocalPort());

          // read in a message
          JSONObject root = NetworkUtils.read(conn);
          JSONObject ret = error("");

          if (root.has("method")) {
            switch (root.getString("method")) {
            case ("add"):
              ret = add(root.getString("data"));
              break;
            case ("list"):
              ret = list();
              break;
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
}
