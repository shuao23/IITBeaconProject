package ipro239.iitbeaconproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class InitializeActivity extends AppCompatActivity {

    public static final String INITIALIZED = "Initialized";
    public static final String INIT = "init";
    private static final int LAYOUT_ID = 376;

    public static boolean Initialized(Context context){
        SharedPreferences preferences = context.getSharedPreferences(OptionsActivity.BEACON_PREF_NAME, MODE_PRIVATE);
        return preferences.getBoolean(INITIALIZED, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_initialize);
    }
}
