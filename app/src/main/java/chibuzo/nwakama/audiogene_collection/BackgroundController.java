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
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Where information is sent to the server
 */
public class BackgroundController extends AsyncTask<String, Void, String> {
    final String urlString = "http://128.255.22.123:8080/index.php/audiograms/insert";
    private MultipartEntityBuilder mpEntity;
    private String encoded_string, image_name;
    @SuppressLint("StaticFieldLeak")
    private AppCompatActivity app;
    private boolean image;
    //private int serverResponseCode = 0;
    private ProgressDialog pd;
    private String rst;

    BackgroundController(AppCompatActivity app, String encoded_string, MultipartEntityBuilder entityBuilder) throws Exception{
        this.app = app;
        this.image = false;
        this.encoded_string = encoded_string;
        pd = new ProgressDialog(app);
        this.mpEntity = entityBuilder;
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

                     HttpPost httpPost = new HttpPost(urlString);
                     HttpEntity entity = mpEntity.build();
                     //
                     httpPost.setEntity(entity);
                     //HttpClient client = new DefaultHttpClient();

                     CloseableHttpClient httpClient = HttpClients.createDefault();
                     CloseableHttpResponse response = httpClient.execute(httpPost);
                     //HttpResponse response = client.execute(httpPost);
                     String st = EntityUtils.toString(response.getEntity());
                     Log.v("log_tag", st);

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

            HttpPost httpPost = new HttpPost(voids[0]);
            HttpEntity entity = mpEntity.build();
            //
            httpPost.setEntity(entity);
            //HttpClient client = new DefaultHttpClient();

            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpPost);
            //HttpResponse response = client.execute(httpPost);
            String st = EntityUtils.toString(response.getEntity());
            rst =st;
            Log.v("log_tag", st);

            /**httpURLConnection = (HttpURLConnection) new URL(voids[0]).openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            //httpURLConnection.setRequestProperty("Host", "android.schoolportal.gr");
            httpURLConnection.connect();
            //httpURLConnection.setRequestProperty("Accept", "application/json");

            DataOutputStream printout;
            DataInputStream input;
            printout = new DataOutputStream(httpURLConnection.getOutputStream ());
            printout.writeBytes(URLEncoder.encode(voids[1],"UTF-8"));
            printout.flush ();
            printout.close ();

            input = new DataInputStream(httpURLConnection.getInputStream());

            int inputStreamData = input.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = input.read();
                data += current;
            }
            Log.v("log_tag", data);
            httpURLConnection.disconnect();*/

            //Writer writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            //writer.write(voids[1]);

            //writer.close();
            /**InputStream inputStream = httpURLConnection.getInputStream();

            BufferedReader reader = null;
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            String JsonResponse = null;
            JsonResponse = buffer.toString(); //response data
            Log.v("log_tag", JsonResponse);*/



            //DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            /**HttpPost post = new HttpPost(urlString);
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
            HttpResponse response;
            StringEntity se = new StringEntity(voids[1]);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
            response = client.execute(post);

            InputStream in = response.getEntity().getContent();


            int inputStreamData = in.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = in.read();
                data += current;
            }
            Log.v("log_tag", data);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;


    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setTitle("Login Status");
        //super.onPreExecute();
        pd.setMessage("Wait Data uploading!");
        pd.show();
    }

    @Override
    protected void onPostExecute(String aVoid) {
        //alertDialog.setMessage(aVoid);
        super.onPostExecute(aVoid);
        pd.hide();
        pd.dismiss();
        //pd.setMessage(rst);
        //pd.show();
        //alertDialog.hide();
        //alertDialog.dismiss();
        makeRequest();
    }

    /**
     *
     */
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

