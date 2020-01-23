package com.rye.catcher.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.rye.catcher.R;
import com.rye.catcher.factory.model.Author;

public class MessageActivity extends AppCompatActivity {
     public static  void show(Context context, Author author){

     }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }
}
