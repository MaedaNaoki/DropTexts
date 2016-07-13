package com.example.dm.droptexts;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.format.Time;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Texts extends Activity {

    String tmpCsvPath;
    String filename;

    public Texts() {

    }
    public void write(List<String> locations,String input) {
        System.out.println("入力された値 : "+input);
        try {
            if(input.equals("")) {
                Date now = new Date(System.currentTimeMillis());
                DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
                filename = formatter.format(now);
            }else {
                filename = input;
            }
            //OutputStream out = openFileOutput(filename+".txt", MODE_PRIVATE); //←エラー
            InputStream in = openFileInput( filename+".txt" );
            OutputStream out = openFileOutput( filename+".txt", Context.MODE_PRIVATE );
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
            for(int i=0; i< locations.size(); i++) {
                writer.println(locations.get(i));
            }
            writer.close();
        }catch(Exception e){
            System.out.println("Error : "+ e);
        }
    }
}
