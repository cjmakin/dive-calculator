package com.chrismakin.divecalculator;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The ScubaActivity class implements calculations for determining the duration of Air Supply
 * during scuba diving operations. Air supply is calculated using several common cylinder floodable
 * volumes (from a dropdown menu), depths, cylinder pressure, and preferred minimum cylinder
 * pressure.
 *
 * @author Christian Makin
 * @version 1.0.1
 * @since 2021-03-04
 */
public class ScubaActivity extends AppCompatActivity {
    private EditText depthEditText;
    private EditText cylPressureEditText;
    private EditText minCylPressureEditText;
    private EditText floodVolEditText;
    private EditText numFlasksEditText;
    private TextView durationTextView;
    private Spinner floodVolSpinner;

    private final int[] IDS = {R.id.depthEditText, R.id.cylPressureEditText,
            R.id.minCylPressureEditText, R.id.floodVolEditText, R.id.numFlasksEditText};
    private final double[] FLOODABLE_VOLUME_VALUES = {.399, .470, .319, .281, .526, .445, .420};

    /**
     * Calculates the consumption rate for a diver using a respiratory minute volume of 1.4
     * and the depth of the diver.
     * @return The consumption rate of the diver at a given depth
     */
    private double calcConsumption() {
        double depth = Double.parseDouble(depthEditText.getText().toString());
        double respiratoryMinuteVolume = 1.4;
        return ((depth + 33) / 33) * respiratoryMinuteVolume;
    }

    /**
     * Calculates the air capacity of a set of cylinders given the cylinder pressure, minimum
     * cylinder pressure, floodable volume of the flask, and number of flasks.
     * @return The air capacity of the flask(s)
     */
    private double calcAirCapacity() {
        double cylPressure = Double.parseDouble(cylPressureEditText.getText().toString());
        double minCylPressure = Double.parseDouble(minCylPressureEditText.getText().toString());
        double floodVol = Double.parseDouble(floodVolEditText.getText().toString());
        int numFlasks = Integer.parseInt(numFlasksEditText.getText().toString());
        return ((cylPressure - minCylPressure) / 14.7) * floodVol * numFlasks;
    }

    /**
     * Calculates the duration of the flask(s) given the diver's consumption rate and the capacity
     * of the flask(s).
     * @param consumption the consumption rate of the diver
     * @param capacity the capacity of the flask(s)
     * @return The duration in minutes rounded down to the nearest whole minute
     */
    private int calcDuration(double consumption, double capacity) {
        double duration = capacity / consumption;
        if (duration < 0) {
            duration = 0;
        }
        return (int)(duration);
    }

    /**
     * Returns true if all required fields are filled. Sets an error prompting the user to
     * input a value if not filled.
     * @return boolean
     */
    private boolean fieldIsFilled() {

        boolean isFilled = true;

        for(int id : IDS) {
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

    /**
     * On click method for calculate button.
     * @param view button view
     */
    @SuppressLint("SetTextI18n")
    public void onClickScuba(View view) {
        if (fieldIsFilled()) {
            double consumption = calcConsumption();
            double capacity = calcAirCapacity();
            int duration = calcDuration(consumption, capacity);
            durationTextView.setText(Integer.toString(duration));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scuba);


        //Set Spinner Values
        floodVolSpinner = findViewById(R.id.egsFloodVolET);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.scuba_flood_vol_spinner_array,
                android.R.layout.simple_spinner_dropdown_item);

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
        floodVolEditText.setText(Double.toString(FLOODABLE_VOLUME_VALUES[0]));
        minCylPressureEditText.setText("250");

        floodVolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(
                    AdapterView<?> parentView, View selectedItemView, int position, long id) {
                
                int index = floodVolSpinner.getSelectedItemPosition();
                floodVolEditText.setText(Double.toString(FLOODABLE_VOLUME_VALUES[index]));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.ssds) {
            Intent intent = new Intent(this, SsdsActivity.class);
            this.startActivity(intent);
        } else if (itemId == R.id.scuba) {
            Intent intent;
            intent = new Intent(this, ScubaActivity.class);
            this.startActivity(intent);
        } else if (itemId == R.id.chamber) {
            Intent intent;
            intent = new Intent(this, ChamberActivity.class);
            this.startActivity(intent);
        } else if (itemId == R.id.conversions) {
            Intent intent;
            intent = new Intent(this, ConversionsActivity.class);
            this.startActivity(intent);
        } else {
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
}
