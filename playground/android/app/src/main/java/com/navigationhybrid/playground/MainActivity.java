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

        Bundle props = new Bundle();
        props.putString("text", "Hello");
        final HybridFragment transparent = getReactBridgeManager().createFragment("Transparent", props, null);

        CustomContainerFragment root = new CustomContainerFragment();
        root.setContentFragment(reactNavigation);
        root.setOverlayFragment(transparent);
        setActivityRootFragment(root);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle props = new Bundle();
                props.putString("text", "World");
                transparent.setAppProperties(props);
            }
        }, 5000);

    }

}
