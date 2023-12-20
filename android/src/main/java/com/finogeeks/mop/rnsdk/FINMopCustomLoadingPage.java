package com.finogeeks.mop.rnsdk;

import android.content.Context;

import androidx.annotation.NonNull;

import com.finogeeks.lib.applet.modules.appletloadinglayout.FinAppletDefaultLoadingPage;

public class FINMopCustomLoadingPage extends FinAppletDefaultLoadingPage {
    public FINMopCustomLoadingPage(@NonNull Context context) {
        super(context);
    }

    @Override
    public boolean hideTechSupport() {
        return true;
    }
}
