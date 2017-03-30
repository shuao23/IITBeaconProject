package ipro239.iitbeaconproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 3/25/2017.
 */

public class OptionsActivity extends AppCompatActivity {

    public static final String BEACON_PREF_NAME = "BeaconPref";
    public static final String BACKGROUND_SCANNING_KEY = "background scanning";

    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_option);

        preferences = getSharedPreferences(BEACON_PREF_NAME, MODE_PRIVATE);

        //Set onclick for auto scan
        Switch aSwitch = (Switch)findViewById(R.id.ui_autoscan);
        aSwitch.setChecked(preferences.getBoolean(BACKGROUND_SCANNING_KEY, false));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(BACKGROUND_SCANNING_KEY, isChecked);
                editor.apply();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
