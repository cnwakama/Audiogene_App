package chibuzo.nwakama.audiogene_collection;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Calendar;


//Genetics, Pattern, FamilyID use autocomplete
@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
	static final int REQUEST_IMAGE_CAPTURE = 1;

	private static final String TAG = "MainActivity";

	EditText user;

	private TextView mDisplayDate;

	private TextView id;

	private TextView relationship;

	private List<Bitmap> audiograms;

	private int index;

	private DatePickerDialog.OnDateSetListener mDateSetListener;

	private Calendar cal = Calendar.getInstance();

	private String[][] selectedItems;

	//private Intent intent;
	//private Bitmap bitmap;

	private List<FileWrapper> images;

	//private ExpandableHeightListView listView;
	private ListView listView;

	private CustomAdapter adapter;

	/**
	 * connects to server and send multiple pictures to the server
	 *
	 * @param view
	 * @throws Exception
	 */
	public void upload(View view) throws Exception
	{
		// Image location URL
		//Log.e("path", "----------------" + picturePath);
		LinkedList<FileWrapper> clone = ((LinkedList<FileWrapper>) images);
		clone = (LinkedList<FileWrapper>) clone.clone();
		Queue<FileWrapper> namesOfPics = clone;
		for (Bitmap bitmap : audiograms)
		{
			//try {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);

			String name = namesOfPics.remove().getImage_name();

			File f = new File(getApplicationContext().getCacheDir(), name);
			f.createNewFile();

			byte[] ba = bao.toByteArray();
			String image_str = Base64.encodeToString(ba, Base64.DEFAULT);

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

		}

	}

	/**
	 * opens camera where a picture is taken and save into a variable
	 *
	 * @param view
	 */
	public void getPhoto(View view)
	{

		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA))
		{
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

		}
		else
		{
			Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
		{
			Bundle extras = data.getExtras();
			Bitmap bitmap = (Bitmap) extras.get("data");

			//encode image to string to be used on FileWrapper Constructor
      ByteArrayOutputStream bao = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);

      cal = Calendar.getInstance();
      String image_name = "Audiogram_" + cal.getTime().toString().replace(" ", "_") + ".jpeg";



      File f = new File(getApplicationContext().getCacheDir(), image_name);
      try
      {
        f.createNewFile();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }

      byte[] ba = bao.toByteArray();
      String encode = Base64.encodeToString(ba, Base64.DEFAULT);

			audiograms.add(bitmap);

			images.add(new FileWrapper(image_name, encode));

			adapter.updateRecords(images);

		}

	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		images = new LinkedList<>();
		//listView = (ExpandableHeightListView) findViewById(R.id.expand_view);
		listView = findViewById(R.id.expand_view);
		adapter = new CustomAdapter(this, R.layout.item_list_item, images);
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
		mDisplayDate = findViewById(R.id.idBirth);
		id = findViewById(R.id.familyid);
		relationship = findViewById(R.id.rel);

		mDisplayDate.setText(sdf.format(myDate));
		mDisplayDate.setInputType(InputType.TYPE_NULL);

		//audiogram = (ImageView) findViewById(R.id.imageView);

		//mDisplayDate.setText();

		Spinner s1 = findViewById(R.id.MFO);
		Spinner s2 = findViewById(R.id.race);
		Spinner s3 = findViewById(R.id.diag);
		Spinner s4 = findViewById(R.id.pat);

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

		mDisplayDate.setOnClickListener(new View.OnClickListener()
		{
			@RequiresApi(api = Build.VERSION_CODES.N)
			@Override
			public void onClick(View view)
			{

				int year = cal.get(Calendar.YEAR);
				int month = cal.get(Calendar.MONTH);
				int day = cal.get(Calendar.DAY_OF_MONTH);

				DatePickerDialog dialog = new DatePickerDialog(
						MainActivity.this,
						android.R.style.Theme_Holo_Light_Dialog_MinWidth,
						mDateSetListener,
						year, month, day);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
				dialog.show();
			}
		});

		mDateSetListener = new DatePickerDialog.OnDateSetListener()
		{
			@RequiresApi(api = Build.VERSION_CODES.N)
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int day)
			{
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

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				final AlertDialog.Builder alertadd = new AlertDialog.Builder(MainActivity.this);
				LayoutInflater factory = LayoutInflater.from(MainActivity.this);
				final View view2 = factory.inflate(R.layout.sample, null);
				ImageView photo = view2.findViewById(R.id.dialog_imageview);
				photo.setImageBitmap(audiograms.get(i));
				photo.setScaleType(ImageView.ScaleType.FIT_XY);
				alertadd.setView(view2);
				alertadd.setNeutralButton("Exit!", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dlg, int sumthin)
					{
						dlg.dismiss();
					}
				});

				alertadd.show();
				return false;
			}
		});

	}

	@Override
	public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l)
	{
		String item = null;
		index = 5;
		final int d = adapterView.getId();

		switch (adapterView.getId())
		{
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
		if (item != null)
		{
			if (item.equals("Other"))
			{
				final AlertDialog.Builder alertadd = new AlertDialog.Builder(MainActivity.this);
				LayoutInflater factory = LayoutInflater.from(MainActivity.this);
				final View view2 = factory.inflate(R.layout.other_input, null);
				final TextView textView = view2.findViewById(R.id.input_text);

				alertadd.setView(view2);

				DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener()
				{

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent KEvent)
					{
						int keyaction = KEvent.getAction();

						if (keyaction == KeyEvent.ACTION_DOWN)
						{
							int keycode = KEvent.getKeyCode();
							int keyunicode = KEvent.getUnicodeChar(KEvent.getMetaState());
							//char character = (char) keyunicode;
							if (keycode == KeyEvent.KEYCODE_ENTER)
							{
								final StringBuilder sb = new StringBuilder(textView.getText().length());
								sb.append(textView.getText());
								selectedItems[0][index] = sb.toString();
								Spinner t = findViewById(adapterView.getId());
								SpinnerAdapter spinnerAdapter = t.getAdapter();
								ArrayAdapter<String> a = (ArrayAdapter<String>) spinnerAdapter;
								ArrayList<String> items = new ArrayList<>();
								for (int i = 0; i < a.getCount() + 1; i++)
								{
									if (i == 0)
									{
										items.add(sb.toString());
										continue;
									}
									if ((a.getItem(i - 1)).equals(selectedItems[1][index]))
									{
										continue;
									}
									items.add(a.getItem(i - 1));
								}
								selectedItems[1][index] = selectedItems[0][index];

								ArrayAdapter<String> arrAdapt = new ArrayAdapter<String>(MainActivity.this,
										android.R.layout.simple_list_item_1, items);
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
	public void onNothingSelected(AdapterView<?> adapterView)
	{
		//Toast.makeText(MainActivity.this, "--------", Toast.LENGTH_SHORT);
	}

	public void getInputs(View v) throws Exception
	{
		Log.v(TAG,
				Arrays.toString(selectedItems) + " " + id.getTextAlignment() + " " + mDisplayDate.getText().toString() + " " +
						relationship.getText().toString());
		Collection c = new Collection(selectedItems[0], id.getText().toString(), cal.getTime(),
				relationship.getText().toString(), this);

		JSONObject object = c.sendJSON();
    JSONArray files = new JSONArray();
    JSONArray names = new JSONArray();

    for (FileWrapper i : images)
    {
      files.put(i.getEncodedFile());
      names.put(i.getImage_name());
    }

    object.put("files", files);
    object.put("name", names);

    MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
    mpEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    mpEntity.addPart("object", new StringBody(object.toString(), ContentType.APPLICATION_JSON));

    //mpEntity.addPart("upload_file", new FileBody(f, ContentType.DEFAULT_BINARY));

    //mpEntity.addPart("name", new StringBody(f.getName(), ContentType.MULTIPART_FORM_DATA));



    //mpEntity.addPart("type", new StringBody(f.getName(), ContentType.MULTIPART_FORM_DATA));

    //ba1 = Base64.encodeBytes(ba);

    //Log.e("base64", "-----" + ba1);

    // Upload image to server
    new BackgroundController(this, object.toString(), mpEntity).execute();

	}

}

