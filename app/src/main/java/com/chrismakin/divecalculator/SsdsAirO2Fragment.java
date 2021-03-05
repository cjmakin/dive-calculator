package com.chrismakin.divecalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;


/**
 * The SsdsAirO2Fragment class implements calculations for determining total air / O2 requirements
 * (air in stowage) for surface supplied diving operations. It is a fragment class that is launched
 * inside of the SsdsActivity class.
 *
 * @author Christian Makin
 * @version 1.0.1
 * @since 2021-03-04
 */
public class SsdsAirO2Fragment extends Fragment {
    private EditText numberOfDiversEditText;
    private EditText bottomDepthEditText;
    private EditText bottomTimeEditText;
    private EditText floodableVolumeAirEditText;
    private EditText numberOfFlasksAirEditText;
    private EditText floodableVolUmeO2EditText;
    private EditText numberOfFlasksO2EditText;

    private TextView airRequiredTextView;
    private TextView o2RequiredTextView;

    private CheckBox decoRequiredCheckBox;
    private CheckBox o2At30CheckBox;
    private CheckBox o2At20CheckBox;

    private ConstraintLayout decoConstraintLayout;
    private ConstraintLayout o2ConstraintLayout;

    //Class Constants
    private final int[] EDITTEXT_IDS = new int[]{R.id.numDiversEditText, R.id.bottomDepthEditText,
            R.id.bottomTimeEditText, R.id.floodVolAirEditText, R.id.numFlasksAirEditText};
    private final int[] O2_EDITTEXT_IDS = new int[]{R.id.floodVolO2EditText,
            R.id.numFlasksO2EditText};
    private final int[] STOP_TIME_IDS = new int[]{R.id.stopTime60ET, R.id.stopTime50ET,
            R.id.stopTime40ET, R.id.stopTime30ET, R.id.stopTime20ET};
    private final double[] AIR_FLASK_FLOODABLE_VOLUMES = new double[]{3.15, .935, 3.15, 8, 4};
    private final double[] O2_FLASK_FLOODABLE_VOLUMES = new double[]{1.64, 1.568};

    //The fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    /**
     * Required empty public constructor.
     */
    public SsdsAirO2Fragment() { }

    /**
     * Fills editText with appropriate floodable volume value.
     * @param spinner Spinner
     * @param editText EditText
     * @param floodableVolumes double[]
     */
    @SuppressLint("SetTextI18n")
    private void setSpinnerEditText(Spinner spinner, EditText editText, double[] floodableVolumes) {
        int spinnerIndex = spinner.getSelectedItemPosition();
        editText.setText(Double.toString(floodableVolumes[spinnerIndex]));
    }

    /**
     * Calculates the total air required during bottom phase of dive given the bottom depth,
     * number of divers, and total planned bottom time.
     * @param bottomDepth Max depth planned for dive
     * @param numDivers Number of divers
     * @param bottomTime Total bottom time
     * @return Air required in SCF
     */
    private double calcAirBottomTime(int bottomDepth, int numDivers, int bottomTime) {
        return ((bottomDepth +33.0) / 33) * 1.4 * numDivers * bottomTime;
    }

    /**
     * Calculates the total air required for ascent phase of the dive given the stage depth
     * and number of divers.
     * @param bottomDepth Stage depth
     * @param numDivers Number of divers
     * @return Air required in SCF
     */
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

    /**
     * Calculates the total air in SCF of all required air stops.
     * @param numDivers Number of divers
     * @return Air required in SCF
     */
    private double calcAirStops(int numDivers) {

        double airForStops = 0;
        double airStopTime;
        int[] stopDepths = {60, 50, 40, 30, 20};

        for (int i = 0; i < STOP_TIME_IDS.length; i++) {
            EditText t = getView().findViewById(STOP_TIME_IDS[i]);
            if (!t.getText().toString().trim().equalsIgnoreCase("")) {
                if ((t.getId() != STOP_TIME_IDS[3] || !o2At30CheckBox.isChecked()) &&
                        (t.getId() != STOP_TIME_IDS[4] || !o2At20CheckBox.isChecked())) {
                    airStopTime = Double.parseDouble(t.getText().toString());
                    airForStops += ((stopDepths[i] + 33.0) / 33) * .75 * numDivers * airStopTime;
                } else {
                    airForStops += 0;
                }
            }
        }
        return airForStops;
    }

    /**
     * Caluclates required O2 for ascent from 30fsw.
     * @param numDivers number of divers
     * @return Total O2 required in SCF
     */
    private double calcO2Ascent(int numDivers) {
        return ((15 + 33.0) / 33) * .75 * numDivers * .5;
    }

    /**
     * Calculates required O2 for stops at 30fsw and 20fsw. Accounts for ventilation.
     * @param numDivers Number of divers
     * @return O2 required in SCF
     */
    private double calcO2Stops(int numDivers) {

        EditText o2At30Et = getView().findViewById(STOP_TIME_IDS[3]);
        EditText o2At20Et = getView().findViewById(STOP_TIME_IDS[4]);
        double timeAt30 = Double.parseDouble(o2At30Et.getText().toString());
        double timeAt20 = Double.parseDouble(o2At20Et.getText().toString());

        double totalO2;

        double o2ForShiftVent;

        if (o2At30CheckBox.isChecked()) {
            o2ForShiftVent = ((30 + 33.0) / 33) * 8 * numDivers * 1;
            totalO2 = (((30 + 33.0) / 33) * .75 * numDivers * timeAt30) +
                    (((20 + 33.0) / 33) * .75 * numDivers * timeAt20) + o2ForShiftVent;
        } else {
            o2ForShiftVent = ((20 + 33.0) / 33) * 8 * numDivers * 1;
            totalO2 = (((20 + 33.0) / 33) * .75 * numDivers * timeAt20) + o2ForShiftVent;
        }
        return totalO2;
    }

