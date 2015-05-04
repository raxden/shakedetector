package com.raxdenstudios.shake;

import android.content.Context;
import android.os.Bundle;

import com.raxdenstudios.app.module.ModuleImpl;

/**
 * Created by agomez on 04/05/2015.
 */
public class ShakeDetectorModule extends ModuleImpl {

    private ShakeDetectorHelper.ShakeDetectorListener mListener;

    public ShakeDetectorModule(ShakeDetectorHelper.ShakeDetectorListener listener) {
        mListener = listener;
    }

    @Override
    public void onModuleCreate(Context context, Bundle savedInstanceState) {
        super.onModuleCreate(context, savedInstanceState);
        ShakeDetectorHelper.getInstance().startShakeDetector(context, mListener);
    }

    @Override
    public void onModuleDestroy(Context context) {
        super.onModuleDestroy(context);
        ShakeDetectorHelper.getInstance().stopShakeDetector();
    }

}
