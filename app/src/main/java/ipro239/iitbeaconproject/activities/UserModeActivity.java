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

/**
 * Created by shuao23 on 3/28/2017.
 */

public class UserModeActivity extends AppCompatActivity {


    private static final String INITIALIZED = "Initialized";
    private static final String USERMODE = "usermode";
    public static final String flag="flagStr";
    private static final String TAG = "UserModeActivity";

    //Represent an integer up to 5 bits to save filter information
    public static String FILTERS="filters";

    private int previousMode;

    public static boolean Initialized(Context context){
        SharedPreferences preferences = context.getSharedPreferences(OptionsActivity.BEACON_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(INITIALIZED, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_mode);
        loadSavedSettings();
        setAllListeners();
    }

    private void loadSavedSettings(){
        SharedPreferences preferences = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,Context.MODE_PRIVATE);

        previousMode = preferences.getInt(USERMODE, R.id.ui_student_button);
        ((RadioGroup)findViewById(R.id.ui_usermode_grp)).check(previousMode);

        Log.d(TAG, "filter number get: " + preferences.getInt(FILTERS,0));
        ((Switch)findViewById(R.id.filter_1)).setChecked((preferences.getInt(FILTERS,0)&1)==1);
        ((Switch)findViewById(R.id.filter_2)).setChecked((preferences.getInt(FILTERS,0)&2)==2);
        ((Switch)findViewById(R.id.filter_3)).setChecked((preferences.getInt(FILTERS,0)&4)==4);
        ((Switch)findViewById(R.id.filter_4)).setChecked((preferences.getInt(FILTERS,0)&8)==8);
        ((Switch)findViewById(R.id.filter_5)).setChecked((preferences.getInt(FILTERS,0)&16)==16);

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

        Switch aSwitch = (Switch)findViewById(R.id.filter_1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,Context.MODE_PRIVATE);
                int filters=preferences.getInt(FILTERS,0);
                Log.d(TAG,"Filter get, number is:"+filters);
                if(isChecked){
                    filters=filters|1;
                }
                else{
                    filters=filters&30;
                }
                Log.d(TAG,"Filter 1 set, number is:"+filters);
                editor.putInt(FILTERS, filters);
                editor.apply();
            }
        });
        aSwitch = (Switch)findViewById(R.id.filter_2);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,Context.MODE_PRIVATE);
                int filters=preferences.getInt(FILTERS,0);
                Log.d(TAG,"Filter get, number is:"+filters);
                if(isChecked){
                    filters=filters|2;
                }
                else{
                    filters=filters&29;
                }
                Log.d(TAG,"Filter 2 set, number is:"+filters);
                editor.putInt(FILTERS, filters);
                editor.apply();
            }
        });
        aSwitch = (Switch)findViewById(R.id.filter_3);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,Context.MODE_PRIVATE);
                int filters=preferences.getInt(FILTERS,0);
                Log.d(TAG,"Filter get, number is:"+filters);
                if(isChecked){
                    filters=filters|4;
                }
                else{
                    filters=filters&27;
                }
                Log.d(TAG,"Filter 3 set, number is:"+filters);
                editor.putInt(FILTERS, filters);
                editor.apply();
            }
        });
        aSwitch = (Switch)findViewById(R.id.filter_4);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,Context.MODE_PRIVATE);
                int filters=preferences.getInt(FILTERS,0);
                Log.d(TAG,"Filter get, number is:"+filters);
                if(isChecked){
                    filters=filters|8;
                }
                else{
                    filters=filters&23;
                }
                Log.d(TAG,"Filter 4 set, number is:"+filters);
                editor.putInt(FILTERS, filters);
                editor.apply();
            }
        });
        aSwitch = (Switch)findViewById(R.id.filter_5);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,Context.MODE_PRIVATE);
                int filters=preferences.getInt(FILTERS,0);
                Log.d(TAG,"Filter get, number is:"+filters);
                if(isChecked){
                    filters=filters|16;
                }
                else{
                    filters=filters&15;
                }
                Log.d(TAG,"Filter 5 set, number is:"+filters);
                editor.putInt(FILTERS, filters);
                editor.apply();
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
