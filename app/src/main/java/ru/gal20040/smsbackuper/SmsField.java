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

    static SmsField getSmsField(Cursor cursor, int fieldNumberZeroBased) {
        switch (cursor.getType(fieldNumberZeroBased)) {
            case (Cursor.FIELD_TYPE_NULL):
                return new SmsField(NULL, null);
            case (Cursor.FIELD_TYPE_STRING):
                return new SmsField(STRING, cursor.getString(fieldNumberZeroBased));
            case (Cursor.FIELD_TYPE_BLOB):
                return new SmsField(BLOB, cursor.getBlob(fieldNumberZeroBased));
            case (Cursor.FIELD_TYPE_FLOAT):
                return new SmsField(FLOAT, cursor.getDouble(fieldNumberZeroBased));
            case (Cursor.FIELD_TYPE_INTEGER):
                return new SmsField(INT, cursor.getLong(fieldNumberZeroBased));
            default:
                return new SmsField(UNKNOWN_FIELD_TYPE, null);
        }
    }
}