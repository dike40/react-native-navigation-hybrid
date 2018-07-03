package com.navigationhybrid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.Callback;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.devsupport.DoubleTapReloadRecognizer;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionListener;

import javax.annotation.Nullable;

/**
 * Created by Listen on 2017/11/17.
 */

public class ReactAppCompatActivityDelegate {

    private static final String TAG = "ReactNative";

    private final int REQUEST_OVERLAY_PERMISSION_CODE = 1111;
    private static final String REDBOX_PERMISSION_GRANTED_MESSAGE =
            "Overlay permissions have been granted.";
    private static final String REDBOX_PERMISSION_MESSAGE =
            "Overlay permissions needs to be granted in order for react native apps to run in dev mode";

    private final AppCompatActivity mActivity;
    private final ReactBridgeManager bridgeManager;

    private @Nullable
    DoubleTapReloadRecognizer mDoubleTapReloadRecognizer;
    private @Nullable
    PermissionListener mPermissionListener;
    private @Nullable
    Callback mPermissionsCallback;

    public ReactAppCompatActivityDelegate(@NonNull AppCompatActivity activity, ReactBridgeManager bridgeManager) {
        this.mActivity = activity;
        this.bridgeManager = bridgeManager;
    }

    private void askPermission() {
        if (getReactNativeHost().getUseDeveloperSupport() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "check overlay permission");
            // Get permission to show redbox in dev builds.
            if (!Settings.canDrawOverlays(getContext())) {
                Log.i(TAG, "request overlay permission");
                Intent serviceIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                FLog.w(ReactConstants.TAG, REDBOX_PERMISSION_MESSAGE);
                Toast.makeText(getContext(), REDBOX_PERMISSION_MESSAGE, Toast.LENGTH_LONG).show();
                ((Activity) getContext()).startActivityForResult(serviceIntent, REQUEST_OVERLAY_PERMISSION_CODE);
            }
        }
    }

    protected ReactNativeHost getReactNativeHost() {
        return bridgeManager.getReactNativeHost();
    }

    protected ReactInstanceManager getReactInstanceManager() {
        return bridgeManager.getReactInstanceManager();
    }

    protected ReactBridgeManager getReactBridgeManager() {
        return bridgeManager;
    }

    protected void onCreate(Bundle savedInstanceState) {
        mDoubleTapReloadRecognizer = new DoubleTapReloadRecognizer();
        if (!bridgeManager.isReactModuleInRegistry()) {
            askPermission();
        }
        IntentFilter intentFilter = new IntentFilter(ReactBridgeManager.REACT_INSTANCE_CONTEXT_INITIALIZED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, intentFilter);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            askPermission();
        }
    };

    protected void onPause() {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostPause(getPlainActivity());
        }
    }

    protected void onResume() {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostResume(
                    getPlainActivity(),
                    (DefaultHardwareBackBtnHandler) getPlainActivity());
        }

        if (mPermissionsCallback != null) {
            mPermissionsCallback.invoke();
            mPermissionsCallback = null;
        }
    }

    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostDestroy(getPlainActivity());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager()
                    .onActivityResult(getPlainActivity(), requestCode, resultCode, data);
        } else {
            // Did we request overlay permissions?
            if (requestCode == REQUEST_OVERLAY_PERMISSION_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getContext())) {
                    Toast.makeText(getContext(), REDBOX_PERMISSION_GRANTED_MESSAGE, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getReactNativeHost().hasInstance()
                && getReactNativeHost().getUseDeveloperSupport()
                && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            event.startTracking();
            return true;
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (getReactNativeHost().hasInstance() && getReactNativeHost().getUseDeveloperSupport()) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
                return true;
            }
            boolean didDoubleTapR = Assertions.assertNotNull(mDoubleTapReloadRecognizer)
                    .didDoubleTapR(keyCode, getPlainActivity().getCurrentFocus());
            if (didDoubleTapR) {
                clearFragments();
                getReactNativeHost().getReactInstanceManager().getDevSupportManager().handleReloadJS();
                return true;
            }
        }
        return false;
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (getReactNativeHost().hasInstance()
                && getReactNativeHost().getUseDeveloperSupport()
                && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
            return true;
        }
        return false;
    }

    public boolean onBackPressed() {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onBackPressed();
            return true;
        }
        return false;
    }

    public boolean onNewIntent(Intent intent) {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onNewIntent(intent);
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        mPermissionListener = listener;
        getPlainActivity().requestPermissions(permissions, requestCode);
    }

    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        mPermissionsCallback = new Callback() {
            @Override
            public void invoke(Object... args) {
                if (mPermissionListener != null && mPermissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                    mPermissionListener = null;
                }
            }
        };
    }

    public void clearFragments() {
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 0) {
            mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            String tag = fragmentManager.getBackStackEntryAt(0).getName();
            fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private Context getContext() {
        return Assertions.assertNotNull(mActivity);
    }

    private Activity getPlainActivity() {
        return ((Activity) getContext());
    }

}
