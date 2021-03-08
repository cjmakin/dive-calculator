package com.chrismakin.divecalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * The SsdsEgsFragment class implements calculations for determining minimum required EGS pressure.
 * It is a fragment that is launched inside of the SsdsActivity class.
 *
 * @author Christian Makin
 * @version 1.0.1
 * @since 2021-03-04
 */
public class SsdsEgsFragment extends Fragment {
    private EditText egsBottomDepthET;
    private EditText egsTimeToStageET;
    private EditText depthOf1stStopET;
    private EditText overBottomET;
    private EditText egsFloodVolET;
    private TextView minimumPsiTV;

    //Class constants
    private final int[] EDITTEXT_IDS = new int[]{R.id.egsBottomDepthET, R.id.timeToStageET,
            R.id.depthFirstStopET, R.id.overBottomET, R.id.egsFloodVolET};
    private final double[] EGS_FLASK_FLOODABLE_VOL = new double[] {
            .399, .470, .319, .281, .526, .445, .420};
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    /**
     * Required empty public constructor.
     */
    public SsdsEgsFragment() { }

    /**
     * Calculates the amount of air needed (SCF) for divers to return to the stage.
     * @param timeToStage Estimated time in minutes for divers to return to stage
     * @param bottomDepth Planned depth of dive
     * @return Amount of air needed in SCF to return to the stage
     */
    private double calcAirToStage(int timeToStage, int bottomDepth) {
        return ((bottomDepth + 33.0) / 33) * 1.4 * timeToStage;
    }

    /**
     * Calculates the amount of air needed for divers to return to first stop during ascent.
     * @param bottomDepth Planned depth of dive
     * @param stopDepth Planned depth of diver's first stop
     * @return Amount of air needed in SCF for divers to reach first stop
     */
    private double calcAirAscent(int bottomDepth, int stopDepth) {
        int timeToStop = ((int) ((bottomDepth - stopDepth) / 30.0)) + 1;
        double avgDepth = (bottomDepth + stopDepth) / 2.0;
        return ((avgDepth + 33) / 33) * .75 * timeToStop;
    }

    /**
     * Calculates the total PSIG required given the SCF and floodable volume of flask.
     * @param totalScf Total air in SCF
     * @param floodVol Floodable volume of the flask
     * @param depthFirstStop Depth of the first stop
     * @param overBottom Overbottom of 1st stage regulator on EGS
     * @return Total PSIG required
     */
    private int calcPsigRequired(double totalScf, double floodVol, int depthFirstStop,
                                 int overBottom) {
        double minCylPressure = overBottom + (depthFirstStop * .445);
        return ((int) (((totalScf / floodVol) * 14.7) + minCylPressure) / 100) * 100 + 100;
    }

    /**
     * Returns true if all required fields are filled and has values greater than 0.
     * Sets an error prompting the user to input a value if not filled, or a value greater than 0.
     * @return boolean
     */
    public boolean fieldIsFilled() {
        boolean isFilled = true;

        for (int id : EDITTEXT_IDS) {
            EditText t = requireView().findViewById(id);
            if (t.getText().toString().trim().equalsIgnoreCase("")) {
                t.setError("Enter a value");
                isFilled = false;
            } else if (Double.parseDouble(t.getText().toString()) == 0 &&
                    id != depthOf1stStopET.getId()) {
                t.setError("Enter a value greater than 0");
                isFilled = false;
            }
        }
        return isFilled;
    }

    //On click
    @SuppressLint("SetTextI18n")
    private void onClickEgs(View view) {

        if (fieldIsFilled()) {

            int bottomDepth = Integer.parseInt(egsBottomDepthET.getText().toString());
            int timeToStage = Integer.parseInt(egsTimeToStageET.getText().toString());
            int depthFirstStop = Integer.parseInt(depthOf1stStopET.getText().toString());
            int overBottom = Integer.parseInt(overBottomET.getText().toString());
            double floodVol = Double.parseDouble(egsFloodVolET.getText().toString());

            double totalScf = calcAirToStage(timeToStage, bottomDepth) +
                    calcAirAscent(bottomDepth, depthFirstStop);
            int psigRequired = calcPsigRequired(totalScf, floodVol, depthFirstStop, overBottom);
            minimumPsiTV.setText(Integer.toString(psigRequired));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static SsdsEgsFragment newInstance(String param1, String param2) {
        SsdsEgsFragment fragment = new SsdsEgsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ssds_egs, container, false);

        Button b = v.findViewById(R.id.egsCalcButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEgs(v);
            }
        });
        return v;
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        egsBottomDepthET = requireView().findViewById(R.id.egsBottomDepthET);
        egsTimeToStageET = requireView().findViewById(R.id.timeToStageET);
        depthOf1stStopET = requireView().findViewById(R.id.depthFirstStopET);
        overBottomET = requireView().findViewById(R.id.overBottomET);
        egsFloodVolET = requireView().findViewById(R.id.egsFloodVolET);
        minimumPsiTV = requireView().findViewById(R.id.minPsiTV);

        //Set Spinner Values
        Spinner egsFloodVolSpinner = requireView().findViewById(R.id.egsFloodVolSpinner);
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.scuba_flood_vol_spinner_array,
                android.R.layout.simple_spinner_dropdown_item);
        egsFloodVolSpinner.setAdapter(adapter);

        egsFloodVolET.setText(Double.toString(EGS_FLASK_FLOODABLE_VOL[0]));

        egsFloodVolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                egsFloodVolET.setText(Double.toString(EGS_FLASK_FLOODABLE_VOL[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
