package com.chrismakin.divecalculator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SsdsAirO2Fragment extends Fragment {
    Activity referenceActivity;

    private EditText numDiversEditText;
    private EditText bottomDepthEditText;
    private EditText bottomTimeEditText;
    private EditText floodVolAirEditText;
    private EditText numFlasksAirEditText;
    private EditText floodVolO2EditText;
    private EditText numFlasksO2EditText;

    private TextView airRequiredTextView;
    private TextView o2RequiredTextView;

    private CheckBox decoRequiredCheckBox;
    private CheckBox o2At30CheckBox;
    private CheckBox o2At20CheckBox;

    private ConstraintLayout decoConstraintLayout;
    private ConstraintLayout o2ConstraintLayout;

    private int[] editTextIds = new int[]{R.id.numDiversEditText, R.id.bottomDepthEditText, R.id.bottomTimeEditText, R.id.floodVolAirEditText, R.id.numFlasksAirEditText};
    private int[] o2EditTextIds = new int[]{R.id.floodVolO2EditText, R.id.numFlasksO2EditText};
    private int[] stopTimeIds = new int[]{R.id.stopTime60ET, R.id.stopTime50ET, R.id.stopTime40ET, R.id.stopTime30ET, R.id.stopTime20ET};
    private double[] airFlaskFloodVols = new double[]{3.15, .935, 3.15, 8, 4};
    private double[] o2FlaskFloodVols = new double[]{1.64, 1.568};


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SsdsAirO2Fragment() {
        // Required empty public constructor
    }

    //onClick Calculate!
    @SuppressLint("SetTextI18n")
    public void onClickAirO2(View view) {



        if (fieldIsFilled()) {

            int bottomDepth = Integer.parseInt(bottomDepthEditText.getText().toString());
            int numDivers = Integer.parseInt(numDiversEditText.getText().toString());
            int bottomTime = Integer.parseInt(bottomTimeEditText.getText().toString());
            int numFlasksAir = Integer.parseInt(numFlasksAirEditText.getText().toString());
            double floodVolAir = Double.parseDouble(floodVolAirEditText.getText().toString());


            double totalAirSCF;
            double totalO2SCF;
            double airStops = 0;
            double o2Stops;
            double o2Ascent;

            double airBottomTime = calcAirBottomTime(bottomDepth, numDivers, bottomTime);
            double airAscent = calcAirAscent(bottomDepth, numDivers);



            if (decoRequiredCheckBox.isChecked()) {
                airStops = calcAirStops(numDivers);

            }

            if (o2At30CheckBox.isChecked() || o2At20CheckBox.isChecked()) {
                int numFlasksO2 = Integer.parseInt(numFlasksO2EditText.getText().toString());
                double floodVolO2 = Double.parseDouble(floodVolO2EditText.getText().toString());
                o2Stops = calcO2Stops(numDivers);
                o2Ascent = calcO2Ascent(numDivers);
                totalO2SCF = o2Stops + o2Ascent;
                int totalO2PSIG = calcO2Psig(totalO2SCF, numFlasksO2, floodVolO2);
                o2RequiredTextView.setText(Integer.toString(totalO2PSIG));
            }

            totalAirSCF = airBottomTime + airAscent + airStops;
            int totalAirPSIG = calcAirPsig(totalAirSCF, bottomDepth, numFlasksAir, floodVolAir);
            airRequiredTextView.setText(Integer.toString(totalAirPSIG));

        }
    }

    //Fields are filled and not zero
    public boolean fieldIsFilled() {

        boolean isFilled = true;

        for (int id : editTextIds) {
            EditText t = getView().findViewById(id);
            if (t.getText().toString().trim().equalsIgnoreCase("")) {
                t.setError("Enter a value");
                isFilled = false;
            } else if (Double.parseDouble(t.getText().toString()) == 0) {
                t.setError("Enter a value greater than 0");
                isFilled = false;
            }
        }

        if (decoRequiredCheckBox.isChecked()) {
            for (int i = 0; i < stopTimeIds.length - 1; i++) {
                EditText t = getView().findViewById(stopTimeIds[i]);
                EditText tNext = getView().findViewById(stopTimeIds[i + 1]);
                if (!t.getText().toString().trim().equalsIgnoreCase("") && tNext.getText().toString().trim().equalsIgnoreCase("")) {
                    tNext.setError("Enter a value");
                    isFilled = false;
                } else if (!t.getText().toString().trim().equalsIgnoreCase("") && Double.parseDouble(tNext.getText().toString()) == 0) {
                    tNext.setError("Enter a value greater than 0");
                    isFilled = false;
                }
            }
        }

        if (o2At20CheckBox.isChecked()) {
            EditText o2At20 = getView().findViewById(R.id.stopTime20ET);
            if (o2At20.getText().toString().trim().equalsIgnoreCase("")) {
                o2At20.setError("Enter a value");
                isFilled = false;
            } else if (Double.parseDouble(o2At20.getText().toString()) == 0) {
                o2At20.setError("Enter a value greater than 0");
                isFilled = false;
            }

            for (int id : o2EditTextIds) {
                EditText t = getView().findViewById(id);
                if (t.getText().toString().trim().equalsIgnoreCase("")) {
                    t.setError("Enter a value");
                    isFilled = false;
                } else if (Double.parseDouble(t.getText().toString()) == 0) {
                    t.setError("Enter a value greater than 0");
                    isFilled = false;
                }
            }
        }
        if (o2At30CheckBox.isChecked()) {
            EditText o2At30 = getView().findViewById(R.id.stopTime30ET);
            if (o2At30.getText().toString().trim().equalsIgnoreCase("")) {
                o2At30.setError("Enter a value");
                isFilled = false;
            } else if (Double.parseDouble(o2At30.getText().toString()) == 0) {
                o2At30.setError("Enter a value greater than 0");
                isFilled = false;
            }
        }
        return isFilled;
    }

    //Set editTexts from spinners
    @SuppressLint("SetTextI18n")
    private void setSpinnerEditText(Spinner spinner, EditText editText, double[] floodVols) {

        int spinnerIndex = spinner.getSelectedItemPosition();
        editText.setText(Double.toString(floodVols[spinnerIndex]));

    }

    //Calculate air for bottom time
    private double calcAirBottomTime(int bottomDepth, int numDivers, int bottomTime) {

        return ((bottomDepth +33.0) / 33) * 1.4 * numDivers * bottomTime;

    }

    //Calculate ascent (air)
    private double calcAirAscent(int bottomDepth, int numDivers) {

        double time;

        if (o2At30CheckBox.isChecked()) {
            time = (bottomDepth - 30.0) / 30;
            bottomDepth += 30;
        } else if (o2At20CheckBox.isChecked()) {
            time = (bottomDepth - 20.0) / 30;
        } else {
            time = bottomDepth / 30.0;
            bottomDepth += 20;
        }
        return (((bottomDepth / 2.0) + 33) / 33) * .75 * numDivers * time;

    }

    //Calculate air stops
    private double calcAirStops(int numDivers) {

        double airForStops = 0;
        double airStopTime;
        int[] stopDepths = {60, 50, 40, 30, 20};

        for (int i = 0; i < stopTimeIds.length; i++) {
            EditText t = getView().findViewById(stopTimeIds[i]);
            if (!t.getText().toString().trim().equalsIgnoreCase("")) {
                if ((t.getId() != stopTimeIds[3] || !o2At30CheckBox.isChecked()) && (t.getId() != stopTimeIds[4] || !o2At20CheckBox.isChecked())) {
                    airStopTime = Double.parseDouble(t.getText().toString());
                    airForStops += ((stopDepths[i] + 33.0) / 33) * .75 * numDivers * airStopTime;
                } else {
                    airForStops += 0;
                }
            }
        }

        return airForStops;

    }

    //Calculate O2 for Ascent;
    private double calcO2Ascent(int numDivers) {

        return ((15 + 33.0) / 33) * .75 * numDivers * .5;

    }

    //Calculate O2 for stops
    private double calcO2Stops(int numDivers) {

        EditText o2At30Et = getView().findViewById(stopTimeIds[3]);
        EditText o2At20Et = getView().findViewById(stopTimeIds[4]);
        double timeAt30 = Double.parseDouble(o2At30Et.getText().toString());
        double timeAt20 = Double.parseDouble(o2At20Et.getText().toString());

        double totalO2;

        double o2ForShiftVent;

        if (o2At30CheckBox.isChecked()) {
            o2ForShiftVent = ((30 + 33.0) / 33) * 8 * numDivers * 1;
            totalO2 = (((30 + 33.0) / 33) * .75 * numDivers * timeAt30) + (((20 + 33.0) / 33) * .75 * numDivers * timeAt20) + o2ForShiftVent;
        } else {
            o2ForShiftVent = ((20 + 33.0) / 33) * 8 * numDivers * 1;
            totalO2 = (((20 + 33.0) / 33) * .75 * numDivers * timeAt20) + o2ForShiftVent;
        }
        return totalO2;

    }

    //Calculate air PSIG
    private int calcAirPsig(double totalAirSCF, int bottomDepth, int numFlasksAir, double floodVolAir) {

        return (int) (((totalAirSCF * 14.7) / (numFlasksAir * floodVolAir)) + ((bottomDepth * .445) + 200)) + 1;

    }

    //Calculate O2 PSIG
    private int calcO2Psig(double totalO2SCF, int numFlasksO2, double floodVolO2) {

        return (int) (((totalO2SCF * 14.7) / (numFlasksO2 * floodVolO2)) + ((30 * .445) + 200)) + 1;

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

    public static SsdsAirO2Fragment newInstance(String param1, String param2) {
        SsdsAirO2Fragment fragment = new SsdsAirO2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ssds_air_o2, container, false);

        Button b = v.findViewById(R.id.calcButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAirO2(v);
            }
        });
        return v;
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(View view, Bundle savedInstanceState) {

        bottomDepthEditText = getView().findViewById(R.id.bottomDepthEditText);
        bottomTimeEditText = getView().findViewById(R.id.bottomTimeEditText);
        numDiversEditText = getView().findViewById(R.id.numDiversEditText);
        floodVolAirEditText = getView().findViewById(R.id.floodVolAirEditText);
        floodVolO2EditText =  getView().findViewById(R.id.floodVolO2EditText);
        numFlasksAirEditText =  getView().findViewById(R.id.numFlasksAirEditText);
        numFlasksO2EditText = getView().findViewById(R.id.numFlasksO2EditText);
        o2RequiredTextView = getView().findViewById(R.id.o2RequiredTextView);
        airRequiredTextView = getView().findViewById(R.id.airRequiredTextView);

        //Set Spinner Values

        final Spinner floodVolAirSpinner =  getView().findViewById(R.id.floodVolAirSpinner);
        final Spinner floodVolO2Spinner =  getView().findViewById(R.id.floodVolO2Spinner);

        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.flood_vol_ssds_air_spinner, android.R.layout.simple_spinner_dropdown_item);
        floodVolAirSpinner.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.flood_vol_ssds_o2_spinner, android.R.layout.simple_spinner_dropdown_item);
        floodVolO2Spinner.setAdapter(adapter);

        floodVolAirEditText.setText(Double.toString(airFlaskFloodVols[0]));
        floodVolO2EditText.setText(Double.toString(o2FlaskFloodVols[0]));

        floodVolAirSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerEditText(floodVolAirSpinner, floodVolAirEditText, airFlaskFloodVols);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        floodVolO2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerEditText(floodVolO2Spinner, floodVolO2EditText, o2FlaskFloodVols);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Decompression and O2 CheckBox + ConstraintLayouts
        decoRequiredCheckBox = (CheckBox) getView().findViewById(R.id.decoRequiredCheckBox);
        o2At30CheckBox = (CheckBox) getView().findViewById(R.id.o2At30CheckBox);
        o2At20CheckBox = (CheckBox) getView().findViewById(R.id.o2At20CheckBox);

        decoConstraintLayout = getView().findViewById(R.id.decoConstraintLayout);
        o2ConstraintLayout = getView().findViewById(R.id.o2ConstraintLayout);

        //Listener for decoRequiredCheckbox to expand or collapse decoConstraintLayout
        decoRequiredCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    expand(decoConstraintLayout);
                } else {
                    collapse(decoConstraintLayout);
                    o2At30CheckBox.setChecked(false);
                    o2At20CheckBox.setChecked(false);
                    o2At20CheckBox.setEnabled(true);
                    collapse(o2ConstraintLayout);
                }
            }
        });

        //Listeners for o2RequiredCheckbox's to expand or collapse o2ConstraintLayout
        o2At30CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    o2At20CheckBox.setChecked(true);
                    o2At20CheckBox.setEnabled(false);
                    expand(o2ConstraintLayout);
                } else {
                    o2At20CheckBox.setEnabled(true);
                }
            }
        });

        o2At20CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    expand(o2ConstraintLayout);
                } else {
                    collapse(o2ConstraintLayout);
                }
            }
        });

    }

}
