package se.umu.nien1121.museumapplication.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class used for reading JSON and converting it to an {@link JSONObject}.
 */
public class JsonReader {

    /**
     * Utility method used for parsing a url to a {@link JSONObject}.
     *
     * @param url url to fetch JSON from
     * @return JSONObject contaning results from url fetch
     * @throws IOException   if url is malformed or helper method throws exception.
     * @throws JSONException if JSONObject can't be created from results of fetch.
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    /**
     * Helper method used to convert a {@link Reader}s contents to a string.
     *
     * @param rd reader object to be examined
     * @return string containing all of the readers contents.
     * @throws IOException if reading from Reader object throws exception
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
