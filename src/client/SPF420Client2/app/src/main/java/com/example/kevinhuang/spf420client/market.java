package com.example.kevinhuang.spf420client;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class market extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        final Button btnOffer = (Button)findViewById(R.id.btnofferitem);
        btnOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent offerintent = new Intent(view.getContext(), offeritem.class);
                startActivity(offerintent);
            }
        });
        final Button btntradebox = (Button)findViewById(R.id.btntradebox);
        btntradebox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tradeintent = new Intent(view.getContext(), tradebox.class);
                startActivity(tradeintent);
            }
        });
        final Button btnfinditem = (Button)findViewById(R.id.btnfinditem);
        btnfinditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finditemintent = new Intent(view.getContext(), finditem.class);
                startActivity(finditemintent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_market, menu);
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
