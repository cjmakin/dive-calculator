package com.chrismakin.divecalculator;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ActivityOptions options;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.ssdsButton) {
            startSpecificActivity(SsdsActivity.class);
        } else if (id == R.id.scubaButton) {
            startSpecificActivity(ScubaActivity.class);
        } else if (id == R.id.chamberButton) {
            startSpecificActivity(ChamberActivity.class);
        } else if (id == R.id.conversionsButton) {
            startSpecificActivity(ConversionsActivity.class);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
