package com.hackathon.aran2.LearnEmotion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.hackathon.aran2.R;

import java.io.ByteArrayOutputStream;

public class DrawImageActivity extends AppCompatActivity {
    Button clearBtn, saveBtn, eraserBtn,
            blackBtn, redBtn, orangeBtn, blueBtn, yellowBtn,
            skyblueBtn, purpleBtn, pinkBtn, whiteBtn, greenBtn;

    Button backBtn, pencilBtn;
    byte[] byteArray;
    TextView sizeInput;
    LinearLayout drawLinear, colorPallete;
    ImageView showImg;
    SeekBar seekBar;
    PorterDuffXfermode clear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    DrawView drawView;
    Switch image_switch;

    int Image;
    String text; // 감정이 뭔지
    // 선의 굵기 최소
    public int number = 0;
    private Typeface mTypeface;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_image); // smalldraw layout을 사용한다.
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);

        saveBtn = (Button) findViewById(R.id.save);
        eraserBtn = findViewById(R.id.eraser);
        backBtn = (Button) findViewById(R.id.back);


        pencilBtn = findViewById(R.id.pencil);
        colorPallete = findViewById(R.id.colorPallete);

        // 팔레트
        whiteBtn = findViewById(R.id.colorWhite);
        blackBtn = findViewById(R.id.colorBlack);
        redBtn = findViewById(R.id.colorRed);
        orangeBtn = findViewById(R.id.colorOrange);
        yellowBtn = findViewById(R.id.colorYellow2);
        greenBtn = findViewById(R.id.colorGreen);
        skyblueBtn = findViewById(R.id.colorSkyBlue);
        blueBtn = findViewById(R.id.colorBlue);
        purpleBtn = findViewById(R.id.colorPurple);
        pinkBtn = findViewById(R.id.colorPink);

        image_switch = findViewById(R.id.image_switch);

        // seekBar
        seekBar = findViewById(R.id.seekBar);
        sizeInput = findViewById(R.id.size);

        Intent intent = getIntent();
        int select = intent.getIntExtra("image", 0);
        String emotion = intent.getExtras().getString("emotion");
        text = emotion;

        showImg = (ImageView) findViewById(R.id.backgroundImage); // background 이미지 , 사진이 보여야함

//         byte[] arr = getIntent().getByteArrayExtra("image");
        // Bitmap ; 이미지를 표현하기 위해 사용
//        Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);
//        showImg.setImageBitmap(img);

        Drawable image_gray = getResources().getDrawable(select).mutate();
        showImg.setImageDrawable(setGrayScale(image_gray));

