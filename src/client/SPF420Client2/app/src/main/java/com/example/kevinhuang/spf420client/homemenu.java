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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class homemenu extends ActionBarActivity {
    private Boolean OpenInventory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homemenu);
        OpenInventory = true;
        final Button btnMap = (Button)findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openmapintent = new Intent(view.getContext(), map.class);
                startActivity(openmapintent);
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
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(OpenInventory) {
                    Intent openinventoryintent = new Intent(view.getContext(), inventory.class);
                    startActivity(openinventoryintent);
                }
            }
        });
        final Button btntesserver = (Button)findViewById(R.id.btntesserver);
        btntesserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openinventoryintent = new Intent(view.getContext(), TesServer.class);
                startActivity(openinventoryintent);
            }
        });
    }

    private void OpenInventory() throws JSONException, ParseException {
        Toast.makeText(this, "Opening inventory...", Toast.LENGTH_SHORT).show();
        JSONObject newrequest = new JSONObject();
        newrequest.put("method","inventory");
        newrequest.put("token",PlayerData.getUserToken());
        //TODO GET REQUEST
        String response = "tes";
        //tes.SendRequest(newrequest.toString());
        //String response = tes.getResponse();

        JSONParser parser = new JSONParser();
        Object temp = parser.parse(response);
        JSONObject responsejson = (JSONObject)temp;
        String response_status = null;
        try {
            response_status = (String) parser.parse(responsejson.get("status").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(response_status.equals("error")){
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
            OpenInventory = false;
        }
        else if(response_status.equals("ok")){
            Toast.makeText(this,"Open Inventory Success",Toast.LENGTH_SHORT).show();
            OpenInventory = true;
            String jsonarrayinventory = null;
            jsonarrayinventory = (String) parser.parse(responsejson.get("inventory").toString());
            PlayerData.setJsonobjectamountinventory(responsejson);
            PlayerData.CreateJSONArraymaps();
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
