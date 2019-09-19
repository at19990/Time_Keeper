package io.github.at19990.Time_Keeper;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.widget.EditText;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static long START_TIME_IN_MILLIS = 10 * 60 * 1000;   //タイマー設定 単位 ミリ秒 初期設定: 10分

    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private Button mButtonSet;

    private View mView;

    private  SoundPool soundPool;


    private int bell_1;
    private int bell_2;
    private int bell_3;

    private String errorMessage;

    private EditText set_time;
    private EditText firstbell_time;
    private EditText secondbell_time;

    private CountDownTimer mCountDownTimer;
    private CountDownTimer mCountDownTimer_first;
    private CountDownTimer mCountDownTimer_second;

    private TextView mTimerText;

    private boolean mTimerRunning;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    private double time_length; // 初期設定: 10分
    private double first_bell;  // 初期設定: 5分前
    private double second_bell; // 初期設定: 3分前

    private double stored_time_length = 10;
    private double stored_first_bell = 5;
    private double stored_second_bell = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        set_time = findViewById(R.id.set_time_value);
        firstbell_time = findViewById(R.id.set_firstbell_value);
        secondbell_time = findViewById(R.id.set_secondbell_value);


        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.buttonreset);
        mButtonSet = findViewById(R.id.button_set);
        mView = findViewById(R.id.view);
        mTimerText = findViewById(R.id.text_view_countdown);

        errorMessage = "値が無効です";

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                // ストリーム数に応じて
                .setMaxStreams(3)
                .build();


        bell_1 = soundPool.load(this, R.raw.firstbell, 1);
        bell_2 = soundPool.load(this, R.raw.secondbell, 1);
        bell_3 = soundPool.load(this, R.raw.thirdbell, 1);


        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* セットボタンを押すとキーボードを格納 */
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                mTimerText.setTextColor(Color.BLACK);

                /* テキストフォームに入力されているか判定 */
                if((TextUtils.isEmpty(set_time.getText().toString())) || (TextUtils.isEmpty(firstbell_time.getText().toString())) || (TextUtils.isEmpty(secondbell_time.getText().toString()))){
                    toastMake(errorMessage, 0, 500);  // 無効な入力の警告
                }else{
                    time_length = Double.parseDouble(set_time.getText().toString());
                    first_bell = Double.parseDouble(firstbell_time.getText().toString());
                    second_bell = Double.parseDouble(secondbell_time.getText().toString());

                    /* 入力フォームの強調・カーソル表示を解除 */
                    set_time.clearFocus();
                    firstbell_time.clearFocus();
                    secondbell_time.clearFocus();

                    set_time.setCursorVisible(false);
                    firstbell_time.setCursorVisible(false);
                    secondbell_time.setCursorVisible(false);

                    /* 不正な入力値を検出 */
                    if((second_bell < first_bell) && (first_bell < time_length) && (second_bell > 0)){
                        mTimeLeftInMillis = (long)time_length * 60 * 1000;


                        stored_time_length = time_length;
                        stored_first_bell = first_bell;
                        stored_second_bell = second_bell;

                        updateCountDownText();
                    }else{
                        toastMake(errorMessage, 0, 500);
                    }
                }


            }
        });

        /* スタート・ストップボタンの動作定義 */
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning){
                    pauseTimer();
                } else {

                    startTimer();
                }
            }
        });

        /* リセットボタンの動作定義 */
        mButtonReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetTimer();

            }
        });

        /* フォーム・ボタン以外の部分をタッチしたときの動作定義 */
        mView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mView.requestFocus();

                /* キーボード格納 */
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });

        updateCountDownText();

    }


    private void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,100) {  // 100ミリ秒単位でカウント
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;

                updateCountDownText();

            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                soundPool.play(bell_3, 1.0f, 1.0f, 1, 0, 1);
                /* 1鈴と2鈴のカウントを終了 */
                mCountDownTimer_first.cancel();
                mCountDownTimer_second.cancel();

                mButtonStartPause.setText("スタート");
                mButtonReset.setVisibility(View.VISIBLE);    // 表示

                mTimerText.setTextColor(Color.BLACK);
            }
        }.start();

        /* 1鈴のカウント */
        if((mTimeLeftInMillis - (long)first_bell * 60 * 1000) >= 0){
            mCountDownTimer_first = new CountDownTimer(mTimeLeftInMillis - (long)first_bell * 60 * 1000,100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // mTimeLeftInMillis = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    soundPool.play(bell_1, 1.0f, 1.0f, 1, 0, 1);

                }
            }.start();
        }

        /* 2鈴のカウント */
        if((mTimeLeftInMillis - (long)second_bell * 60* 1000) >= 0){
            mCountDownTimer_second = new CountDownTimer(mTimeLeftInMillis - (long)second_bell * 60 * 1000,100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // mTimeLeftInMillis = millisUntilFinished;

                }

                @Override
                public void onFinish() {
                    soundPool.play(bell_2, 1.0f, 1.0f, 1, 0, 1);
                    mTimerText.setTextColor(Color.RED);
                }
            }.start();
        }


        mTimerRunning = true;   // タイマー作動中
        mButtonStartPause.setText("一時停止");
        mButtonReset.setVisibility(View.INVISIBLE);
    }


    private void toastMake(String message, int x, int y){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(android.view.Gravity.CENTER, x, y);
        toast.show();
    }

    private void pauseTimer(){

        mTimerRunning = false;

        mCountDownTimer.cancel();

        mCountDownTimer_first.cancel();
        // mCountDownTimer_first = null;

        mCountDownTimer_second.cancel();
        // mCountDownTimer_second = null;

        mButtonStartPause.setText("スタート");
        mButtonReset.setVisibility(View.VISIBLE);
    }


    private void resetTimer(){
        time_length = stored_time_length;
        mTimeLeftInMillis = (long)time_length * 1000 * 60;

        first_bell = stored_first_bell;
        second_bell = stored_second_bell;

        updateCountDownText();
        mButtonStartPause.setVisibility(View.VISIBLE);
        mButtonReset.setVisibility(View.INVISIBLE);

        mTimerText.setTextColor(Color.BLACK);
    }

    // タイマー表示
    private void updateCountDownText(){
        int minutes = (int)(mTimeLeftInMillis/1000)/60;
        int seconds = (int)(mTimeLeftInMillis/1000)%60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d",minutes,seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }



}
