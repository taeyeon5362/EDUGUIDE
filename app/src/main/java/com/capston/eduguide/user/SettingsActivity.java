package com.capston.eduguide.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.capston.eduguide.R;
import com.capston.eduguide.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private String userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_settings);

        // userEmail 값 받기
        userEmail = getIntent().getStringExtra("userEmail");

        // 프로필 편집 클릭
        TextView profileEdit = findViewById(R.id.profile_edit);
        profileEdit.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // 개인정보 설정 클릭
        TextView personalinfoEdit = findViewById(R.id.personalinfo_edit);
        personalinfoEdit.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, EditpersonalinfoActivity.class);
            startActivity(intent);
        });

        // 스크랩 클릭
        TextView Scrap = findViewById(R.id.scrap_user);
        Scrap.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, UserScrapActivity.class);
            intent.putExtra("userEmail", userEmail); // userEmail 전달
            startActivity(intent);
        });

        // 문의 클릭
        TextView inquiryWrite = findViewById(R.id.inquiry_edit);
        inquiryWrite.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ListinquiryActivity.class);
            startActivity(intent);
        });

        //공지 클릭
        TextView noticeWrite = findViewById(R.id.notice_edit);
        noticeWrite.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, WriteNoticeActivity.class);
            startActivity(intent);
        });

        // 로그아웃 버튼 찾기
        TextView logoutButton = findViewById(R.id.logout_edit);

        // 로그아웃 버튼 클릭 리스너 등록하기
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Firebase에서 로그아웃
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 로그인 화면으로 이동하면서 이전의 액티비티 스택 제거하지 않음
            startActivity(intent);
        });
    }
}
