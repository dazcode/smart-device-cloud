package com.dazcodeapps.mobileskillcameratest1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class NetworkRequests {

    private static void getJSONObjectFromURL(NetResponseDelegate networkCallback, String urlString, String messageBody) {


        CallNetworkTask networkTask = new CallNetworkTask();
        networkTask.delegate = networkCallback;
        networkTask.execute(urlString, "", messageBody);
    }


    public static void register_smart_device(NetResponseDelegate networkCallback, String url_endpoint, String device_id, String account_id) {


        String register_url_string = url_endpoint + "?device_id=" + device_id + "&account_id=" + account_id;
        Log.d("AWS_REGISTER", register_url_string);
        try {
            getJSONObjectFromURL(networkCallback, register_url_string, "");

        } catch (Exception ex) {
            Log.d("AWSERROR", ex.toString());
        }
    }

    public static void sync_smart_device_status(NetResponseDelegate networkCallback, String url_endpoint, String device_id, String account_id) {


        String register_url_string = url_endpoint + "?device_id=" + device_id + "&account_id=" + account_id;


        Log.d("AWS_REGISTER", register_url_string);

        try {
            getJSONObjectFromURL(networkCallback, register_url_string, "");

        } catch (Exception ex) {
            Log.d("AWSERROR", ex.toString());
        }


    }

    public void upload_smart_photo(String url_endpoint, NetResponseDelegate networkCallback, String device_id, String account_id, String path_to_image) {


        Bitmap bm = BitmapFactory.decodeFile(path_to_image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        encodedImage = encodedImage.replaceAll("\n", "");
        String update_device_url_string = url_endpoint + "?device_id=" + device_id + "&account_id=" + account_id + "&device_on=True";

        Log.d("AWS_SEND_PHOTO", update_device_url_string);
        Log.d("AWS_SEND_PHOTO", encodedImage);


        getJSONObjectFromURL(networkCallback, update_device_url_string, encodedImage);


    }


    public interface NetResponseDelegate {
        void onDataRecieved(Object[] results);
    }


    public static class CallNetworkTask extends AsyncTask<String, Void, Object[]> {


        public NetResponseDelegate delegate;

        protected Object[] doInBackground(String... tmpParams) {
            HttpsURLConnection urlConnection = null;

            String[] params = tmpParams;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                PrintStream printStream = new PrintStream(out);
                printStream.print(String.valueOf(params[2]));
                printStream.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                String jsonString = sb.toString();


                //Object convert labels
                JSONObject jsonResponse = new JSONObject(jsonString);
                JSONArray labelsDetected = jsonResponse.getJSONArray("Labels");

                ArrayList<RekognitionLabel> labels = new ArrayList<RekognitionLabel>();
                for (int i = 0; i < labelsDetected.length(); i++) {
                    JSONObject currentLabel = labelsDetected.getJSONObject(i);
                    String labelName = currentLabel.getString("Name");
                    String labelConfidence = currentLabel.getString("Confidence");


                    RekognitionLabel label = new RekognitionLabel();
                    label.setLabelName(labelName);
                    label.setLabelConfidence(String.valueOf((int) Double.parseDouble(labelConfidence)) + "%");
                    labels.add(label);
                    Log.d("REKOGNITIONLabel", "" + labelName);
                    Log.d("REKOGNITIONName", "" + labelConfidence);
                }

                Log.d("AsyncTask", "" + jsonString);

                String responseType = "";
                if (labels.size() > 0) {
                    responseType = "ReckognitionLabels";
                }

                Object[] result = new Object[2];
                result[0] = responseType;
                result[1] = labels;

                return result;


            } catch (Exception e) {
                Log.d("AsyncTask ERROR!", "" + e.toString());
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }


        }

        protected void onPostExecute(Object[] results) {
            Log.d("onPostExecute", "" + results[0]);
            Log.d("onPostExecute", "" + results[1]);


            delegate.onDataRecieved(results);
        }
    }
}
