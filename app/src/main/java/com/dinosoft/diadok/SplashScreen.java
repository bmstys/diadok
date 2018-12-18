package com.dinosoft.diadok;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SplashScreen extends AppCompatActivity
{
    private ProgressBar mProgressBar;
    private int progressStatus = 0;

    private Handler mHandler = new Handler();

    private static boolean setPersistence = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Untuk biar ketika offline data tetep bisa diakses
        if (! setPersistence)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            setPersistence = true;
        }

        FirebaseDatabase.getInstance().getReference().keepSynced(true);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                doWork();
                startApp();
                finish();
            }
        }).start();
    }

    private void startApp()
    {
        Intent intent = new Intent(SplashScreen.this,MainActivity.class);
        startActivity(intent);
    }

    private void doWork()
    {
        for(int i=0;i<100;i++)
        {
            try
            {
                Thread.sleep(25);
                mProgressBar.setProgress(i);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
