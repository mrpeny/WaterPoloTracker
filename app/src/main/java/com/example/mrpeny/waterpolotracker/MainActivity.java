package com.example.mrpeny.waterpolotracker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity keeps track of the score of 2 water polo teams, moreover the number of the current
 * period, the period time, the possession time, indicates which team possesses the ball,
 * tracks the number of fouls and the time of the exclusion fouls.
 */
public class MainActivity extends AppCompatActivity {
    private final String finishedTimerState = "00:00";
    private int periodCount, homeScoreCount, guestScoreCount, homeFoulsCount, guestFoulsCount;
    private LinearLayout homeLinearLayout, guestLinearLayout;
    private TextView periodCounterTextView, periodTimerTextView, possessionTimerTextView, homeScoreCountTextView,
            guestScoreCountTextView, homeFoulCounterTextView, guestFoulCounterTextView;
    private ImageView homeBallImageView, guestBallImageView;
    private Button resetButton, startButton, homeGoalButton, guestGoalButton, homePossessionButton,
            guestPossessionButton, homeFoulButton, guestFoulButton;
    private String start, stop, resume, reset, initialPeriodTime, initialPossessionTime;
    private CountDownTimer periodCountDownTimer, possessionCountDownTimer;
    private List<CountDownTimer> guestFoulsCountDownTimerList, homeFoulsCountDownTimerList;

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
        periodCounterTextView.setText(getString(R.string.period_count, periodCount));
        periodTimerTextView = (TextView) findViewById(R.id.period_time_text_view);
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
        initialPeriodTime = getString(R.string.game_timer);
        initialPossessionTime = getString(R.string.possession_timer);

        disablePausedButtons(true);

        /*
        * Initialize period Countdown Timer with 8 minutes total time and 1 second countdown interval
        */
        periodCountDownTimer = new CountDownTimer(8 * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long mm = millisUntilFinished / 1000 / 60;
                long ss = millisUntilFinished / 1000 % 60;

                periodTimerTextView.setText(String.format("%1$02d:%2$02d", mm, ss));
            }

