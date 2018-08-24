package chibuzo.nwakama.audiogene_collection;

//import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Period;
import java.time.Year;
import java.util.Date;
import java.util.UUID;
import java.util.Calendar;
//import android.widget.Spinner;


public class Collection{
    //dropdown values are Gender, Ethnicity, Genetic Diagnoses, Inheritance Pattern
    private String[] spinnerValues;
    private String familyID;
    private Date date;
    private String relationship;
    private AppCompatActivity app;
    private int age;

    public Collection(String[] spinnerValues, String familyID, Date date, String relationship,
                      AppCompatActivity appCompatActivity) {
        this.spinnerValues = spinnerValues;
        this.familyID = familyID;
        this.date = date;
        this.relationship = relationship;
        this.app = appCompatActivity;

        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(date);
        Calendar today = Calendar.getInstance();

        this.age = Calendar.getInstance().get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
            this.age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        }else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
            this.age--;
        }
        //this.app = appCompatActivity;
    }


    /**
     * Inserting to into a database
     * create random strings in php
     */


    /**
     * Connect to the Server
     */
    public JSONObject sendJSON() throws Exception{
        JSONObject postData = new JSONObject();
        try {
            postData.put("FamilyID", familyID);
            postData.put("Relationship", relationship);
            postData.put("Gender", spinnerValues[0]);
            postData.put("Ethnicity", spinnerValues[1]);
            postData.put("Genetic_Diagnosis", spinnerValues[2]);
            postData.put("Inheritance_Pattern", spinnerValues[3]);
            //postData.put("date_of_collection", "06/06/06"); //Date object
            postData.put("Age", age + ""); //calculate the age
            //http://172.17.141.94:8080/Audiograms/insert

            MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
            //MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            mpEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            mpEntity.addPart("object", new StringBody(postData.toString(), ContentType.APPLICATION_JSON));

            return postData;

            //new BackgroundController(app, postData.toString(), mpEntity).execute("http://128.255.22.123:8080/index.php/patients/insert", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;

        }
    }


}
