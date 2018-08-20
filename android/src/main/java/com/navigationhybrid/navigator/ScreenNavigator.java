package com.navigationhybrid.navigator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.navigationhybrid.HybridFragment;
import com.navigationhybrid.ReactBridgeManager;
import com.navigationhybrid.ReactNavigationFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.listenzz.navigation.AwesomeFragment;
import me.listenzz.navigation.FragmentHelper;

public class ScreenNavigator implements Navigator {

    private List<String> supportActions = Arrays.asList("present", "presentLayout", "dismiss", "showModal", "showModalLayout", "hideModal");

    @Override
    public String name() {
        return "screen";
    }

    @Override
    public List<String> supportActions() {
        return supportActions;
    }

    @Override
    @Nullable
    public AwesomeFragment createFragment(ReadableMap layout) {
        if (layout.hasKey(name())) {
            ReadableMap screen = layout.getMap(name());
            String moduleName = screen.getString("moduleName");
            Bundle props = null;
            if (screen.hasKey("props")) {
                ReadableMap map = screen.getMap("props");
                props = Arguments.toBundle(map);
            }

            Bundle options = null;
            if (screen.hasKey("options")) {
                ReadableMap map = screen.getMap("options");
                options = Arguments.toBundle(map);
            }
            return getReactBridgeManager().createFragment(moduleName, props, options);
        }
        return null;
    }

    @Override
    public boolean buildRouteGraph(AwesomeFragment fragment, ArrayList<Bundle> graph, ArrayList<Bundle> modalContainer) {
        if (fragment instanceof HybridFragment) {
            HybridFragment screen = (HybridFragment) fragment;
            Bundle bundle = new Bundle();
            bundle.putString("type", name());
            Bundle route = new Bundle();
            route.putString("moduleName", screen.getModuleName());
            route.putString("sceneId", screen.getSceneId());
            bundle.putBundle(name(), route);
            graph.add(bundle);
            return true;
        }
        return false;
    }

    @Override
    public HybridFragment primaryChildFragment(@NonNull AwesomeFragment fragment) {
        if (fragment instanceof HybridFragment) {
            AwesomeFragment presented = FragmentHelper.getLatterFragment(fragment.requireFragmentManager(), fragment);
            if (presented != null) {
                return (HybridFragment) presented;
            }
            return (HybridFragment) fragment;
        }
        return null;
    }

    @Override
    public void handleNavigation(@NonNull AwesomeFragment fragment, @NonNull String action,  @NonNull ReadableMap extras) {
        AwesomeFragment target = null;
        if (extras.hasKey("moduleName")) {
            String moduleName = extras.getString("moduleName");
            if (moduleName != null) {
                Bundle props = null;
                Bundle options = null;
                if (extras.hasKey("props")) {
                    props = Arguments.toBundle(extras.getMap("props"));
                }
                if (extras.hasKey("options")) {
                    options = Arguments.toBundle(extras.getMap("options"));
                }
                target = getReactBridgeManager().createFragment(moduleName, props, options);
            }
        }
        switch (action) {
            case "present":
                if (target != null) {
                    int requestCode = extras.getInt("requestCode");
                    ReactNavigationFragment reactNavigationFragment = new ReactNavigationFragment();
                    reactNavigationFragment.setRootFragment(target);
                    fragment.presentFragment(reactNavigationFragment, requestCode);
                }
                break;
            case "dismiss":
                fragment.dismissFragment();
                break;
            case "showModal":
                if (target != null) {
                    int requestCode = extras.getInt("requestCode");
                    fragment.showDialog(target, requestCode);
                }
                break;
            case "hideModal":
                fragment.dismissDialog();
                break;
            case "presentLayout":
                ReadableMap layout = extras.getMap("layout");
                target = getReactBridgeManager().createFragment(layout);
                if (target != null) {
                    int requestCode = extras.getInt("requestCode");
                    fragment.presentFragment(target, requestCode);
                }
                break;
            case "showModalLayout":
                ReadableMap modalLayout = extras.getMap("layout");
                target = getReactBridgeManager().createFragment(modalLayout);
                if (target != null) {
                    int requestCode = extras.getInt("requestCode");
                    fragment.showDialog(target, requestCode);
                }
                break;

        }
    }

    private ReactBridgeManager getReactBridgeManager() {
        return ReactBridgeManager.instance;
    }
}
