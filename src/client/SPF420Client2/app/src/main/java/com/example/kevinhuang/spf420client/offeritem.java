package com.example.kevinhuang.spf420client;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class offeritem extends ActionBarActivity implements GameSetting {
    private final int TotalItem = 10;
    private final int MinimumMergeAmount = 3;
    private boolean CanOffer;
    private ImageView imginventory[];
    private int selecteditem1;
    private int selecteditem2;
    private int selecteditem3;
    private ImageButton temp;
    private ImageButton btnselecteditem1;
    private ImageButton btnselecteditem2;
    private EditText edtsumitem1;
    private EditText edtsumitem2;
    private Button btnoffer;
    private TextView ItemAmount[] = new TextView[IdItemAmount.length];
    private ImageView listitems[] = new ImageView[ItemsName.length];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offeritem);
        selecteditem1 = -99;
        selecteditem2 = -99;
        selecteditem3 = -99;
        CanOffer = false;
        edtsumitem1 = (EditText) findViewById(R.id.edtsumitem1);
        edtsumitem2 = (EditText) findViewById(R.id.edtsumitem2);
        try {
            SetTextAmount(true);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SetBlankButton();
        SetAllItemButton();
        SetButtonOffer();
    }

    private void SetTextAmount(boolean initial) throws ParseException {
        for(int textamountid = 0;textamountid<IdItemAmount.length;textamountid++)
        {
            TextView temp = (TextView) findViewById(IdItemAmount[textamountid]);
            temp.setText("Amount : "+PlayerData.getItemsAmount()[textamountid]);
        }
    }

    private void SetBlankButton(){
        btnselecteditem1 = (ImageButton) findViewById(R.id.imgselecteditem1);
        btnselecteditem2 = (ImageButton) findViewById(R.id.imgselecteditem2);
        btnselecteditem1.setBackgroundResource(R.drawable.blankitem1);
        btnselecteditem2.setBackgroundResource(R.drawable.blankitem2);
        btnselecteditem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelSelectedItem(1);
            }
        });
        btnselecteditem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelSelectedItem(2);
            }
        });

    }

    private void SetAllItemButton(){
        for(int imageid = 0;imageid<IdItemImg.length;imageid++)
        {
            ImageView Imagetemp = (ImageView)findViewById(IdItemImg[imageid]);
            final int finalImageid = imageid;
            Imagetemp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectThisItem(finalImageid);
                }
            });
            listitems[imageid] = Imagetemp;
            temp = btnselecteditem1;
        }
    }

    private void SetButtonOffer(){
        btnoffer = (Button) findViewById(R.id.btnofferitem);
        btnoffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OfferSelectedItem();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void OfferSelectedItem() throws ParseException {
        CheckIfCanOffer();
        if(CanOffer)
        {
            PlayerData.changeItemsAmount(selecteditem1,-Integer.parseInt(edtsumitem1.getText().toString()));
            CancelSelectedItem(1);
            CancelSelectedItem(2);
            edtsumitem1.setText("");
            edtsumitem2.setText("");
            CanOffer = false;
            SetTextAmount(false); //not inital = false
            //TODO Send Request Here
            //JSON Object from response insert into JSONObject jsonobjectamountinventory
            //TODO setJsonArrayAmount again

        }
        else
        {
            Toast.makeText(this, "Offer unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void CancelSelectedItem(int whichbuttonclicked){
        if(selecteditem1 != -99 && whichbuttonclicked == 1) {
            btnselecteditem1.setBackgroundResource(R.drawable.blankitem1);
            selecteditem1 = -99;
        }
        if(selecteditem2 != -99 && whichbuttonclicked == 2){
            btnselecteditem2.setBackgroundResource(R.drawable.blankitem2);
            selecteditem2 = -99;
        }
        CanOffer = false;
        selecteditem3 = -99;
    }

    private void SelectThisItem(int itemselected){
        if (selecteditem1 != -99 && selecteditem2 != -99) //2 Items already selected
        {
            Toast.makeText(this, "Please cancel item first!", Toast.LENGTH_SHORT).show();
        }else {
            if (selecteditem1 == -99) {
                btnselecteditem1.setBackgroundResource(ResItemImg[itemselected]);
                selecteditem1 = itemselected;
            } else {
                if (selecteditem2 == -99) {
                    btnselecteditem2.setBackgroundResource(ResItemImg[itemselected]);
                    selecteditem2 = itemselected;
                } else {
                    //error
                }
            }
        }
    }

    private void CheckIfCanOffer(){
        if(!edtsumitem1.getText().toString().equals("") && !edtsumitem2.getText().toString().equals("") &&
                selecteditem1 != -99 && selecteditem2 != -99){
            if(Integer.parseInt(edtsumitem1.getText().toString()) <= PlayerData.getSpecificItemsAmount(selecteditem1)){
                CanOffer = true;
            }
        }
    }
    /*private void lockimagesize(){
        for(int i =0;i<IdItemImg.length;i++)
        {
            ImageView temp = listitems[i];
            int width = temp.getWidth();
            temp.setMaxWidth(width);
            temp.setMinimumWidth(width);

            int height = temp.getHeight();
            temp.setMaxHeight(height);
            temp.setMinimumHeight(height);

        }
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_offer, menu);
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
