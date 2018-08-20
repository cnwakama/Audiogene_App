package chibuzo.nwakama.audiogene_collection;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBar;


//import com.android.internal.http.multipart.MultipartEntity;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import static android.icu.util.Calendar.getInstance;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

//Genetics, Pattern, FamilyID use autocomplete
@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private TextView mDisplayDate;
    private TextView id;
    private TextView relationship;
    private List<Bitmap> audiograms;
    private int index;
    //private ImageView audiogram;
    //private String uploadUrl = "";
    EditText user, pass;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Calendar cal = getInstance();
    private static final String TAG = "MainActivity";
    private String[][] selectedItems;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    //private Intent intent;
    //private Bitmap bitmap;

    private List<UserModel> images;
    //private ExpandableHeightListView listView;
    private  ListView listView;
    private CustomAdapter adapter;

    //String picturePath;

    //String ba1;
    //private Uri fileUri;
    //Uri selectedImage;

    // new addition
    //private String encoded_string, image_name;
    //private File file;
    //private Uri file_uri;


    //String[] spinnerValue = new String[4];

    /**@Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.option_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item); // get the spinner
        //spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        return super.onCreateOptionsMenu(menu);
    }*/

    public void upload(View view) throws Exception {
        // Image location URL
        //Log.e("path", "----------------" + picturePath);
        LinkedList<UserModel> clone = ((LinkedList<UserModel>) images);
        clone = (LinkedList<UserModel>) clone.clone();
        Queue<UserModel> namesOfPics = clone;
        for (Bitmap bitmap: audiograms) {
            //try {
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);

                //new 2
                String name = namesOfPics.remove().userName;
                ContentBody contentBody = new ByteArrayBody(bao.toByteArray(), name);
                //MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
                //MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                //reqEntity.addPart("upload_file", contentBody);
                //new BackgroundController(this, name, reqEntity, true).execute();

                //String l = Environment.getExternalStorageDirectory().getAbsolutePath();
                File f = new File(getApplicationContext().getCacheDir(), name);
                f.createNewFile();


                byte[] ba = bao.toByteArray();

                OutputStream os = new BufferedOutputStream(new FileOutputStream(f));

                FileOutputStream file = new FileOutputStream(f);
                file.write(ba);
                file.flush();
                file.close();

                //com.android.internal.http.multipart.MultipartEntity m = new com.android.internal.http.multipart.MultipartEntity();


                MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
                        //MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                mpEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                mpEntity.addPart("upload_file", new FileBody(f, ContentType.DEFAULT_BINARY));
                mpEntity.addPart("name", new StringBody(f.getName(), ContentType.MULTIPART_FORM_DATA));
                //mpEntity.addPart("type", new StringBody(f.getName(), ContentType.MULTIPART_FORM_DATA));

                //ba1 = Base64.encodeBytes(ba);

                //Log.e("base64", "-----" + ba1);

                // Upload image to server
            new BackgroundController(this, name, mpEntity, true).execute();




            /**
             * HttpClient httpclient = new DefaultHttpClient();
             HttpPost httppost = new HttpPost("http://128.255.22.123:8080/index.php/audiograms/insert");

             //String n = voids[0];

             httppost.setEntity(bitmap);
             HttpResponse response = httpclient.execute(httppost);
             String st = EntityUtils.toString(response.getEntity());
             Log.v("log_tag", st);
             */
                //new BackgroundController(this, f.getName(), f, true).execute();
                //String st = EntityUtils.toString(response.getEntity());
           // } catch (Exception e) {
             //   e.printStackTrace();
            //}
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
            Bitmap bitmap = (Bitmap) extras.get("data");
            audiograms.add(bitmap);
            //audiogram.setImageBitmap(this.bitmap);
            cal = Calendar.getInstance();
            String image_name = "Audiogram_" + cal.getTime().toString().replace(" ", "_") + ".jpeg";
            images.add(new UserModel(image_name));
            //listView.setAdapter(adapter);
            adapter.updateRecords(images);

        }

        /**if (requestCode == 10 && resultCode == RESULT_OK){
         new BackgroundController(file_uri, this, true).execute();
         }*/
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        images = new LinkedList<>();
        //listView = (ExpandableHeightListView) findViewById(R.id.expand_view);
        listView = (ListView) findViewById(R.id.expand_view);
        adapter = new CustomAdapter(this, R.layout.item_list_item ,images);
        audiograms = new ArrayList<>();

        listView.setAdapter(adapter);


        //set the Calander date to today's date
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date myDate = new Date();
        selectedItems = new String[2][4];

        selectedItems[0][0] = getResources().getStringArray(R.array.Gender)[0];
        selectedItems[0][1] = getResources().getStringArray(R.array.Ethnicity)[0];
        selectedItems[0][2] = getResources().getStringArray(R.array.Genetic_Diagnosis)[0];
        selectedItems[0][3] = getResources().getStringArray(R.array.Pattern)[0];

        //Date d = cal.getTime();
        //String s = sdf.format(myDate);
        mDisplayDate = (TextView) findViewById(R.id.idBirth);
        id = (TextView) findViewById(R.id.familyid);
        relationship = (TextView) findViewById(R.id.rel);

        mDisplayDate.setText(sdf.format(myDate));
        mDisplayDate.setInputType(InputType.TYPE_NULL);

        //audiogram = (ImageView) findViewById(R.id.imageView);

        //mDisplayDate.setText();

        Spinner s1 = (Spinner) findViewById(R.id.MFO);
        Spinner s2 = (Spinner) findViewById(R.id.race);
        Spinner s3 = (Spinner) findViewById(R.id.diag);
        Spinner s4 = (Spinner) findViewById(R.id.pat);

        //setup the dropdown
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Gender));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(myAdapter);

        myAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Ethnicity));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(myAdapter);

        myAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Genetic_Diagnosis));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s3.setAdapter(myAdapter);

        myAdapter = new ArrayAdapter<>(MainActivity.this,
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
                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                mDisplayDate.setText(sdf.format(cal.getTime()));
            }
        };


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final AlertDialog.Builder alertadd = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                final View view2 = factory.inflate(R.layout.sample, null);
                ImageView photo = view2.findViewById(R.id.dialog_imageview);
                photo.setImageBitmap(audiograms.get(i));
                photo.setScaleType(ImageView.ScaleType.FIT_XY);
                alertadd.setView(view2);
                alertadd.setNeutralButton("Exit!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                        dlg.dismiss();
                    }
                });

                alertadd.show();
                return false;
            }
        });


    }

    @Override
    public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) {
        String item = null;
        index = 5;
        final int d = adapterView.getId();

        switch (adapterView.getId()) {
            case R.id.MFO:
                item = adapterView.getSelectedItem().toString();
                selectedItems[0][index = 0] = item;
                break;

            case R.id.race:
                item = adapterView.getSelectedItem().toString();
                selectedItems[0][index = 1] = item;
                break;

            case R.id.diag:
                item = adapterView.getSelectedItem().toString();
                selectedItems[0][index = 2] = item;
                break;

            case R.id.pat:
                item = adapterView.getSelectedItem().toString();
                selectedItems[0][index = 3] = item;
                break;
        }
        if (item != null){
            if (item.equals("Other")){
                final AlertDialog.Builder alertadd = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                final View view2 = factory.inflate(R.layout.other_input, null);
                final TextView textView = view2.findViewById(R.id.input_text);

                alertadd.setView(view2);

                DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent KEvent) {
                        int keyaction = KEvent.getAction();

                        if(keyaction == KeyEvent.ACTION_DOWN)
                        {
                            int keycode = KEvent.getKeyCode();
                            int keyunicode = KEvent.getUnicodeChar(KEvent.getMetaState() );
                            //char character = (char) keyunicode;
                            if (keycode==KeyEvent.KEYCODE_ENTER){
                                final StringBuilder sb = new StringBuilder(textView.getText().length());
                                sb.append(textView.getText());
                                selectedItems[0][index] = sb.toString();
                                Spinner t = findViewById(adapterView.getId());
                                SpinnerAdapter spinnerAdapter = t.getAdapter();
                                ArrayAdapter<String> a = (ArrayAdapter<String>) spinnerAdapter;
                                ArrayList<String> items = new ArrayList<>();
                                for (int i = 0; i<a.getCount() + 1; i++){
                                    if (i == 0) {
                                        items.add(sb.toString());
                                        continue;
                                    }
                                    if ((a.getItem(i - 1)).equals(selectedItems[1][index])){
                                        continue;
                                    }
                                    items.add(a.getItem(i - 1));
                                }
                                selectedItems[1][index] = selectedItems[0][index];

                                ArrayAdapter<String> arrAdapt= new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                                Spinner ss = findViewById(d);
                                ss.setAdapter(arrAdapt);

                                dialog.dismiss();
                            }
                        }
                        return false;
                    }
                };
                alertadd.setOnKeyListener(keylistener);
                /**alertadd.setNeutralButton("Exit!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                        dlg.dismiss();
                    }
                });*/

                alertadd.show();
            }
        }
        //Toast.makeText(MainActivity.this, adapterView.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //Toast.makeText(MainActivity.this, "--------", Toast.LENGTH_SHORT);
    }


    public void getInputs(View v) throws Exception{
        Log.v(TAG, Arrays.toString(selectedItems) + " " + id.getTextAlignment() + " " + mDisplayDate.getText().toString() + " " + relationship.getText().toString());
        Collection c = new Collection(selectedItems[0], id.getText().toString(), cal.getTime(), relationship.getText().toString(), this);
        c.sendJSON();

    }

}

