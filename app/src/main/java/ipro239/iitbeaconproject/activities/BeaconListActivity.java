package ipro239.iitbeaconproject.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.Collection;
import java.util.List;

import ipro239.iitbeaconproject.R;
import ipro239.iitbeaconproject.activities.helper.BeaconRVAdapter;
import ipro239.iitbeaconproject.beacon.Beacon;

/**
 * Created by shuao23 on 3/30/2017.
 */

public abstract class BeaconListActivity extends AppCompatActivity{

    //Key used to extract data from intent extras
    public static final String BEACON_LIST_KEY = "BeaconList";

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_beacon_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.beacon_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        initAdapter();
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

    protected abstract List<Beacon> getData();

    protected void initAdapter(){
        BeaconRVAdapter adapter = new BeaconRVAdapter(getData());
        recyclerView.setAdapter(adapter);
    }
}
