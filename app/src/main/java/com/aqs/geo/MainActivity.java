package com.aqs.geo;


import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.aqs.adapters.ItemListAdapter;
import com.aqs.db.helpers.DatabaseHelper;
import com.aqs.db.models.GeoPoint;
import com.aqs.db.models.Pacerelle;
import com.aqs.helpers.FileHelper;
import com.aqs.helpers.SimpleDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "FormActivity";
    private ArrayList<Pacerelle> PacerelleArrayList;
    private ItemListAdapter mAdapter;
    private RecyclerView recyclerView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(MainActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);



        fetchItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:

                ArrayList<Pacerelle> pacerelleList = db.getAllPacerelles();

                for(int i = 0; i<pacerelleList.size();i++){
                    String Data = System.currentTimeMillis()+"\n\n";
                    int pacerelleId = (int)pacerelleList.get(i).getId();
                    String code = pacerelleList.get(i).getCode();
                    Data += code + "\n";
                    ArrayList<GeoPoint> geoList = db.getPacerelleAllGeo(pacerelleId);

                    for(int j = 0; j<geoList.size();j++){
                        Data +=
                                "ID" + geoList.get(j).getId()        +"\n"
                                        +"LAT:"+geoList.get(j).getLatitude()  +"\n"
                                        +"LON:"+geoList.get(j).getLongitude() +"\n\n";
                    }

                    Data += "######################################";

                    new FileHelper(getApplicationContext())
                            .createFile("_" + code, Data);
                }

                db.closeDB();
                Toast.makeText(MainActivity.this,"Fichier texte crée!",Toast.LENGTH_SHORT);

                return true;

            case R.id.action_new:
                Intent intent = new Intent(this, FormActivity.class);
                intent.putExtra("id", 0);
                intent.putExtra("code", "");
                intent.putExtra("description", "");
                intent.putExtra("photo", "");
                startActivity(intent);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void DialogBox(int position){

        final int pacerelle_pos = position;
        final Pacerelle pacerelle = PacerelleArrayList.get(pacerelle_pos);
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        // Add the buttons
        builder.setPositiveButton(R.string.dialog_edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                Intent intent = new Intent(MainActivity.this, FormActivity.class);
                intent.putExtra("id", pacerelle.getId());
                intent.putExtra("code", pacerelle.getCode());
                intent.putExtra("description", pacerelle.getDescription());
                intent.putExtra("geo_num", pacerelle.getGeoNum());
                intent.putExtra("created_at", pacerelle.getDateCreated());
                intent.putExtra("photo", pacerelle.getPhoto());
                startActivity(intent);

            }
        });
        builder.setNegativeButton(R.string.dialog_point, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

                // when chat is clicked, launch full chat thread activity
                Intent intent = new Intent(MainActivity.this, ItemDetailsActivity.class);
                intent.putExtra("id", pacerelle.getId());
                intent.putExtra("code", pacerelle.getCode());
                intent.putExtra("description", pacerelle.getDescription());
                intent.putExtra("geo_num", pacerelle.getGeoNum());
                intent.putExtra("created_at", pacerelle.getDateCreated());
                intent.putExtra("photo", pacerelle.getPhoto());
                startActivity(intent);

            }
        });


        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * fetching the chat rooms by making http call
     */
    private void fetchItems() {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        PacerelleArrayList = new ArrayList<>();
        PacerelleArrayList = db.getAllPacerelles();
        mAdapter = new ItemListAdapter(this, PacerelleArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ItemListAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new ItemListAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                DialogBox(position);


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        Toast.makeText(MainActivity.this, "Chargement de données", Toast.LENGTH_LONG).show();
        mAdapter.notifyDataSetChanged();
    }
}
