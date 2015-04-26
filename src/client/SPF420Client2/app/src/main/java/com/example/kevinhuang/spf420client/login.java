package com.example.kevinhuang.spf420client;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.logging.Logger;


public class login extends ActionBarActivity {
    private EditText usernametxt;
    private EditText passwordtxt;
    private EditText porttxt;
    private EditText iptxt;
    private PlayerData playerData;
    private boolean LoginSuccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginSuccess = true;
        playerData = new PlayerData();
        playerData.setData();
        Toast.makeText(this,PlayerData.getItemOnMaps().toString(),Toast.LENGTH_SHORT).show();
        usernametxt = (EditText) findViewById(R.id.edtUsername);
        passwordtxt = (EditText) findViewById(R.id.edtPassword);
        porttxt = (EditText) findViewById(R.id.edtport);
        iptxt = (EditText) findViewById(R.id.edtport);
        final Button btnLogin = (Button)findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Login();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if(LoginSuccess) {
                            Intent loginintent = new Intent(view.getContext(), homemenu.class);
                            startActivity(loginintent);
                        }
                    }}
            });
            final Button btnRegister = (Button)findViewById(R.id.btnregister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerintent = new Intent(view.getContext(), register.class);
                startActivity(registerintent);
            }
        });
    }

    private void Login() throws IOException, JSONException, ParseException {
        //"100.100.101.216"
        Toast.makeText(this,"Username: "+usernametxt.getText().toString()+"\nPassword: "+passwordtxt.getText().toString(),Toast.LENGTH_SHORT).show();
        JSONObject newrequest = new JSONObject();
        newrequest.put("method","login");
        newrequest.put("username",usernametxt.getText().toString());
        newrequest.put("password",passwordtxt.getText().toString());
        //TODO GET REQUEST
        ClientServerConnector cs = new ClientServerConnector("100.100.101.216",8002);
        String response = cs.actionLogin(usernametxt.getText().toString(), passwordtxt.getText().toString());
        Log.d(null,response);
        Toast.makeText(this,response,Toast.LENGTH_LONG).show();
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
        if(response_status.equals("fail")){
            String description = (String) parser.parse(responsejson.get("description").toString());
            Toast.makeText(this,description,Toast.LENGTH_SHORT).show();
            LoginSuccess = false;
        }
        else if(response_status.equals("error")){
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
            LoginSuccess = false;
        }
        else if(response_status.equals("ok")){
            Toast.makeText(this,"Login Success",Toast.LENGTH_SHORT).show();
            LoginSuccess = true;
            String token = (String) parser.parse(responsejson.get("token").toString());
            int corx = (int) parser.parse(responsejson.get("x").toString());
            int cory = (int) parser.parse(responsejson.get("y").toString());
            Long time = (Long) parser.parse(responsejson.get("time").toString());
            PlayerData.setCurrentPositionX(corx);
            PlayerData.setCurrentPositionY(cory);
            PlayerData.setMovingTimeUTC(time);
            PlayerData.setUserToken(token);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_login, menu);
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
