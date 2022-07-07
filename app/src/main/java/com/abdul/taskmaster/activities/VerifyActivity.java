package com.abdul.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abdul.taskmaster.R;
import com.amplifyframework.core.Amplify;

public class VerifyActivity extends AppCompatActivity {
    String TAG = "VerifyAccountActivity";
    static String TAG_VERIFY =  "VerifyAccountTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        verifyBtn();
    }

    public void verifyBtn()
    {
        Button verifyBtn = findViewById(R.id.verifyButton);
        Intent gettingIntent = getIntent();
        String email = gettingIntent.getStringExtra(SignupActivity.TAG_SIGNUP_EMAIL);
        EditText verifyEmail = findViewById(R.id.verifyTextEmailAddress);
        verifyEmail.setText(email);

        verifyBtn.setOnClickListener( v -> {
            String userEmail = verifyEmail.getText().toString();
            String verification = ((EditText)findViewById(R.id.verifyTextPassword)).getText().toString();

            Amplify.Auth.confirmSignUp(
                    userEmail,
                    verification,
                    success -> {
                        Log.i(TAG,"Verify successful " + success);
                        Intent goLogin = new Intent(VerifyActivity.this, LoginActivity.class);
                        startActivity(goLogin);
                    },
                    fail -> {
                        Log.e(TAG, "Failed Verify :" + fail);

                        runOnUiThread(() ->
                        {
                            Toast.makeText(VerifyActivity.this, "Verification Failed!", Toast.LENGTH_SHORT);
                        });

                    });
        });
    }
}