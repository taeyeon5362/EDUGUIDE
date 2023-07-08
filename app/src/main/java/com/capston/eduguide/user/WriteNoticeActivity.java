package com.capston.eduguide.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.capston.eduguide.R;

public class WriteNoticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_notice);

        // notice1_2 클릭
        TextView profileEdit = findViewById(R.id.notice1_2);
        profileEdit.setOnClickListener(view -> {
            Intent intent = new Intent(WriteNoticeActivity.this, UserNotice1Activity.class);
            startActivity(intent);
        });

        // notice2_2 클릭
        TextView personalinfoEdit = findViewById(R.id.notice2_2);
        personalinfoEdit.setOnClickListener(view -> {
            Intent intent = new Intent(WriteNoticeActivity.this, UserNotice2Activity.class);
            startActivity(intent);
        });

        // notice3_2 클릭
        TextView Scrap = findViewById(R.id.notice3_2);
        Scrap.setOnClickListener(view -> {
            Intent intent = new Intent(WriteNoticeActivity.this, UserNotice3Activity.class);
            startActivity(intent);
        });

    }


}