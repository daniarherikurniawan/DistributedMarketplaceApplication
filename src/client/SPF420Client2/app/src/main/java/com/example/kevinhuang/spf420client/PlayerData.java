package com.example.kevinhuang.spf420client;


import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Kevin Huang on 4/25/2015.
 */
public class PlayerData implements GameSetting {
    private static String UserName;
    private static SimpleDateFormat MovingToNextPileTime;
    private static int CurrentPositionX;
    private static int CurrentPositionY;
    private static int MAP_ROWS;
    private static int MAP_COLS;
    private static String UserToken;
    private static int MovingTimeUTC;
    private static JSONObject jsonobjectmap;
    private static JSONObject jsonobjectoffers;
    private static JSONObject jsonobjecttrades;
    private static JSONObject jsonobjectamountinventory;
    private static int[] ItemsAmount;
    private static JSONArray jsonarrayamountinvetory;
    private static int[][] ItemOnMaps;
    private static JSONArray jsonarraymaps;
    private static JSONArray jsonarrayoffers;
    private static JSONArray jsonarraytrades;
    private static String name;
    private static String ip;
    private static int port;

    public void setData() throws JSONException {
        CurrentPositionX = 0;
        CurrentPositionY = 0;
        MovingToNextPileTime = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
        MovingToNextPileTime.setTimeZone(timeZone);
        MovingToNextPileTime.format(0);
        UserName = "None";
        UserToken = "";
        MAP_COLS = 4;
        MAP_ROWS = 5;
        MovingTimeUTC = 1427730710;
        ItemsAmount = new int[TotalItem];
        ItemOnMaps = new int[MAP_ROWS][MAP_COLS];
        /*CreateJSONObjectMapdummy();
        createjsonobjectoffersdummy();
        createjsonobjecttradesdummy();
        createjsonobjectinventorysdummy();
        try {
            setJSONArrayamountinventory();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        CreateJSONArraymaps();
        try {
            CreateItemonMaps();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            SetItemsAmount(true);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            setJSONArrayoffers();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            setJSONArraytrades();
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

    }

    private static void CreateJSONObjectMapdummy() throws JSONException {
        jsonobjectmap = new JSONObject();
        JSONArray mapjson = new JSONArray();
        int itemid = 0;
        for(int row =0;row<MAP_ROWS;row++){
            JSONArray maprowjson = new JSONArray();
            for(int col=0;col<MAP_COLS;col++){
                maprowjson.add(itemid);
                if(itemid < GameSetting.TotalItem-1){
                    itemid++;
                }
                else
                {
                    itemid = 0;
                }
            }
            mapjson.add(maprowjson);
        }
        jsonobjectmap.put("name", UserName);
        jsonobjectmap.put("width",MAP_COLS);
        jsonobjectmap.put("height",MAP_ROWS);
        jsonobjectmap.put("map", mapjson);
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public SimpleDateFormat getMovingToNextPileTime() {
        return MovingToNextPileTime;
    }

    public void setMovingToNextPileTime(SimpleDateFormat movingToNextPileTime) {
        MovingToNextPileTime = movingToNextPileTime;
    }

    public static int getCurrentPositionX() {
        return CurrentPositionX;
    }

    public static void setCurrentPositionX(int currentPositionX) {
        CurrentPositionX = currentPositionX;
    }

    public static int getCurrentPositionY() {
        return CurrentPositionY;
    }

    public static void setCurrentPositionY(int currentPositionY) {
        CurrentPositionY = currentPositionY;
    }

    public static int getMAP_ROWS() {
        return MAP_ROWS;
    }

    public static void setMAP_ROWS(int MAP_ROWS) {
        PlayerData.MAP_ROWS = MAP_ROWS;
    }

    public static int getMAP_COLS() {
        return MAP_COLS;
    }

    public static void setMAP_COLS(int MAP_COLS) {
        PlayerData.MAP_COLS = MAP_COLS;
    }

    public static String getUserToken() {
        return UserToken;
    }

    public static void setUserToken(String userToken) {
        UserToken = userToken;
    }


    public static int getMovingTimeUTC() {
        return MovingTimeUTC;
    }

    public static void setMovingTimeUTC(int movingTimeUTC) {
        MovingTimeUTC = movingTimeUTC;
    }


    public static JSONObject getJsonobjectmap() {
        return jsonobjectmap;
    }

    public static void setJsonobjectmap(JSONObject jsonobjectmap) {
        PlayerData.jsonobjectmap = jsonobjectmap;
    }

    public static JSONObject getJsonobjectoffers() {
        return jsonobjectoffers;
    }

    public static void setJsonobjectoffers(JSONObject jsonobjectoffers) {
        PlayerData.jsonobjectoffers = jsonobjectoffers;
    }

    public static JSONObject getJsonobjectamountinventory() {
        return jsonobjectamountinventory;
    }

    public static void setJsonobjectamountinventory(JSONObject jsonobjectamountinventory) {
        PlayerData.jsonobjectamountinventory = jsonobjectamountinventory;
    }

    public static int[] getItemsAmount() {
        return ItemsAmount;
    }

    public static void setItemsAmount(int[] itemsAmount) {
        ItemsAmount = itemsAmount;
    }

    public JSONArray getJsonarrayamountinvetory() {
        return jsonarrayamountinvetory;
    }

    public void setJsonarrayamountinvetory(JSONArray jsonarrayamountinvetory) {
        this.jsonarrayamountinvetory = jsonarrayamountinvetory;
    }


    public static int[][] getItemOnMaps() {
        return ItemOnMaps;
    }

    public static JSONArray getJsonarraymaps() {
        return jsonarraymaps;
    }

    public void setJsonarraymaps(JSONArray jsonarraymaps) {
        this.jsonarraymaps = jsonarraymaps;
    }

    public static JSONArray getJsonarrayoffers() {
        return jsonarrayoffers;
    }

    public static void setJsonarrayoffers(JSONArray jsonarrayoffers) {
        PlayerData.jsonarrayoffers = jsonarrayoffers;
    }

    public static JSONArray getJsonarraytrades() {
        return jsonarraytrades;
    }

    public static void setJsonarraytrades(JSONArray jsonarraytrades) {
        PlayerData.jsonarraytrades = jsonarraytrades;
    }

    public static void setItemOnMaps(int[][] itemOnMaps) {
        ItemOnMaps = itemOnMaps;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        PlayerData.name = name;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        PlayerData.ip = ip;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        PlayerData.port = port;
    }

    private static void createjsonobjectoffersdummy() throws JSONException {
        jsonobjectoffers = new JSONObject();
        JSONArray offersjson = new JSONArray();
        JSONArray offerjson = new JSONArray();
        offerjson.add(2); //Clay
        offerjson.add(10); //10 Clay
        offerjson.add(3); //Mineral
        offerjson.add(8);
        offerjson.add(false);
        offerjson.add("0ff34asdf0as9dfa0sdf9");

        JSONArray offerjson2 = new JSONArray();
        offerjson2.add(3); //Clay
        offerjson2.add(9); //10 Clay
        offerjson2.add(5); //Mineral
        offerjson2.add(2);
        offerjson2.add(true);
        offerjson2.add("0ff98asd9f8asd9f0");

        offersjson.add(offerjson);
        offersjson.add(offerjson2);
        offersjson.add(offerjson);
        offersjson.add(offerjson2);
        jsonobjectoffers.put("status","ok");
        jsonobjectoffers.put("offers", offersjson);
    }

    private static void createjsonobjecttradesdummy() throws JSONException {
        jsonobjecttrades= new JSONObject();
        JSONArray tradesjson = new JSONArray();
        JSONArray tradejson = new JSONArray();
        tradejson.add(9); //Clay
        tradejson.add(100); //10 Clay
        tradejson.add(8); //Mineral
        tradejson.add(99);
        tradejson.add(false);
        tradejson.add("0ff34asdf0as9dfa0sdf9");

        JSONArray tradejson2 = new JSONArray();
        tradejson2.add(7); //Clay
        tradejson2.add(88); //10 Clay
        tradejson2.add(6); //Mineral
        tradejson2.add(77);
        tradejson2.add(true);
        tradejson2.add("0ff98asd9f8asd9f0");

        tradesjson.add(tradejson);
        tradesjson.add(tradejson2);
        tradesjson.add(tradejson);
        tradesjson.add(tradejson2);
        jsonobjecttrades.put("status","ok");
        jsonobjecttrades.put("offers", tradesjson);
    }

    private static void createjsonobjectinventorysdummy() throws JSONException {
        jsonobjectamountinventory = new JSONObject();
        JSONArray sumiteminventoryjson = new JSONArray();
        sumiteminventoryjson.add(1); //Clay
        sumiteminventoryjson.add(2); //10 Clay
        sumiteminventoryjson.add(3); //Mineral
        sumiteminventoryjson.add(4);
        sumiteminventoryjson.add(5);
        sumiteminventoryjson.add(6);
        sumiteminventoryjson.add(7);
        sumiteminventoryjson.add(8);
        sumiteminventoryjson.add(9);
        sumiteminventoryjson.add(10);
        jsonobjectamountinventory.put("status", "ok");
        jsonobjectamountinventory.put("inventory", sumiteminventoryjson);
    }

    private void SetItemsAmount(boolean initial)  {
        for(int textamountid = 0;textamountid<IdItemAmount.length;textamountid++)
        {
            if(initial) {
                ItemsAmount[textamountid] = getamountitembyid(textamountid);
            }
        }
    }
    private int getamountitembyid(int itemid)  {
        int itemamount = (int) jsonarrayamountinvetory.get(itemid);
        return itemamount;
    }
    private static void setJSONArrayamountinventory() throws  JSONException {
        jsonarrayamountinvetory = (JSONArray) jsonobjectamountinventory.get("inventory");
    }
    public static void changeItemsAmount(int selecteditem,int delta){
        ItemsAmount[selecteditem] += delta;
    }
    public static int getSpecificItemsAmount(int itemselected){
        return ItemsAmount[itemselected];
    }
    public static void CreateJSONArraymaps(){
        try {
            //CreateJSONObjectMapdummy();
            jsonarraymaps = (JSONArray) jsonobjectmap.get("map");
            System.out.println(jsonarraymaps.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void CreateItemonMaps()  {
        for(int row = 0;row<PlayerData.getMAP_ROWS();row++) {
            for (int col = 0; col < PlayerData.getMAP_COLS(); col++) {
                ItemOnMaps[row][col] = getitemidonmap(row, col);
            }
        }
    }
    private static int getitemidonmap(int row, int col) {
        JSONArray jsonarraymaprow = (JSONArray) jsonarraymaps.get(row);
        System.out.print("asdfasdf");
        int itemid = (int)jsonarraymaprow.get(col);
        return itemid;
    }
    public static void CreateJSONArrayoffers() throws JSONException {
        jsonarrayoffers = (JSONArray) jsonobjectoffers.get("offers");
    }

    private void setJSONArraytrades() throws JSONException {
        jsonarraytrades = (JSONArray)jsonobjecttrades.get("offers");
    }

    public static JSONObject getJsonobjecttrades() {
        return jsonobjecttrades;
    }

    public static void setJsonobjecttrades(JSONObject jsonobjecttrades) {
        PlayerData.jsonobjecttrades = jsonobjecttrades;
    }

}
