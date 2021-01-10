package com.chrismakin.divecalculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    ActivityOptions options;

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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ssdsButton:
                startSpecificActivity(SsdsActivity.class);
                break;

            case R.id.scubaButton:
                startSpecificActivity(ScubaActivity.class);
                break;

            case R.id.chamberButton:
                startSpecificActivity(ChamberActivity.class);
                break;

            case R.id.conversionsButton:
                startSpecificActivity(ConversionsActivity.class);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startSpecificActivity(Class<?> otherActivityClass) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(this);
        }
        Intent intent = new Intent(getApplicationContext(), otherActivityClass);
        startActivity(intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
