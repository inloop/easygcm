package eu.inloop.easygcm.sample;

import android.app.Activity;
import android.os.Bundle;

import eu.inloop.easygcm.GcmHelper;


public class MainActivity extends Activity {

    private String SENDER_ID = "835909578313"; // easygcm-sample project

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        GcmHelper.init(this, SENDER_ID);
    }

}
