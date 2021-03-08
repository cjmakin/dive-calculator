package com.chrismakin.divecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * The ChamberActivity class implements calculations for determining total air / O2 requirements
 * during recompression treatment tables 5, 6, 6a (to 165fsw), and 9. Calculates for recompression
 * chambers with and without BIBS overboard dump systems installed.
 *
 * @author Christian Makin
 * @version 1.0.1
 * @since 2021-03-04
 */
public class ChamberActivity extends AppCompatActivity {
    private EditText floodableVolumeEditText;
    private EditText numberOfPatientsEditText;
    private EditText numberOfTendersEditText;
    private TextView airRequirementTextView;
    private TextView o2RequirementsTextView;
    private Spinner floodableVolumeSpinner;
    private Spinner treatmentTableSpinner;
    private RadioGroup radioGroup;
    private CheckBox bibsCheckBox;

    //Class constants
    private final int[] EDITTEXT_IDS =
            {R.id.floodVolEditText, R.id.numPatientsEditText, R.id.numTendersEditText};
    private final int[] RADIO_GROUP_IDS =
            {R.id.innerRadioGroup, R.id.outerRadioGroup, R.id.bothRadioGroup};
    private final double[] INNER_LOCK_FLOODABLE_VOLUMES =
            {45, 123, 136, 162, 440, 192, 285, 134};
    private final double[] OUTER_LOCK_FLOODABLE_VOLUMES =
            {45.5, 69, 65, 61, 144, 37, 140, 68};
    private final double[] INNER_OUTER_FLOODABLE_VOLUMES =
            {90.5, 192, 201, 223, 584, 229, 425, 202};

