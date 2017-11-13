package org.telegram.services;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.telegram.BuildVars;
import org.telegram.database.DatabaseManager;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief Weather service
 * @date 20 of June of 2015
 */
public class WeatherService {
    private static final String LOGTAG = "WEATHERSERVICE";

    public static final String METRICSYSTEM = "metric";
    public static final String IMPERIALSYSTEM = "imperial";

    private static final String BASEURL = "http://api.openweathermap.org/data/2.5/"; ///< Base url for REST
    private static final String FORECASTPATH = "forecast/daily";
    private static final String CURRENTPATH = "weather";
    private static final String APIIDEND = "&APPID=" + BuildVars.OPENWEATHERAPIKEY;
    private static final String FORECASTPARAMS = "&cnt=3&units=@units@&lang=@language@";
    private static final String ALERTPARAMS = "&cnt=1&units=@units@&lang=@language@";
    private static final String CURRENTPARAMS = "&cnt=1&units=@units@&lang=@language@";
    private static final DateTimeFormatter dateFormaterFromDate = DateTimeFormatter.ofPattern("dd/MM/yyyy"); ///< Date to text formater
    private static volatile WeatherService instance; ///< Instance of this class

    /**
     * Constructor (private due to singleton pattern)
     */
    private WeatherService() {
    }

    /**
     * Singleton
     *
     * @return Return the instance of this class
     */
    public static WeatherService getInstance() {
        WeatherService currentInstance;
        if (instance == null) {
            synchronized (WeatherService.class) {
                if (instance == null) {
                    instance = new WeatherService();
                }
                currentInstance = instance;
            }
        } else {
            currentInstance = instance;
        }
        return currentInstance;
    }

    /**
     * Fetch the weather of a city for one day
     *
     * @param cityId City to get the weather
     * @return userHash to be send to use
     * @note Forecast for the day
     */
    public String fetchWeatherAlert(int cityId, int userId, String language, String units) {
        String cityFound;
        String responseToUser;
        try {
            String completURL = BASEURL + FORECASTPATH + "?" + getCityQuery(cityId + "") +
                    ALERTPARAMS.replace("@language@", language).replace("@units@", units) + APIIDEND;
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(completURL);

            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf, "UTF-8");

            JSONObject jsonObject = new JSONObject(responseString);
            BotLogger.info(LOGTAG, jsonObject.toString());
            if (jsonObject.getInt("cod") == 200) {
                cityFound = jsonObject.getJSONObject("city").getString("name") + " (" +
                        jsonObject.getJSONObject("city").getString("country") + ")";
                saveRecentWeather(userId, cityFound, jsonObject.getJSONObject("city").getInt("id"));
                responseToUser = String.format(LocalisationService.getString("weatherAlert", language),
                        cityFound, convertListOfForecastToString(jsonObject, language, units, false));
            } else {
                BotLogger.warn(LOGTAG, jsonObject.toString());
                responseToUser = LocalisationService.getString("cityNotFound", language);
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
            responseToUser = LocalisationService.getString("errorFetchingWeather", language);
        }
        return responseToUser;
    }

    /**
     * Fetch the weather of a city
     *
     * @param city City to get the weather
     * @return userHash to be send to use
     * @note Forecast for the following 3 days
     */
    public String fetchWeatherForecast(String city, Integer userId, String language, String units) {
        String cityFound;
        String responseToUser;
        try {
            String completURL = BASEURL + FORECASTPATH + "?" + getCityQuery(city) +
                    FORECASTPARAMS.replace("@language@", language).replace("@units@", units) + APIIDEND;
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(completURL);

            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf, "UTF-8");

            JSONObject jsonObject = new JSONObject(responseString);
            BotLogger.info(LOGTAG, jsonObject.toString());
            if (jsonObject.getInt("cod") == 200) {
                cityFound = jsonObject.getJSONObject("city").getString("name") + " (" +
                        jsonObject.getJSONObject("city").getString("country") + ")";
                saveRecentWeather(userId, cityFound, jsonObject.getJSONObject("city").getInt("id"));
                responseToUser = String.format(LocalisationService.getString("weatherForcast", language),
                        cityFound, convertListOfForecastToString(jsonObject, language, units, true));
            } else {
                BotLogger.warn(LOGTAG, jsonObject.toString());
                responseToUser = LocalisationService.getString("cityNotFound", language);
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
            responseToUser = LocalisationService.getString("errorFetchingWeather", language);
        }
        return responseToUser;
    }

