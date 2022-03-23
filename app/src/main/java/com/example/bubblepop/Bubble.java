package com.example.bubblepop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Bubble {
    public int x, y, rad;           // 좌표, 반지름
    public Bitmap imgBall;          // 비트맵 이미지
    public boolean dead = false;    // 터트림 여부

    private int _rad;               // 원래의 반지름
    private int sx, sy;             // 이동 방향 및 속도
    private int width, height;      // View의 크기
    private Bitmap Bubbles[] = new Bitmap[6];   // 풍선 애니메이션 용 이미지
    private int imgNum = 0;         // 이미지 번호
    private int loop = 0;           // 루프 카운터
    private int counter = 0;        // 벽과 충돌 회수

    // 생성자 - Context를 받아온다.
    public Bubble(Context context, int _x, int _y, int _width, int _height){
        width = _width;
        height = _height;
        x = _x;
        y = _y;

        imgBall = BitmapFactory.decodeResource(context.getResources(), R.drawable.p5);

        Random rnd = new Random();
        _rad = rnd.nextInt(11) + 20;        // 반지름 : 20 ~ 30
        rad = _rad;                                // 현재 애니메이션 중인 반지름

        // 반지름이 2 간격으로 커졌다 작아지는 공 6개 만들기
        for (int i = 0; i <= 3; i++)
            Bubbles[i] = Bitmap.createScaledBitmap(imgBall, _rad*2 + i*2, _rad*2 + i*2, false);
        Bubbles[4] = Bubbles[2];        // 작아지는 것은 앞의 이미지를 그대로 사용
        Bubbles[5] = Bubbles[1];
        imgBall = Bubbles[0];           // 맨 처음에 사용할 이미지

        sx = rnd.nextInt(2) == 0 ? -1 : 1;
        sy = rnd.nextInt(2) == 0 ? -2 : 2;  // 이동 방향 및 속도
        MoveBubble();
    }

    // Move
    public void MoveBubble(){
        loop++;                 // 애니메이션용 루프 카운터
        if (loop % 3 == 0) {
            imgNum++;       // 풍선 번호
            if (imgNum > 5) imgNum =0;
            imgBall = Bubbles[imgNum];

            // 풍선의 반지름 설정
            rad = _rad + (imgNum <= 3 ? imgNum : 6 - imgNum) *2;
        }

        x += sx;
        y += sy;
        if (x <= rad || x >= width - rad) {      // 좌우로 충돌
            sx = -sx;
            x += sx;
            counter++;
        }
        if (y <= rad || y >= height - rad) {      // 상하로 충돌
            sy = -sy;
            y += sy;
            counter++;
        }
//        if (counter >= 3) dead = true;          // 벽과 충돌 횟수
    }
}   //Bubble
