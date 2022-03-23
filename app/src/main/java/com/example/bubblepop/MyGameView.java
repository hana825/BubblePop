package com.example.bubblepop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MyGameView extends SurfaceView implements SurfaceHolder.Callback {
    GameThread mThread;         // SurfaceView 레벨의 전역변수 . 게임 Thread
    SurfaceHolder mHolder;      // Surfaceholder 보존
    Context mContext;           // Context 보존


    ArrayList<Bubble> mBall = new ArrayList<Bubble>();      // 풍선

    // 생성자
    public MyGameView(Context context, AttributeSet attrs){
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mHolder = holder;       // holder와 Context 보존
        mContext = context;
        mThread = new GameThread(holder, context);  // Thread 생성

        setFocusable(true);     // Focus 받기
    }
    public MyGameView(Context context){
        super(context);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mHolder = holder;
        mThread = new GameThread(holder, context);
    }

    /***** 기능구현 *****/
    // SurfaceView가 생성될 때 실행되는 부분
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        try {
            mThread.start();    // 일단 Thread 실행하고
        } catch (Exception e){
            RestartGame();      // 에러가 발생하면 Thread 새로 만듬
        }
    }

    // SurfaceView가 바뀔 때 실행되는 부분
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder arg0, int format, int width, int height) {

    }

    // SurfaceView가 해제될 때 실행되는 부분
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean done = true;
        while (done) {
            try {
                mThread.join();     // thread가 현재 step을 끝낼 때 까지 대기
                done = false;
            } catch (InterruptedException e) {  // 인터럽트 신호가 오면?
                // 그 신호는 무시 - 아무것도 하지 않음
            }
        } // while
        StopGame();
    }

    /******Thread*******/
    // GameThread Class
    class GameThread extends Thread {
        SurfaceHolder mHolder;      // SurfaceHolder를 저장할 변수
        Context mContext;           // Context 보존

        int width, height;
        Bitmap imgBack;
        ArrayList<Bubble> mBall = new ArrayList<Bubble>();

        // 변수 추가
        boolean canRun = true;
        boolean isWait = false;

        // 생성자
        public GameThread(SurfaceHolder holder, Context context){
            mHolder = holder;       // SurfaceHolder 보존
            mContext = context;

            Display display =((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            width = display.getWidth();         // View의 가로 폭
            height = display.getHeight() - 50;  // View의 세로 높이

            imgBack = BitmapFactory.decodeResource(getResources(), R.drawable.sky_pixel);
            imgBack = Bitmap.createScaledBitmap(imgBack, width, height, false);
        }

        /** Thread 추가 부분 **/
        // Thread 완전 정지
        public void StopThread(){
            canRun = false;
            synchronized (this){
                this.notify();
            }
        }
        // Thread 일시 정지 / 재기동
        public void PauseNResume(boolean wait){
            isWait = wait;
            synchronized (this){
                this.notify();
            }
        }

        // 풍선 만들기 - Touch Event에서 호출
        public void MakeBubble(int x, int y){
            boolean flag = false;

            // 풍선을 터치했는지 판정
            for (Bubble tmp : mBall){
                if (Math.pow(tmp.x - x, 2) + Math.pow(tmp.y -y, 2)
                <= Math.pow(tmp.rad, 2)) {
                    tmp.dead = true;        // 풍선 Touch일 경우
                    flag = true;
                }
            }
            if (flag == false)              // 풍선 Touch가 아니면 풍선 생성
                mBall.add(new Bubble(mContext, x, y, width, height));
        }

        // 풍선 이동 - run에서 호출
        public void MoveBubble(){
            for (int i = mBall.size() -1; i>=0; i--){
                mBall.get(i).MoveBubble();
                if (mBall.get(i).dead == true)
                    mBall.remove(i);    // 불필요한 것 삭제
            }
        }

        // Canvas에 그리기
        public void run(){
            Canvas canvas = null;       // Canvas를 만든다
            while (canRun) {
                canvas = mHolder.lockCanvas(null);  // canvas를 잠그고 버퍼 할당
                try {
                    synchronized (mHolder) {     // 동기화 유지
                        MoveBubble();           // 앞의 메소드를 실행시킨다.
                        canvas.drawBitmap(imgBack, 0, 0, null);
                        for (Bubble tmp : mBall) {
                            canvas.drawBitmap(tmp.imgBall, tmp.x - tmp.rad, tmp.y - tmp.rad, null);
                        }
                    } //sync
                } finally {                 // 버퍼 작업이 끝나면
                    if (canvas != null)     // 버퍼의 내용을 View에 전송
                        mHolder.unlockCanvasAndPost(canvas);
                }

                // Thread 일시정지
                synchronized (this) {
                    if (isWait) {
                        try {
                            wait();             // Thread 대기
                        } catch (Exception e) {
                            // nothing
                        }
                    }
                } //sync
            } //while
        } // run


    }  // GameThread 끝

    // Thread 완전 정지
    public void StopGame(){
        mThread.StopThread();
    }
    // Thread 일시 정지
    public void PauseGame(){
        mThread.PauseNResume(true);
    }
    // Thread 재기동
    public void ResumeGame(){
        mThread.PauseNResume(false);
    }
    // 게임 초기화
    public void RestartGame(){
        mThread.StopThread();       // Thread 중지

        // 현재 Thread 비우고 다시 생성
        mThread = null;
        mThread = new GameThread(mHolder, mContext);
        mThread.start();
    }

    // onTouch Event
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            synchronized (mHolder){
                int x = (int) event.getX();
                int y = (int) event.getY();
                mThread.MakeBubble(x,y);        // mThread의 메소드 실행
            }
        }
        return true;
    } // Touch

} // SurfaceView