    /**
     * Converts total air in SCF to PSIG given bottom depth for MMP, floodable volume of flasks,
     * and the number of flasks.
     * @param totalAirSCF Total air in SCF
     * @param bottomDepth Bottom Depth
     * @param numFlasksAir Number of flasks
     * @param floodVolAir Floodable volume of flasks
     * @return Air required in PSIG
     */
    private int calcAirPsig(double totalAirSCF, int bottomDepth,
                            int numFlasksAir, double floodVolAir) {

        return (int) (((totalAirSCF * 14.7) / (numFlasksAir * floodVolAir)) +
                ((bottomDepth * .445) + 200)) + 1;
    }

    /**
     * Converts total O2 in SCF to PSIG given number of flasks and floodable volume of flasks.
     * @param totalO2SCF Total O2 in SCF
     * @param numFlasksO2 Number of flasks
     * @param floodVolO2 Floodable volume of flasks
     * @return
     */
    private int calcO2Psig(double totalO2SCF, int numFlasksO2, double floodVolO2) {
        return (int) (((totalO2SCF * 14.7) / (numFlasksO2 * floodVolO2)) + ((30 * .445) + 200)) + 1;
    }

    /**
     * Returns true if all required fields are filled and has values greater than 0.
     * Sets an error prompting the user to input a value if not filled, or a value greater than 0.
     * @return boolean
     */
    public boolean fieldIsFilled() {
        boolean isFilled = true;

        for (int id : EDITTEXT_IDS) {
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
            for (int i = 0; i < STOP_TIME_IDS.length - 1; i++) {
                EditText t = getView().findViewById(STOP_TIME_IDS[i]);
                EditText tNext = getView().findViewById(STOP_TIME_IDS[i + 1]);

                if (!t.getText().toString().trim().equalsIgnoreCase("") &&
                        tNext.getText().toString().trim().equalsIgnoreCase("")) {

                    tNext.setError("Enter a value");
                    isFilled = false;
                } else if (!t.getText().toString().trim().equalsIgnoreCase("") &&
                        Double.parseDouble(tNext.getText().toString()) == 0) {

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

            for (int id : O2_EDITTEXT_IDS) {
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

    //onClick Calculate!
    @SuppressLint("SetTextI18n")
    public void onClickAirO2(View view) {

        if (fieldIsFilled()) {

            int bottomDepth = Integer.parseInt(bottomDepthEditText.getText().toString());
            int numDivers = Integer.parseInt(numberOfDiversEditText.getText().toString());
            int bottomTime = Integer.parseInt(bottomTimeEditText.getText().toString());
            int numFlasksAir = Integer.parseInt(numberOfFlasksAirEditText.getText().toString());
            double floodVolAir = Double.parseDouble(
                    floodableVolumeAirEditText.getText().toString());

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
                int numFlasksO2 = Integer.parseInt(numberOfFlasksO2EditText.getText().toString());
                double floodVolO2 = Double.parseDouble(
                        floodableVolUmeO2EditText.getText().toString());

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

    //Expand Animation
    private static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View)
                v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                0, View.MeasureSpec.UNSPECIFIED);
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
        a.setDuration(((int)(targetHeight /
                v.getContext().getResources().getDisplayMetrics().density)) * 3);
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
                    v.getLayoutParams().height = initialHeight -
                            (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration(((int)(initialHeight /
                v.getContext().getResources().getDisplayMetrics().density))  * 3);
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
        numberOfDiversEditText = getView().findViewById(R.id.numDiversEditText);
        floodableVolumeAirEditText = getView().findViewById(R.id.floodVolAirEditText);
        floodableVolUmeO2EditText =  getView().findViewById(R.id.floodVolO2EditText);
        numberOfFlasksAirEditText =  getView().findViewById(R.id.numFlasksAirEditText);
        numberOfFlasksO2EditText = getView().findViewById(R.id.numFlasksO2EditText);
        o2RequiredTextView = getView().findViewById(R.id.o2RequiredTextView);
        airRequiredTextView = getView().findViewById(R.id.airRequiredTextView);

        //Set Spinner Values

        final Spinner floodVolAirSpinner =  getView().findViewById(R.id.floodVolAirSpinner);
        final Spinner floodVolO2Spinner =  getView().findViewById(R.id.floodVolO2Spinner);

        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.flood_vol_ssds_air_spinner, android.R.layout.simple_spinner_dropdown_item);
        floodVolAirSpinner.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.flood_vol_ssds_o2_spinner, android.R.layout.simple_spinner_dropdown_item);
        floodVolO2Spinner.setAdapter(adapter);

        floodableVolumeAirEditText.setText(Double.toString(AIR_FLASK_FLOODABLE_VOLUMES[0]));
        floodableVolUmeO2EditText.setText(Double.toString(O2_FLASK_FLOODABLE_VOLUMES[0]));

        floodVolAirSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerEditText(floodVolAirSpinner, floodableVolumeAirEditText,
                        AIR_FLASK_FLOODABLE_VOLUMES);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        floodVolO2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerEditText(floodVolO2Spinner, floodableVolUmeO2EditText,
                        O2_FLASK_FLOODABLE_VOLUMES);
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
        decoRequiredCheckBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

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
