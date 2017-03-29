package ipro239.iitbeaconproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class UserModeActivity extends AppCompatActivity {


    private static final String INITIALIZED = "Initialized";
    private static final String USERMODE = "usermode";
    private static final String FILTER1 = "filter1";
    private static final String FILTER2 = "filter2";
    private static final String FILTER3 = "filter3";
    private static final String FILTER4 = "filter4";
    private static final String FILTER5 = "filter5";

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

        ((Switch)findViewById(R.id.filter_1)).setChecked(preferences.getBoolean(FILTER1, false));
        ((Switch)findViewById(R.id.filter_2)).setChecked(preferences.getBoolean(FILTER2, false));
        ((Switch)findViewById(R.id.filter_3)).setChecked(preferences.getBoolean(FILTER3, false));
        ((Switch)findViewById(R.id.filter_4)).setChecked(preferences.getBoolean(FILTER4, false));
        ((Switch)findViewById(R.id.filter_5)).setChecked(preferences.getBoolean(FILTER5, false));

        forceEnableCustomOptions(previousMode);
    }

    private void setAllListeners(){
        final SharedPreferences.Editor editor =
                getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,Context.MODE_PRIVATE).edit();
        Button applyButton = (Button) findViewById(R.id.user_mode_apply);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                editor.putBoolean(INITIALIZED, true);
                editor.apply();
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
                editor.putBoolean(FILTER1, isChecked);
            }
        });
        aSwitch = (Switch)findViewById(R.id.filter_2);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(FILTER2, isChecked);
            }
        });
        aSwitch = (Switch)findViewById(R.id.filter_3);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(FILTER3, isChecked);
            }
        });
        aSwitch = (Switch)findViewById(R.id.filter_4);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(FILTER4, isChecked);
            }
        });
        aSwitch = (Switch)findViewById(R.id.filter_5);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(FILTER5, isChecked);
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
