package ru.gal20040.smsbackuper;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import static ru.gal20040.smsbackuper.ExceptionHandler.LOG_TAG;

public class MainActivity
        extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_sms);
    }

    public  void GetSMSList(View v){
        Context context = this;
        Uri uriSms = Uri.parse("content://sms/");
        Cursor cur = context.getContentResolver().query(uriSms, null,null,null,null);
        startManagingCursor(cur);
        if (cur != null && cur.getCount() > 0) {
            String data = "";
            data = data.concat("SMS count=" + cur.getCount());
            cur.moveToNext();
            String[] strArray = cur.getColumnNames();
            int i = 0;
            String valueType;
            for (String str : strArray) {
                switch (cur.getType(i)) {
                    case (Cursor.FIELD_TYPE_NULL):
                        valueType = "null";
                        break;
                    case (Cursor.FIELD_TYPE_STRING):
                        valueType = "string";
                        break;
                    case (Cursor.FIELD_TYPE_BLOB):
                        valueType = "blob";
                        break;
                    case (Cursor.FIELD_TYPE_FLOAT):
                        valueType = "float";
                        break;
                    case (Cursor.FIELD_TYPE_INTEGER):
                        valueType = "int";
                        break;
                    default:
                        valueType = "!!!unknown!!!";
                        break;
                }
                data = data.concat("\n" + str + ":" + valueType);
                i++;
            }

            ReaderWriter readerWriter = new ReaderWriter();
            String response = readerWriter.writeFileSD("sms_backup", "sms_backup_json.txt", data);
            Log.d(LOG_TAG, response);
        }
    }
}