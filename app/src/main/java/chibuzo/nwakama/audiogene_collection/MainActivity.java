package chibuzo.nwakama.audiogene_collection;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static android.icu.util.Calendar.getInstance;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

//Genetics, Pattern, FamilyID use autocomplete
@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private TextView mDisplayDate;
    private TextView id;
    private TextView relationship;
    private ImageView audiogram;
    //private String uploadUrl = "";
    EditText user, pass;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private final Calendar cal = getInstance();
    private static final String TAG = "MainActivity";
    private String[] selectedItems;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    //private Intent intent;
    private Bitmap bitmap;

    String picturePath;

    String ba1;
    private Uri fileUri;
    //Uri selectedImage;

    // new addition
    private String encoded_string, image_name;
    private File file;
    private Uri file_uri;


    String[] spinnerValue = new String[4];

    public void upload(View view) {
        // Image location URL
        //Log.e("path", "----------------" + picturePath);

        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);

            image_name = "Audiogram_" + cal.getTime().toString().replace(" ", "_");

            String l = Environment.getExternalStorageDirectory().getAbsolutePath();
            File f = new File(getApplicationContext().getCacheDir(), image_name +".jpg");
            f.createNewFile();



            byte[] ba = bao.toByteArray();

            //OutputStream os = new BufferedOutputStream(new FileOutputStream(f));

            FileOutputStream file = new FileOutputStream(f);
            file.write(ba);
            file.flush();
            file.close();

            MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            mpEntity.addPart("type", new FileBody(f, "application/octet"));
            mpEntity.addPart("tmp_name", new StringBody(f.getName()));

            //ba1 = Base64.encodeBytes(ba);

            //Log.e("base64", "-----" + ba1);

            // Upload image to server
            new BackgroundController(this, image_name, mpEntity).execute();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getPhoto(View view){
        //Condition - intent.resolveActivity(getPackageManager()) != null

        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

            //file_uri = Uri.fromFile(file);

        }
        else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Uri selectedImage = data.getData();
            Bundle extras = data.getExtras();
            this.bitmap = (Bitmap) extras.get("data");
            audiogram.setImageBitmap(this.bitmap);
        }

        /**if (requestCode == 10 && resultCode == RESULT_OK){
         new BackgroundController(file_uri, this, true).execute();
         }*/
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the Calander date to today's date
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date myDate = new Date();
        selectedItems = new String[4];

        selectedItems[0] = getResources().getStringArray(R.array.Gender)[0];
        selectedItems[1] = getResources().getStringArray(R.array.Ethnicity)[0];
        selectedItems[2] = getResources().getStringArray(R.array.Genetic_Diagnosis)[0];
        selectedItems[3] = getResources().getStringArray(R.array.Pattern)[0];

        //Date d = cal.getTime();
        //String s = sdf.format(myDate);
        mDisplayDate = (TextView) findViewById(R.id.idBirth);
        id = (TextView) findViewById(R.id.familyid);
        relationship = (TextView) findViewById(R.id.rel);

        mDisplayDate.setText(sdf.format(myDate));

        audiogram = (ImageView) findViewById(R.id.imageView);

        //mDisplayDate.setText();

        Spinner s1 = (Spinner) findViewById(R.id.MFO);
        Spinner s2 = (Spinner) findViewById(R.id.race);
        Spinner s3 = (Spinner) findViewById(R.id.diag);
        Spinner s4 = (Spinner) findViewById(R.id.pat);

        //setup the dropdown
         ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Gender));
         myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(myAdapter);

        myAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Ethnicity));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(myAdapter);

        myAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Genetic_Diagnosis));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s3.setAdapter(myAdapter);

        myAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Pattern));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s4.setAdapter(myAdapter);

        s1.setOnItemSelectedListener(this);
        s2.setOnItemSelectedListener(this);
        s3.setOnItemSelectedListener(this);
        s4.setOnItemSelectedListener(this);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //month = month + 1;
                //Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                //String date = month + "/" + day + "/" + year;
                //mDisplayDate.setText(date);
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                // myCalendar.add(Calendar.DATE, 0);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                mDisplayDate.setText(sdf.format(cal.getTime()));
            }
        };



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch (adapterView.getId()) {
            case R.id.MFO:
                selectedItems[0] = adapterView.getSelectedItem().toString();
                break;

            case R.id.race:
                selectedItems[1] = adapterView.getSelectedItem().toString();
                break;

            case R.id.diag:
                selectedItems[2] = adapterView.getSelectedItem().toString();
                break;

            case R.id.pat:
                selectedItems[3] = adapterView.getSelectedItem().toString();
                break;
        }
        //Toast.makeText(MainActivity.this, adapterView.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //Toast.makeText(MainActivity.this, "--------", Toast.LENGTH_SHORT);
    }


    public void getInputs(View v) {
        Log.v(TAG, Arrays.toString(selectedItems) + " " + id.getTextAlignment() + " " + mDisplayDate.getText().toString() + " " + relationship.getText().toString());
        Collection c = new Collection(selectedItems, id.getText().toString(), cal.getTime(), relationship.getText().toString(), this);
        c.sendJSON();

    }

}

