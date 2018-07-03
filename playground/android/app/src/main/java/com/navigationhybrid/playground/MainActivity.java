package com.navigationhybrid.playground;

import android.os.Bundle;
import android.os.Handler;

import com.navigationhybrid.HybridFragment;
import com.navigationhybrid.ReactAppCompatActivity;
import com.navigationhybrid.ReactNavigationFragment;

import me.listenzz.navigation.AwesomeFragment;


public class MainActivity extends ReactAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateMainComponent() {

        AwesomeFragment react = getReactBridgeManager().createFragment("Navigation");
        ReactNavigationFragment reactNavigation = new ReactNavigationFragment();
        reactNavigation.setRootFragment(react);

        CustomContainerFragment root = new CustomContainerFragment();
        root.setBackgroundFragment(reactNavigation);
        Bundle props = new Bundle();
        props.putString("text", "Hello");
        ReactNavigationFragment navigationFragment = new ReactNavigationFragment();
        final HybridFragment options = (HybridFragment) getReactBridgeManager().createFragment("Options", props, null);

        navigationFragment.setRootFragment(options);
        root.setContainerFragment(navigationFragment);

        setActivityRootFragment(root);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle props = new Bundle();
                props.putString("text", "World");
                options.setAppProperties(props);
            }
        }, 5000);

    }

}
