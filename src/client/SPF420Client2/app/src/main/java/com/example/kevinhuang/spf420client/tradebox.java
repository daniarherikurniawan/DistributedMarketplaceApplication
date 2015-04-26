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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class tradebox extends ActionBarActivity {
    private final String[] ItemsName = new String[]{"Honey","Herbs","Clays","Minerals","Potion","Incense","Gems","Life Elixir","Mana Crystal","Philosopher Stone"};
    private final String[] ColomnName = new String[]{"No","OfferedItem","Sum","DesiredItem","Sum","Availabilty","Accept?"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradebox);
        TableLayout tablelayout = (TableLayout)findViewById(R.id.tabletradeboxoffers);
        int colomnid = 0;
        try {
            createTableHeader(tablelayout,colomnid);
            getofferbynumber(0);
            createListOffer(tablelayout,colomnid);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    private void createListOffer(TableLayout tableLayout,int colomnid) throws ParseException {
        //Initalize offer list
        for(int row = 0;row<PlayerData.getJsonobjecttrades().size()+1;row++){
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
                    if(getData(offer,5) == false) //Col 5 adalah data tentang availability
                    {
                        Button buttonfetch = new Button(this);
                        buttonfetch.setText("Fetch");
                        //Make text not clip small buttons
                        buttonfetch.setPadding(0,0,0,0);
                        buttonfetch.setId(row);
                        tableRow.addView(buttonfetch);
                    }
                    else if(getData(offer,5) == true)
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

    private JSONArray getofferbynumber(int offernum) throws ParseException {
        JSONParser parser = new JSONParser();
        TextView txtout = (TextView) findViewById(R.id.txtout);
        JSONArray offer = (JSONArray) PlayerData.getJsonarraytrades().get(offernum);
        return offer;
    }

    private Object getData(JSONArray offer,int col){
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
