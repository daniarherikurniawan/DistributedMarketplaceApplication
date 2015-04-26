package com.example.kevinhuang.spf420client;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import android.os.AsyncTask;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 *
 * @author daniar heri
 */
public class ClientServerConnector{

    private Socket socket;
    private JSONObject jsonMsg ;
    private JSONObject jsonResponse ;
    private String response;
    String ip;
    int port;
    public ClientServerConnector(String prm_socket, int prm_port){
        ip =  prm_socket;
        port = prm_port;
        jsonMsg = new JSONObject();
    }

    private String getResponse() throws IOException{
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    socket.setSoTimeout(0);
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    OutputStream outToServer = socket.getOutputStream();
                    DataOutputStream out =
                            new DataOutputStream(outToServer);
                    out.write(jsonMsg.toString().getBytes("UTF-8"));
                    out.flush();
                    InputStream inFromServer = socket.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inFromServer, "UTF8"));
                    response = in.readLine();
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    response = "UnknownHostException: " + e.toString();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    response = "IOException: " + e.toString();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    response = "Exception: " + e.toString();
                } finally{
                    if(socket != null){
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        Thread thr = new Thread(r);
        thr.start();
        try {
            thr.join();
        }
        catch (Exception e){

        }
        finally {

        }

        return response;
    }

    public String actionJoin(String IP, Integer Port) throws IOException{
        jsonMsg.put("ip", IP);
        jsonMsg.put("port", Port);
        return getResponse();
    }

    public String actionSignup(String username, String password) throws IOException{
        jsonMsg.put("method","signup");
        jsonMsg.put("username",username);
        jsonMsg.put("password", password);
        return getResponse();
    }

    public String actionLogin(String username,  String password) throws IOException{
        jsonMsg.put("method","login");
        jsonMsg.put("username", username);
        jsonMsg.put("password", password);
        return getResponse();
    }

    public String actionInventory(String token) throws IOException{
        jsonMsg.put("method","inventory");
        jsonMsg.put("token", token);
        return getResponse();
    }

    public String actionMixItem(String token, Integer item1, Integer item2) throws IOException{
        jsonMsg.put("method","mixitem");
        jsonMsg.put("token", token);
        jsonMsg.put("item1", item1);
        jsonMsg.put("item2", item2);
        return getResponse();
    }

    public String actionMap(String token) throws IOException{
        jsonMsg.put("method","map");
        jsonMsg.put("token", token);
        return getResponse();
    }

    public String actionMove(String token, Integer x,  Integer y) throws IOException{
        jsonMsg.put("method","move");
        jsonMsg.put("token", token);
        jsonMsg.put("x", x);
        jsonMsg.put("y", y);
        return getResponse();
    }

    public String actionField(String token) throws IOException{
        jsonMsg.put("method","field");
        jsonMsg.put("token", token);
        return getResponse();
    }

    public String actionOffer(String token, Integer offered_item, Integer n1, Integer demanded_item, Integer n2) throws IOException{
        jsonMsg.put("method","offer");
        jsonMsg.put("token", token);
        jsonMsg.put("offered_item", offered_item);
        jsonMsg.put("n1", n1);
        jsonMsg.put("demanded_item", demanded_item);
        jsonMsg.put("n2", n2);
        return getResponse();
    }

    public String actionTradebox(String token) throws IOException{
        jsonMsg.put("method","tradebox");
        jsonMsg.put("token", token);
        return getResponse();
    }

    public String actionSendFind(String token, Integer item) throws IOException{
        jsonMsg.put("method","sendfind");
        jsonMsg.put("token", token);
        jsonMsg.put("item", item);
        return getResponse();
    }

    public String actionFindOffer(Integer item) throws IOException{
        jsonMsg.put("method","findoffer");
        jsonMsg.put("item", item);
        return getResponse();
    }

    public String actionSendAccept(String token, String offer_token) throws IOException{
        jsonMsg.put("method","sendaccept");
        jsonMsg.put("token", token);
        jsonMsg.put("offer_token", offer_token);
        return getResponse();
    }

    public String actionAccept(String offer_token) throws IOException{
        jsonMsg.put("method","accept");
        jsonMsg.put("offer_token", offer_token);
        return getResponse();
    }

    public String actionFetchItem(String token, String offer_token) throws IOException{
        jsonMsg.put("method","fetchitem");
        jsonMsg.put("token", token);
        jsonMsg.put("offer_token", offer_token);
        return getResponse();
    }

    public String actionCancelOffer(String token, String offer_token) throws IOException{
        jsonMsg.put("method","canceloffer");
        jsonMsg.put("token", token);
        jsonMsg.put("offer_token", offer_token);
        //System.out.println(jsonMsg);
        return getResponse();
    }

    public String actionJSON(JSONObject abs) throws Exception{
        jsonMsg=abs;
        return getResponse();
    }

    public static void main(String[] args) throws IOException {
        ClientServerConnector CSConnector = new ClientServerConnector("127.0.0.1",8001);
        System.out.println("Response = "+CSConnector.actionSignup("daniar","1"));
        //System.out.println("Response = "+CSConnector.actionLogin("daniar","1"));
        //Response = {"status": "ok", "x": 0, "time": 1430024591, "token": "4c9e1be14dc0e31bbe91761f3b336687", "y": 0}
        //System.out.println("Response = "+CSConnector.actionInventory("4c9e1be14dc0e31bbe91761f3b336687"));
        //System.out.println("Response = "+CSConnector.actionMap("4c9e1be14dc0e31bbe91761f3b336687"));
        //Response = {"status": "ok", "height": 4, "width": 4, "name": "Vapor Island"}
        //System.out.println("Response = "+CSConnector.actionMove("4c9e1be14dc0e31bbe91761f3b336687", 0, 1));
        //System.out.println("Response = "+CSConnector.actionField("4c9e1be14dc0e31bbe91761f3b336687"));
        //System.out.println("Response = "+CSConnector.actionCancelOffer("0495835803958035324242","1"));

    }
}
