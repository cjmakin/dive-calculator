package com.chrismakin.divecalculator;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;


/**
 * The ConversionActivity class implements calculations for various conversions commonly needed
 * for diving.
 *
 * @author Christian Makin
 * @version 1.0.1
 * @since 2021-03-04
 */
public class ConversionsActivity extends AppCompatActivity {
    private Spinner leftSpinner;
    private Spinner rightSpinner;
    private EditText leftEditText;
    private EditText rightEditText;
    private TextView formulaTextView;
    private String[] formulas;
    private ActivityOptions options;

    private final DecimalFormat DF = new DecimalFormat("#.##");


    /**
     * Performs the correct calculation depending on which spinner items the user has selected.
     * EditTexts are updated automatically as the user inputs values (Like the Google conversion
     * tool). Updates the formula TextView as the user changes spinner items.
     * @param inSpinner Spinner that corresponds to the EditText the user is currently typing in
     * @param outSpinner Spinner that corresponds to the output EditText.
     * @param inEditText EditText that the user is typing in
     * @param outEditText EditText that is serving as output
     */
    private void calculateConversion(Spinner inSpinner, Spinner outSpinner,
                                    EditText inEditText, EditText outEditText) {
        String answer;

        double input = Double.parseDouble(inEditText.getText().toString());

        if (inSpinner.getSelectedItemPosition() == 0 &&
                outSpinner.getSelectedItemPosition() == 1) {

            //Depth to ATA
            answer = DF.format(calcDepthToAta(input));
            outEditText.setText(answer);
            formulaTextView.setText(formulas[0]);

        } else if (inSpinner.getSelectedItemPosition() == 0 &&
                outSpinner.getSelectedItemPosition() == 2) {

            //Depth to PSIG
            answer = DF.format(calcDepthToPsig(input));
            outEditText.setText(answer);
            formulaTextView.setText(formulas[4]);

        } else if (inSpinner.getSelectedItemPosition() == 1 &&
                outSpinner.getSelectedItemPosition() == 0) {

            //Ata to Depth
            answer = DF.format(calcAtaToDepth(input));
            outEditText.setText(answer);
            formulaTextView.setText(formulas[1]);

        } else if (inSpinner.getSelectedItemPosition() == 1 &&
                outSpinner.getSelectedItemPosition() == 2) {

            //Ata to PSIG
            answer = DF.format(calcAtaToPsig(input));
            outEditText.setText(answer);
            formulaTextView.setText(formulas[3]);

        } else if (inSpinner.getSelectedItemPosition() == 2 &&
                outSpinner.getSelectedItemPosition() == 0) {

            //PSIG to Depth
            answer = DF.format(calcPsigToDepth(input));
            outEditText.setText(answer);
            formulaTextView.setText(formulas[5]);

        } else if (inSpinner.getSelectedItemPosition() == 2 &&
                outSpinner.getSelectedItemPosition() == 1) {

            //PSIG to Ata
            answer = DF.format(calcPsigToAta(input));
            outEditText.setText(answer);
            formulaTextView.setText(formulas[2]);

        } else if (inSpinner.getSelectedItemPosition() == 3) {

            //PP ATA to PP mmHg
            outSpinner.setSelection(4);
            answer = DF.format(calcAtaToMmhg(input));
            outEditText.setText(answer);
            formulaTextView.setText(formulas[6]);

        } else if (inSpinner.getSelectedItemPosition() == 4) {

            //PP mmHg to PP ATA
            outSpinner.setSelection(3);
            answer = DF.format(calcMmhgToAta(input));
            outEditText.setText(answer);
            formulaTextView.setText(formulas[7]);

        } else if (inSpinner.getSelectedItemPosition() == 5) {

            //F to C
            outSpinner.setSelection(6);
            answer = DF.format(calcFahrenToCel(input));
            outEditText.setText(answer);
            formulaTextView.setText(formulas[8]);

        } else if (inSpinner.getSelectedItemPosition() == 6) {

            //C to F
            outSpinner.setSelection(5);
            answer = DF.format(calcCelToFahren(input));
            outEditText.setText(answer);
            formulaTextView.setText(formulas[9]);

        } else {
            rightEditText.setText(leftEditText.getText().toString());
        }
    }

    //Conversion methods
    private double calcDepthToAta(Double depth) {
        return (depth + 33) / 33;
    }

    private double calcAtaToDepth(double ata) {
        return (ata - 1) * 33;
    }

    private double calcPsigToAta(double psig) {
        return (psig + 14.7) / 14.7;
    }

    private double calcAtaToPsig(double ata) {
        return (ata - 1) * 14.7;
    }

    private double calcDepthToPsig(double depth) {
        return depth * .445;
    }

    private double calcPsigToDepth(double psig) {
        return psig / .445;
    }

    private double calcFahrenToCel(double F) {
        return (F - 32) * (5.0 / 9.0);
    }

    private double calcCelToFahren(double C) {
        return ((9.0 / 5.0) * C) + 32;
    }

    private double calcAtaToMmhg(double ata) {
        return ata * 760;
    }

    private double calcMmhgToAta(double mmhg) {
        return mmhg / 760;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversions);

        Resources res = this.getResources();
        formulas = res.getStringArray(R.array.conversion_formula_array);

        //Set initial formula
        formulaTextView = findViewById(R.id.formula_textview);
        formulaTextView.setText(formulas[0]);

        //Set Spinner Values
        leftSpinner = findViewById(R.id.spinner1);
        rightSpinner = findViewById(R.id.spinner2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.conversion_spinner_array, android.R.layout.simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leftSpinner.setAdapter(adapter);
        rightSpinner.setAdapter(adapter);
        rightSpinner.setSelection(1);

        //EditText Listener
        leftEditText = findViewById(R.id.editText1);
        rightEditText = findViewById(R.id.editText2);


        leftEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (leftEditText.hasFocus()) {

                    if (leftEditText.getText().toString().equals("")) {
                        rightEditText.setText("");
                    } else if (leftEditText.getText().toString().equals("0") &&
                            leftSpinner.getSelectedItemPosition() != 5 &&
                            leftSpinner.getSelectedItemPosition() != 6) {
                        rightEditText.setText("0");
                    } else {
                        calculateConversion(leftSpinner, rightSpinner, leftEditText, rightEditText);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        rightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (rightEditText.hasFocus()) {

                    if (rightEditText.getText().toString().equals("")) {
                        leftEditText.setText("");

                    } else if (rightEditText.getText().toString().equals("0") &&
                            rightSpinner.getSelectedItemPosition() != 5 &&
                            rightSpinner.getSelectedItemPosition() != 6) {

                        leftEditText.setText("0");

                    } else {
                        calculateConversion(rightSpinner, leftSpinner, rightEditText, leftEditText);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ssds:
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startSpecificActivity(Class<?> otherActivityClass) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(this);
        }
        Intent intent = new Intent(getApplicationContext(), otherActivityClass);
        startActivity(intent, options.toBundle());
    }
}