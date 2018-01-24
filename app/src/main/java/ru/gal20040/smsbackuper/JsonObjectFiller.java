package ru.gal20040.smsbackuper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import static ru.gal20040.smsbackuper.ExceptionHandler.LOG_TAG;

class JsonObjectFiller {

    private JSONObject jsonObject;
    private Random random = new Random();

    JsonObjectFiller() {
        jsonObject = new JSONObject();
    }

    public JSONObject getJsonObject() { return jsonObject; }

    void addNewJSONObject(String newName, Object newData) {
        newName = getRandomUniqueName(newName, newData);

        try{
            jsonObject.put(newName, newData);
            Log.i(LOG_TAG, String.format("Added new json object:\nname=%s\ndata=%s", newName, newData));
        } catch (JSONException ex){
            ExceptionHandler exceptionHandler = new ExceptionHandler();
            exceptionHandler.errorAction(ex);
        }
    }

    /**
     * Проверяет строки на null и "" (пустая строка).
     * //todo сделать проверку, что строка содержит хоть какие-то нормальные символы в строке кроме пробелов, переносов строк и прочей фигни.
     *
     * @return true, если строка равна null или "". false - во всех других случаях.
     */
    private boolean isStringNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Проверяет уникальность newName в рамках jsonObject.
     * Предварительно запускает проверку на null и "" через метод isStringNullOrEmpty().
     *
     * @return true, если (newName не null и не "") И (newName нет в составе jsonObject). false - во всех других случаях.
     */
    private boolean isNameUnique(String newName) {
        return !isStringNullOrEmpty(newName)
                && !jsonObject.has(newName);
    }

    private String getRandomUniqueName(String newName, Object newData) {
        if (isStringNullOrEmpty(newName))
            newName = newData.getClass().toString().replace("class java.lang.", "");

        byte randomSeed = 100;
        while (!isNameUnique(newName)) {   //проверяем уникальность newName
            newName += random.nextInt(randomSeed); //подбираем уникальное имя
        }

        return newName;
    }
}