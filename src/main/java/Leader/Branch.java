
/*
File: Branch.java
Author: Amy Ma
Description:  A class extended from Node that acts as the Transcaction Coordinator between the nodes and the Client
*/

package Leader;

import org.json.JSONArray;
import org.json.JSONObject;

public class Branch extends Node {
    private int _firstPort;
    private int _secondPort;
    private String _firstHost;
    private String _secondHost;

    public Branch(int port, int first, int second, String firstH, String secondH) {
        super(port);
        _firstPort = first;
        _secondPort = second;
        _firstHost = firstH;
        _secondHost = secondH;
    }

    public JSONObject prepare() {
        JSONObject ret = new JSONObject();
        ret.put("method", "prepare");
        return ret;
    }

    public JSONObject add(String tip) {
        JSONObject response1 = NetworkUtils.send(_firstHost, _firstPort, prepare());
        JSONObject response2 = NetworkUtils.send(_secondHost, _secondPort, prepare());
        JSONObject ret = new JSONObject();

        if (response1.has("error") || response2.has("error")) {
            try {
                System.out.println(response1.getString("error"));
                System.out.println(response2.getString("error"));
            } catch (Exception e) {
            }
            return error("One or more nodes has error");
        }

        if (response1.getBoolean("ready") && response2.getBoolean("ready")) {
            System.out.println("Both nodes ready");
            ret.put("method", "add");
            ret.put("data", tip);
            response1 = NetworkUtils.send(_firstHost, _firstPort, ret);
            response2 = NetworkUtils.send(_secondHost, _secondPort, ret);
            System.out.println("Sent out ADD requests");

            if (response1.has("complete") && response2.has("complete")) {
                ret.put("complete", true);
                System.out.println("Action has been completed by both nodes");
            }
        } else {
            ret = error("One or more nodes not ready");
        }
        return ret;
    }

    public JSONObject list() {
        JSONObject response1 = NetworkUtils.send(_firstHost, _firstPort, prepare());
        JSONObject response2 = NetworkUtils.send(_secondHost, _secondPort, prepare());
        JSONObject ret = new JSONObject();

        if (response1.has("error") || response2.has("error")) {
            return error("One or more nodes has error");
        }

        if (response1.getBoolean("ready") && response2.getBoolean("ready")) {
            System.out.println("Both nodes ready");
            ret.put("method", "list");
            response1 = NetworkUtils.send(_firstHost, _firstPort, ret);
            response2 = NetworkUtils.send(_secondHost, _secondPort, ret);
            System.out.println("Sent out LIST requests");
            if (response1.has("complete") && response2.has("complete")) {
                System.out.println("Action has been completed by both nodes");
                System.out.println("size: " + response1.getInt("size"));
                ret.put("data", response1.getJSONArray("data"));
                ret.put("complete", true);
            }

        } else {
            ret = error("One or more nodes not ready");
        }
        return ret;
    }

    public JSONObject error(String error) {
        JSONObject ret = new JSONObject();
        ret.put("error", error);
        return ret;
    }

    public static void main(String[] args) {
        System.out.println("TC Starting..");
        new Branch(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]), "localhost",
                "localhost").run();

    }

}
