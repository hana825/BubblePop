package com.example.bubblepop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    MyGameView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (MyGameView) findViewById(R.id.mGameView);

    }

    // Option Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "게임종료");
        menu.add(0, 2, 0, "일시정지");
        menu.add(0, 3, 0, "계속진행");
        menu.add(0, 4, 0, "게임초기화");
        return true;
    }

    // onOptionItemSelected
    public boolean onOptionItemSelected(MenuItem item){
        switch (item.getItemId()){
            case 1:     // 종료
                Toast.makeText(getApplicationContext(), "게임 종료", Toast.LENGTH_SHORT).show();
                view.StopGame();
                finish();
                break;
            case 2:     // 일시 정지
                Toast.makeText(getApplicationContext(), "일시 정지", Toast.LENGTH_SHORT).show();
                view.PauseGame();
                break;
            case 3:     // 계속 진행
                Toast.makeText(getApplicationContext(), "계속 진행", Toast.LENGTH_SHORT).show();
                view.ResumeGame();
                break;
            case 4:     // 게임 재시작
                Toast.makeText(getApplicationContext(), "게임 재시작", Toast.LENGTH_SHORT).show();
                view.RestartGame();
        }
        return true;
    }

    @Override
    protected void onPause(){
        super.onPause();    // 조상 method 먼저 호출
        view.PauseGame();   // Thread 일시 정지
    }
    @Override
    protected void onResume(){
        super.onResume();
        view.ResumeGame();  // Thread 재기동
    }
    @Override
    protected void onStop(){
        super.onStop();     // Thread 일시 정지
    }

}