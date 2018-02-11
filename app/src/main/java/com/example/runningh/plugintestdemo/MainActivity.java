package com.example.runningh.plugintestdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.goto_plugin_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.example.runningh.plugin"
                        , "com.example.runningh.plugin.MainActivity"));
                /*intent.setComponent(new ComponentName("com.huanju.chajiandemo",
                        "com.huanju.chajiandemo.TestActivity"));*/
                startActivity(intent);
            }
        });
    }
}
