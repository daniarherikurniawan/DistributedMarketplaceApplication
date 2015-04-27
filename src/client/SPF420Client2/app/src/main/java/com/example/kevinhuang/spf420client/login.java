package com.example.kevinhuang.spf420client;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;


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
        LoginSuccess = false;
        playerData = new PlayerData();
        try {
            playerData.setData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(this,PlayerData.getItemOnMaps().toString(),Toast.LENGTH_SHORT).show();
        usernametxt = (EditText) findViewById(R.id.edtUsername);
        passwordtxt = (EditText) findViewById(R.id.edtPassword);
        porttxt = (EditText) findViewById(R.id.edtlogport);
        iptxt = (EditText) findViewById(R.id.edtlogip);
        final Button btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Login();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(LoginSuccess) {
                    Intent homemenuintent = new Intent(view.getContext(), homemenu.class);
                    startActivity(homemenuintent);
                }
            }
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

    private void Login() throws IOException, JSONException {
        //Toast.makeText(this,"Username: "+usernametxt.getText().toString()+"\nPassword: "+passwordtxt.getText().toString(),Toast.LENGTH_SHORT).show();
        String username = usernametxt.getText().toString();
        String password = passwordtxt.getText().toString();
        String ip = iptxt.getText().toString();
        int port = Integer.parseInt(String.valueOf(porttxt.getText()));
        ClientServerConnector cs = new ClientServerConnector(ip,port);
        String response = cs.actionLogin(username, password);
        Toast.makeText(this,response,Toast.LENGTH_SHORT).show();
        //tes.SendRequest(newrequest.toString());
        //String response = tes.getResponse();


        org.json.JSONObject responsejson = new org.json.JSONObject(response);
        String response_status = null;
        response_status = (String) responsejson.get("status");
        if(response_status.equals("fail")){
            String description = (String) responsejson.get("description");
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
            String token = (String) responsejson.get("token");
            int corx = (int) responsejson.get("x");
            int cory = (int) responsejson.get("y");
            int time = (int) responsejson.get("time");
            PlayerData.setCurrentPositionX(corx);
            PlayerData.setCurrentPositionY(cory);
            PlayerData.setMovingTimeUTC(time);
            PlayerData.setUserToken(token);
            PlayerData.setIp(ip);
            PlayerData.setPort(port);
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
