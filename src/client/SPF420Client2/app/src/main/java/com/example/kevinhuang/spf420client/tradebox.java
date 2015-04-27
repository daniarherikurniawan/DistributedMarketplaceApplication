package com.example.kevinhuang.spf420client;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class tradebox extends ActionBarActivity implements  GameSetting{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradebox);
        TableLayout tablelayout = (TableLayout)findViewById(R.id.tabletradeboxoffers);
        int colomnid = 0;
        createTableHeader(tablelayout,colomnid);
        getofferbynumber(0);
        try {
            createListOffer(tablelayout,colomnid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void createListOffer(TableLayout tableLayout,int colomnid) throws JSONException {
        //Initalize offer list
        for(int row = 0;row<PlayerData.getJsonobjecttrades().length()+1;row++){
            //TODO Clean code for initial row = 1
            if(row == 0)
            {
                row++; //row 0 sudah diisi dengan header table
            }
            JSONArray offer = new JSONArray();
            offer = getofferbynumber(row-1); //array of offer mulai dari 0 di baris 1
            TableRow tableRow = new TableRow(this);
            tableRow.setId(row);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            ));
            tableLayout.addView(tableRow);
            for(int col = 0;col<ColomnName.length;col++){
                AutoResizeTextView txtoffer = new AutoResizeTextView(this);
                txtoffer.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                //Make text not clip small buttons
                txtoffer.setPadding(0, 0, 0, 0);
                txtoffer.setId(colomnid++);
                if(col == 0)
                {
                    txtoffer.setText(""+row); //No offer
                    tableRow.addView(txtoffer);
                }
                else if(col > 5)
                {
                    if((Boolean)getData(offer,5) == false) //Col 5 adalah data tentang availability
                    {
                        Button buttonfetch = new Button(this);
                        buttonfetch.setText("Fetch");
                        //Make text not clip small buttons
                        buttonfetch.setPadding(0,0,0,0);
                        buttonfetch.setId(row);
                        tableRow.addView(buttonfetch);
                    }
                    else if((Boolean)getData(offer,5) == true)
                    {
                        Button buttoncancel = new Button(this);
                        buttoncancel.setText("Cancel");
                        //Make text not clip small buttons
                        buttoncancel.setPadding(0,0,0,0);
                        buttoncancel.setId(row);
                        tableRow.addView(buttoncancel);
                    }
                }
                else
                {
                    txtoffer.setText(getData(offer,col).toString());
                    tableRow.addView(txtoffer);
                }

            }
        }
    }

    private JSONArray getofferbynumber(int offernum) {
        TextView txtout = (TextView) findViewById(R.id.txtout);
        JSONArray offer = (JSONArray) PlayerData.getJsonarraytrades().get(offernum);
        return offer;
    }

    private Object getData(JSONArray offer,int col) throws JSONException {
        Object data = new Object();
        data = offer.get(col-1);
        if(col-1 == 0 || col-1 ==2) //Kode nama item
        {
            data = ItemsName[((int)(long) data)];
        }
        return data;
    }

    private void createTableHeader(TableLayout tablelayout,int colomnid){
        //Initalize first row
        TableRow firsttableRow = new TableRow(this);
        firsttableRow.setId(0);
        firsttableRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT,
                1.0f
        ));
        tablelayout.addView(firsttableRow);

        //Initaliaze first row colomn
        for(int col = 0;col<ColomnName.length;col++) {
            AutoResizeTextView firstrowcolomn = new AutoResizeTextView(this);
            firstrowcolomn.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1.0f
            ));
//            firstrowcolomn.setMaxLines(1);
            firstrowcolomn.setSingleLine(true);
            firstrowcolomn.setText(ColomnName[col]);
            firstrowcolomn.setPadding(0, 0, 0, 0);
            firstrowcolomn.setId(colomnid++);
            firsttableRow.addView(firstrowcolomn);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tradebox, menu);
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