    /**
     * Sets the values in the floodableVolumeSpinner according to which RadioGroup item is
     * selected (inner, outer, or both).
     */
    private void setFloodVolEditText() {
        int spinnerIndex = floodableVolumeSpinner.getSelectedItemPosition();
        int selectedRadioGroupId = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioGroupId == RADIO_GROUP_IDS[0]) {
            floodableVolumeEditText.setText(String.format(
                    Locale.US, "%10.1f", INNER_LOCK_FLOODABLE_VOLUMES[spinnerIndex]));
        } else if (selectedRadioGroupId == RADIO_GROUP_IDS[1]) {
            floodableVolumeEditText.setText(String.format(
                    Locale.US, "%10.1f", OUTER_LOCK_FLOODABLE_VOLUMES[spinnerIndex]));
        } else {
            floodableVolumeEditText.setText(String.format(
                    Locale.US, "%10.1f", INNER_OUTER_FLOODABLE_VOLUMES[spinnerIndex]));
        }
    }

    /**
     * Calculates the total air requirement needed for compression to treatment depth.
     * Multiplies the depth for selected treatment (in ATAs) by the floodable volume of the chamber.
     * @return double
     */
    private double getAirCompressionRequirement() {
        double depth;
        int treatmentTableIndex = treatmentTableSpinner.getSelectedItemPosition();
        double floodVol = Double.parseDouble(floodableVolumeEditText.getText().toString());

        if (treatmentTableIndex == 0 || treatmentTableIndex == 1) {
            depth = 60;
        } else if (treatmentTableIndex == 2) {
            depth = 165;
        } else {
            depth = 45;
        }
        return ((depth + 33) / 33) * floodVol;
    }

    /**
     * Calculates the total air needed for ventilation at treatment depth, respective treatment
     * stops, and ascent. Number of patients and number of tenders are multiplied by ventilation
     * requirements (On O2: 12.5 ACFM for each person on O2, On Air: 2 ACFM for each patient, and
     * 4 ACFM for each tender).
     * @return double
     */
    private double getVentilationRequirement() {
        double ata;
        double airOnBottom;
        double airAtStop;
        double airOnAscent;
        double totalAir;
        int numberOfPatients = Integer.parseInt(numberOfPatientsEditText.getText().toString());
        int numberOfTenders = Integer.parseInt(numberOfTendersEditText.getText().toString());

        double ventilationRequirementO2 = numberOfPatients * 12.5;
        double ventilationRequirementAir = (numberOfPatients * 4) + (numberOfTenders * 2);

        int treatmentTableIndex = treatmentTableSpinner.getSelectedItemPosition();

        if (treatmentTableIndex == 0) {
            ata = (60 + 33.0) / 33;
            airOnBottom = (ata * ventilationRequirementO2 * 40)
                    + (ata * ventilationRequirementAir * 5);
            ata = (30 + 33.0) / 33;
            airOnAscent = (ata * ventilationRequirementO2 * 60)
                    + (((15 + 33) / 33.0) * (25 * numberOfTenders) * 30);
            airAtStop = (ata * ventilationRequirementO2 * 20)
                    + (ata * ventilationRequirementAir * 10);
            totalAir = airOnBottom + airOnAscent + airAtStop;

        } else if (treatmentTableIndex == 1) {
            ata = (60 + 33.0) / 33;
            airOnBottom = (ata * ventilationRequirementO2 * 60)
                    + (ata * ventilationRequirementAir * 15);
            ata = (30 + 33.0) / 33;
            airOnAscent = (ata * ventilationRequirementO2 * 60)
                    + (((15 + 33) / 33.0) * (25 * numberOfTenders) * 30);
            airAtStop = (ata * ventilationRequirementO2 * 120)
                    + (ata * ventilationRequirementAir * 30) + (((30 + 33) / 33.0)
                    * (25 * numberOfTenders) * 30);
            totalAir = airOnBottom + airOnAscent + airAtStop;

        } else if (treatmentTableIndex == 2) {
            ata = (165 + 33.0) / 33;
            airOnBottom = ata * ventilationRequirementAir * 30;
            ata = (30 + 33.0) / 33;
            airOnAscent = (ata * ventilationRequirementO2 * 60)
                    + (((15 + 33) / 33.0) * (25 * numberOfTenders) * 30)
                    + (((112.5 + 33) / 33) * ventilationRequirementAir);
            airAtStop = (((60 + 33.0) / 33) * ventilationRequirementO2 * 60)
                    + (((60 + 33.0) / 33) * ventilationRequirementAir * 15)
                    + (ata * ventilationRequirementO2 * 120)
                    + (ata * ventilationRequirementAir * 30);
            totalAir = airOnBottom + airOnAscent + airAtStop;

        } else {
            ata = (45 + 33.0) / 33;
            airOnBottom = (ata * ventilationRequirementO2 * 90)
                    + (ata * ventilationRequirementAir * 10) + (ata * (numberOfTenders * 25) * 15);
            ata = (22.5 + 33.0) / 33;
            airOnAscent = (ata * ventilationRequirementO2 * 2.25)
                    + (ata * (25 * numberOfTenders) * 2.25);
            totalAir = airOnBottom + airOnAscent;
        }
        return totalAir;
    }

    /**
     * Calculates the total O2 needed for selected treatment table during treatment depth, stops,
     * and ascent. Calculation assumes minimum tender O2 breathing requirements (i.e. no previous
     * hyperbaric exposure).
     * @return double
     */
    private double getO2Requirement() {
        int treatmentTableIndex = treatmentTableSpinner.getSelectedItemPosition();
        int numberOfPatients = Integer.parseInt(numberOfPatientsEditText.getText().toString());
        int numberOfTenders = Integer.parseInt(numberOfTendersEditText.getText().toString());
        double o2Bottom;
        double o2Stop;
        double o2Ascent;
        double totalO2;
        double ata;
        double acfm = 0.3;

        if (treatmentTableIndex == 0) {
            ata = (60 + 33) / 33.0;
            o2Bottom = ata * acfm * numberOfPatients * 40;
            ata = (30 + 33) / 33.0;
            o2Ascent = (ata * acfm * numberOfPatients * 60)
                    + (((15 + 33) / 33.0) * numberOfTenders * 30);
            o2Stop = (ata * acfm * numberOfPatients * 20);
            totalO2 = o2Bottom + o2Ascent + o2Stop;

        } else if (treatmentTableIndex == 1) {
            ata = (60 + 33) / 33.0;
            o2Bottom = ata * acfm * numberOfPatients * 60;
            ata = (30 + 33) / 33.0;
            o2Ascent = (ata * acfm * numberOfPatients * 60)
                    + (((15 + 33) / 33.0) * numberOfTenders * 30);
            o2Stop = (ata * acfm * numberOfPatients * 120)
                    + (ata * acfm * numberOfTenders * 30);
            totalO2 = o2Bottom + o2Ascent + o2Stop;

        } else if (treatmentTableIndex == 2) {
            ata = (60 + 33) / 33.0;
            o2Bottom = ata * acfm * numberOfPatients * 60;
            ata = (30 + 33) / 33.0;
            o2Ascent = (ata * acfm * numberOfPatients * 60)
                    + (((15 + 33) / 33.0) * numberOfTenders * 30);
            o2Stop = (ata * acfm * numberOfPatients * 120)
                    + (ata * acfm * numberOfTenders * 60);
            totalO2 = o2Bottom + o2Ascent + o2Stop;

        } else {
            ata = (45 + 33) / 33.0;
            totalO2 = (ata * acfm * numberOfPatients * 90)
                    + (ata * acfm * numberOfTenders * 15)
                    + (((22.5 + 33) / 33) * acfm * numberOfTenders * 2.25);
        }
        return totalO2;
    }

    /**
     * On click method for calculate button.
     * @param view button view
     */
    public void onClickChamber(View view) {
        int totalAir;

        if (fieldIsFilled()) {
            int totalO2 = (int) (getO2Requirement()) + 1;
            o2RequirementsTextView.setText(String.format(Locale.US, "%d", totalO2));

            if (bibsCheckBox.isChecked()) {
                totalAir = (int) getAirCompressionRequirement() + 1;
                airRequirementTextView.setText(String.format(Locale.US, "%d", totalAir));
            } else if (!bibsCheckBox.isChecked()) {
                totalAir = (int) (getAirCompressionRequirement() + getVentilationRequirement()) + 1;
                airRequirementTextView.setText(String.format(Locale.US, "%d", totalAir));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chamber);

        //Set Spinner Values
        treatmentTableSpinner = findViewById(R.id.treatTableSpinner);
        floodableVolumeSpinner = findViewById(R.id.egsFloodVolET);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.chamber_flood_vol_spinner_array,
                android.R.layout.simple_spinner_dropdown_item);
        floodableVolumeSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(
                this, R.array.treatment_table_flood_vol_spinner_array,
                android.R.layout.simple_spinner_dropdown_item);

        treatmentTableSpinner.setAdapter(adapter);

        //Set RadioGroup and CheckBox
        radioGroup = findViewById(R.id.radioGroup);
        bibsCheckBox = findViewById(R.id.bibsCheckBox);

        //Set EditTexts and TextView
        floodableVolumeEditText = findViewById(R.id.floodVolEditText);
        numberOfPatientsEditText = findViewById(R.id.numPatientsEditText);
        numberOfTendersEditText = findViewById(R.id.numTendersEditText);
        airRequirementTextView = findViewById(R.id.totalAirTextView);
        o2RequirementsTextView = findViewById(R.id.totalO2TextView);
        floodableVolumeEditText.setText(String.format(
                Locale.US, "%10.2f", INNER_LOCK_FLOODABLE_VOLUMES[0]));

        //Spinner listener, radio group listener for updating floodable volume EditText
        floodableVolumeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setFloodVolEditText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setFloodVolEditText();
            }
        });
    }

    /**
     * Returns true if all required fields are filled. Sets an error prompting the user to
     * input a value if not filled.
     * @return boolean
     */
    private boolean fieldIsFilled() {
        boolean isFilled = true;

        for (int id : EDITTEXT_IDS) {
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