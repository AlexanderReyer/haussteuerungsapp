package com.example.xander.haussteuerung;

/**
 * Created by xander on 18.12.2017.
 */


// "X:\Program Files\Android\Android Studio\jre\bin\javac" -cp x:\
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SSDPsearch implements Runnable{
        public      String strOutput = "";
        public      Map<String, String> dictionary = new HashMap<String, String>();
        TextView    edtOutput;
        String[]    strLine = new String[255];
        ArrayList<device>   arDevices;


        public SSDPsearch(TextView tvOut, ArrayList devices)
        {
            edtOutput = tvOut;
            arDevices = devices;
        }

        boolean arrayListcontainsIP(String ip)
        {
            for(device d: arDevices)
            {
                if(d.ip.equals(ip))
                    return true;
            }
            return false;
        }

        public void run()
        {
            device dev;
            try {

                final String eol = System.getProperty("line.separator");

                DatagramSocket server = new DatagramSocket();
                server.setSoTimeout(3000);
                //System.out.println("DatagramSocket startet at " + server.getPort() + " " + server);
//                edtOutput.setText("DatagramSocket startet at " + server.getPort() + " " + server);
                strOutput = ("DatagramSocket startet at " + server.getPort() + " " + server);
                //String strText = "M-SEARCH * HTTP/1.1\r\n HOST:239.255.255.250:1982\r\n MAN:\"ssdp:discover\"\r\n ST:yeelink:yeebox\r\n MAC:00000001\r\n MX:3\r\n\n\r\n";
                String strText = "M-SEARCH * HTTP/1.1\r\nHOST: 239.255.255.250:1982\r\nMAN: \"ssdp:discover\"\r\nST: wifi_bulb \r\n";

                byte buffer[] = new byte[65536];
                buffer = new String(strText).getBytes(); //"utf-8");
                DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("239.255.255.250"), 1982);

                //System.out.println("client send started to " + dPacket.getAddress());
                strOutput += ("client send started to " + dPacket.getAddress());
                Log.d("client send started to", "" + dPacket.getAddress());
                server.send(dPacket);    // blocking
                buffer = new byte[25536];
                dPacket = new DatagramPacket(buffer, buffer.length);
                int i = 0;
                try
                {
                    arDevices.clear();

                    while (i < 255)
                    {
                        Log.d("Socket","receiving Packet");
                        server.receive(dPacket);
                        strLine[i] = new String(dPacket.getData(), 0, dPacket.getLength());
                        Log.d("Packet getData: ", strLine[i] + i);
                        if(0 <= strLine[i].indexOf("Location"))
                        {
                            strLine[i] = strLine[i].substring(strLine[i].indexOf("Location: yeelight://") + 21, strLine[i].indexOf("\n", strLine[i].indexOf("Location")));
                            Log.d("dictionary put: ", strLine[i]);
                            strOutput += "id: " + i + " " + strLine[i];
                            dev = new device();
                            dev.id = i;
                            dev.ip = strLine[i].split(":")[0];
                            dev.name = strLine[i].split(":")[0];
                            dev.port = strLine[i].split(":")[1];
                            dev.power = false;
                            if(!arrayListcontainsIP(dev.ip))
                            {
                                arDevices.add(dev);
                                i++;
                            }
                        }
                        Log.d("app", new String(dPacket.getData(), 0, dPacket.getLength() ));
                        //System.out.println(new String(dPacket.getData(), 0, dPacket.getLength() ));
                        strOutput += (new String(dPacket.getData(), 0, dPacket.getLength()));
                    }
                    }
                catch (SocketTimeoutException e) {
                    Log.d("excto", e.toString());
                    //System.out.println(e);
                    strOutput += (e);
                }
                Log.d("dictionary: ", dictionary.toString());
                if(server.isConnected())
                {
                    server.close();
                }
                for(device d: arDevices) {
                    Log.d("arDevices: ", d.ip + ":" + d.port + " id: " + d.id);
                }
                //return strOutput;
            }
            catch (IOException e)
            {
                Log.d("exc", e.toString());
                edtOutput.setText(e.toString());
            }
        }
}


