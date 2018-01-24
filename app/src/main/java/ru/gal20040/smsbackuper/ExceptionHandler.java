package ru.gal20040.smsbackuper;

import android.util.Log;

class ExceptionHandler {
    static final String LOG_TAG = "myLogs";

    void errorAction(Exception ex) {
        Log.d(LOG_TAG, getStackTrace(ex));
    }

    private String getStackTrace(Exception ex) {
        StringBuilder sb = new StringBuilder(500);
        StackTraceElement[] st = ex.getStackTrace();
        sb.append(ex.getClass().getName()).append(": ").append(ex.getMessage()).append("\n");
        for (StackTraceElement aSt : st) {
            sb.append("\t at ").append(aSt.toString()).append("\n");
        }
        return sb.toString();
    }
}