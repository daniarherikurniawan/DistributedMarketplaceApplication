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

import org.json.JSONObject;

import java.io.IOException;


public class register extends ActionBarActivity {
    private Button btnregister;
    private EditText usernametxt;
    private EditText passwordtxt;
    private Boolean RegisterSuccess;
    private EditText porttxt;
    private EditText iptxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        RegisterSuccess = true;
        usernametxt = (EditText) findViewById(R.id.edtregisusername);
        passwordtxt = (EditText) findViewById(R.id.edtregispass);
        porttxt = (EditText) findViewById(R.id.porttxt);
        iptxt = (EditText) findViewById(R.id.iptxt);
        btnregister = (Button) findViewById(R.id.btnregister);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Register(usernametxt.getText().toString(),passwordtxt.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(RegisterSuccess) {
                    Intent loginintent = new Intent(v.getContext(), login.class);
                    startActivity(loginintent);
                }
            }
        });
    }

    private void Register(String username,String password) throws JSONException, IOException {
        JSONObject newrequest = new JSONObject();
        String ip = iptxt.getText().toString();
        int port = Integer.parseInt(String.valueOf(porttxt.getText()));
        ClientServerConnector cs = new ClientServerConnector(ip,port);
        String response = cs.actionSignup(username,password);
        Toast.makeText(this,response,Toast.LENGTH_SHORT).show();
        //tes.SendRequest(newrequest.toString());
        System.out.println(response);

        //Object temp = parser.parse(response);
        JSONObject responsejson = new JSONObject(response);
        String response_status = null;
        response_status = (String) responsejson.get("status");
        if(response_status.equals("fail")){
            String description = (String) responsejson.get("description");
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
