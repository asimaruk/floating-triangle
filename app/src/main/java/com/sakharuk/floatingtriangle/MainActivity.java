package com.sakharuk.floatingtriangle;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        FragmentManager fm = getFragmentManager();
        Fragment f = fm.findFragmentByTag(SurfaceFragment.class.getCanonicalName());
        if (f == null) {
            fm.beginTransaction()
                    .add(R.id.fragment_container, new SurfaceFragment(), SurfaceFragment.class.getCanonicalName())
                    .commit();
        }
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        getWindow().setBackgroundDrawable(null);
    }
}
