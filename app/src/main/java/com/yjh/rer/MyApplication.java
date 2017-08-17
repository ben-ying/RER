package com.yjh.rer;


import android.app.Application;

public class MyApplication extends Application {

//    private final RedEnvelopeComponent redEnvelopeComponent = createComponent();

    @Override
    public void onCreate() {
        super.onCreate();
    }

//    protected RedEnvelopeComponent createComponent() {
//        return DaggerCountdownComponent.builder()
//                .countdownModule(new RedEnvelopeModule(this))
//                .build();
//    }
//
//    public RedEnvelopeComponent getRedEnvelopeComponent() {
//        return redEnvelopeComponent;
//    }

}
