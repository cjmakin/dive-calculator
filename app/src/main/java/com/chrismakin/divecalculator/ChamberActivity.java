package com.chrismakin.divecalculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
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

public class ChamberActivity extends AppCompatActivity {
    private EditText floodVolEditText;
    private EditText numPatientsEditText;
    private EditText numTendersEditText;
    private TextView airReqTextView;
    private TextView o2ReqTextView;
    private Spinner floodVolSpinner;
    private Spinner treatTableSpinner;
    RadioGroup radioGroup;
    CheckBox bibsCheckBox;

    private final int[] editTextIds = {R.id.floodVolEditText, R.id.numPatientsEditText, R.id.numTendersEditText};
    final int[] radioGroupIds = {R.id.innerRadioGroup, R.id.outerRadioGroup, R.id.bothRadioGroup};

    final double[] innerLockFloodVols = {45, 123, 136, 162, 440, 192, 285, 134};
    final double[] outerLockFloodVols = {45.5, 69, 65, 61, 144, 37, 140, 68};
    final double[] bothFloodVols = {90.5, 192, 201, 223, 584, 229, 425, 202};


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


    @SuppressLint("SetTextI18n")
    public void onClick(View view) {

        int totalAir;

        if (fieldIsFilled()) {

            int totalO2 = (int) (getO2Requirement()) + 1;
            o2ReqTextView.setText(Integer.toString(totalO2));

            if (bibsCheckBox.isChecked()) {
                totalAir = (int) getCompressionRequirement() + 1;
                airReqTextView.setText(Integer.toString(totalAir));
            } else if (!bibsCheckBox.isChecked()) {
                totalAir = (int) (getCompressionRequirement() + getVentilationRequirement()) + 1;
                airReqTextView.setText(Integer.toString(totalAir));
            }
        }
    }

