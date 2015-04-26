package com.example.kevinhuang.spf420client;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class map extends ActionBarActivity implements GameSetting {
    private EditText coordinatex;
    private EditText coordinatey;
    private TextView DisplayCurrentTime;
    private TextView DisplayMovingTime;
    private Button btnMove;
    private Button btnFetch;
    private TableLayout tablelayout;
    private Thread CurrentTimeThread;
    private String movingtime;
    private long Movingtimestamp;
    private long Currenttimestamp;
    private final Handler handler = new Handler();
    private Button buttons[][];
    private TextView tempout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        coordinatex = (EditText)findViewById(R.id.edtcoorx);
        coordinatey = (EditText)findViewById(R.id.edtcoory);
        DisplayCurrentTime = (TextView) findViewById(R.id.txtDisplayCurrentTime);
        DisplayMovingTime = (TextView)findViewById(R.id.txtDisplayMovingTime);
        btnMove = (Button)findViewById(R.id.btnMove);
        btnFetch = (Button)findViewById(R.id.btnFetch);
        tablelayout = (TableLayout)findViewById(R.id.tablemaps);
        SetMovingTime();
        SetThreadDisplayTime();
        CurrentTimeThread.start();
        //MovingTimeThread.start();
        SetButtonMovenFetch();
        buttons = new Button[PlayerData.getMAP_ROWS()][PlayerData.getMAP_COLS()];
        createMapButton();
        Toast.makeText(this,PlayerData.getItemOnMaps().toString(),Toast.LENGTH_SHORT).show();
    }

    private void SetButtonMovenFetch(){
        btnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               MoveCharacter();
            }
        });
        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchItem();
            }
        });
    }

    private void MoveCharacter(){
        Currenttimestamp = Calendar.getInstance().getTime().getTime();
        if(Movingtimestamp <= Currenttimestamp*1000){
            //TODO SEND JSON HERE
            final int movetox = Integer.parseInt(String.valueOf(coordinatex.getText()));
            final int movetoy = Integer.parseInt(String.valueOf(coordinatey.getText()));
            if(movetox <= PlayerData.getMAP_ROWS() && movetoy <= PlayerData.getMAP_COLS()){
                final int templastcoorx = PlayerData.getCurrentPositionX();
                final int templastcoory = PlayerData.getCurrentPositionY();
                PlayerData.setCurrentPositionX(movetox);
                PlayerData.setCurrentPositionY(movetoy);
                Button lastplayerpositionbutton = buttons[templastcoory][templastcoorx];
                Button movedtobutton = buttons[movetoy][movetox];
                lastplayerpositionbutton.setBackground(getResources().getDrawable(R.drawable.grass));
                lastplayerpositionbutton.setText("" + templastcoorx + "," + templastcoory);
                lastplayerpositionbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gridButtonClicked(templastcoorx, templastcoory);
                    }
                });
                movedtobutton.setBackground(getResources().getDrawable(R.drawable.akatsuki2));
                Toast.makeText(this,"Last pos : "+templastcoorx+","+templastcoory,Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void FetchItem(){
        int corx = PlayerData.getCurrentPositionX();
        int cory = PlayerData.getCurrentPositionY();
        Long iditem = PlayerData.getItemOnMaps()[cory][corx];
        //TODO hapus tempout
        //tempout = (TextView)findViewById(R.id.txtTimeTo);
        //tempout.setText("Item Fetched : " +iditem);
        Toast.makeText(this,"Item : "+iditem,Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show();
    }

    private void SetMovingTime(){
        //TODO Get Json Time Here
        Movingtimestamp =  PlayerData.getMovingTimeUTC() * 1000;
    }

    private void SetThreadDisplayTime(){
        Runnable myRunnableThread = new TimeRunner();
        CurrentTimeThread= new Thread(myRunnableThread);

    }

    private void createMapButton() {
        int buttonid = 0;
        for(int row = 0;row<PlayerData.getMAP_ROWS();row++){
            TableRow tableRow = new TableRow(this);
            tableRow.setId(row);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            ));
            tablelayout.addView(tableRow);
            for(int col = 0;col<PlayerData.getMAP_COLS();col++){
                final int FINAL_COL = col;
                final int FINAL_ROW = row;
                Button button = new Button(this);
                button.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                //Make text not clip small buttons
                button.setPadding(0,0,0,0);
                button.setId(buttonid++);

                if(PlayerData.getCurrentPositionX() == col && PlayerData.getCurrentPositionY() == row)
                {
                    button.setBackground(getResources().getDrawable(R.drawable.akatsuki2));
                }
                else
                {
                    button.setText(""+col+","+row);
                    button.setBackground(getResources().getDrawable(R.drawable.grass));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gridButtonClicked(FINAL_COL, FINAL_ROW);
                        }
                    });
                }
                tableRow.addView(button);
                buttons[row][col] = button;

            }
        }
    }

    private void gridButtonClicked(int col,int row){
        Toast.makeText(this, "Button cliked" + col + "," + row, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, ""+Movingtimestamp, Toast.LENGTH_SHORT).show();
        coordinatex.setText("" + col);
        coordinatey.setText("" + row);
        //TODO Hightlight bitmap grass when clicked
        /*//lock button size
        lockButtonSize();
        //Resource untuk ganti image hightlight
        Button button = buttons[row][col];
        int newWidth = button.getWidth();
        int newHeight = button.getHeight();
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grasshighlight);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
        Resources resource = getResources();
        button.setBackground(new BitmapDrawable(resource, scaledBitmap));*/
    }

    private void lockButtonSize(){
        for(int row = 0;row<PlayerData.getMAP_ROWS();row++){
            for(int col = 0;col<PlayerData.getMAP_COLS();col++){
                Button button = buttons[row][col];
                int width = button.getWidth();
                button.setMinWidth(width);
                button.setMaxWidth(width);
                int height = button.getHeight();
                button.setMinHeight(height);
                button.setMaxHeight(height);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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
    public void UpdateTimeText() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy h:mm:ss a");
        sdf.setTimeZone(timeZone);
        final String currenttime = sdf.format(Calendar.getInstance().getTime());
        final SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM d, yyyy h:mm:ss a");
        sdf2.setTimeZone(timeZone);
        movingtime = sdf2.format(Movingtimestamp);
        DisplayMovingTime.setText(movingtime);
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    DisplayCurrentTime.setText(currenttime);
                }catch (Exception e) {}
            }
        });

    }
    class TimeRunner implements Runnable{
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    UpdateTimeText();
                    Thread.sleep(1000); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }
}
