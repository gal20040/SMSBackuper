package ru.gal20040.smsbackuper;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity
        extends AppCompatActivity {

    EditText mText;
    String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_sms);
        mText = findViewById(R.id.editText);
    }

    public  void GetSMSList(View v){
        Context context = this;
        String retStr = readFromFile(context);
        File path = context.getFilesDir();

        String data = "some data";
        writeToFile(data, context);
        Uri uriSms = Uri.parse("content://sms/");
        Cursor cur = context.getContentResolver().query(uriSms, null,null,null,null);
        startManagingCursor(cur);
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        if (cur.getCount() > 0){
            mText.append("SMS count=" + cur.getCount());
            cur.moveToNext();
            String[] strArray = cur.getColumnNames();
            for (String str : strArray) {
                mText.append("\n" + str);
            }
        }
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
//            errorAction(e);
            Log.e(LOG_TAG, "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void errorAction(Exception ex) {
        Log.d(LOG_TAG, getStackTrace(ex));
    }

    private static String getStackTrace(Exception ex) {
        StringBuilder sb = new StringBuilder(500);
        StackTraceElement[] st = ex.getStackTrace();
        sb.append(ex.getClass().getName()).append(": ").append(ex.getMessage()).append("\n");
        for (StackTraceElement aSt : st) {
            sb.append("\t at ").append(aSt.toString()).append("\n");
        }
        return sb.toString();
    }
}