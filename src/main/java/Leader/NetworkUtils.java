/**
  File: NetworkUtils.java
  Author: Amy Ma
  Description: Network utilities class for sending and receiving JSONObjects between nodes
*/

package Leader;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONObject;
import org.json.JSONTokener;

public class NetworkUtils {

  public static JSONObject error(String error) {
    JSONObject ret = new JSONObject();
    ret.put("error", error);
    return ret;
  }

  /**
   * Performs a request on a remote node and waits for a reply which it rebuilds
   * into a message
   * 
   * @param message to send to remote node
   * @return the reply message it read back
   */
  public static JSONObject send(String host, int port, JSONObject message) {
    Socket socket = null;
    JSONObject root;
    try {
      // open socket
      socket = new Socket(host, port);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      // write message
      out.println(message.toString());
      // expect message in reply
      String line = in.readLine();
      JSONTokener tokener = new JSONTokener(line);
      root = new JSONObject(tokener);

      // cleanup
      in.close();
      out.close();
      socket.close();

      // give back reply
      return root;
    } catch (SocketException | EOFException e) {
      // client disconnect
      // e.printStackTrace();
      root = error("Unable to connect, please check servers and try again");
      return root;
    } catch (IOException e) {
      root = error("Bad connection inputs");
    } finally {
      if (socket != null)
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
    return root;
  }

  public static JSONObject read(Socket conn) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line = in.readLine();
    JSONTokener tokener = new JSONTokener(line);
    JSONObject root = new JSONObject(tokener);
    return root;
  }

  public static void respond(Socket conn, JSONObject message) throws IOException {
    PrintWriter out = new PrintWriter(conn.getOutputStream(), true);
    out.println(message.toString());
  }
}
