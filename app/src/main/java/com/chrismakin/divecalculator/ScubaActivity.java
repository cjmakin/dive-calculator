package com.chrismakin.divecalculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ScubaActivity extends AppCompatActivity {
    private EditText depthEditText;
    private EditText cylPressureEditText;
    private EditText minCylPressureEditText;
    private EditText floodVolEditText;
    private EditText numFlasksEditText;
    private TextView durationTextView;
    private Spinner floodVolSpinner;
    ActivityOptions options;

    private final int[] ids = {R.id.depthEditText, R.id.cylPressureEditText, R.id.minCylPressureEditText, R.id.floodVolEditText, R.id.numFlasksEditText};
    private final double[] floodVolNumValues = {.399, .470, .319, .281, .526, .445, .420};

    //Check if spinner is empty method

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startSpecificActivity(Class<?> otherActivityClass) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(this);
        }
        Intent intent = new Intent(getApplicationContext(), otherActivityClass);
        startActivity(intent, options.toBundle());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.hardhat:
                Intent intent = new Intent(this, SsdsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.scuba:
                intent = new Intent(this, ScubaActivity.class);
                this.startActivity(intent);
                break;
            case R.id.chamber:
                intent = new Intent(this, ChamberActivity.class);
                this.startActivity(intent);
                break;
            case R.id.conversions:
                intent = new Intent(this, ConversionsActivity.class);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean fieldIsFilled() {

        boolean isFilled = true;

        for(int id : ids) {
            EditText t = findViewById(id);
            if (t.getText().toString().trim().equalsIgnoreCase("")) {
                t.setError("Enter a value");
                isFilled = false;
            } else if (Double.parseDouble(t.getText().toString()) == 0) {
                t.setError("Enter a value greater than 0");
                isFilled = false;
            }
        }
        return isFilled;

    }

    @SuppressLint("SetTextI18n")
    public void onClick(View view) {

        if (fieldIsFilled()) {

            double consumption = calcConsumption();
            double capacity = calcAirCapacity();
            int duration = calcDuration(consumption, capacity);
            durationTextView.setText(Integer.toString(duration));

        }
    }

    public double calcConsumption() {

        double depth = Double.parseDouble(depthEditText.getText().toString());
        return ((depth + 33) / 33) * 1.4;

    }

    public double calcAirCapacity() {

        double cylPressure = Double.parseDouble(cylPressureEditText.getText().toString());
        double minCylPressure = Double.parseDouble(minCylPressureEditText.getText().toString());
        double floodVol = Double.parseDouble(floodVolEditText.getText().toString());
        int numFlasks = Integer.parseInt(numFlasksEditText.getText().toString());
        return ((cylPressure - minCylPressure) / 14.7) * floodVol * numFlasks;

    }

    public int calcDuration(double consumption, double capacity) {
        double duration = capacity / consumption;
        if (duration < 0) {
            duration = 0;
        }
        return (int)(duration);
    }



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scuba);


        //Set Spinner Values
        Resources res = this.getResources();
        floodVolSpinner = findViewById(R.id.egsFloodVolET);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.scuba_flood_vol_spinner_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floodVolSpinner.setAdapter(adapter);

        //Set EditTexts / TextView
        depthEditText = findViewById(R.id.depthEditText);
        cylPressureEditText = findViewById(R.id.cylPressureEditText);
        minCylPressureEditText = findViewById(R.id.minCylPressureEditText);
        floodVolEditText = findViewById(R.id.floodVolEditText);
        numFlasksEditText = findViewById(R.id.numFlasksEditText);
        durationTextView = findViewById(R.id.durationTextView);


        //Set initial flood volume, and min cylinder pressure values
        floodVolEditText.setText(Double.toString(floodVolNumValues[0]));
        minCylPressureEditText.setText("250");

        floodVolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                int index = floodVolSpinner.getSelectedItemPosition();
                floodVolEditText.setText(Double.toString(floodVolNumValues[index]));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }
}
