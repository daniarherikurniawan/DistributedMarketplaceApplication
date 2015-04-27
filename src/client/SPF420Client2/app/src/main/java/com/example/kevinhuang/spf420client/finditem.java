package com.example.kevinhuang.spf420client;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.JSONObject;

import java.io.IOException;


public class finditem extends ActionBarActivity implements GameSetting {
    private Spinner myspinner;
    private Button Find;
    private JSONArray jsonarrayoffers;
    private TableLayout tablelayout;
    private AutoResizeTextView txtoffers[][] = new AutoResizeTextView[2][ColomnName.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finditem);
        myspinner = (Spinner)findViewById(R.id.spinneritem1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,ItemsName);
        myspinner.setAdapter(adapter);
        Find = (Button)findViewById(R.id.btnFind);
        Find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FindItem();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        tablelayout = (TableLayout)findViewById(R.id.tableoffers);
        int colomnid = 0;
        //setJSONArrayoffers(PlayerData.getJsonobjectoffers().toString());
        //createTableHeader(tablelayout,colomnid);
        //createListOffer(tablelayout,colomnid);
    }

    private void setJSONArrayoffers(String jsonobjectoffers) throws  JSONException {
        JSONObject jsonobjectoffersobject = new JSONObject(jsonobjectoffers);
        jsonarrayoffers = (JSONArray) jsonobjectoffersobject.get("offers");
        TextView txtout = (TextView) findViewById(R.id.txtout);
    }

    private void createListOffer(TableLayout tableLayout,int colomnid)  {
        //Initalize offer list
        for(int row = 0;row<jsonarrayoffers.size()+1;row++){
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
            if((Boolean)getData(offer, 5) == false){
                //skip the row
            }
            else{
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
                        Button buttonaccept = new Button(this);
                        buttonaccept.setText("Accept");
                        //Make text not clip small buttons
                        buttonaccept.setPadding(0,0,0,0);
                        buttonaccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    FindItem();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        buttonaccept.setId(row);
                        tableRow.addView(buttonaccept);
                    }
                    else
                    {
                        txtoffer.setText(getData(offer,col).toString());
                        tableRow.addView(txtoffer);
                    }

                }
            }

        }
    }

    private JSONArray getofferbynumber(int offernum){
        TextView txtout = (TextView) findViewById(R.id.txtout);
        JSONArray offer = (JSONArray) jsonarrayoffers.get(offernum);
        return offer;
    }

    private Object getData(JSONArray offer,int col){
        Object data = new Object();
        data = offer.get(col-1);
        if(col-1 == 0 || col-1 ==2) //Kode nama item
        {
            data = ItemsName[(int)(long)data];
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

    private void FindItem() throws IOException, JSONException {
        Toast.makeText(this, "Item selected" + myspinner.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();
        int temp = myspinner.getSelectedItemPosition();
        System.out.print("swt");
        ClientServerConnector cs = new ClientServerConnector(PlayerData.getIp(),PlayerData.getPort());
        String response = cs.actionSendFind(PlayerData.getUserToken(),temp);
        //Toast.makeText(this,response,Toast.LENGTH_SHORT).show();
        //tes.SendRequest(newrequest.toString());
        //String response = tes.getResponse();
        System.out.println("asdflanlsdfj");
        System.out.println("ping");
        JSONObject responsejson = new JSONObject(response);
        String response_status = null;
        response_status = (String) responsejson.get("status");
        if(response_status.equals("fail")){
            String description = (String) responsejson.get("description");
            Toast.makeText(this,description,Toast.LENGTH_SHORT).show();
        }
        else if(response_status.equals("error")){
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
        }
        else if(response_status.equals("ok")){
            Toast.makeText(this,"Login Success",Toast.LENGTH_SHORT).show();
            int colomnid = 0;
            PlayerData.setJsonobjectoffers(new JSONObject(response));
            PlayerData.CreateJSONArrayoffers();
            createTableHeader(tablelayout, colomnid);
            createListOffer(tablelayout,colomnid);

        }

        //Json here

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_finditem, menu);
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
