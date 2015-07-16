package com.hozakan.accountchecker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.hozakan.accountchecker.data.AccountCheckerProvider;
import com.hozakan.accountchecker.data.entry.AccountEntry;
import com.hozakan.accountchecker.data.entry.DataClassEntry;
import com.hozakan.accountchecker.data.entry.DataClassToBreachEntry;
import com.hozakan.accountchecker.data.contract.DataClassContract;
import com.hozakan.accountchecker.data.contract.DataClassToBreachContract;
import com.hozakan.accountchecker.data.contract.BreachContract;
import com.hozakan.accountchecker.data.entry.BreachEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by gimbert on 15-07-07.
 */
public class CheckAccountTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = CheckAccountTask.class.getSimpleName();

    private final Context mContext;

    public CheckAccountTask(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    protected Void doInBackground(String... params) {


        if (params.length != 1) {
            return null;
        }

        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String pwndJsonString = null;

        Uri accountUri = AccountEntry.buildUriWithId(params[0]);

        Cursor cursor = mContext.getContentResolver()
                .query(
                        accountUri,
                        new String[] {AccountEntry.COLUMN_ACCOUNT_NAME},
                        null,
                        null,
                        null
                );

        if (!cursor.moveToFirst()) {
            return null;
        }

        final String accountName = cursor.getString(0);

        final String PWND_BASE_URL =
                "https://troyhunt-have-i-been-pwned.p.mashape.com/v2/breachedaccount";

        Uri builtUri = Uri.parse(PWND_BASE_URL).buildUpon()
                .appendPath(accountName)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("X-Mashape-Key", "9SvbaWccaAmshXvPNRyLRTZNMVnEp1w3NW9jsnhJN0ySug15fx");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            getPwndDataFromJson(buffer.toString(), params[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getPwndDataFromJson(String pwndDataString, String accountId) {

        deletePwndDataForAccount(accountId);

        // pwnd information
        final String TITLE = "Title";
        final String NAME = "Name";
        final String DOMAIN = "Domain";
        final String BREACH_DATE = "BreachDate";
        final String ADDED_DATE = "AddedDate";
        final String DESCRIPTION = "Description";
        final String PWN_COUNT = "PwnCount";

        final String DATA_CLASSES = "DataClasses";
//
//        // Location coordinate
//        final String OWM_LATITUDE = "lat";
//        final String OWM_LONGITUDE = "lon";
//
//        // Weather information.  Each day's forecast info is an element of the "list" array.
//        final String OWM_LIST = "list";
//
//        final String OWM_PRESSURE = "pressure";
//        final String OWM_HUMIDITY = "humidity";
//        final String OWM_WINDSPEED = "speed";
//        final String OWM_WIND_DIRECTION = "deg";
//
//        // All temperatures are children of the "temp" object.
//        final String OWM_TEMPERATURE = "temp";
//        final String OWM_MAX = "max";
//        final String OWM_MIN = "min";
//
//        final String OWM_WEATHER = "weather";
//        final String OWM_DESCRIPTION = "main";
//        final String OWM_WEATHER_ID = "id";
        try {
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray pwndArray = new JSONArray(pwndDataString);

            for (int i = 0; i < pwndArray.length(); i++) {
                JSONObject pwndData = pwndArray.getJSONObject(i);
                JSONArray dataClassArray = pwndData.getJSONArray(DATA_CLASSES);

                List<String> dataClassIds = new ArrayList<>();
                for (int j = 0; j < dataClassArray.length(); j++) {
                    dataClassIds.add(addDataClass(dataClassArray.getString(j)));
                }

                ContentValues contentValues = new ContentValues();

                contentValues.put(BreachEntry.COLUMN_ACCOUNT_ID, accountId);
                contentValues.put(BreachEntry.COLUMN_TITLE, pwndData.getString(TITLE));
                contentValues.put(BreachEntry.COLUMN_NAME, pwndData.getString(NAME));
                contentValues.put(BreachEntry.COLUMN_DOMAIN, pwndData.getString(DOMAIN));
                contentValues.put(BreachEntry.COLUMN_BREACH_DATE, pwndData.getString(BREACH_DATE));
                contentValues.put(BreachEntry.COLUMN_ADDED_DATE, pwndData.getString(ADDED_DATE));
                contentValues.put(BreachEntry.COLUMN_DESCRIPTION, pwndData.getString(DESCRIPTION));
                contentValues.put(BreachEntry.COLUMN_BREACH_COUNT, pwndData.getInt(PWN_COUNT));

                Uri uri = mContext.getContentResolver().insert(BreachContract.CONTENT_URI, contentValues);
                String pwdId = BreachContract.getIdFromUri(uri);

                Vector<ContentValues> cVVector = new Vector<>(dataClassIds.size());

                for (String dataClassId : dataClassIds) {
                    contentValues = new ContentValues();
                    contentValues.put(DataClassToBreachEntry.COLUMN_DATACLASS_ID, dataClassId);
                    contentValues.put(DataClassToBreachEntry.COLUMN_BREACH_ID, pwdId);
                    cVVector.add(contentValues);
                }

                mContext.getContentResolver()
                        .bulkInsert(DataClassToBreachContract.CONTENT_URI, cVVector.toArray(new ContentValues[]{}));

            }


//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
//            String cityName = cityJson.getString(OWM_CITY_NAME);
//
//            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
//            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
//            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);
//
//            long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);


//
//            // Insert the new weather information into the database
//            Vector<ContentValues> cVVector = new Vector<>(weatherArray.length());
//
//            // OWM returns daily forecasts based upon the local time of the city that is being
//            // asked for, which means that we need to know the GMT offset to translate this data
//            // properly.
//
//            // Since this data is also sent in-order and the first day is always the
//            // current day, we're going to take advantage of that to get a nice
//            // normalized UTC date for all of our weather.
//
//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();
//
//            for(int i = 0; i < weatherArray.length(); i++) {
//                // These are the values that will be collected.
//                long dateTime;
//                double pressure;
//                int humidity;
//                double windSpeed;
//                double windDirection;
//
//                double high;
//                double low;
//
//                String description;
//                int weatherId;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//                // Cheating to convert this to UTC time, which is what we want anyhow
//                dateTime = dayTime.setJulianDay(julianStartDay+i);
//
//                pressure = dayForecast.getDouble(OWM_PRESSURE);
//                humidity = dayForecast.getInt(OWM_HUMIDITY);
//                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
//                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);
//
//                // Description is in a child array called "weather", which is 1 element long.
//                // That element also contains a weather code.
//                JSONObject weatherObject =
//                        dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//                weatherId = weatherObject.getInt(OWM_WEATHER_ID);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                high = temperatureObject.getDouble(OWM_MAX);
//                low = temperatureObject.getDouble(OWM_MIN);
//
//                ContentValues weatherValues = new ContentValues();
//
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
//                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);
//
//                cVVector.add(weatherValues);
//            }
//
//            // add to database
//            if ( cVVector.size() > 0 ) {
//                // Student: call bulkInsert to add the weatherEntries to the database here
//                getContext().getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, cVVector.toArray(new ContentValues[] {}));
//
//                getContext().getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
//                        WeatherContract.WeatherEntry.COLUMN_DATE + " < ?",
//                        new String[] {Long.toString(WeatherContract.normalizeDate(new Date().getTime()))});
//            }
//
////            // Sort order:  Ascending, by date.
////            String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";
////            Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
////                    locationSetting, System.currentTimeMillis());
////
////            // Students: Uncomment the next lines to display what what you stored in the bulkInsert
////
////            Cursor cur = mContext.getContentResolver().query(weatherForLocationUri,
////                    null, null, null, sortOrder);
////
////            cVVector = new Vector<>(cur.getCount());
////            if ( cur.moveToFirst() ) {
////                do {
////                    ContentValues cv = new ContentValues();
////                    DatabaseUtils.cursorRowToContentValues(cur, cv);
////                    cVVector.add(cv);
////                } while (cur.moveToNext());
////            }
////
////            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");
////
////            String[] resultStrs = convertContentValuesToUXFormat(cVVector);
////            return resultStrs;

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private void deletePwndDataForAccount(String accountId) {
        mContext.getContentResolver()
                .delete(
                    BreachContract.CONTENT_URI,
                    AccountCheckerProvider.sBreachesForAccountIdSelection,
                    new String[] {accountId}
                );
    }

    private String addDataClass(String dataClassName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataClassEntry.COLUMN_NAME, dataClassName);
        return DataClassContract.getIdFromUri(mContext.getContentResolver().insert(DataClassContract.CONTENT_URI, contentValues));
    }
}
