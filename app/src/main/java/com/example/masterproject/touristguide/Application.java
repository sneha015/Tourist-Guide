package com.example.masterproject.touristguide;

import com.tramsun.libs.prefcompat.Pref;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Aladin-Regular.ttf");

        Pref.init(this);
    }
}
