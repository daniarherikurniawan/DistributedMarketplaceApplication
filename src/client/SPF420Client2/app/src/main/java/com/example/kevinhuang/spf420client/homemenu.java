package com.example.kevinhuang.spf420client;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class homemenu extends ActionBarActivity {
    private Boolean OpenInventory;
    private Boolean OpenMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homemenu);
        OpenInventory = false;
        OpenMap = false;
        final Button btnMap = (Button)findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    OpenMap();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(OpenMap){
                    Intent openmapintent = new Intent(view.getContext(), map.class);
                    startActivity(openmapintent);
                }

            }
        });
        final Button btnMarket = (Button)findViewById(R.id.btnMarket);
        btnMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openmarketintent = new Intent(view.getContext(), market.class);
                startActivity(openmarketintent);
            }
        });
        final Button btnInventory = (Button)findViewById(R.id.btnInventory);
        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    OpenInventory();
                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(OpenInventory) {
                    Intent openinventoryintent = new Intent(view.getContext(), inventory.class);
                    startActivity(openinventoryintent);
                }
            }
        });
    }

    private void OpenInventory() throws JSONException, IOException {
        System.out.println(PlayerData.getIp()+"  "+PlayerData.getPort());
        Toast.makeText(this, "Opening inventory...", Toast.LENGTH_SHORT).show();
        ClientServerConnector cs = new ClientServerConnector(PlayerData.getIp(),PlayerData.getPort());
        String response = cs.actionInventory(PlayerData.getUserToken());
        //TODO GET REQUEST
        //tes.SendRequest(newrequest.toString());
        //String response = tes.getResponse();

        JSONObject responsejson = new JSONObject(response);
        Toast.makeText(this,response,Toast.LENGTH_SHORT).show();
        String response_status = null;
        response_status = (String)responsejson.get("status");
        if(response_status == "error"){
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
            OpenInventory = false;
        }
        else if(response_status.equals("ok")){
            Toast.makeText(this,"Open Inventory Success",Toast.LENGTH_SHORT).show();
            OpenInventory = true;
            String jsonarrayinventory = null;
            jsonarrayinventory = responsejson.get("inventory").toString();
            PlayerData.setJsonobjectamountinventory(responsejson);
        }
    }

    private void OpenMap() throws JSONException, IOException {
        Toast.makeText(this, "Opening map...", Toast.LENGTH_SHORT).show();
        //TODO Sent Request
        ClientServerConnector cs = new ClientServerConnector(PlayerData.getIp(),PlayerData.getPort());
        //TODO GET REQUEST
        //tes.SendRequest(newrequest.toString());
        //String response = tes.getResponse();
        String response = cs.actionMap(PlayerData.getUserToken());
        JSONObject responsejson = new JSONObject(response);
        String response_status = null;
        response_status = (String)responsejson.get("status");
        if(response_status.equals("error")){
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
            OpenInventory = false;
        }
        else if(response_status.equals("ok")){
            Toast.makeText(this,"Open Map Success",Toast.LENGTH_SHORT).show();
            OpenMap = true;
            //String jsonarrayinventory = null;
            //jsonarrayinventory = (String) parser.parse(responsejson.get("inventory").toString());
            String name = (String)responsejson.get("name");
            int width = (int) responsejson.get("width");
            int height = (int) responsejson.get("height");
            PlayerData.setName(name);
            PlayerData.setMAP_COLS(width);
            PlayerData.setMAP_ROWS(height);
            PlayerData.setJsonobjectmap(responsejson);
            //PlayerData.CreateJSONArraymaps();
            //PlayerData.CreateItemonMaps();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