            @Override
            public void onFinish() {
                possessionCountDownTimer.cancel();
                this.cancel();

                cancelFoulCountDownTimers();

                periodTimerTextView.setText(finishedTimerState);
                periodTimerTextView.setTextColor(Color.RED);
                possessionTimerTextView.setTextColor(Color.RED);

                periodCount++;
                disablePausedButtons(true);
                startButton.setText(R.string.next_period);
            }
        };

        /*
        * Initialize Possession Countdown Timer with 30 seconds total time and 1 second countdown interval
        */
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

    /*
    * OnClick methods
    */
    public void startPauseResumeGame(View view) {
        // if all the 4 periods has been played
        if (periodCount > 4) {
            Toast toast = Toast.makeText(this, R.string.game_over, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        // If game hasn't been started yet
        if (!periodCountDownTimer.isStarted()) {
            startGame();
            disablePausedButtons(false);
            startButton.setText(this.stop);
        } else {
            // If game has been started but was paused
            if (periodCountDownTimer.isPaused()) {
                resumeGame();
                disablePausedButtons(false);
                startButton.setText(this.stop);
            }
            // If game has been started but and is running
            else {
                pauseGame();
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

        periodCountDownTimer.cancel();
        periodTimerTextView.setTextColor(Color.BLACK);
        periodTimerTextView.setText(initialPeriodTime);

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

        periodCounterTextView.setText(getString(R.string.period_count, periodCount));
        homeScoreCountTextView.setText(String.valueOf(homeScoreCount));
        guestScoreCountTextView.setText(String.valueOf(guestScoreCount));
        homeFoulCounterTextView.setText(String.valueOf(homeFoulsCount));
        guestFoulCounterTextView.setText(String.valueOf(guestFoulsCount));

        startButton.setText(this.start);
        disablePausedButtons(true);
    }

    public void addHomeScore(View view) {
        if (periodCountDownTimer.isStarted() && !periodCountDownTimer.isPaused()) {
            homeScoreCount++;
            homeScoreCountTextView.setText(Integer.toString(homeScoreCount));
        }
    }

    public void addGuestScore(View view) {
        if (periodCountDownTimer.isStarted() && !periodCountDownTimer.isPaused()) {
            guestScoreCount++;
            guestScoreCountTextView.setText(Integer.toString(guestScoreCount));
        }
    }

    public void homePossesses(View view) {
        if (periodCountDownTimer.isStarted() && !periodCountDownTimer.isPaused()) {
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
        if (periodCountDownTimer.isStarted() && !periodCountDownTimer.isPaused()) {
            if (!possessionCountDownTimer.isStarted()) {
                possessionCountDownTimer.start();
            } else {
                possessionCountDownTimer.cancel();
                possessionCountDownTimer.start();
            }
            homeBallImageView.setVisibility(View.INVISIBLE);
            guestBallImageView.setVisibility(View.VISIBLE);
            possessionTimerTextView.setTextColor(Color.BLACK);
        }
    }

    public void addHomeFoul(View view) {
       /* Creating a TextView based on a predefined layout that will be added later as a child of
        * a LinearLayout
        */
        final TextView homeTimeoutTextView = (TextView) getLayoutInflater().inflate(R.layout.timeout_timer_text_view_template, null);
        /*
        * Initialize Fouls Countdown Timer for Home team with 10 seconds total time and 1 second countdown interval
        */
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

        // Adding the current fouls CountDown Timer to a List to be able to handle it later
        homeFoulsCountDownTimerList.add(foulsCountDownTimer);

        // Adding the above inflated TextView to the parent LinearLayout
        homeLinearLayout.addView(homeTimeoutTextView);

        homeFoulsCount++;
        homeFoulCounterTextView.setText(Integer.toString(homeFoulsCount));
    }

    public void addGuestFoul(View view) {
        /* Creating a TextView based on a predefined layout that will be added later as a child of
        * a LinearLayout
        */
        final TextView guestTimeoutTextView = (TextView) getLayoutInflater().inflate(R.layout.timeout_timer_text_view_template, null);

        /*
        * Initialize Fouls Countdown Timer for Guest team with 10 seconds total time and 1 second countdown interval
        */
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

        // Adding the current fouls CountDown Timer to a List to be able to handle it later
        guestFoulsCountDownTimerList.add(foulsCountDownTimer);

        // Adding the above inflated TextView to the parent LinearLayout
        guestLinearLayout.addView(guestTimeoutTextView);

        guestFoulsCount++;
        guestFoulCounterTextView.setText(Integer.toString(guestFoulsCount));
    }

    /*
    * Helper methods
    */
    private void startGame() {
        periodCounterTextView.setText(getString(R.string.period_count, periodCount));
        periodCountDownTimer.start();
        periodTimerTextView.setTextColor(Color.BLACK);
        possessionTimerTextView.setTextColor(Color.BLACK);
        periodTimerTextView.setText(initialPeriodTime);
        possessionTimerTextView.setText(initialPossessionTime);
        homeBallImageView.setVisibility(View.INVISIBLE);
        guestBallImageView.setVisibility(View.INVISIBLE);
        homeLinearLayout.removeViews(6, homeFoulsCountDownTimerList.size());
        homeFoulsCountDownTimerList.clear();
        guestLinearLayout.removeViews(6, guestFoulsCountDownTimerList.size());
        guestFoulsCountDownTimerList.clear();
    }

    private void resumeGame() {
        periodCountDownTimer.resume();

        if (possessionCountDownTimer.isStarted()) {
            possessionCountDownTimer.resume();
        }

        for (CountDownTimer countDownTimer : homeFoulsCountDownTimerList) {
            countDownTimer.resume();
        }

        for (CountDownTimer countDownTimer : guestFoulsCountDownTimerList) {
            countDownTimer.resume();
        }
    }

    private void pauseGame() {
        periodCountDownTimer.pause();
        possessionCountDownTimer.pause();

        for (CountDownTimer countDownTimer : homeFoulsCountDownTimerList) {
            countDownTimer.pause();
        }

        for (CountDownTimer countDownTimer : guestFoulsCountDownTimerList) {
            countDownTimer.pause();
        }
    }

    /**
     * Sets buttons unclickable and with faded background showing the user that they are inactive
     *
     * @param button   the button object to be set clickable or unclickable
     * @param disabled sets the button clickable or unclickable and sets corresponding background
     */
    private void disableButton(Button button, boolean disabled) {
        if (disabled) {
            button.setClickable(false);
            button.setAlpha(0.5f);
        } else {
            button.setClickable(true);
            button.setAlpha(1f);
        }
    }

    /**
     * Sets buttons disabled that are useles when game is stopped
     *
     * @param disabled true if buttons should be unclickable false if clickable
     */
    private void disablePausedButtons(boolean disabled) {
        disableButton(guestGoalButton, disabled);
        disableButton(homeGoalButton, disabled);
        disableButton(homePossessionButton, disabled);
        disableButton(guestPossessionButton, disabled);
        disableButton(homeFoulButton, disabled);
        disableButton(guestFoulButton, disabled);
    }

    private void cancelFoulCountDownTimers() {
        for (CountDownTimer countDownTimer : homeFoulsCountDownTimerList) {
            countDownTimer.cancel();
        }
        for (CountDownTimer countDownTimer : guestFoulsCountDownTimerList) {
            countDownTimer.cancel();
        }
    }
}
