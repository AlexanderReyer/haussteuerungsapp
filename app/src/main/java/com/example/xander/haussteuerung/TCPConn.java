package com.example.xander.haussteuerung;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by xander on 19.12.2017.
 */

public class TCPConn implements Runnable{

    public String strOutput;
    public int i = 0;
    public SSDPsearch objSSDPSearch;
    public String yeelightcommandstring;

    public TCPConn(int pi, SSDPsearch pSSDPSearch)
    {
        i = pi;
        objSSDPSearch = pSSDPSearch;
    }

    public void run()
    {
        toggle_yeelight(i, objSSDPSearch);
    }

    public void build_yeelightcommandstring(int id, String method, String params)
    {
        yeelightcommandstring = ("{ \"id\": " + id + ", \"method\": \"" + method + "\", \"params\":[" + params + "]} \r\n");
    }

    public void toggle_yeelight(int i, SSDPsearch objSSDPSearch)
    {
        Log.d("toggle_yeelight","Button geklickt " + i);
        try
        {
            strOutput = "";
            // http://forum.iobroker.net/viewtopic.php?t=5439
            // echo -ne '{ "id": 1, "method": "set_power", "params":["on", "smooth", 500]} \r\n' | nc -w1 192.168.2.173 55443
            // connect to Yeelight
            //Socket conn = new Socket(objSSDPSearch.dictionary.get("IP"+i), 55443);
            Socket conn = new Socket(objSSDPSearch.arDevices.get(i).ip, (int)Integer.parseInt(objSSDPSearch.arDevices.get(i).port.toString().trim())); // 55443);

            conn.setSoTimeout(5000);
            OutputStream outp = conn.getOutputStream();
            InputStream inp = conn.getInputStream();

            //Log.d("toogle_yeelight", "gesendet: " + ("{ \"id\": 1, \"method\": \"toggle\", \"params\":[\"" + i + "\", \"smooth\", 500]} \r\n"));
            //strOutput += ("{ \"id\": 1, \"method\": \"toggle\", \"params\":[\"" + i + "\", \"smooth\", 500]} \r\n");
            //outp.write(("{ \"id\": 1, \"method\": \"toggle\", \"params\":[\"" + "on" + "\", \"smooth\", 500]} \r\n").getBytes());
            strOutput += " yeelightcommandstring: " + yeelightcommandstring + "\r\n";
            Log.d("toggle_yee:", strOutput);
            outp.write(yeelightcommandstring.getBytes());
            outp.flush();
            byte[] buffer = new byte[1024];
            int count = inp.read(buffer, 0, 1023);
            strOutput += new String(count + " " + new String(buffer, 0, count));
            outp.close();
            inp.close();
            conn.close();
        }
        catch(UnknownHostException e)
        {Log.d("exc", e.toString());
        }
        catch(IOException e)
        {Log.d("exc", e.toString());
        }
    }
}
