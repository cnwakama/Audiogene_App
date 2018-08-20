package chibuzo.nwakama.audiogene_collection;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BackgroundController extends AsyncTask<String, Void, String> {
    final String urlString = "http://128.255.22.123:8080/index.php/audiograms/insert";
    private MultipartEntityBuilder mpEntity;
    private String encoded_string, image_name;
    @SuppressLint("StaticFieldLeak")
    private AppCompatActivity app;
    private boolean image;
    //private int serverResponseCode = 0;
    private ProgressDialog pd;

    BackgroundController(AppCompatActivity app, String encoded_string) throws Exception{
        this.app = app;
        this.image = false;
        this.encoded_string = encoded_string;
        pd = new ProgressDialog(app);
    }

    BackgroundController(AppCompatActivity app, String image_name, MultipartEntityBuilder bitmap, boolean image) throws Exception{
        this.app = app;
        this.image = true;
        this.image_name = image_name;
        pd = new ProgressDialog(app);
        this.mpEntity = bitmap;
    }


    @Override
    protected String doInBackground(String... voids) {
        if (image) {
            try {
                /**URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setFixedLengthStreamingMode(mpEntity.getContentLength());
                //conn.addRequestProperty("Content-length", reqEntity.getContentLength() + "");
                conn.addRequestProperty(mpEntity.getContentType().getName(), mpEntity.getContentType().getValue());

                OutputStream os = conn.getOutputStream();
                mpEntity.writeTo(conn.getOutputStream());
                os.close();
                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = null;
                    StringBuilder builder = new StringBuilder();

                        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                            builder.append("\n");
                        }
                        Log.v("Result", builder.toString());

                }*/


            //try {
            /**String sourceFileUri = mpEntity.getAbsolutePath();

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);

            if (sourceFile.isFile()) {

                try {
                    String upLoadServerUri = "http://128.255.22.123:8080/index.php/audiograms/insert";

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP connection to the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", sourceFileUri);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + sourceFileUri + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math
                                .min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,
                                bufferSize);

                    }

                    // send multipart form data necesssary after file
                    // data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    //conn.getErrorStream();
                    //String str = conn.getContentEncoding();
                    //String i = conn.getHeaderField(0);
                    // Responses from the server (code and message)
                    //serverResponseCode = conn.getResponseCode();
                    //String serverResponseMessage = conn.getResponseMessage();
                    BufferedReader br;

                    if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    String result = null;
                    StringBuffer sb = new StringBuffer();
                    String inputLine = "";

                    while ((inputLine = br.readLine()) != null) {
                        sb.append(inputLine);
                        sb.append("\n");
                    }
                    result = sb.toString();
                    Log.v("upload_File", result);

                   // if (serverResponseCode == 200) {

                        // messageText.setText(msg);
                        //Toast.makeText(ctx, "File Upload Complete.",
                        //      Toast.LENGTH_SHORT).show();

                        // recursiveDelete(mDirectory1);

                    //}

                    // close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                    conn.disconnect();*/


                     HttpPost httpPost = new HttpPost("http://128.255.22.123:8080/index.php/audiograms/insert");
                     HttpEntity entity = mpEntity.build();
                     //
                     httpPost.setEntity(entity);
                     //HttpClient client = new DefaultHttpClient();

                     CloseableHttpClient httpClient = HttpClients.createDefault();
                     CloseableHttpResponse response = httpClient.execute(httpPost);
                     //HttpResponse response = client.execute(httpPost);
                     String st = EntityUtils.toString(response.getEntity());
                     Log.v("log_tag", st);
                    //HttpClient httpclient = new DefaultHttpClient();
                     //HttpPost httppost = new HttpPost("http://128.255.22.123:8080/index.php/audiograms/insert");

                     //String n = voids[0];

                     //httppost.setEntity(bitmap);
                     //HttpResponse response = httpclient.execute(httppost);
                     //String st = EntityUtils.toString(response.getEntity());
                     //Log.v("log_tag", st);
                    //String st = EntityUtils.toString(serverResponseMessage);
                    //Log.v("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode + " " + i + ":" + str);
                    return "Success";
                    //return "Success";
                }
                catch (IOException e) {
                    e.printStackTrace();
                    return "Fail"; //null
                }
            }


        String data = "";

        HttpURLConnection httpURLConnection = null;
        try {

            httpURLConnection = (HttpURLConnection) new URL(voids[0]).openConnection();
            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes("PostData=" + voids[1]);
            wr.flush();
            wr.close();

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return data;


    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setTitle("Login Status");
        //super.onPreExecute();
        pd.setMessage("Wait image uploading!");
        pd.show();
    }

    @Override
    protected void onPostExecute(String aVoid) {
        //alertDialog.setMessage(aVoid);
        super.onPostExecute(aVoid);
        pd.hide();
        pd.dismiss();
        //alertDialog.hide();
        //alertDialog.dismiss();
        makeRequest();
    }

    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(app);
        StringRequest request = new StringRequest(Request.Method.POST, "http://128.255.22.123:8080/index.php/audiograms/insert",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                if (encoded_string == null)
                    map.put("image_name", image_name);
                else {
                    map.put("encoded_string", encoded_string);
                    //map.put("image_name", image_name);
                }

                return map;
            }
        };
        requestQueue.add(request);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}

