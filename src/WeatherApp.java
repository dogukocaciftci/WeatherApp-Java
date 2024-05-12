import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    // Method to get weather data for a given location name
    public static JSONObject getWeatherData(String locationName){

        // Get location data using the location name
        JSONArray locationData = getLocationData(locationName);

        // Extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // Build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Europe%2FBerlin";

        try {
            // Call API and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            if (conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()){
                resultJson.append(scanner.nextLine());
            }

            scanner.close();

            conn.disconnect();

            // Parse the JSON response and extract weather data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());

            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // Method to get location data for a given location name
    public static JSONArray getLocationData(String locationName){

        // Replace spaces with '+' in the location name
        locationName = locationName.replaceAll(" ", "+");

        // Construct the URL for the geocoding API
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            // Fetch the API response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // Check if the API response is successful
            if (conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                // Read the response and parse it as JSON
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                while (scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();

                conn.disconnect();

                // Parse the JSON response and extract location data
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(resultJson.toString());

                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Method to fetch API response for a given URL
    private static HttpURLConnection fetchApiResponse(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    // Method to find the index of the current time in the time list
    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        for (int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }

        return 0;
    }

    // Method to get the current time in the required format
    public static String getCurrentTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    // Method to convert weather code to human-readable format
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if (weathercode == 0L){
            weatherCondition = "Clear";
        } else if (weathercode <= 3L && weathercode > 0L) {
            weatherCondition = "Cloudy";
        } else if ((weathercode >= 51L && weathercode <= 67L)
                || (weathercode >= 80L && weathercode <= 99L)) {
            weatherCondition = "Rain";
        } else if (weathercode >= 71L && weathercode <= 77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
