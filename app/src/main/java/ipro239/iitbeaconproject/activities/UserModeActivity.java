package ipro239.iitbeaconproject.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;

import ipro239.iitbeaconproject.R;
import ipro239.iitbeaconproject.beacon.BeaconFilters;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class UserModeActivity extends AppCompatActivity {

    //sharedPref key to determine if usermode has been initialized
    private static final String INITIALIZED = "Initialized";
    //sharedPref key to store the usermode
    private static final String USERMODE = "usermode";
    //sharedPref key to store the flag
    public static final String FILTERS="filters";

    private int previousMode;
    private BeaconFilters filters;
    private SharedPreferences preferences;

    public static boolean Initialized(Context context){
        SharedPreferences preferences = context.getSharedPreferences(OptionsActivity.BEACON_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(INITIALIZED, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filters = new BeaconFilters(this);
        preferences = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,Context.MODE_PRIVATE);

        setContentView(R.layout.activity_user_mode);
        loadSavedSettings();
        setAllListeners();
    }

    private void loadSavedSettings(){
        previousMode = preferences.getInt(USERMODE, R.id.ui_student_button);
        ((RadioGroup)findViewById(R.id.ui_usermode_grp)).check(previousMode);

        filters.setFlag(preferences.getInt(FILTERS,0));
        LinearLayout layout = (LinearLayout)findViewById(R.id.custom_settings_group);
        for(int i = 0; i < filters.getFilterCount(); i++){
            Switch filterSwitch = new Switch(this);
            filterSwitch.setChecked(filters.isFilterFlagged(i));
            filterSwitch.setText(filters.findFilterByIndex(i));
            filterSwitch.setTextSize(18);
            int padding = getResources().getDimensionPixelOffset(R.dimen.option_padding);
            filterSwitch.setPadding(padding,padding,padding,padding);
            layout.addView(filterSwitch);

            filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        filters.setFilter(buttonView.getText().toString());
                    }else {
                        filters.clearFilter(buttonView.getText().toString());
                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(FILTERS, filters.getFlag());
                    editor.apply();
                }
            });
        }
        forceEnableCustomOptions(previousMode);
    }

    private void setAllListeners(){
        final SharedPreferences.Editor editor =
                getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,Context.MODE_PRIVATE).edit();
        Button applyButton = (Button) findViewById(R.id.user_mode_apply);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(INITIALIZED, true);
                editor.apply();
                finish();
            }
        });

        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.ui_usermode_grp);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                editor.putInt(USERMODE, checkedId);
                enableCustomOptions(checkedId);
            }
        });
    }

    private void enableCustomOptions(int id){
        boolean enable = (id == R.id.ui_customuser_button);
        if (!enable && previousMode != R.id.ui_customuser_button)
            return;
        if (enable && previousMode == R.id.ui_customuser_button)
            return;

        forceEnableCustomOptions(id);
        previousMode = id;
    }

    private void forceEnableCustomOptions(int id){
        boolean enable = (id == R.id.ui_customuser_button);
        LinearLayout layout = (LinearLayout)findViewById(R.id.custom_settings_group);
        for(int i = 0; i < layout.getChildCount(); i++){
            Switch child = (Switch) layout.getChildAt(i);
            child.setEnabled(enable);
        }
        previousMode = id;
    }
}
