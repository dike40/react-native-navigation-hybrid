package com.navigationhybrid.playground;

import android.os.Bundle;

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
        root.setContainerFragment(getReactBridgeManager().createFragment("Transparent"));

        setActivityRootFragment(root);

    }

}
