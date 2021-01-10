package com.chrismakin.divecalculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import java.text.DecimalFormat;

public class ConversionsActivity extends AppCompatActivity {
    Spinner topSpinner1;
    Spinner topSpinner2;
    EditText topEditText1;
    EditText topEditText2;
    TextView topFormulaTextView;
    String[] topFormulas;
    String[] topSpinnerValues;
    ActivityOptions options;

    final DecimalFormat df = new DecimalFormat("#.##");

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

    //Expand Animation
    private static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ConstraintLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration(((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density)) * 3);
        v.startAnimation(a);
    }


    //Collapse Animation
    private static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration(((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density))  * 3);
        v.startAnimation(a);
    }

    public void showFormula(View view) {
        if (!topFormulaTextView.isShown()) {
            expand(topFormulaTextView);
        } else {
            collapse(topFormulaTextView);
        }

    }

    public void calculateConversion(Spinner inSpinner, Spinner outSpinner, EditText inEditText, EditText outEditText) {
        String answer;

        double input = Double.parseDouble(inEditText.getText().toString());

        if (inSpinner.getSelectedItemPosition() == 0 && outSpinner.getSelectedItemPosition() == 1) {

            //Depth to ATA
            answer = df.format(calcDepthToAta(input));
            outEditText.setText(answer);
            topFormulaTextView.setText(topFormulas[0]);

        } else if (inSpinner.getSelectedItemPosition() == 0 && outSpinner.getSelectedItemPosition() == 2) {

            //Depth to PSIG
            answer = df.format(calcDepthToPsig(input));
            outEditText.setText(answer);
            topFormulaTextView.setText(topFormulas[4]);

        } else if (inSpinner.getSelectedItemPosition() == 1 && outSpinner.getSelectedItemPosition() == 0) {

            //Ata to Depth
            answer = df.format(calcAtaToDepth(input));
            outEditText.setText(answer);
            topFormulaTextView.setText(topFormulas[1]);

        } else if (inSpinner.getSelectedItemPosition() == 1 && outSpinner.getSelectedItemPosition() == 2) {

            //Ata to PSIG
            answer = df.format(calcAtaToPsig(input));
            outEditText.setText(answer);
            topFormulaTextView.setText(topFormulas[3]);

        } else if (inSpinner.getSelectedItemPosition() == 2 && outSpinner.getSelectedItemPosition() == 0) {

            //PSIG to Depth
            answer = df.format(calcPsigToDepth(input));
            outEditText.setText(answer);
            topFormulaTextView.setText(topFormulas[5]);

        } else if (inSpinner.getSelectedItemPosition() == 2 && outSpinner.getSelectedItemPosition() == 1) {

            //PSIG to Ata
            answer = df.format(calcPsigToAta(input));
            outEditText.setText(answer);
            topFormulaTextView.setText(topFormulas[2]);

        } else if (inSpinner.getSelectedItemPosition() == 3) {

            //PP ATA to PP mmHg
            outSpinner.setSelection(4);
            answer = df.format(calcAtaToMmhg(input));
            outEditText.setText(answer);
            topFormulaTextView.setText(topFormulas[6]);

        } else if (inSpinner.getSelectedItemPosition() == 4) {

            //PP mmHg to PP ATA
            outSpinner.setSelection(3);
            answer = df.format(calcMmhgToAta(input));
            outEditText.setText(answer);
            topFormulaTextView.setText(topFormulas[7]);

        } else if (inSpinner.getSelectedItemPosition() == 5) {

            //F to C
            outSpinner.setSelection(6);
            answer = df.format(calcFahrenToCel(input));
            outEditText.setText(answer);
            topFormulaTextView.setText(topFormulas[8]);

        } else if (inSpinner.getSelectedItemPosition() == 6) {

            //C to F
            outSpinner.setSelection(5);
            answer = df.format(calcCelToFahren(input));
            outEditText.setText(answer);
            topFormulaTextView.setText(topFormulas[9]);

        } else {
            topEditText2.setText(topEditText1.getText().toString());
        }
    }

    public double calcDepthToAta(Double depth) {
        return (depth + 33) / 33;
    }

    public double calcAtaToDepth(double ata) {
        return (ata - 1) * 33;
    }

    public double calcPsigToAta(double psig) {
        return (psig + 14.7) / 14.7;
    }

    public double calcAtaToPsig(double ata) {
        return (ata - 1) * 14.7;
    }

    public double calcDepthToPsig(double depth) {
        return depth * .445;
    }

    public double calcPsigToDepth(double psig) {
        return psig / .445;
    }

    public double calcFahrenToCel(double F) {
        return (F - 32) * (5.0 / 9.0);
    }

    public double calcCelToFahren(double C) {
        return ((9.0/5.0) * C) + 32;
    }

    public double calcAtaToMmhg(double ata) {
        return ata * 760;
    }

    public double calcMmhgToAta(double mmhg) {
        return mmhg / 760;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversions);

        Resources res = this.getResources();
        topFormulas = res.getStringArray(R.array.conversion_formula_array);

        //Set initial formula
        topFormulaTextView = findViewById(R.id.formulaTextView);
        topFormulaTextView.setText(topFormulas[0]);

        //Set Spinner Values
        topSpinner1 = findViewById(R.id.spinner1);
        topSpinner2 = findViewById(R.id.spinner2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.conversion_spinner_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topSpinner1.setAdapter(adapter);
        topSpinner2.setAdapter(adapter);
        topSpinner2.setSelection(1);

        //EditText Listener
        topEditText1 = findViewById(R.id.editText1);
        topEditText2 = findViewById(R.id.editText2);



            topEditText1.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (topEditText1.hasFocus()) {

                        if (topEditText1.getText().toString().equals("")) {

                            topEditText2.setText("");

                        } else if (topEditText1.getText().toString().equals("0") && topSpinner1.getSelectedItemPosition() != 5 && topSpinner1.getSelectedItemPosition() != 6) {

                            topEditText2.setText("0");

                        } else {

                            calculateConversion(topSpinner1, topSpinner2, topEditText1, topEditText2);

                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            topEditText2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (topEditText2.hasFocus()) {

                        if (topEditText2.getText().toString().equals("")) {

                            topEditText1.setText("");

                        } else if (topEditText2.getText().toString().equals("0") && topSpinner2.getSelectedItemPosition() != 5 && topSpinner2.getSelectedItemPosition() != 6) {

                            topEditText1.setText("0");

                        } else {

                            calculateConversion(topSpinner2, topSpinner1, topEditText2, topEditText1);

                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

    }
}
