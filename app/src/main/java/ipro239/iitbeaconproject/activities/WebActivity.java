package ipro239.iitbeaconproject.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 3/31/2017.
 */

public class WebActivity extends AppCompatActivity {

    public static final String TITTLE_KEY = "tittleKey";
    public static final String URL_KEY = "urlKey";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Parent view
        FrameLayout frameLayout = new FrameLayout(this);
        setContentView(frameLayout);

        WebView webView = new WebView(this);
        webView.setLayoutParams(
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getIntent().getExtras().getString(URL_KEY));
        setTitle(getIntent().getExtras().getString("", "IIT Website"));
        frameLayout.addView(webView);

        final ProgressBar progressBar = new ProgressBar(this);
        int size = getResources().getDimensionPixelSize(R.dimen.loading_size);
        progressBar.setLayoutParams(
                new FrameLayout.LayoutParams(size, size, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        frameLayout.addView(progressBar);

        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                if(progress < 90 && progressBar.getVisibility() == ProgressBar.GONE){
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }

                if(progress >= 90) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
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
