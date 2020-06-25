package com.example.mynewapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    Button btnStart;
    Button btnInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = (Button) findViewById(R.id.MainActivityStart);
        btnStart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start(); // метод который все будет заставлять работать
            }
        });
        btnInfo = (Button) findViewById(R.id.MainActivityInfo);
        btnInfo.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Information();
            }
        });
    }

    private void Start() {
        Intent intent = new Intent(this, CodeActivity.class);
        startActivity(intent);
    }

    private void Information() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

}
