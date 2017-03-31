package ipro239.iitbeaconproject.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 3/31/2017.
 */

public class WebActivity extends AppCompatActivity {

    public static final String URL_KEY = "urlKey";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getIntent().getExtras().getString(URL_KEY));
        setContentView(webView);
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
