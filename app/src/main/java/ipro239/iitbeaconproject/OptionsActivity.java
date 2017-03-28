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
 * Created by shuao23 on 3/25/2017.
 */

public class OptionsActivity extends AppCompatActivity {

    public static final String BEACON_PREF_NAME = "BeaconPref";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup the back button
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup options
        Fragment newFragment = new OptionFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FrameLayout layout = new FrameLayout(this);
        transaction.replace(layout.getId(), newFragment);
        transaction.commit();
    }
}
