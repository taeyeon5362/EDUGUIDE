package com.capston.eduguide.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.capston.eduguide.MainActivity;
import com.capston.eduguide.R;
import com.capston.eduguide.login.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_profile);

        // Firebase 실시간 데이터베이스 객체 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        // 현재 사용자 가져오기
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> saveProfileChanges());

    }

    private void saveProfileChanges() {
        EditText nicknameEditText = findViewById(R.id.profile_name_edittext);
        EditText introEditText = findViewById(R.id.self_intro_edittext);

        String newNickname = nicknameEditText.getText().toString().trim();
        String newIntro = introEditText.getText().toString().trim();

        if (currentUser != null) {
            // 사용자 정보 업데이트
            DatabaseReference userRef = databaseReference.child(currentUser.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);

                        if (user != null) {
                            user.setNickname(newNickname);
                            user.setIntro(newIntro);

                            userRef.setValue(user)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditProfileActivity.this, "프로필이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                            // MainActivity로 이동
                                            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(EditProfileActivity.this, "프로필 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 처리 중단 또는 에러 처리
                    Toast.makeText(EditProfileActivity.this, "데이터베이스 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(EditProfileActivity.this, "로그인 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}
