package com.example.mrpeny.waterpolotracker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final String finishedTimerState = "00:00";
    TextView gameTimerTextView, possessionTimerTextView, homeScoreCountTextView, guestScoreCountTextView;
    ImageView homeBallImageView, guestBallImageView;
    Button resetButton, startButton, homeGoalButton, guestGoalButton, homePossessionButton, guestPossessionButton;
    String start;
    String stop;
    String resume;
    String reset;
    String initialGameTime;
    String initialPossessionTime;
    CountDownTimer gameCountDownTimer;
    CountDownTimer possessionCountDownTimer;

    private int homeScoreCount;
    private int guestScoreCount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeScoreCount = 0;
        guestScoreCount = 0;

        gameTimerTextView = (TextView) findViewById(R.id.game_time_text_view);
        possessionTimerTextView = (TextView) findViewById(R.id.possession_timer_text_view);
        homeScoreCountTextView = (TextView) findViewById(R.id.home_score_count_text_view);
        guestScoreCountTextView = (TextView) findViewById(R.id.guest_score_count_text_view);

        homeBallImageView = (ImageView) findViewById(R.id.home_ball_image_view);
        guestBallImageView = (ImageView) findViewById(R.id.guest_ball_image_view);

        resetButton = (Button) findViewById(R.id.reset_button);
        startButton = (Button) findViewById(R.id.start_game_button);
        homeGoalButton = (Button) findViewById(R.id.home_goal_button);
        guestGoalButton = (Button) findViewById(R.id.guest_goal_button);
        homePossessionButton = (Button) findViewById(R.id.home_possession_button);
        guestPossessionButton = (Button) findViewById(R.id.guest_possession_button);

        start = getString(R.string.start);
        stop = getString(R.string.stop);
        resume = getString(R.string.resume);
        reset = getString(R.string.reset);
        initialGameTime = getString(R.string.game_timer);
        initialPossessionTime = getString(R.string.possession_timer);

        disablePausedButtons(true);

        gameCountDownTimer = new CountDownTimer(60000 * 8, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long mm = millisUntilFinished / 1000 / 60;
                long ss = millisUntilFinished / 1000 % 60;

                gameTimerTextView.setText(String.format("%1$02d:%2$02d", mm, ss));
            }

            @Override
            public void onFinish() {
                gameTimerTextView.setText(finishedTimerState);
                gameTimerTextView.setTextColor(Color.RED);
                possessionCountDownTimer.cancel();

                disableGameOverButtons(true);
            }
        };

        possessionCountDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long mm = millisUntilFinished / 1000 / 60;
                long ss = millisUntilFinished / 1000 % 60;

                possessionTimerTextView.setText(String.format("%1$02d:%2$02d", mm, ss));
            }

            @Override
            public void onFinish() {
                possessionTimerTextView.setText(finishedTimerState);
                possessionTimerTextView.setTextColor(Color.RED);
            }
        };
    }

    // Start-stop button behaviour
    public void startPauseResumeGame(View view) {
        if (!gameCountDownTimer.isStarted()) {
            gameCountDownTimer.start();
            disablePausedButtons(false);
            startButton.setText(this.stop);
        } else {
            if (gameCountDownTimer.isPaused()) {
                gameCountDownTimer.resume();
                if (possessionCountDownTimer.isStarted()) {
                    possessionCountDownTimer.resume();
                }

                disablePausedButtons(false);

                startButton.setText(this.stop);
            } else {
                gameCountDownTimer.pause();
                possessionCountDownTimer.pause();

                disablePausedButtons(true);

                startButton.setText(this.resume);
            }
        }
    }

    public void reset(View view) {
        gameCountDownTimer.cancel();
        gameTimerTextView.setTextColor(Color.BLACK);
        gameTimerTextView.setText(initialGameTime);

        possessionCountDownTimer.cancel();
        possessionTimerTextView.setTextColor(Color.BLACK);
        possessionTimerTextView.setText(initialPossessionTime);

        homeBallImageView.setVisibility(View.INVISIBLE);
        guestBallImageView.setVisibility(View.INVISIBLE);

        homeScoreCount = 0;
        guestScoreCount = 0;
        homeScoreCountTextView.setText(String.valueOf(homeScoreCount));
        guestScoreCountTextView.setText(String.valueOf(guestScoreCount));

        startButton.setText(this.start);
        disableGameOverButtons(false);
        disablePausedButtons(true);
    }

    public void addHomeScore(View view) {
        if (gameCountDownTimer.isStarted()) {
            homeScoreCount++;
            homeScoreCountTextView.setText(Integer.toString(homeScoreCount));
        }
    }

    public void addGuestScore(View view) {
        if (gameCountDownTimer.isStarted()) {
            guestScoreCount++;
            guestScoreCountTextView.setText(Integer.toString(guestScoreCount));
        }
    }

    public void homePossesses(View view) {
        if (gameCountDownTimer.isStarted() && !gameCountDownTimer.isPaused()) {
            if (!possessionCountDownTimer.isStarted()) {
                possessionCountDownTimer.start();
            } else {
                possessionCountDownTimer.cancel();
                possessionCountDownTimer.start();
            }
            guestBallImageView.setVisibility(View.INVISIBLE);
            homeBallImageView.setVisibility(View.VISIBLE);
        }
    }

    public void guestPossesses(View view) {
        if (gameCountDownTimer.isStarted() && !gameCountDownTimer.isPaused()) {
            if (!possessionCountDownTimer.isStarted()) {
                possessionCountDownTimer.start();
            } else {
                possessionCountDownTimer.cancel();
                possessionCountDownTimer.start();
            }
            homeBallImageView.setVisibility(View.INVISIBLE);
            guestBallImageView.setVisibility(View.VISIBLE);
        }
    }

    /*
    * Helper methods
    * */
    private void disableButton(Button button, boolean disabled) {
        if (disabled) {
            button.setClickable(false);
            button.setAlpha(0.5f);
        } else {
            button.setClickable(true);
            button.setAlpha(1f);
        }
    }

    private void disableGameOverButtons(boolean disabled) {
        disableButton(startButton, disabled);
        disableButton(guestGoalButton, disabled);
        disableButton(homeGoalButton, disabled);
        disableButton(homePossessionButton, disabled);
        disableButton(guestPossessionButton, disabled);
    }

    private void disablePausedButtons(boolean disabled) {
        disableButton(guestGoalButton, disabled);
        disableButton(homeGoalButton, disabled);
        disableButton(homePossessionButton, disabled);
        disableButton(guestPossessionButton, disabled);
    }


}
