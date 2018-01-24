package ru.gal20040.smsbackuper;

import android.database.Cursor;

import static ru.gal20040.smsbackuper.SmsFieldType.*;

class SmsField {
    SmsFieldType smsFieldType;
    Object smsFieldValue;

    private SmsField(SmsFieldType smsFieldType, Object smsFieldValue) {
        this.smsFieldType = smsFieldType;
        this.smsFieldValue = smsFieldValue;
    }

    static SmsField getSmsField(Cursor cursor, int columnIndex) {
        SmsField smsField;
        switch (cursor.getType(columnIndex)) {
            case (Cursor.FIELD_TYPE_NULL):
                return new SmsField(NULL, null);
            case (Cursor.FIELD_TYPE_STRING):
                return new SmsField(STRING, cursor.getString(columnIndex));
            case (Cursor.FIELD_TYPE_BLOB):
                return new SmsField(BLOB, cursor.getBlob(columnIndex));
            case (Cursor.FIELD_TYPE_FLOAT):
                return new SmsField(FLOAT, cursor.getDouble(columnIndex));
            case (Cursor.FIELD_TYPE_INTEGER):
                smsField = new SmsField(INT, cursor.getLong(columnIndex));
                return smsField;
            default:
                //todo почему-то предыдущие case после того как полностью отработают перескакивают на этот case - строка с созданием нового SmsField не выполняется, выполняется только return - при этом метод возвращает корректные данные.
                smsField = new SmsField(UNKNOWN_FIELD_TYPE, null);
                return smsField;
        }
    }
}