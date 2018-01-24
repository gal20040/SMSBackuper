package ru.gal20040.smsbackuper;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ru.gal20040.smsbackuper.ExceptionHandler.LOG_TAG;
import static ru.gal20040.smsbackuper.SmsField.getSmsField;

/*{
	"Description": "SMS backup",
	"Backup date": "today",
	"SMS count": "939",
	"default_field_types": [
				{"sms_counter_zero_based": "INT"},
				{"sender_number": "STRING"},
				{"text": "STRING"},
			    {"send_date": "FLOAT"},
				{"other_field_name_1": "NULL"},
				{"other_field_name_2": "BLOB"},
				...
	//для каждого элемента в sms_array могут быть определены свои типы полей (только для отличающихся типов),
	//например: {"send_date": "STRING"} и {"send_date_milliseconds": "FLOAT"}.
	//если в элементе sms_array отсутствует или пуст массив field_types, значит типы полей соответствуют default_field_types.
	],
	"sms_array": [ (JSONArray)
		{ (JSONArrayItem)
			"sms_counter_zero_based": "1",
			"sender_number": "+7987 654 32 10"
			"text": "asdasdasdasd"
			"send_date": "2017.12.12 12:12:12"
			"other_fields": [
				{"other_field_name_1": "other_field_value"},
				{"other_field_name_2": "other_field_value"}
			]
			"field_types": [
			    {"send_date": "STRING"},
				{"send_date_milliseconds": "FLOAT"},
			]
		}
		{ (JSONArrayItem)
			"sms_counter_zero_based": "2",
			"sender_number": "+7987 654 32 10"
			"text": "asdasdasdasd"
			"send_date": "2017.12.12 12:12:12"
			"other_fields": [
				{"other_field_name_1": "other_field_value"},
				{"other_field_name_2": "other_field_value"}
			]
			"field_types": []
		}
	]
}*/

public class MainActivity extends AppCompatActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private final String dateTimeFormat = "HH:mm:ss dd.MM.yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_sms);
    }

    public void GetSMSList(View v){
        JsonObjectFiller smsBackup;
        Uri uriSms = Uri.parse("content://sms/");
        Cursor cursor = this.getContentResolver().query(uriSms, null, null, null, null);
        startManagingCursor(cursor);
        if (cursor == null || cursor.getCount() <= 0) { return; }

        smsBackup = new JsonObjectFiller();
        parseSmsListToJsonObject(cursor, smsBackup);
        cursor.close();

        if (smsBackup.getJsonObject().length() > 0) {
            try {
                ReaderWriter readerWriter = new ReaderWriter();
                String response = readerWriter.writeFileSD("sms_backup", "sms_backup_json.txt",
                        smsBackup.getJsonObject().toString(3));
                Log.d(LOG_TAG, response);
            } catch (JSONException e) {
                ExceptionHandler exceptionHandler = new ExceptionHandler();
                exceptionHandler.errorAction(e);
            }
        }
    }

    private void parseSmsListToJsonObject(Cursor cursor, JsonObjectFiller smsBackup) {
        if (cursor == null || cursor.getCount() <= 0) { return; }

        JSONArray defaultFieldTypes = new JSONArray();
        JSONArray smsArray = new JSONArray();

        //service info
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateTimeFormat);
        smsBackup.addNewJSONObject("description", "SMS backup");
        smsBackup.addNewJSONObject("backup_date", dateFormat.format(new Date()));
        smsBackup.addNewJSONObject("sms_count", cursor.getCount());

        fillUpSmsArray(cursor, defaultFieldTypes, smsArray);
        smsBackup.addNewJSONObject("default_field_types", defaultFieldTypes);
        smsBackup.addNewJSONObject("sms_array", smsArray);
    }

    //todo подумать, как не перепутать между собой два JSONArray.
    private void fillUpSmsArray(Cursor cursor, JSONArray defaultFieldTypes, JSONArray smsArray) {
        short smsCounter_zeroBased = 0;
        String fieldName;

        Map<String, SmsFieldType> defaultFieldTypesMap =
                new HashMap<>(); //только для быстрой проверки наличия поля с нужным типом в JSONArray defaultFieldTypes.

        JsonObjectFiller currentSms;
        JSONArray currentSmsOtherFields;
        JSONArray currentSmsFieldTypes;

        while(cursor.moveToNext()) {
            currentSms = new JsonObjectFiller();
            currentSms.addNewJSONObject("smsCounter_zeroBased", smsCounter_zeroBased);

            currentSmsOtherFields = new JSONArray();
            currentSmsFieldTypes = new JSONArray();

            for (int columnIndex = 0; columnIndex < cursor.getColumnCount(); columnIndex++) {
                fieldName = cursor.getColumnName(columnIndex);

                SmsField smsField = getSmsField(cursor, columnIndex);

                //заполняем defaultFieldTypes/currentSmsFieldTypes
                processCurrentFieldType(defaultFieldTypesMap,
                        fieldName, smsField.smsFieldType,
                        defaultFieldTypes, currentSmsFieldTypes);

                //заполняем smsArray
                currentSms.addNewJSONObject(fieldName, smsField.smsFieldValue);
            }

            if (currentSmsOtherFields.length() > 0)
                currentSms.addNewJSONObject("other_fields", currentSmsOtherFields);
            if (currentSmsFieldTypes.length() > 0)
                currentSms.addNewJSONObject("field_types", currentSmsFieldTypes);

            smsArray.put(currentSms.getJsonObject());
            smsCounter_zeroBased++;
        }
    }

    private void processCurrentFieldType(Map<String, SmsFieldType> defaultFieldTypesMap,
                                         String fieldName, SmsFieldType smsFieldType,
                                         JSONArray defaultFieldTypes, JSONArray currentSmsFieldTypes) {
        if (defaultFieldTypesMap.containsKey(fieldName)) {
            if (defaultFieldTypesMap.get(fieldName) != smsFieldType) {
                try {
                    JSONObject jso = new JSONObject();
                    jso.put(fieldName, smsFieldType.text);
                    currentSmsFieldTypes.put(jso);
                } catch (JSONException e) {
                    ExceptionHandler exceptionHandler = new ExceptionHandler();
                    exceptionHandler.errorAction(e);
                }
            }
        } else {
            defaultFieldTypesMap.put(fieldName, smsFieldType);
            try {
                JSONObject jso = new JSONObject();
                jso.put(fieldName, smsFieldType.text);
                defaultFieldTypes.put(jso);
            } catch (JSONException e) {
                ExceptionHandler exceptionHandler = new ExceptionHandler();
                exceptionHandler.errorAction(e);
            }
        }
    }
}