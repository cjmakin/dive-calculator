package com.chrismakin.divecalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class SsdsEgsFragment extends Fragment {
    private EditText egsBottomDepthET;
    private EditText egsTimeToStageET;
    private EditText depth1stStopET;
    private EditText overBottomET;
    private EditText egsFloodVolET;

    private Spinner egsFloodVolSpinner;

    TextView minPsiTV;

    private int[] editTextIds = new int[]{R.id.egsBottomDepthET, R.id.timeToStageET, R.id.depthFirstStopET, R.id.overBottomET, R.id.egsFloodVolET};
    private double[] egsFlaskFloodVols = new double[]{.399, .470, .319, .281, .526, .445, .420};

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public SsdsEgsFragment() {
        // Required empty public constructor
    }

   //On click
    @SuppressLint("SetTextI18n")
    private void onClickEgs(View view) {

        if (fieldIsFilled()) {

            int bottomDepth = Integer.parseInt(egsBottomDepthET.getText().toString());
            int timeToStage = Integer.parseInt(egsTimeToStageET.getText().toString());
            int depthFirstStop = Integer.parseInt(depth1stStopET.getText().toString());
            int overBottom = Integer.parseInt(overBottomET.getText().toString());
            double floodVol = Double.parseDouble(egsFloodVolET.getText().toString());

            double totalScf = calcAirToStage(timeToStage, bottomDepth) + calcAirAscent(bottomDepth, depthFirstStop);
            int psigRequired = calcPsigRequired(totalScf, floodVol, depthFirstStop, overBottom);

            minPsiTV.setText(Integer.toString(psigRequired));
        }

    }

    //Calculate air for return to stage
    private double calcAirToStage(int timeToStage, int bottomDepth) {
        return ((bottomDepth + 33.0) / 33) * 1.4 * timeToStage;
    }

    //Calculate air for ascent to first stop
    private double calcAirAscent(int bottomDepth, int stopDepth) {
        int timeToStop = ((int) ((bottomDepth - stopDepth) / 30.0)) + 1;
        double avgDepth = (bottomDepth + stopDepth) / 2.0;
        return ((avgDepth + 33) / 33) * .75 * timeToStop;
    }

    //Calculate psig required
    private int calcPsigRequired(double totalScf, double floodVol, int depthFirstStop, int overBottom) {
        double minCylPressure = overBottom + (depthFirstStop * .445);
        return ((int) (((totalScf / floodVol) * 14.7) + minCylPressure) / 100) * 100 + 100;
    }

    public static SsdsEgsFragment newInstance(String param1, String param2) {
        SsdsEgsFragment fragment = new SsdsEgsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public boolean fieldIsFilled() {
        boolean isFilled = true;

        for (int id : editTextIds) {
            EditText t = getView().findViewById(id);
            if (t.getText().toString().trim().equalsIgnoreCase("")) {
                t.setError("Enter a value");
                isFilled = false;
            } else if (Double.parseDouble(t.getText().toString()) == 0 && id != depth1stStopET.getId()) {
                t.setError("Enter a value greater than 0");
                isFilled = false;
            }
        }
        return isFilled;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
    public void onViewCreated(View view, Bundle savedInstanceState) {

        egsBottomDepthET = getView().findViewById(R.id.egsBottomDepthET);
        egsTimeToStageET = getView().findViewById(R.id.timeToStageET);
        depth1stStopET = getView().findViewById(R.id.depthFirstStopET);
        overBottomET = getView().findViewById(R.id.overBottomET);
        egsFloodVolET = getView().findViewById(R.id.egsFloodVolET);

        minPsiTV = getView().findViewById(R.id.minPsiTV);

        //Set Spinner Vals
        egsFloodVolSpinner = getView().findViewById(R.id.egsFloodVolSpinner);
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.scuba_flood_vol_spinner_array, android.R.layout.simple_spinner_dropdown_item);
        egsFloodVolSpinner.setAdapter(adapter);

        egsFloodVolET.setText(Double.toString(egsFlaskFloodVols[0]));

        egsFloodVolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                egsFloodVolET.setText(Double.toString(egsFlaskFloodVols[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


}
