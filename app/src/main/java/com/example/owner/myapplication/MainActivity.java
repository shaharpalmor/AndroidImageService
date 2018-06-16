package com.example.owner.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void startService(View view){
        Intent intent = new Intent(this, ImageService.class);
        startService(intent);
        //Toast.makeText(this,"Service starting...", Toast.LENGTH_SHORT).show();
    }

    public void stopService(View view){
        //Toast.makeText(this,"Service ending...", Toast.LENGTH_SHORT).show();
         Intent intent = new Intent(this, ImageService.class);
         stopService(intent);
    }

}