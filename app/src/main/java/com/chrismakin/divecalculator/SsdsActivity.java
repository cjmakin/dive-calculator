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
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * The SsdsActivity contains a spinner and FragmentContainerView. The user selects either EGS
 * requirement or Air / O2 Requirement calculation from the spinner which launches the respective
 * fragment inside the FragmentContainerView.
 *
 * @author Christian Makin
 * @version 1.0.1
 * @since 2021-03-04
 */
public class SsdsActivity extends AppCompatActivity {
    Fragment airO2Fragment;
    Fragment egsFragment;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssds);

        airO2Fragment = new Fragment();
        egsFragment = new Fragment();


        //Sets the AirO2Fragment inside of the FragmentContainerView
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, new SsdsAirO2Fragment());
        ft.commit();

        //Set Spinner Values
        Spinner chooseCalcSpinner = findViewById(R.id.chooseCalcSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.ssds_calculation_spinner, R.layout.spinner_item);

        chooseCalcSpinner.setAdapter(adapter);

        chooseCalcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
                    ft.replace(R.id.flFragment, new SsdsAirO2Fragment());
                    ft.commit();
                } else {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
                    ft.replace(R.id.flFragment, new SsdsEgsFragment());
                    ft.commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}


