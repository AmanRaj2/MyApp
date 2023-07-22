package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TicTacToeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttons = new Button[3][3];
    private Button buttonReset;
    private boolean player1turn = true, awake = true;
    private int roundCount, gameCount;
    private int player1Points, player2Points;
    private TextView textViewPlayer1, textViewPlayer2, textViewGameCount;
    private long backPressedTime;
    private Toast backToast;
    private int[] arr = new int[6];
    private SoundPool soundPool;
    private int m24sound, awm_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);
        textViewGameCount = findViewById(R.id.text_view_gameCount);
        buttonReset = findViewById(R.id.reset_btn);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        m24sound = soundPool.load(this, R.raw.m24sound, 1);
        awm_sound = soundPool.load(this, R.raw.awm_sound, 1);


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "btn_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }
        buttonReset.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.lightRed), PorterDuff.Mode.MULTIPLY);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
        setBoardColor();
        if (savedInstanceState == null) {
            resetBoard();
        }
    }

    @Override
    public void onClick(View v) {
        if (awake) {
            if (!((Button) v).getText().toString().equals("")) {
                return;
            }

            if (player1turn) {
                ((Button) v).setText("X");
                setP2textColor();
                playm24Sound();
            } else {
                ((Button) v).setText("O");
                setP1textColor();
                playm24Sound();
            }

            roundCount++;

            if (checkForWin()) {
                gameCount++;
                if (player1turn) {
                    player1Wins();
                } else {
                    player2Wins();
                }
            } else if (roundCount == 9) {
                gameCount++;
                draw();
            } else {
                player1turn = !player1turn;
            }
        }
    }

    private void playm24Sound() {
        soundPool.play(m24sound, 1, 1, 0, 0, 1);
    }

    private void playawmSound() {
        soundPool.play(awm_sound, 1, 1, 0, 0, 1);
    }

    private void player1Wins() {
        playawmSound();
        player1Points++;
        Toast.makeText(this, "Player X Wins!", Toast.LENGTH_SHORT).show();
        setWinColor();
        awake = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePointsText();
                resetBoard();
                awake = true;
            }
        }, 3000);

    }

    private void player2Wins() {
        playawmSound();
        player2Points++;
        Toast.makeText(this, "Player O Wins!", Toast.LENGTH_SHORT).show();
        setWinColor();
        awake = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePointsText();
                resetBoard();
                awake = true;
            }
        }, 3000);
    }

    private void draw() {
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        resetBoard();
        updatePointsText();
    }

    private void updatePointsText() {
        textViewPlayer1.setText("Player X: " + player1Points);
        textViewPlayer2.setText("Player O: " + player2Points);
        textViewGameCount.setText("Game Played : " + gameCount);
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        roundCount = 0;
        if (gameCount % 2 == 0) {
            player1turn = true;
            setP1textColor();
            Toast.makeText(this, "Palyer X turn", Toast.LENGTH_SHORT).show();
        } else {
            player1turn = false;
            setP2textColor();
            Toast.makeText(this, "Palyer O turn", Toast.LENGTH_SHORT).show();
        }
        setBoardColor();
    }

    private void setBoardColor() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].getBackground().setColorFilter(ContextCompat.getColor(this, R.color.boardColor), PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    private void setP1textColor() {
        textViewPlayer2.setBackgroundColor(Color.TRANSPARENT);
        textViewPlayer1.setBackgroundColor(getResources().getColor(R.color.turnColor));
    }

    private void setP2textColor() {
        textViewPlayer1.setBackgroundColor(Color.TRANSPARENT);
        textViewPlayer2.setBackgroundColor(getResources().getColor(R.color.turnColor));
    }

    private void setWinColor() {
        buttons[arr[0]][arr[1]].getBackground().setColorFilter(ContextCompat.getColor(this, R.color.winColor), PorterDuff.Mode.MULTIPLY);
        buttons[arr[2]][arr[3]].getBackground().setColorFilter(ContextCompat.getColor(this, R.color.winColor), PorterDuff.Mode.MULTIPLY);
        buttons[arr[4]][arr[5]].getBackground().setColorFilter(ContextCompat.getColor(this, R.color.winColor), PorterDuff.Mode.MULTIPLY);

    }

    private void resetGame() {
        player1Points = 0;
        player2Points = 0;
        gameCount = 0;
        updatePointsText();
        resetBoard();
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                arr[0] = i;
                arr[1] = 0;
                arr[2] = i;
                arr[3] = 1;
                arr[4] = i;
                arr[5] = 2;
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                arr[0] = 0;
                arr[1] = i;
                arr[2] = 1;
                arr[3] = i;
                arr[4] = 2;
                arr[5] = i;
                return true;
            }
        }
        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            arr[0] = 0;
            arr[1] = 0;
            arr[2] = 1;
            arr[3] = 1;
            arr[4] = 2;
            arr[5] = 2;
            return true;
        }
        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            arr[0] = 0;
            arr[1] = 2;
            arr[2] = 1;
            arr[3] = 1;
            arr[4] = 2;
            arr[5] = 0;
            return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("roundCount", roundCount);
        outState.putInt("player1Points", player1Points);
        outState.putInt("player2Points", player2Points);
        outState.putInt("gameCount", gameCount);
        outState.putBoolean("player1Turn", player1turn);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        roundCount = savedInstanceState.getInt("roundCount");
        gameCount = savedInstanceState.getInt("gameCount");
        player1Points = savedInstanceState.getInt("player1Points");
        player2Points = savedInstanceState.getInt("player2Points");
        player1turn = savedInstanceState.getBoolean("player1Turn");
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }
}