    /**
     * Fetch the weather of a city
     *
     * @return userHash to be send to use
     * @note Forecast for the following 3 days
     */
    public String fetchWeatherForecastByLocation(Float longitude, Float latitude, Integer userId, String language, String units) {
        String cityFound;
        String responseToUser;
        try {
            String completURL = BASEURL + FORECASTPATH + "?lat=" + URLEncoder.encode(latitude + "", "UTF-8") + "&lon="
                    + URLEncoder.encode(longitude + "", "UTF-8") +
                    FORECASTPARAMS.replace("@language@", language).replace("@units@", units) + APIIDEND;;
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(completURL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf, "UTF-8");

            JSONObject jsonObject = new JSONObject(responseString);
            if (jsonObject.getInt("cod") == 200) {
                cityFound = jsonObject.getJSONObject("city").getString("name") + " (" +
                        jsonObject.getJSONObject("city").getString("country") + ")";
                saveRecentWeather(userId, cityFound, jsonObject.getJSONObject("city").getInt("id"));
                responseToUser = String.format(LocalisationService.getString("weatherForcast", language),
                        cityFound, convertListOfForecastToString(jsonObject, language, units, true));
            } else {
                BotLogger.warn(LOGTAG, jsonObject.toString());
                responseToUser = LocalisationService.getString("cityNotFound", language);
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
            responseToUser = LocalisationService.getString("errorFetchingWeather", language);
        }
        return responseToUser;
    }

    /**
     * Fetch the weather of a city
     *
     * @param city City to get the weather
     * @return userHash to be send to use
     * @note Forecast for the following 3 days
     */
    public String fetchWeatherCurrent(String city, Integer userId, String language, String units) {
        String cityFound;
        String responseToUser;
        Emoji emoji = null;
        try {
            String completURL = BASEURL + CURRENTPATH + "?" + getCityQuery(city) +
                    CURRENTPARAMS.replace("@language@", language).replace("@units@", units) + APIIDEND;
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(completURL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf, "UTF-8");

            JSONObject jsonObject = new JSONObject(responseString);
            if (jsonObject.getInt("cod") == 200) {
                cityFound = jsonObject.getString("name") + " (" +
                        jsonObject.getJSONObject("sys").getString("country") + ")";
                saveRecentWeather(userId, cityFound, jsonObject.getInt("id"));
                emoji = getEmojiForWeather(jsonObject.getJSONArray("weather").getJSONObject(0));
                responseToUser = String.format(LocalisationService.getString("weatherCurrent", language),
                        cityFound, convertCurrentWeatherToString(jsonObject, language, units, emoji));
            } else {
                BotLogger.warn(LOGTAG, jsonObject.toString());
                responseToUser = LocalisationService.getString("cityNotFound", language);
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
            responseToUser = LocalisationService.getString("errorFetchingWeather", language);
        }
        return responseToUser;
    }

    /**
     * Fetch the weather of a city
     *
     * @return userHash to be send to use
     * @note Forecast for the following 3 days
     */
    public String fetchWeatherCurrentByLocation(Float longitude, Float latitude, Integer userId, String language, String units) {
        String cityFound;
        String responseToUser;
        try {
            String completURL = BASEURL + CURRENTPATH + "?lat=" + URLEncoder.encode(latitude + "", "UTF-8") + "&lon="
                    + URLEncoder.encode(longitude + "", "UTF-8") +
                    CURRENTPARAMS.replace("@language@", language).replace("@units@", units) + APIIDEND;;
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(completURL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf, "UTF-8");

            JSONObject jsonObject = new JSONObject(responseString);
            if (jsonObject.getInt("cod") == 200) {
                cityFound = jsonObject.getString("name") + " (" +
                        jsonObject.getJSONObject("sys").getString("country") + ")";
                saveRecentWeather(userId, cityFound, jsonObject.getInt("id"));
                responseToUser = String.format(LocalisationService.getString("weatherCurrent", language),
                        cityFound, convertCurrentWeatherToString(jsonObject, language, units, null));
            } else {
                BotLogger.warn(LOGTAG, jsonObject.toString());
                responseToUser = LocalisationService.getString("cityNotFound", language);
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
            responseToUser = LocalisationService.getString("errorFetchingWeather", language);
        }
        return responseToUser;
    }

    private String convertCurrentWeatherToString(JSONObject jsonObject, String language, String units, Emoji emoji) {
        String temp = String.format("%+.0f", jsonObject.getJSONObject("main").getDouble("temp"))+"";
        String cloudiness = jsonObject.getJSONObject("clouds").getInt("all") + "%";
        String weatherDesc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
        String winter = get_wind_dir(jsonObject.getJSONObject("main").getInt("deg"))+ " " +
                        String.format("%.0f",jsonObject.getJSONObject("wind").getDouble("speed"));// + " м/с";
        String pressure = calcPressure(jsonObject.getJSONObject("main").getString("pressure"));

        String responseToUser;
        if (units.equals(METRICSYSTEM)) {
            responseToUser = LocalisationService.getString("currentWeatherPartMetric", language);
        } else {
            responseToUser = LocalisationService.getString("currentWeatherPartImperial", language);
        }
        responseToUser = String.format(responseToUser, emoji.toString() + weatherDesc, cloudiness, temp, winter, pressure);

        return responseToUser;
    }

    /**
     * Convert a list of weather forcast to a list of strings to be sent
     *
     * @param jsonObject JSONObject contining the list
     * @return String to be sent to the user
     */
    private String convertListOfForecastToString(JSONObject jsonObject, String language, String units, boolean addDate) {
        String responseToUser = "";
        for (int i = 0; i < jsonObject.getJSONArray("list").length(); i++) {
            JSONObject internalJSON = jsonObject.getJSONArray("list").getJSONObject(i);
            responseToUser += convertInternalInformationToString(internalJSON, language, units, addDate);
        }
        return responseToUser;
    }

    /**
     * Convert internal part of then answer to string
     *
     * @param internalJSON JSONObject containing the part to convert
     * @return String to be sent to the user
     */
    private String convertInternalInformationToString(JSONObject internalJSON, String language, String units, boolean addDate) {
        String responseToUser = "";
        LocalDate date;
        String tempMax;
        String tempMin;
        String weatherDesc;
        String winter;
        String pressure;
        date = Instant.ofEpochSecond(internalJSON.getLong("dt")).atZone(ZoneId.systemDefault()).toLocalDate();
        tempMax = String.format("%+.0f", internalJSON.getJSONObject("temp").getDouble("max")) + "";
        tempMin = String.format("%+.0f", internalJSON.getJSONObject("temp").getDouble("min")) + "";
        JSONObject weatherObject = internalJSON.getJSONArray("weather").getJSONObject(0);
        Emoji emoji = getEmojiForWeather(internalJSON.getJSONArray("weather").getJSONObject(0));
        weatherDesc = weatherObject.getString("description");
        winter = get_wind_dir(internalJSON.getInt("deg"))+ " " +
                        String.format("%.0f",internalJSON.getDouble("speed"));
        pressure = calcPressure(internalJSON.getString("pressure"));
        
        if (units.equals(METRICSYSTEM)) {
            if (addDate) {
                responseToUser = LocalisationService.getString("forecastWeatherPartMetric", language);
            } else {
                responseToUser = LocalisationService.getString("alertWeatherPartMetric", language);
            }
        } else {
            if (addDate) {
                responseToUser = LocalisationService.getString("forecastWeatherPartImperial", language);
            } else {
                responseToUser = LocalisationService.getString("alertWeatherPartImperial", language);
            }
        }
        if (addDate) {
            responseToUser = String.format(responseToUser, Emoji.LARGE_ORANGE_DIAMOND.toString(),
                    dateFormaterFromDate.format(date), emoji.toString() + weatherDesc, tempMin, tempMax, winter, pressure);
        } else {
            responseToUser = String.format(responseToUser, emoji.toString() + weatherDesc,
                    tempMin, tempMax, winter, pressure);
        }

        return responseToUser;
    }

    private void saveRecentWeather(Integer userId, String cityName, int cityId) {
        DatabaseManager.getInstance().addRecentWeather(userId, cityId, cityName);
    }

    private String getCityQuery(String city) throws UnsupportedEncodingException {
        String cityQuery = "";
        try {
            cityQuery += "id=" + URLEncoder.encode(Integer.parseInt(city)+"", "UTF-8");
        } catch(NumberFormatException | NullPointerException  e) {
            cityQuery += "q=" + URLEncoder.encode(city, "UTF-8");
        }
        return cityQuery;
    }

    private Emoji getEmojiForWeather(JSONObject weather) {
        Emoji emoji;

        switch(weather.getString("icon")) {
            case "01n":
            case "01d":
                emoji = Emoji.SUN_WITH_FACE;
                break;
            case "02n":
            case "02d":
                emoji = Emoji.SUN_BEHIND_CLOUD;
                break;
            case "03n":
            case "03d":
            case "04n":
            case "04d":
                emoji = Emoji.CLOUD;
                break;
            case "09n":
            case "09d":
            case "10n":
            case "10d":
                emoji = Emoji.UMBRELLA_WITH_RAIN_DROPS;
                break;
            case "11n":
            case "11d":
                emoji = Emoji.HIGH_VOLTAGE_SIGN;
                break;
            case "13n":
            case "13d":
                emoji = Emoji.SNOWFLAKE;
                break;
            case "50n":
            case "50d":
                emoji = Emoji.FOGGY;
                break;
            default:
                emoji = null;
        }

        return emoji;
    }
   
// Перевод в строку направление ветра    
    private String get_wind_dir(int deg) {
        String[] l0 = {"\u21d1", "\u21d7", "\u21db", "\u21d8", "\u21d3", "\u21d9", "\u21da", "\u21d6"};
        String[] l1 = {"С","СВ","В","ЮВ","Ю","ЮЗ","З","СЗ"};
        int step;
        int min;
        int max;
        String res = null;

        for (int i=0; i<8; i++) {
            step = 45;
            min = i*step - 45/2;
            max = i*step + 45/2;
            if (i == 0 & deg > 360-45/2)
                deg = deg - 360;
            if (deg >= min & deg <= max)
                res = l0[i] + l1[i];
        }
        return res;
    }

// Расчет давления в мм.рт.ст.    
    private String calcPressure(String data) {
        if (data == null || data.isEmpty()) return "";
        double mbar = Double.valueOf(data);
        int mmrs = (int) Math.round((mbar / 1.3332));
        return String.valueOf(mmrs);// + " мм рт.ст.";
    }
}
