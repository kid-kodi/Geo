package com.aqs.geo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aqs.adapters.GeoPointListAdapter;
import com.aqs.db.helpers.DatabaseHelper;
import com.aqs.db.models.GeoPoint;
import com.aqs.db.models.Pacerelle;
import com.aqs.helpers.FileHelper;
import com.aqs.helpers.GPSTracker;
import com.aqs.helpers.SimpleDividerItemDecoration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ItemDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private ArrayList<GeoPoint> geoPointArrayList;
    private long pacerelle_id;
    private GeoPointListAdapter mAdapter;
    private GPSTracker gps;
    private DatabaseHelper db;
    private TextView geo_numText;
    private String code;
    private String description;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView mImageView;
    private String mCurrentPhotoPath;
    private Uri photoUri;
    String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pacerelle+";
    private ImageView img_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        db = new DatabaseHelper(ItemDetailsActivity.this);
        Intent intent = getIntent();
        pacerelle_id = intent.getLongExtra("id", 0);
        code = intent.getStringExtra("code");
        description = intent.getStringExtra("description");
        long geo_num = intent.getLongExtra("geo_num", 0);
        String created_at = intent.getStringExtra("created_at");
        String photo = intent.getStringExtra("photo");

        if(photo.length()>0){
            File Photo = new File(fullPath,photo);
            if(Photo.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(Photo.getPath());
                img_test = (ImageView) findViewById(R.id.img_test);
                img_test.setImageBitmap(bitmap);
            }
        }

        TextView codeText = (TextView) findViewById(R.id.code);
        codeText.setText(code);

        TextView descriptionText = (TextView) findViewById(R.id.desription);
        descriptionText.setText(description);

        geo_numText = (TextView) findViewById(R.id.geo_num);
        geo_numText.setText(geo_num + "");

        TextView created_atText = (TextView) findViewById(R.id.created_at);
        created_atText.setText(GeoPointListAdapter.getTimeStamp(created_at));



        ImageButton geo_btn = (ImageButton)findViewById(R.id.geo_btn);
        geo_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(ItemDetailsActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    GeoPoint geoPoint = new GeoPoint();

                    geoPoint.setPacerelle_id((int) pacerelle_id);
                    geoPoint.setLatitude(latitude);
                    geoPoint.setLongitude(longitude);

                    DialogBox((int)pacerelle_id,geoPoint);


                    // \n is for new line
                    //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });


        ImageButton camera_btn = (ImageButton)findViewById(R.id.camera_btn);
        camera_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object

                dispatchTakePictureIntent();

                //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                //}


            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.geoList);
        db = new DatabaseHelper(getApplicationContext());
        geoPointArrayList = new ArrayList<>();
        geoPointArrayList = db.getPacerelleAllGeo((int)pacerelle_id);
        mAdapter = new GeoPointListAdapter(this, geoPointArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        int geo_count = geoPointArrayList.size();
        geo_numText.setText(geo_count + "");

        recyclerView.addOnItemTouchListener(new GeoPointListAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GeoPointListAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                DialogBoxDelete(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        mAdapter.notifyDataSetChanged();


        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        fetchGeoPoint();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ArrayList<GeoPoint> geoList = db.getPacerelleAllGeo((int) pacerelle_id);
            //String AppFolderAboslutePath = createDir();
            String Data = System.currentTimeMillis()+"###"+code;
            int geo_count = geoList.size();
            for(int i = 0; i<geo_count;i++){
                Data +=
                         "ID" + geoList.get(i).getId()        +"\n"
                        +"LAT:"+geoList.get(i).getLatitude()  +"\n"
                        +"LON:"+geoList.get(i).getLongitude() +"\n\n";
            }

            new FileHelper(getApplicationContext())
                    .createFile("_" + code, Data);

            Toast.makeText(ItemDetailsActivity.this, "Fichier texte crée!", Toast.LENGTH_SHORT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void DialogBox(int pacerelle_id, final GeoPoint geoPoint){

        final int pacerelle_pos = pacerelle_id;
        final Pacerelle pacerelle = db.getPacerelle(pacerelle_pos);
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetailsActivity.this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("\nLatitude: "+ geoPoint.getLatitude()
                + "\nLong: "+ geoPoint.getLongitude())
                .setTitle(R.string.dialog_title);

        // Add the buttons
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                db.createGeoPoint(geoPoint);
                fetchGeoPoint();

            }
        });



        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void DialogBoxDelete(final int geo_id){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetailsActivity.this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Voulez-vous supprimez ?")
                .setTitle(R.string.dialog_title);

        // Add the buttons
        builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                GeoPoint geo = geoPointArrayList.get(geo_id);
                db.deleteGeo(geo.getId());
                fetchGeoPoint();

            }
        });



        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * fetching the chat rooms by making http call
     */
    private void fetchGeoPoint() {
        db = new DatabaseHelper(getApplicationContext());
        geoPointArrayList.clear();
        ArrayList<GeoPoint> geoList = db.getPacerelleAllGeo((int)pacerelle_id);
        int geo_count = geoList.size();
        for(int i = 0; i<geo_count;i++){
            geoPointArrayList.add(geoList.get(i));
        }

        mAdapter.notifyDataSetChanged();
        if (mAdapter.getItemCount() > 1) {
            // scrolling to bottom of the recycler view
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
        }
        geo_numText.setText(geo_count + "");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            Bitmap photoBitmap = BitmapFactory.decodeFile(photoUri.getPath(),
                    options);
            String imageFileName = new FileHelper(ItemDetailsActivity.this).saveImageToExternalStorage(photoBitmap);
            Pacerelle updated_pacerelle = db.getPacerelle(pacerelle_id);
            updated_pacerelle.setPhoto(imageFileName);
            db.updatePacerelle(updated_pacerelle);
            db.closeDB();

            File Photo = new File(fullPath,imageFileName);
            if(Photo.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(Photo.getPath());
                img_test = (ImageView) findViewById(R.id.img_test);
                img_test.setImageBitmap(bitmap);
            }
            //Toast.makeText(getApplicationContext(),data.toString(),Toast.LENGTH_SHORT);
            //Bundle extras = data.getExtras();

            //ImageView mImageView;
            //mImageView.setImageBitmap(imageBitmap);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = Uri.fromFile(photoFile);
                //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }
}
