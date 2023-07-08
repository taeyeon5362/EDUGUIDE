package com.capston.eduguide.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capston.eduguide.R;
import com.capston.eduguide.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditPasswordActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_password);

        // Firebase 실시간 데이터베이스 객체 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        // 현재 사용자 가져오기
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        EditText currentPasswordEditText = findViewById(R.id.edittext_current_password);
        EditText newPasswordEditText = findViewById(R.id.edittext_new_password);
        EditText confirmPasswordEditText = findViewById(R.id.edittext_confirm_password);

        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // 현재 비밀번호가 맞는지 확인
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = databaseReference.child(userId);

            userRef.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String savedPassword = snapshot.getValue(String.class);
                        assert savedPassword != null;
                        if (savedPassword.equals(currentPassword)) {
                            // 현재 비밀번호가 일치하는 경우
                            // 새로운 비밀번호가 확인 비밀번호와 일치하는지 확인
                            if (!newPassword.equals(confirmPassword)) {
                                Toast.makeText(EditPasswordActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // 새로운 비밀번호를 Firebase Authentication을 통해 변경
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user != null) {
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(EditPasswordActivity.this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();

                                                // Firebase 실시간 데이터베이스의 password 필드 업데이트
                                                String userId = currentUser.getUid();
                                                DatabaseReference userRef = databaseReference.child(userId);
                                                userRef.child("password").setValue(newPassword);

                                                // 로그아웃 처리
                                                FirebaseAuth.getInstance().signOut(); // Firebase에서 로그아웃

                                                // 로그인 화면으로 이동
                                                Intent intent = new Intent(EditPasswordActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(EditPasswordActivity.this, "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // 현재 비밀번호가 일치하지 않는 경우
                            Toast.makeText(EditPasswordActivity.this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 처리 중단 또는 에러 처리
                    Toast.makeText(EditPasswordActivity.this, "데이터베이스 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(EditPasswordActivity.this, "로그인 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}