package ipro239.iitbeaconproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class UserModeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_mode, container, false);
        Button applyButton = (Button) view.findViewById(R.id.user_mode_apply);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyOptions();
                getActivity().finish();
            }
        });
        return view;
    }

    private void applyOptions(){

    }
}
