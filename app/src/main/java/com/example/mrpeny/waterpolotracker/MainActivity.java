package com.example.mrpeny.waterpolotracker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String finishedTimerState = "00:00";
    LinearLayout homeLinearLayout, guestLinearLayout;
    TextView periodCounterTextView, gameTimerTextView, possessionTimerTextView, homeScoreCountTextView,
            guestScoreCountTextView, homeFoulCounterTextView, guestFoulCounterTextView, homeTimeOutTimer;
    ImageView homeBallImageView, guestBallImageView;
    Button resetButton, startButton, homeGoalButton, guestGoalButton, homePossessionButton,
            guestPossessionButton, homeFoulButton, guestFoulButton;
    String start, stop, resume, reset, initialGameTime, initialPossessionTime;
    CountDownTimer gameCountDownTimer, possessionCountDownTimer;
    List<CountDownTimer> guestFoulsCountDownTimerList;
    List<CountDownTimer> homeFoulsCountDownTimerList;

    private int homeScoreCount, guestScoreCount, periodCount, homeFoulsCount, guestFoulsCount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        periodCount = 1;
        homeScoreCount = 0;
        guestScoreCount = 0;
        homeFoulsCount = 0;
        guestFoulsCount = 0;

        homeLinearLayout = (LinearLayout) findViewById(R.id.home_linear_layout);
        guestLinearLayout = (LinearLayout) findViewById(R.id.guest_linear_layout);

        periodCounterTextView = (TextView) findViewById(R.id.period_counter_text_view);
        gameTimerTextView = (TextView) findViewById(R.id.game_time_text_view);
        possessionTimerTextView = (TextView) findViewById(R.id.possession_timer_text_view);
        homeScoreCountTextView = (TextView) findViewById(R.id.home_score_count_text_view);
        guestScoreCountTextView = (TextView) findViewById(R.id.guest_score_count_text_view);
        homeFoulCounterTextView = (TextView) findViewById(R.id.home_foul_counter_text_view);
        guestFoulCounterTextView = (TextView) findViewById(R.id.guest_foul_counter_text_view);
        guestFoulsCountDownTimerList = new ArrayList<>();
        homeFoulsCountDownTimerList = new ArrayList<>();

        homeBallImageView = (ImageView) findViewById(R.id.home_ball_image_view);
        guestBallImageView = (ImageView) findViewById(R.id.guest_ball_image_view);

        resetButton = (Button) findViewById(R.id.reset_button);
        startButton = (Button) findViewById(R.id.start_game_button);
        homeGoalButton = (Button) findViewById(R.id.home_goal_button);
        guestGoalButton = (Button) findViewById(R.id.guest_goal_button);
        homePossessionButton = (Button) findViewById(R.id.home_possession_button);
        guestPossessionButton = (Button) findViewById(R.id.guest_possession_button);
        homeFoulButton = (Button) findViewById(R.id.homer_foul_button);
        guestFoulButton = (Button) findViewById(R.id.guest_foul_button);

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
                possessionCountDownTimer.cancel();
                this.cancel();
                for (CountDownTimer countDownTimer : homeFoulsCountDownTimerList) {
                    countDownTimer.cancel();
                }

                for (CountDownTimer countDownTimer : guestFoulsCountDownTimerList) {
                    countDownTimer.cancel();
                }

                gameTimerTextView.setText(finishedTimerState);
                gameTimerTextView.setTextColor(Color.RED);
                possessionTimerTextView.setTextColor(Color.RED);

                periodCount++;

                disablePausedButtons(true);

                startButton.setText("Next Period");
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
            periodCounterTextView.setText(String.valueOf(periodCount));
            gameCountDownTimer.start();
            gameTimerTextView.setTextColor(Color.BLACK);
            possessionTimerTextView.setTextColor(Color.BLACK);
            gameTimerTextView.setText(initialGameTime);
            possessionTimerTextView.setText(initialPossessionTime);
            homeBallImageView.setVisibility(View.INVISIBLE);
            guestBallImageView.setVisibility(View.INVISIBLE);
            homeLinearLayout.removeViews(6, homeFoulsCountDownTimerList.size());
            homeFoulsCountDownTimerList.clear();
            guestLinearLayout.removeViews(6, guestFoulsCountDownTimerList.size());
            guestFoulsCountDownTimerList.clear();
            disablePausedButtons(false);
            startButton.setText(this.stop);
        } else {
            if (gameCountDownTimer.isPaused()) {
                gameCountDownTimer.resume();
                if (possessionCountDownTimer.isStarted()) {
                    possessionCountDownTimer.resume();
                }

                for (CountDownTimer countDownTimer : homeFoulsCountDownTimerList) {
                    countDownTimer.resume();
                }

                for (CountDownTimer countDownTimer : guestFoulsCountDownTimerList) {
                    countDownTimer.resume();
                }

                disablePausedButtons(false);

                startButton.setText(this.stop);
            } else {
                gameCountDownTimer.pause();
                possessionCountDownTimer.pause();

                for (CountDownTimer countDownTimer : homeFoulsCountDownTimerList) {
                    countDownTimer.pause();
                }

                for (CountDownTimer countDownTimer : guestFoulsCountDownTimerList) {
                    countDownTimer.pause();
                }

                disablePausedButtons(true);

                startButton.setText(this.resume);
            }
        }
    }

    public void reset(View view) {
        periodCount = 1;
        homeScoreCount = 0;
        guestScoreCount = 0;
        homeFoulsCount = 0;
        guestFoulsCount = 0;

        gameCountDownTimer.cancel();
        gameTimerTextView.setTextColor(Color.BLACK);
        gameTimerTextView.setText(initialGameTime);

        possessionCountDownTimer.cancel();
        possessionTimerTextView.setTextColor(Color.BLACK);
        possessionTimerTextView.setText(initialPossessionTime);

        homeBallImageView.setVisibility(View.INVISIBLE);
        guestBallImageView.setVisibility(View.INVISIBLE);

        if (homeFoulsCountDownTimerList.size() > 0) {
            homeLinearLayout.removeViews(6, homeFoulsCountDownTimerList.size());
            homeFoulsCountDownTimerList.clear();
        }

        if (guestFoulsCountDownTimerList.size() > 0) {
            guestLinearLayout.removeViews(6, guestFoulsCountDownTimerList.size());
            guestFoulsCountDownTimerList.clear();
        }

        periodCounterTextView.setText(String.valueOf(periodCount));
        homeScoreCountTextView.setText(String.valueOf(homeScoreCount));
        guestScoreCountTextView.setText(String.valueOf(guestScoreCount));
        homeFoulCounterTextView.setText(String.valueOf(homeFoulsCount));
        guestFoulCounterTextView.setText(String.valueOf(guestFoulsCount));

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
            possessionTimerTextView.setTextColor(Color.BLACK);
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

    public void addHomeFoul(View view) {
        final TextView homeTimeoutTextView = (TextView) getLayoutInflater().inflate(R.layout.timeout_timer_text_view_template, null);

        CountDownTimer foulsCountDownTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long mm = millisUntilFinished / 1000 / 60;
                long ss = millisUntilFinished / 1000 % 60;

                homeTimeoutTextView.setText(String.format("%1$02d:%2$02d", mm, ss));
            }

            @Override
            public void onFinish() {
                homeLinearLayout.removeView(homeTimeoutTextView);
                homeFoulsCountDownTimerList.remove(this);
            }
        }.start();

        homeFoulsCountDownTimerList.add(foulsCountDownTimer);

        homeLinearLayout.addView(homeTimeoutTextView);

        homeFoulsCount++;
        homeFoulCounterTextView.setText(Integer.toString(homeFoulsCount));
    }

    public void addGuestFoul(View view) {
        final TextView guestTimeoutTextView = (TextView) getLayoutInflater().inflate(R.layout.timeout_timer_text_view_template, null);

        CountDownTimer foulsCountDownTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long mm = millisUntilFinished / 1000 / 60;
                long ss = millisUntilFinished / 1000 % 60;

                guestTimeoutTextView.setText(String.format("%1$02d:%2$02d", mm, ss));
            }

            @Override
            public void onFinish() {
                guestLinearLayout.removeView(guestTimeoutTextView);
                guestFoulsCountDownTimerList.remove(this);
            }
        }.start();

        guestFoulsCountDownTimerList.add(foulsCountDownTimer);

        guestLinearLayout.addView(guestTimeoutTextView);

        guestFoulsCount++;
        guestFoulCounterTextView.setText(Integer.toString(guestFoulsCount));
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
        disableButton(homeFoulButton, disabled);
        disableButton(guestFoulButton, disabled);
    }

    private void disablePausedButtons(boolean disabled) {
        disableButton(guestGoalButton, disabled);
        disableButton(homeGoalButton, disabled);
        disableButton(homePossessionButton, disabled);
        disableButton(guestPossessionButton, disabled);
        disableButton(homeFoulButton, disabled);
        disableButton(guestFoulButton, disabled);
    }
}
