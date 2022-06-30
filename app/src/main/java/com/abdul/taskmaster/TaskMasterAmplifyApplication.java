package com.abdul.taskmaster;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;

public class TaskMasterAmplifyApplication extends Application {
    public static final String TAG = "TASKMASTER APPLICATION";

    @Override
    public void onCreate() {
        super.onCreate();
        try{
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException aEx) {
            Log.e(TAG,"Error with initializing AMplify" + aEx.getMessage());
        }
    }
}
