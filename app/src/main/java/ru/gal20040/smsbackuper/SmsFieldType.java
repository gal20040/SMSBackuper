package ru.gal20040.smsbackuper;

enum SmsFieldType {
    NULL("NULL"),
    STRING("STRING"),
    BLOB("BLOB"),
    FLOAT("FLOAT"),
    INT("INT"),
    UNKNOWN_FIELD_TYPE("!!!unknown field type!!!");

//    private static final String NULL_STRING = NULL.toString();
//    private static final String STRING_STRING = STRING.toString();
//    private static final String BLOB_STRING = BLOB.toString();
//    private static final String FLOAT_STRING = FLOAT.toString();
//    private static final String INT_STRING = INT.toString();
//    private static final String UNKNOWN_FIELD_TYPE_STRING = UNKNOWN_FIELD_TYPE.toString();

    //private final String text;
    public final String text;

    SmsFieldType(final String text) { this.text = text; }

//    @Override
//    public String toString() { return text; }
}