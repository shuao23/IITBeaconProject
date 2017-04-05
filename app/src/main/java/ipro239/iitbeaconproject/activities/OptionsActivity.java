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

import ipro239.iitbeaconproject.BeaconService;
import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 3/25/2017.
 */

public class OptionsActivity extends AppCompatActivity {

    public static final String BEACON_PREF_NAME = "BeaconPref";
    public static final String BACKGROUND_SCANNING_KEY = "background scanning";
    public static final String NOTIFICATION_KEY = "NOTIFICATION_KEY";

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

        aSwitch = (Switch)findViewById(R.id.ui_notification);
        aSwitch.setChecked(preferences.getBoolean(NOTIFICATION_KEY, false));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(NOTIFICATION_KEY, isChecked);
                editor.apply();

                //Start service
                if(isChecked)
                    startService(new Intent(OptionsActivity.this, BeaconService.class));
                else
                    stopService(new Intent(OptionsActivity.this, BeaconService.class));
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
