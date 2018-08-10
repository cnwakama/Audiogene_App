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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
    private MultipartEntity bitmap;
    private String encoded_string, image_name;
    @SuppressLint("StaticFieldLeak")
    private AppCompatActivity app;
    boolean image;

    private ProgressDialog pd;


    BackgroundController(AppCompatActivity app, String image_name, MultipartEntity bit){
        this.app = app;
        image = true;
        this.image_name = image_name;
        this.bitmap = bit;
        pd = new ProgressDialog(app);
    }

    BackgroundController(AppCompatActivity app, String encoded_string, String image_name){
        this.app = app;
        image = false;
        this.encoded_string = encoded_string;
        this.image_name = image_name;
        pd = new ProgressDialog(app);
    }

   // private ProgressDialog pd = new ProgressDialog(BackgroundController.this);


    @Override
    protected String doInBackground(String... voids) {
        if (image) {
            //bitmap = BitmapFactory.decodeFile(file_uri.getPath());
            //ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            //bitmap.recycle();


            //byte[] array = stream.toByteArray();
            //encoded_string = Base64.encodeToString(array, 0);
            //return encoded_string;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://128.255.22.123:8080/index.php/audiograms/insert");

                //String n = voids[0];

                httppost.setEntity(bitmap);
                HttpResponse response = httpclient.execute(httppost);
                String st = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", st);
                return "Success";
            }
            catch (IOException e){
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
                HashMap<String,String> map = new HashMap<>();
                if (encoded_string == null)
                    map.put("image_name", image_name);
                else {
                    map.put("encoded_string", encoded_string);
                    map.put("image_name", image_name);
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
