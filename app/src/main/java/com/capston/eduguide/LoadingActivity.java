package com.capston.eduguide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        //로딩화면 메소드 호출
        Loadingstart();
    }

    //로딩화면 메소드
    private void Loadingstart(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);

                //일정 시간 후 로딩화면 종료
                finish();
            }
        },500);
    }

}
