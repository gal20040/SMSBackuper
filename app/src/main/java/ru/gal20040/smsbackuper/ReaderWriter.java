package ru.gal20040.smsbackuper;

import android.os.Environment;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static ru.gal20040.smsbackuper.ExceptionHandler.LOG_TAG;

class ReaderWriter {
    String writeFileSD(String dirName, String fileName, String data) {
        String response = "";
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            response = "SD-карта не доступна: " + Environment.getExternalStorageState();
            Log.d(LOG_TAG, response);
            return response;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + dirName); // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, fileName);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write(data);
            // закрываем поток
            bw.close();
            response = "Файл записан на SD: " + sdFile.getAbsolutePath();
            Log.d(LOG_TAG, response);
        } catch (IOException e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler();
            exceptionHandler.errorAction(e);
        }
        return response;
    }
}