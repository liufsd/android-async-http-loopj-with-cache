package com.eusoft;

import android.app.Activity;
import android.os.Bundle;
import com.eusoft.utils.JniApi;
import com.eusoft.R;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JniApi.appcontext = this;
        setContentView(R.layout.activity_main);
    }
}
