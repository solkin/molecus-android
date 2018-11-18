package com.tomclaw.molecus.util;

import android.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Solkin
 * Date: 04.11.13
 * Time: 14:40
 */
public class HttpUtil {

    public static final String GET = "GET";
    public static final String POST = "POST";

    private static final int TIMEOUT_SOCKET = 70 * 1000;
    private static final int TIMEOUT_CONNECTION = 60 * 1000;

    public static final String UTF8_ENCODING = "UTF-8";

    private static final String EQUAL = "=";
    private static final String AMP = "&";

    /**
     * Builds Url request string from specified parameters.
     *
     * @param pairs
     * @return String - Url request parameters.
     * @throws UnsupportedEncodingException
     */
    public static String prepareParameters(List<Pair<String, String>> pairs)
            throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        // Perform pair concatenation.
        for (Pair<String, String> pair : pairs) {
            if (builder.length() > 0) {
                builder.append(AMP);
            }
            builder.append(pair.first)
                    .append(EQUAL)
                    .append(StringUtil.urlEncode(pair.second));
        }
        return builder.toString();
    }

    public static String executePost(String urlString, HttpParamsBuilder params) throws IOException {
        return executePost(urlString, stringToArray(params.build()));
    }

    public static String executePost(String urlString, byte[] data) throws IOException {
        InputStream responseStream = null;
        HttpURLConnection connection = null;
        try {
            // Create and config connection.
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(TIMEOUT_CONNECTION);
            connection.setReadTimeout(TIMEOUT_SOCKET);

            // Execute request.
            responseStream = HttpUtil.executePost(connection, data);
            return HttpUtil.streamToString(responseStream);
        } catch (IOException ex) {
            throw new IOException(ex);
        } finally {
            try {
                if (responseStream != null) {
                    responseStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static InputStream executePost(HttpURLConnection connection, String data) throws IOException {
        return executePost(connection, stringToArray(data));
    }

    public static InputStream executePost(HttpURLConnection connection, byte[] data) throws IOException {
        connection.setRequestMethod(POST);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        // Write data into output stream.
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(data);
        outputStream.flush();
        // Open connection to response.
        connection.connect();

        return getResponse(connection);
    }

    public static InputStream executeGet(HttpURLConnection connection) throws IOException {
        connection.setRequestMethod(GET);
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setConnectTimeout(TIMEOUT_CONNECTION);
        connection.setReadTimeout(TIMEOUT_SOCKET);

        return getResponse(connection);
    }

    public static InputStream getResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        InputStream in;
        // Checking for this is error stream.
        if (responseCode >= 400) {
            return connection.getErrorStream();
        } else {
            return connection.getInputStream();
        }
    }

    public static String streamToString(InputStream inputStream) throws IOException {
        return new String(streamToArray(inputStream), HttpUtil.UTF8_ENCODING);
    }

    public static byte[] streamToArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, read);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] stringToArray(String string) throws IOException {
        return string.getBytes(HttpUtil.UTF8_ENCODING);
    }
}
