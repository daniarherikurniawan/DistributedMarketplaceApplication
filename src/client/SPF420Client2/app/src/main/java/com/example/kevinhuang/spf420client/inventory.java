package com.example.kevinhuang.spf420client;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class inventory extends ActionBarActivity implements GameSetting{
    private final int TotalItem = 10;
    private final int MinimummixAmount = 3;
    private boolean Canmix;
    private ImageView imginventory[];
    private int selecteditem1;
    private int selecteditem2;
    private int selecteditem3;
    private ImageButton temp;
    private ImageButton btnselecteditem1;
    private ImageButton btnselecteditem2;
    private ImageView imgselecteditem3;
    private Button btnmix;
    private TextView ItemAmount[] = new TextView[IdItemAmount.length];
    private ImageView listitems[] = new ImageView[ItemsName.length];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        selecteditem1 = -99;
        selecteditem2 = -99;
        selecteditem3 = -99;
        Canmix = false;

        try {

            SetTextAmount(true);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SetBlankButton();
        SetAllItemButton();
        SetButtonmix();
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
        imgselecteditem3 = (ImageView) findViewById(R.id.imgselecteditem3);
        btnselecteditem1.setBackgroundResource(R.drawable.blankitem1);
        btnselecteditem2.setBackgroundResource(R.drawable.blankitem2);
        imgselecteditem3.setBackgroundResource(R.drawable.blankitem3);
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

    private void SetButtonmix(){
        btnmix = (Button) findViewById(R.id.btnmix);
        btnmix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mixSelectedItem();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void mixSelectedItem() throws ParseException {
        /*if(Canmix)
        {
            if(selecteditem3 != -99)
            {
                PlayerData.changeItemsAmount(selecteditem3,1);
                PlayerData.changeItemsAmount(selecteditem1,-MinimummixAmount);
                PlayerData.changeItemsAmount(selecteditem2,-MinimummixAmount);
                CancelSelectedItem(1);
                CancelSelectedItem(2);
                Canmix = false;
                SetTextAmount(false);
            }
            else
            {
                //error
                Toast.makeText(this, "mix Item ID unidentifiable : "+selecteditem3, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "mix unavailable", Toast.LENGTH_SHORT).show();
        }*/
        if(Canmix) {
            Toast.makeText(this, "Merge :\n" + ItemsName[selecteditem1] + " with " + ItemsName[selecteditem2], Toast.LENGTH_SHORT).show();
            //TODO SEND REQUESt
            JSONObject newrequest = new JSONObject();
            newrequest.put("method", "mixitem");
            newrequest.put("token",PlayerData.getUserToken());
            newrequest.put("item1",selecteditem1);
            newrequest.put("item2",selecteditem2);
            //TODO GET REQUEST
            String response = null;
            //tes.SendRequest(newrequest.toString());
            //String response = tes.getResponse();

            JSONParser parser = new JSONParser();
            Object temp = parser.parse(response);
            JSONObject responsejson = (JSONObject) temp;
            String response_status = null;
            try {
                response_status = (String) parser.parse(responsejson.get("status").toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (response_status.equals("fail")) {
                String description = (String) parser.parse(responsejson.get("description").toString());
                Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
            } else if (response_status.equals("error")) {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            } else if (response_status.equals("ok")) {
                Toast.makeText(this, "Mix Success", Toast.LENGTH_SHORT).show();
                Long mixeditem = (Long) parser.parse(responsejson.get("item").toString());
                //PlayerData.setItemsAmount(PlayerData.getItemsAmount()[s(int)(long)mixeditem]++);
            }
        }
        else
        {
            Toast.makeText(this, "Mix unavailable", Toast.LENGTH_SHORT).show();
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
        imgselecteditem3.setBackgroundResource(R.drawable.blankitem3);
        Canmix = false;
        selecteditem3 = -99;
    }

    private void SelectThisItem(int itemselected){
        if (PlayerData.getSpecificItemsAmount(itemselected) < MinimummixAmount) {
            Toast.makeText(this, "You must have at least : " + MinimummixAmount + " items to mix", Toast.LENGTH_SHORT).show();
        }
        else {
            if (selecteditem1 != -99 && selecteditem2 != -99) //2 Items already selected
            {
                Toast.makeText(this, "Please mix or cancel item first!", Toast.LENGTH_SHORT).show();
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
                if (selecteditem1 != -99 && selecteditem2 != -99) {
                    CheckIfmixAble();
                }
            }
        }
    }

    private void CheckIfmixAble(){
        //TODO Eksternally Read Item mix Formula
        // Level 1
        if((selecteditem1 == 0 && selecteditem2 == 1) || (selecteditem1 == 1 && selecteditem2 == 0)) { // Honey + Herb
            selecteditem3 = 4; //Potion
            Canmix = true;
        }
        if((selecteditem1 == 1 && selecteditem2 == 2) || (selecteditem1 == 2 && selecteditem2 == 1)) { // Herb + Clay
            selecteditem3 = 5; //Incense
            Canmix = true;
        }
        if((selecteditem1 == 2 && selecteditem2 == 3) || (selecteditem1 == 3 && selecteditem2 == 2)) { // Clay + Mineral
            selecteditem3 = 6; ///Gems
            Canmix = true;
        }
        //Level 2
        if((selecteditem1 == 4 && selecteditem2 == 5) || (selecteditem1 == 5 && selecteditem2 == 4)) { //Potion + Incense
            selecteditem3 = 7; //Life Elixir
            Canmix = true;
        }
        if((selecteditem1 == 5 && selecteditem2 == 6) || (selecteditem1 == 6 && selecteditem2 == 5)) { //Incense + Gems
            selecteditem3 = 8; //Mana Crsystal
            Canmix = true;
        }

        //Level 3
        if((selecteditem1 == 7 && selecteditem2 == 8) || (selecteditem1 == 8 && selecteditem2 == 7)) { //Potion + Incense
            selecteditem3 = 9; //Philosopher Stone
            Canmix = true;
        }
        if(selecteditem3 != -99)
        {
            imgselecteditem3.setBackgroundResource(ResItemImg[selecteditem3]);
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
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
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
