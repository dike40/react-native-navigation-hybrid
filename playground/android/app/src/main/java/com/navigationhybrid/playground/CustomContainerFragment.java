package com.navigationhybrid.playground;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.listenzz.navigation.AwesomeFragment;
import me.listenzz.navigation.FragmentHelper;

public class CustomContainerFragment extends AwesomeFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_container, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            contentFragment = (AwesomeFragment) getChildFragmentManager().findFragmentById(R.id.content);
            overlayFragment = (AwesomeFragment) getChildFragmentManager().findFragmentById(R.id.overlay);
        } else {
            if (overlayFragment == null || contentFragment == null) {
                throw new IllegalArgumentException("必须指定 overlayFragment 以及 contentFragment");
            } else {
                FragmentHelper.addFragmentToAddedList(getChildFragmentManager(), R.id.content, contentFragment, true);
                FragmentHelper.addFragmentToAddedList(getChildFragmentManager(), R.id.overlay, overlayFragment, false);
            }
        }
    }

    private AwesomeFragment overlayFragment;

    private AwesomeFragment contentFragment;

    public void setOverlayFragment(@NonNull final AwesomeFragment overlayFragment) {
        this.overlayFragment = overlayFragment;
    }

    public void setContentFragment(@NonNull final AwesomeFragment contentFragment) {
        this.contentFragment = contentFragment;
    }
}
