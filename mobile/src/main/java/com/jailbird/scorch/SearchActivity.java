package com.jailbird.scorch;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class SearchActivity extends AppCompatActivity implements InterestFragment.OnListFragmentInteractionListener {
FrameLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_dialog);
        container= (FrameLayout) findViewById(R.id.container);
    }

    @Override
    protected void onResume() {
        super.onResume();
        InterestFragment f1 = new InterestFragment();
        FragmentManager fM = getFragmentManager();
        fM.beginTransaction().replace(R.id.container, f1)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(Interest item) {

    }
}
