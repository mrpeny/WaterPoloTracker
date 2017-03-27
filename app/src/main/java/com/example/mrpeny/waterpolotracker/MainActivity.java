package com.example.mrpeny.waterpolotracker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView gameTimerTextView;

    Button resetButton;
    Button startButton;

    String start;
    String stop;
    String resume;
    String reset;

    CountDownTimer countDownTimer;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameTimerTextView = (TextView) findViewById(R.id.game_time_text_view);
        resetButton = (Button) findViewById(R.id.reset_button);
        startButton = (Button) findViewById(R.id.start_game_button);

        start = getString(R.string.start);
        stop = getString(R.string.stop);
        resume = getString(R.string.resume);
        reset = getString(R.string.reset);

        countDownTimer = new CountDownTimer(60000 * 8, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long mm = millisUntilFinished / 1000 / 60;
                long ss = millisUntilFinished / 1000 % 60;

                gameTimerTextView.setText(String.format("%1$02d:%2$02d", mm, ss));
            }

            @Override
            public void onFinish() {
                gameTimerTextView.setTextColor(Color.RED);
            }
        };


    }

    public void startPauseResumeGame(View view) {
        if (!countDownTimer.hasStarted() || countDownTimer.wasCancelled()) {
            countDownTimer.start();
            startButton.setText(this.stop);
        } else {
            if (countDownTimer.isPaused()) {
                countDownTimer.resume();
                startButton.setText(this.stop);
            } else {
                countDownTimer.pause();
                startButton.setText(this.resume);
            }
        }

    }

    public void reset(View view) {
        countDownTimer.cancel();
        startButton.setText(this.start);
        gameTimerTextView.setText(getString(R.string.game_timer));
    }

    public void homeGoal(View view) {
        countDownTimer.start();
    }
}
