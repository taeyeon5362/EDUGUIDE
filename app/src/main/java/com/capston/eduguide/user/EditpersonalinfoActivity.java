package com.capston.eduguide.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.capston.eduguide.R;
import com.capston.eduguide.login.LoginActivity;
import com.capston.eduguide.login.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditpersonalinfoActivity extends AppCompatActivity {

    EditText userName; //이름
    EditText userBirth; //생년월일
    EditText userPhone; //전화번호
    EditText userEmail; //이메일

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_personalinfo);

        userName = findViewById(R.id.edit_text_name);
        userBirth = findViewById(R.id.edit_text_birth);
        userPhone = findViewById(R.id.edit_text_phone);
        userEmail = findViewById(R.id.edit_text_email);

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> savePersonalInfo());

        Button changePasswordButton = findViewById(R.id.button_change_password);
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditPasswordActivity.class);
            startActivity(intent);
        });

        // "button_unregister" 버튼 클릭 이벤트 처리
        Button unregisterButton = findViewById(R.id.button_unregister);
        unregisterButton.setOnClickListener(v -> unregisterUser());

        // Firebase 실시간 데이터베이스 객체 초기화
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    userName.setText(user.getName());
                    userBirth.setText(user.getAge());
                    userPhone.setText(user.getPhone());
                    userEmail.setText(user.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditpersonalinfoActivity.this, "데이터를 가져오는 중 에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //개인정보 저장
    private void savePersonalInfo() {
        String name = userName.getText().toString().trim();
        String birth = userBirth.getText().toString().trim();
        String phone = userPhone.getText().toString().trim();
        String email = userEmail.getText().toString().trim();

        // Firebase 실시간 데이터베이스 객체 초기화
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();

        // 사용자 정보 업데이트
        databaseReference.child(userId).child("name").setValue(name);
        databaseReference.child(userId).child("age").setValue(birth);
        databaseReference.child(userId).child("phone").setValue(phone);
        databaseReference.child(userId).child("email").setValue(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditpersonalinfoActivity.this, "개인정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditpersonalinfoActivity.this, "개인정보 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unregisterUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // Firebase 인증을 사용하여 사용자 삭제
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // 사용자 삭제 성공
                            Toast.makeText(EditpersonalinfoActivity.this, "회원 탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                            // 로그인 화면으로 이동
                            Intent intent = new Intent(EditpersonalinfoActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // 사용자 삭제 실패
                            Toast.makeText(EditpersonalinfoActivity.this, "회원 탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}