    public boolean fieldIsFilled() {

        boolean isFilled = true;

        for(int id : editTextIds) {
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
    public void setFloodVolEditText() {

        int spinnerIndex = floodVolSpinner.getSelectedItemPosition();
        int selectedRadioGroupId = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioGroupId == radioGroupIds[0]) {
            floodVolEditText.setText(Double.toString(innerLockFloodVols[spinnerIndex]));
        } else if (selectedRadioGroupId == radioGroupIds[1]) {
            floodVolEditText.setText(Double.toString(outerLockFloodVols[spinnerIndex]));
        } else {
            floodVolEditText.setText(Double.toString(bothFloodVols[spinnerIndex]));
        }

    }

    public double getCompressionRequirement() {

        double depth;
        int treatTableIndex = treatTableSpinner.getSelectedItemPosition();
        double floodVol = Double.parseDouble(floodVolEditText.getText().toString());

        if (treatTableIndex == 0 || treatTableIndex == 1) {
            depth = 60;
        } else if (treatTableIndex == 2) {
            depth = 165;
        } else {
            depth = 45;
        }

        return (depth / 33) * floodVol;

    }

    public double getVentilationRequirement() {

        double ata;
        double airOnBottom;
        double airAtStop;
        double airOnAscent;
        double totalAir;
        int numPatients = Integer.parseInt(numPatientsEditText.getText().toString());
        int numTenders = Integer.parseInt(numTendersEditText.getText().toString());

        double ventRequirementO2 = numPatients * 12.5;
        double ventRequirementAir = (numPatients * 4) + (numTenders * 2);

        int treatTableIndex = treatTableSpinner.getSelectedItemPosition();

        if (treatTableIndex == 0) {

            ata = (60 + 33.0) / 33;
            airOnBottom = (ata * ventRequirementO2 * 40) + (ata * ventRequirementAir * 5);
            ata = (30 + 33.0) / 33;
            airOnAscent = (ata * ventRequirementO2 * 60) + (((15 + 33) / 33.0) * (25 * numTenders) * 30);
            airAtStop = (ata * ventRequirementO2 * 20) + (ata * ventRequirementAir * 10);
            totalAir = airOnBottom + airOnAscent + airAtStop;


        } else if (treatTableIndex == 1) {

            ata = (60 + 33.0) / 33;
            airOnBottom = (ata * ventRequirementO2 * 60) + (ata * ventRequirementAir * 15);
            ata = (30 + 33.0) / 33;
            airOnAscent = (ata * ventRequirementO2 * 60) + (((15 + 33) / 33.0) * (25 * numTenders) * 30);
            airAtStop = (ata * ventRequirementO2 * 120) + (ata * ventRequirementAir * 30) + (((30 + 33) / 33.0) * (25 * numTenders) * 30);
            totalAir = airOnBottom + airOnAscent + airAtStop;


        } else if (treatTableIndex == 2) {

            ata = (165 + 33.0) / 33;
            airOnBottom = ata * ventRequirementAir * 30;
            ata = (30 + 33.0) / 33;
            airOnAscent = (ata * ventRequirementO2 * 60) + (((15 + 33) / 33.0) * (25 * numTenders) * 30) + (((112.5 + 33) / 33) * ventRequirementAir);
            airAtStop = (((60 + 33.0) / 33) * ventRequirementO2 * 60) + (((60 + 33.0) / 33) * ventRequirementAir * 15) + (ata * ventRequirementO2 * 120) + (ata * ventRequirementAir * 30);
            totalAir = airOnBottom + airOnAscent + airAtStop;

        } else {

            ata = (45 + 33.0) / 33;
            airOnBottom = (ata * ventRequirementO2 * 90) + (ata * ventRequirementAir * 10) + (ata * (numTenders * 25) * 15);
            ata = (22.5 + 33.0) / 33;
            airOnAscent = (ata * ventRequirementO2 * 2.25) + (ata * (25 * numTenders) * 2.25);
            totalAir = airOnBottom + airOnAscent;

        }

        return totalAir;

    }

    public double getO2Requirement() {

        int treatTableIndex = treatTableSpinner.getSelectedItemPosition();
        int numPatients = Integer.parseInt(numPatientsEditText.getText().toString());
        int numTenders = Integer.parseInt(numTendersEditText.getText().toString());
        double o2Bottom;
        double o2Stop;
        double o2Ascent;
        double totalO2;
        double ata;
        double acfm = 0.3;


        if (treatTableIndex == 0) {

            ata = (60 + 33) / 33.0;
            o2Bottom = ata * acfm * numPatients * 40;
            ata = (30 + 33) / 33.0;
            o2Ascent = (ata * acfm * numPatients * 60) + (((15 + 33) / 33.0) * numTenders * 30);
            o2Stop = (ata * acfm * numPatients * 20);
            totalO2 = o2Bottom + o2Ascent + o2Stop;

        } else if (treatTableIndex == 1) {

            ata = (60 + 33) / 33.0;
            o2Bottom = ata * acfm * numPatients * 60;
            ata = (30 + 33) / 33.0;
            o2Ascent = (ata * acfm * numPatients * 60) + (((15 + 33) / 33.0) * numTenders * 30);
            o2Stop = (ata * acfm * numPatients * 120) + (ata * acfm * numTenders * 30);
            totalO2 = o2Bottom + o2Ascent + o2Stop;

        }   else if (treatTableIndex == 2) {

            ata = (60 + 33) / 33.0;
            o2Bottom = ata * acfm * numPatients * 60;
            ata = (30 + 33) / 33.0;
            o2Ascent = (ata * acfm * numPatients * 60) + (((15 + 33) / 33.0) * numTenders * 30);
            o2Stop = (ata * acfm * numPatients * 120) + (ata * acfm * numTenders * 60);
            totalO2 = o2Bottom + o2Ascent + o2Stop;

        } else {

            ata = (45 + 33) / 33.0;
            totalO2 = (ata * acfm * numPatients * 90) + (ata * acfm * numTenders * 15) + (((22.5 + 33) / 33) * acfm * numTenders * 2.25);

        }

        return totalO2;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chamber);

        //Set Spinner Values

        treatTableSpinner = findViewById(R.id.treatTableSpinner);
        floodVolSpinner = findViewById(R.id.egsFloodVolET);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.chamber_flood_vol_spinner_array, android.R.layout.simple_spinner_dropdown_item);
        floodVolSpinner.setAdapter(adapter);
        adapter =  ArrayAdapter.createFromResource(this, R.array.treatment_table_flood_vol_spinner_array, android.R.layout.simple_spinner_dropdown_item);
        treatTableSpinner.setAdapter(adapter);

        //Set RadioGroup / checkBox
        radioGroup = findViewById(R.id.radioGroup);
        bibsCheckBox = findViewById(R.id.bibsCheckBox);

        //Set EditTexts / TextView
        floodVolEditText = findViewById(R.id.floodVolEditText);
        numPatientsEditText = findViewById(R.id.numPatientsEditText);
        numTendersEditText = findViewById(R.id.numTendersEditText);
        airReqTextView = findViewById(R.id.totalAirTextView);
        o2ReqTextView = findViewById(R.id.totalO2TextView);

        floodVolEditText.setText(Double.toString(innerLockFloodVols[0]));

        //Spinner listener, radio group listener for updating floodable volume editText
        floodVolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
}
