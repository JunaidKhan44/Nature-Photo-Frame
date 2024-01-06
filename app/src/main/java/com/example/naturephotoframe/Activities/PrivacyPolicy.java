package com.example.naturephotoframe.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.naturephotoframe.R;

public class PrivacyPolicy extends AppCompatActivity {


    TextView agreeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        agreeBtn = findViewById(R.id.agree);

        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrivacyPolicy.this, MainActivity.class));
                finish();
            }
        });
    }
}