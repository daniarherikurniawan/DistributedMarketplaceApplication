package com.example.kevinhuang.spf420client;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class TesServer extends ActionBarActivity {
    private EditText edtrequestjson;
    private TextView txtresponse;
    private Socket socket;
    private String dstAddress = "100.100.101.60";
    private int dstPort = 8000;
    private String response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tes_server);
        final Button btnsendrequest = (Button)findViewById(R.id.btnrequest);
        btnsendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendRequest();
            }
        });
        edtrequestjson = (EditText)findViewById(R.id.edttesjson);
        txtresponse = (TextView)findViewById(R.id.txtresponse);

    }

    private void SendRequest(){
        new SocketOperation().execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tes_server, menu);
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

    private class SocketOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String request = edtrequestjson.getText().toString();
            System.out.println("Connect to socket");
            try {
                socket = new Socket(dstAddress, dstPort);
                socket.setSoTimeout(10);

                OutputStream outToServer = socket.getOutputStream();
                DataOutputStream out =
                        new DataOutputStream(outToServer);

                out.write(request.getBytes("UTF-8"));
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
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            txtresponse.setText(response);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
