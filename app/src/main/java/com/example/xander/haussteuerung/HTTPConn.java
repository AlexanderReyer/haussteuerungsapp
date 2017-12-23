package com.example.xander.haussteuerung;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by xander on 23.12.2017.
 */

public class HTTPConn implements Runnable{
    public StringBuilder    result = new StringBuilder();

    public void run()
    {
        try
        {
            result.delete(0,result.length());
            getHTML("http://192.168.178.28/cm?cmnd=Power%20toggle");
        }
        catch(Exception e)
        {
            result.append(e.toString());
        }
    }

    public String getHTML(String urlToRead) throws Exception {
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }
}