//        Bitmap img = BitmapFactory.decodeResource(getResources(), select );
//        showImg.setImageBitmap(img);

        drawLinear = (LinearLayout) findViewById(R.id.drawLayout);
        clearBtn = (Button) findViewById(R.id.clear);

        clearBtn.setOnClickListener(new View.OnClickListener() { //지우기 버튼 눌렸을때
            @Override
            public void onClick(View v) {
                drawView.clear();
            }
        });

        // 저장하기 버튼
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뷰가 업데이트 될 때 마다 그때의 뷰 이미지를 저장
                drawLinear.setBackgroundResource(R.drawable.border);
                drawLinear.setDrawingCacheEnabled(true);    // 캐쉬허용
                Bitmap screenshot = Bitmap.createBitmap(drawLinear.getDrawingCache());
                drawLinear.setDrawingCacheEnabled(false);   // 캐쉬닫기
                // 완료하면 녹음창으로 넘어가기
                // intent 수정하기 !
                Intent outIntent = new Intent(getApplication(), RecordActivity.class);
                // 새로운 바이트 배열 출력 스트림
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                screenshot.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();

                Bitmap bitmap;
                // byte 배열 형태로 되어있는 이미지를 bitmap으로 만들때 사용
                bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                showImg.setImageBitmap(bitmap);

                // 다음 intent로 어떤값을 넘기고 싶을때 putextra를 사용한다.
                // 그림 그린값 넘기기
                outIntent.putExtra("drawing", byteArray);

                outIntent.putExtra("emotion", text);
                System.out.println(text + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");

                startActivityForResult(outIntent, 5000);
//                setResult(RESULT_OK, outIntent);
//                finish();
            }

        });
        image_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(image_switch.isChecked())
                {showImg.setVisibility(View.INVISIBLE);
                    drawLinear.setBackgroundResource(R.color.colorWhite);}
                else {
                    showImg.setVisibility(View.VISIBLE);
                    drawLinear.setBackground(null);
                }


            }
        });
        // 이전으로 버튼
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // 지우개 버튼

        eraserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawView.setColorAndEraser(Color.BLACK, clear);

            }
        });
        // 펜슬버튼

        pencilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (colorPallete.getVisibility() == view.GONE) {
                    colorPallete.setVisibility(view.VISIBLE);
                } else {
                    colorPallete.setVisibility(view.GONE);
                }
            }
        });

        // 팔레트 버튼들
        redBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setColorAndEraser(getResources().getColor(R.color.colorRed), null);
            }
        });

        blackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setColorAndEraser(getResources().getColor(R.color.colorBlack), null);
            }
        });

        whiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setColorAndEraser(getResources().getColor(R.color.colorWhite), null);

            }
        });

        orangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setColorAndEraser(getResources().getColor(R.color.colorOrange), null);
            }
        });
        yellowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setColorAndEraser(getResources().getColor(R.color.colorYellow2), null);
            }
        });
        greenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setColorAndEraser(getResources().getColor(R.color.colorGreen), null);
            }
        });
        skyblueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setColorAndEraser(getResources().getColor(R.color.colorSkyBlue), null);
            }
        });
        blueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawView.setColorAndEraser(getResources().getColor(R.color.colorBlue), null);
            }
        });
        purpleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setColorAndEraser(getResources().getColor(R.color.colorPurple), null);
            }
        });
        pinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setColorAndEraser(getResources().getColor(R.color.colorPink), null);
            }
        });

        // 크기 조절 바
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                // 메소드 이름대로 사용자가 SeekBar를 움직일때 실행됩니다
                // 주로사용
                if (progress < 1) {
                    progress = 1;
                    seekBar.setProgress(progress);
                }
                number = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //메소드 이름대로 사용자가 SeekBar를 터치했을때 실행됩니다
                number = seekBar.getProgress();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 메소드 이름대로 사용자가 SeekBar를 손에서 땠을때 실행됩니다
                drawView.setSize((float) seekBar.getProgress());
                number = seekBar.getProgress();
                update();
            }
        });
    }


    // seekbar size 나타내주는 메소드
    public void update() {
        sizeInput.setText(new StringBuilder().append(number));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && drawView == null) {
            if (drawLinear != null) {
                drawView = new DrawView(this, drawLinear.getMeasuredWidth(), drawLinear.getMeasuredHeight());
                drawLinear.addView(drawView);
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }

    // 그림그리는 클래스
    class DrawView extends View {
        PorterDuffXfermode clear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        Path drawLine;
        Canvas canvas;
        Paint paint;
        Bitmap drawingSpace;

        int x, y;
        int pointX = -1;
        int pointY = -1;
        //        int color = Color.BLACK;
        int width, height;
        PorterDuffXfermode use = null;

        public DrawView(Context context, int width, int height) {
            super(context);
            this.width = width;
            this.height = height;

            drawingSpace = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            canvas = new Canvas(drawingSpace);
            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(15);
            paint.setStyle(Paint.Style.STROKE);
            drawLine = new Path();
        }

        public void setSize(float size) {
            paint.setStrokeWidth(size);
        }

        public void setColorAndEraser(int color, PorterDuffXfermode type) {
            paint.setColor(color);
            paint.setXfermode(type);
        }

        public void clear() {
            drawingSpace = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(drawingSpace);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (drawingSpace != null) {
                canvas.drawBitmap(drawingSpace, 0, 0, null);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            x = (int) event.getX(0);
            y = (int) event.getY(0);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawLine.reset();
                    drawLine.moveTo(x, y);
                    pointX = x;
                    pointY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (pointX != -1) {
                        pointX = x;
                        pointY = y;
                        drawLine.lineTo(x, y);
                        canvas.drawPath(drawLine, paint);
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    if (pointX != -1) {
                        invalidate();
                    }
                    pointX = -1;
                    pointY = -1;
                    break;

            }

            System.out.println("그림");
            invalidate();
            return true;
        }


    }

    public Drawable setGrayScale(Drawable d) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);                        //0이면 grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        d.setColorFilter(cf);

        return d;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 5000:
                    Intent outintent = new Intent();
                    setResult(RESULT_OK, outintent);
                    finish();
                    break;
            }
        }
    }
    void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(mTypeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child);
        }
    }
}

