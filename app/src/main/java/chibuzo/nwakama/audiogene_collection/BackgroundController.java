package chibuzo.nwakama.audiogene_collection;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Where information is sent to the server
 */
public class BackgroundController extends AsyncTask<String, Void, String> {
    final String URLSTRING = "http://128.255.22.123:8080/index.php/patients/insert";
    private MultipartEntityBuilder mpEntity;
    private String encoded_string;//, image_name;
    @SuppressLint("StaticFieldLeak")
    private AppCompatActivity app;
    private ProgressDialog pd;

  BackgroundController(AppCompatActivity app, String encoded_string, MultipartEntityBuilder entityBuilder) throws Exception{
        this.app = app;
        //this.image = false;
        this.encoded_string = encoded_string;
        pd = new ProgressDialog(app);
        this.mpEntity = entityBuilder;
    }

    BackgroundController(AppCompatActivity app, String image_name, MultipartEntityBuilder bitmap, boolean image) throws Exception{
        this.app = app;
        //this.image = true;
        //this.image_name = image_name;
        pd = new ProgressDialog(app);
        this.mpEntity = bitmap;
    }


    @Override
    protected String doInBackground(String... voids) {



        //String data = "";

        //HttpURLConnection httpURLConnection = null;
        try {

            HttpPost httpPost = new HttpPost(URLSTRING);
            HttpEntity entity = mpEntity.build();
            //
            httpPost.setEntity(entity);
            //HttpClient client = new DefaultHttpClient();

            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpPost);
            //HttpResponse response = client.execute(httpPost);
            String st = EntityUtils.toString(response.getEntity());
          Log.v("log_tag", st);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Success";


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
        StringRequest request = new StringRequest(Request.Method.POST, URLSTRING,
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
                /**if (encoded_string == null)
                    //map.put("image_name", image_name);
                else {*/
                    map.put("encoded_string", encoded_string);
                    //map.put("image_name", image_name);
                //}

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

