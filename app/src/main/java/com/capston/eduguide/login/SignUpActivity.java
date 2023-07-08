package com.capston.eduguide.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capston.eduguide.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText signUpIdEditText;
    private EditText signUpPasswordEditText;
    private EditText signUpEmailEditText;
    private EditText signUpPhoneEditText;
    private EditText signUpNameEditText;
    private EditText signUpAgeEditText;
    private Button signUpButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_sign_up_form);

        signUpIdEditText = findViewById(R.id.signUp_id);
        signUpPasswordEditText = findViewById(R.id.signUp_password);
        signUpEmailEditText = findViewById(R.id.signUp_email);
        signUpPhoneEditText = findViewById(R.id.signUp_phone);
        signUpNameEditText = findViewById(R.id.signUp_name);
        signUpAgeEditText = findViewById(R.id.signUp_age);
        signUpButton = findViewById(R.id.btn_signUp);

        // Firebase 인증 객체 초기화
        firebaseAuth = FirebaseAuth.getInstance();

        // Firebase 실시간 데이터베이스 객체 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = signUpIdEditText.getText().toString().trim();
                String password = signUpPasswordEditText.getText().toString().trim();
                String email = signUpEmailEditText.getText().toString().trim();
                String phone = signUpPhoneEditText.getText().toString().trim();
                String name = signUpNameEditText.getText().toString().trim();
                String age = signUpAgeEditText.getText().toString().trim();

                // Firebase Authentication을 사용하여 회원 가입 정보 저장
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 회원 가입이 성공하면 Firebase Realtime Database에 회원 정보 저장
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String userId = user.getUid();

                                    User newUser = new User(id, password, email, phone, name, age);
                                    newUser.setGrade("0"); // 항상 등급을 0으로 설정

                                    newUser.saveToFirebase();

                                    databaseReference.child(userId).setValue(newUser)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SignUpActivity.this, "회원 가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(SignUpActivity.this, "회원 가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(SignUpActivity.this, "회원 가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
