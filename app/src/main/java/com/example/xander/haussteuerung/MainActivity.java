package com.example.xander.haussteuerung;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

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
            Button btnYeelight;
            btnYeelight = new Button(this);

            btnYeelight.setText(d.ip );
            btnYeelight.setId(d.id);
            lp.weight = 1.0f;
            btnYeelight.setLayoutParams(lp);
            li.addView(btnYeelight, lp);

            btnYeelight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        edtOutput.setText("");
                        objTCPConn.i = v.getId();
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
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    Thread.sleep(2000);
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
