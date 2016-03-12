package com.aqs.geo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aqs.db.helpers.DatabaseHelper;
import com.aqs.db.models.Pacerelle;


public class FormActivity extends AppCompatActivity {
    private long pacerelle_id;
    private EditText codeText;
    private EditText descriptionText;
    private EditText pacerelle_idText;
    private String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        codeText = (EditText) findViewById(R.id.code_input);
        descriptionText = (EditText) findViewById(R.id.desription_input);
        pacerelle_idText = (EditText) findViewById(R.id.pacerelle_id);
        pacerelle_idText.setVisibility(View.GONE);
        
        Intent intent = getIntent();

        pacerelle_id = intent.getLongExtra("id", 0);
        pacerelle_idText.setText(pacerelle_id+"");

        photo = intent.getStringExtra("photo");
        
        String code = intent.getStringExtra("code");
        codeText.setText(code);

        String description = intent.getStringExtra("description");
        descriptionText.setText(description);


        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_accept:
                int geoNum = 0;

                int id = (int)pacerelle_id;
                String code = codeText.getText().toString();
                String description = descriptionText.getText().toString();

                Pacerelle pacerelle = new Pacerelle();

                pacerelle.setCode(code);
                pacerelle.setPhoto(photo);
                pacerelle.setDescription(description);

                DatabaseHelper db = new DatabaseHelper(getApplicationContext());

                if(id == 0){
                    pacerelle_id = db.createPacerelle(pacerelle);
                }
                else{
                    pacerelle.setId(id);
                    db.updatePacerelle(pacerelle);
                }

                codeText.setText("");
                descriptionText.setText("");

                Pacerelle added_pacerelle = db.getPacerelle(pacerelle_id);
                Intent intent = new Intent(FormActivity.this, ItemDetailsActivity.class);
                intent.putExtra("id", added_pacerelle.getId());
                intent.putExtra("code", added_pacerelle.getCode());
                intent.putExtra("description", added_pacerelle.getDescription());
                intent.putExtra("geo_num", added_pacerelle.getGeoNum());
                intent.putExtra("created_at", added_pacerelle.getDateCreated());
                intent.putExtra("photo", added_pacerelle.getPhoto());
                startActivity(intent);

                Toast.makeText(FormActivity.this, "Enregistrement Effectué", Toast.LENGTH_LONG).show();
                FormActivity.this.finish();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
