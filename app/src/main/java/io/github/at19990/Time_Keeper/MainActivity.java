package io.github.at19990.Time_Keeper;

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
    private static long START_TIME_IN_MILLIS = 10* 60 * 1000;   //タイマー設定 単位 ミリ秒   final 変更できない設定値

    private TextView mTextViewCountDown;  //アクセス修飾子
    private Button mButtonStartPause;
    private Button mButtonReset;
    private Button mButtonSet;

    private View mView;

    private  SoundPool soundPool;
    /*
    private int bell_0;
    */
    private int bell_1;
    private int bell_2;
    private int bell_3;


    private String errorMessage;

    private EditText set_time;
    private EditText firstbell_time;
    private EditText secondbell_time;



    private CountDownTimer mCountDownTimer;   // OS内の CountDownTimerクラス
    private CountDownTimer mCountDownTimer_first;   // OS内の CountDownTimerクラス
    private CountDownTimer mCountDownTimer_second;   // OS内の CountDownTimerクラス

    private boolean mTimerRunning;   // OS内の CallTimeクラス

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    /*
    private long mTimeFirstBell;
    private long mTimeSecondBell;
    */

    private double time_length;
    private double first_bell = 5;
    private double second_bell = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        set_time = findViewById(R.id.setted_time_value);
        firstbell_time = findViewById(R.id.setted_firstbell_value);
        secondbell_time = findViewById(R.id.setted_secondbell_value);


        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.buttonreset);
        mButtonSet = findViewById(R.id.button_set);
        mView = findViewById(R.id.view);

        errorMessage = "値が無効です";

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                // USAGE_MEDIA
                // USAGE_GAME
                .setUsage(AudioAttributes.USAGE_GAME)
                // CONTENT_TYPE_MUSIC
                // CONTENT_TYPE_SPEECH, etc.
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                // ストリーム数に応じて
                .setMaxStreams(3)
                .build();
        /*
        bell_0 = soundPool.load(this, R.raw.bell_0, 1);
        */
        bell_1 = soundPool.load(this, R.raw.firstbell, 1);
        bell_2 = soundPool.load(this, R.raw.secondbell, 1);
        bell_3 = soundPool.load(this, R.raw.thirdbell, 1);


        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if((TextUtils.isEmpty(set_time.getText().toString())) || (TextUtils.isEmpty(firstbell_time.getText().toString())) || (TextUtils.isEmpty(secondbell_time.getText().toString()))){
                    toastMake(errorMessage, 0, 500);
                }else{
                    time_length = Double.parseDouble(set_time.getText().toString());
                    first_bell = Double.parseDouble(firstbell_time.getText().toString());
                    second_bell = Double.parseDouble(secondbell_time.getText().toString());

                    set_time.clearFocus();
                    firstbell_time.clearFocus();
                    secondbell_time.clearFocus();

                    set_time.setCursorVisible(false);
                    firstbell_time.setCursorVisible(false);
                    secondbell_time.setCursorVisible(false);


                    if((second_bell < first_bell) && (first_bell < time_length) && (second_bell > 0)){
                        mTimeLeftInMillis = (long)time_length * 60 * 1000;
                        /*
                        mTimeFirstBell = mTimeLeftInMillis - (long)first_bell * 60 * 1000;
                        mTimeSecondBell = mTimeLeftInMillis - (long)second_bell * 60 * 1000;
                        */

                        updateCountDownText();
                    }else{
                        toastMake(errorMessage, 0, 500);
                    }
                }


            }
        });

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

        mButtonReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetTimer();

            }
        });


        mView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mView.requestFocus();

                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });



        updateCountDownText();



    }

    private void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,100) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;

                updateCountDownText();

            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                soundPool.play(bell_3, 1.0f, 1.0f, 1, 0, 1);
                mCountDownTimer_first.cancel();
                mCountDownTimer_second.cancel();
                mButtonStartPause.setText("スタート");
                mButtonReset.setVisibility(View.VISIBLE);    //非表示
            }
        }.start();


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

        if((mTimeLeftInMillis - (long)second_bell * 60* 1000) >= 0){
            mCountDownTimer_second = new CountDownTimer(mTimeLeftInMillis - (long)second_bell * 60 * 1000,100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // mTimeLeftInMillis = millisUntilFinished;

                }

                @Override
                public void onFinish() {
                    soundPool.play(bell_2, 1.0f, 1.0f, 1, 0, 1);
                }
            }.start();
        }








        mTimerRunning = true;   // タイマー作動中
        mButtonStartPause.setText("一時停止");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    private void toastMake(String message, int x, int y){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        // 位置調整
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
        mTimeLeftInMillis = START_TIME_IN_MILLIS;

        first_bell = 5;
        second_bell = 3;

        updateCountDownText();
        mButtonStartPause.setVisibility(View.VISIBLE);
        mButtonReset.setVisibility(View.INVISIBLE);


    }

    // タイマー表示
    private void updateCountDownText(){
        int minutes = (int)(mTimeLeftInMillis/1000)/60;
        int seconds = (int)(mTimeLeftInMillis/1000)%60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d",minutes,seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }



}
