package com.bis5.fitjourney;

import android.app.Application;

import com.bis5.fitjourney.models.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FitJourneyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Pre-warm the database on a background thread.
        // This prevents the main thread from blocking when the first ViewModel is created.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase.getDatabase(this);
        });
    }
}
