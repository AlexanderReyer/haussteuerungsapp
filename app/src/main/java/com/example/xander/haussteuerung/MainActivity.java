package com.example.xander.haussteuerung;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button              btnSSDPsearch;
    Button              btnPlug;
    Button              btnExit;
    TextView            edtOutput;
    SSDPsearch          objSSDPSearch;
    TCPConn             objTCPConn;
    HTTPConn            objHTTPConn;
    ArrayList<device>   arDevices = new ArrayList<>();
    ColorPickerDialog   colorPickerDialog;
    int                 colorofbulb;


    /**
     * Show buttons corresponding to found ip addresses and a color picker for each button
     * https://android-arsenal.com/details/1/89#!package
     */
    public void show_IPPortButtons()
    {
        objTCPConn = new TCPConn(0, objSSDPSearch);
        //RelativeLayout rl = findViewById(R.id.rlButtons);
        LinearLayout li =  findViewById(R.id.llButtons);
        RelativeLayout.LayoutParams lpr = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //lp.addRule(RelativeLayout.ALIGN_LEFT);

        li.removeAllViews();
        for(device d: arDevices)
        {
            Button btnYeeColorPicker;
            Button btnYeelight;
            btnYeelight = new Button(this);
            btnYeeColorPicker = new Button(this);

            btnYeeColorPicker.setText("Farbe " + d.id);
            btnYeeColorPicker.setId(d.id);
            btnYeelight.setText(d.ip );
            btnYeelight.setId(d.id);
            lp.weight = 1.0f;
            btnYeelight.setLayoutParams(lp);
            btnYeeColorPicker.setLayoutParams(lp);

            LinearLayout litemp = findViewById(1234 + d.id);
            if(null == litemp)
            {
                litemp = new LinearLayout(this);
                litemp.setId(1234 + d.id);
                litemp.setOrientation(LinearLayout.HORIZONTAL);
                litemp.setLayoutParams(lp);
            }
            litemp.removeAllViews();
            litemp.addView(btnYeelight, lp);
            litemp.addView(btnYeeColorPicker);
            li.addView(litemp);

            btnYeelight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        edtOutput.setText("");
                        objTCPConn.i = v.getId();
                        //outp.write(("{ \"id\": 1, \"method\": \"toggle\", \"params\":[\"" + "on" + "\", \"smooth\", 500]} \r\n").getBytes());
                        objTCPConn.build_yeelightcommandstring(v.getId(), "toggle", "\"on\", \"smooth\", 500");
                        Thread t = new Thread(objTCPConn);
                        t.start();
                        Thread.sleep(500);
                        edtOutput.setText("toogle_yeelight Antw: " + objTCPConn.strOutput);
                    }
                    catch(InterruptedException e)
                    {
                        edtOutput.setText(e.toString());
                    }
                }
            });
            btnYeeColorPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int classid = v.getId();
                    final View vtemp = v;
                    v.setBackgroundColor(colorPickerDialog.getColor());
                    colorPickerDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                        @Override
                        public void onColorChanged(int color) {
                            try {
                                color = 16777216 + color;
                                edtOutput.setText("ID " + classid + " Farbe: " + color);
                                objTCPConn.i = classid;
                                objTCPConn.build_yeelightcommandstring(classid, "set_rgb", color + ", \"smooth\", 500");
                                Thread t = new Thread(objTCPConn);
                                t.start();
                                Thread.sleep(1000);
                                edtOutput.setText(edtOutput.getText() + "\r\n" + objTCPConn.strOutput);
                                vtemp.setBackgroundColor(colorPickerDialog.getColor());
                            }
                            catch(Exception e)
                            {
                                edtOutput.setText(edtOutput.getText() + "\r\n" + e.toString());
                            }
                        }
                    });
                    colorPickerDialog.show();
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorofbulb = Color.parseColor("#a39481");
        colorPickerDialog = new ColorPickerDialog(this, colorofbulb);
        colorPickerDialog.setAlphaSliderVisible(false);
        colorPickerDialog.setTitle("Yeelight Farbauswahl");
        colorPickerDialog.setHexValueEnabled(true);


        edtOutput = findViewById(R.id.edtOutput);
        edtOutput.clearFocus();
        objHTTPConn = new HTTPConn();
        objSSDPSearch = new SSDPsearch(edtOutput, arDevices);
        btnSSDPsearch = findViewById(R.id.btnSSDP);
        btnSSDPsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Thread th = new Thread(objSSDPSearch);
                    th.start();
                    Thread.sleep(3000);
                    edtOutput.setText(objSSDPSearch.strOutput);
                    show_IPPortButtons();
                }
                catch(Exception e)
                {
                    edtOutput.setText(e.toString());
                }
            }
        });
        btnPlug = findViewById(R.id.btnPlug);
        btnPlug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Thread t = new Thread(objHTTPConn);
                    t.start();
                    Thread.sleep(1000);
                    edtOutput.setText(objHTTPConn.result);
                }
                catch(Exception e)
                {
                    edtOutput.setText(e.toString());
                }
            }
        });
        btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }
}
