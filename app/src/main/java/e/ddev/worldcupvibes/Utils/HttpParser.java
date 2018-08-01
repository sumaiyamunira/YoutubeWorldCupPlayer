package e.ddev.worldcupvibes.Utils;

/**
 * Created by user on 6/24/2018.
 */

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;


public class HttpParser {

    private static int CONNECTION_TIME_OUT = 30000;
    private static int READ_TIME_OUT = 30000;

    public static String sendYouTubeHttpReq(String reqUrl, Context context) throws Exception{
        HttpURLConnection urlConnection = null;
        StringBuilder responseString = null;
        try {
            URL url = new URL(reqUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty ("X-Android-Package", context.getPackageName());
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Language", "en-US");

            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(CONNECTION_TIME_OUT);
            urlConnection.setReadTimeout(READ_TIME_OUT);
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                InputStream it = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader read = new InputStreamReader(it);
                BufferedReader buff = new BufferedReader(read);
                responseString = new StringBuilder();
                String chunks;
                while ((chunks = buff.readLine()) != null) {
                    responseString.append(chunks);
                }
                return responseString.toString();
            } else {
            }
        } catch (IOException e) {
        } catch (InternalError error){
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        throw new IOException ();

    }


}
