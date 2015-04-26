package com.example.kevinhuang.spf420client;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.security.spec.RSAKeyGenParameterSpec;


public class register extends ActionBarActivity {
    private Button btnregister;
    private EditText usernametxt;
    private EditText passwordtxt;
    private Boolean RegisterSuccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        RegisterSuccess = true;
        usernametxt = (EditText) findViewById(R.id.edtregisusername);
        passwordtxt = (EditText) findViewById(R.id.edtregispass);
        btnregister = (Button) findViewById(R.id.btnregister);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Register(usernametxt.getText().toString(),passwordtxt.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void Register(String username,String password) throws JSONException, ParseException {
        Toast.makeText(this,"Username: "+username+"\nPassword: "+password,Toast.LENGTH_SHORT).show();
        JSONObject newrequest = new JSONObject();
        newrequest.put("method","signup");
        newrequest.put("username",username);
        newrequest.put("password",password);
        //TODO GET REQUEST
        String response = null;
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
            RegisterSuccess = false;
        }
        else if(response_status.equals("error")){
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
            RegisterSuccess = false;
        }
        else if(response_status.equals("ok")){
            Toast.makeText(this,"Register Success",Toast.LENGTH_SHORT).show();
            RegisterSuccess = true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_register, menu);
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
