package com.google.engedu.ghost;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends Activity implements View.OnClickListener {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    TextView tvStatus, tvText, tvScore;
    Button bRestart, bChallenge;
    boolean isPlaying = true;
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        tvText = (TextView) findViewById(R.id.ghostText);
        tvStatus = (TextView) findViewById(R.id.gameStatus);
        bRestart = (Button) findViewById(R.id.restart);
        bRestart.setOnClickListener(this);
        bChallenge = (Button) findViewById(R.id.challenge);
        bChallenge.setOnClickListener(this);
        tvScore = (TextView) findViewById(R.id.score);
        try {
            InputStream is = getAssets().open("words.txt");
            dictionary = new SimpleDictionary(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (savedInstanceState != null) {
            Log.i("got saved", savedInstanceState.toString());
            tvStatus.setText(savedInstanceState.getString("status"));
            tvText.setText(savedInstanceState.getString("text"));
            userTurn = savedInstanceState.getBoolean("userTurn");
            isPlaying = savedInstanceState.getBoolean("isPlaying");
            score = savedInstanceState.getInt("score");
            tvScore.setText("Score : " + score);
            bChallenge.setEnabled(isPlaying);
        } else
            onStart(null);
    }

    private void computerTurn() {
        String text = tvText.getText().toString();

        String nextWord = dictionary.getAnyWordStartingWith(text);
        if (nextWord == null) {
            tvStatus.setText("You Lose :(");
            isPlaying = false;
            bChallenge.setEnabled(false);
            userTurn = true;
            score -= text.length();
            tvScore.setText("Score : " + score);
            return;
        }
        char nextLetter = nextWord.charAt(text.length());
        text += nextLetter;
        tvText.setText(text);
        if (text.length() >= 4 && dictionary.isWord(text)) {
            tvStatus.setText("You Win :)");
            isPlaying = false;
            bChallenge.setEnabled(false);
            score += text.length();
            tvScore.setText("Score : " + score);
            return;
        }
        userTurn = true;
        tvStatus.setText(USER_TURN);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (isPlaying && Character.isLetter(event.getUnicodeChar())) {
            Log.i("got char", keyCode + "");

            String text = tvText.getText().toString();
            text += (char) event.getUnicodeChar();
            tvText.setText(text);
        } else {
            Log.i("got not char", keyCode + "");
            return super.onKeyUp(keyCode, event);
        }
        return true;
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     *
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        isPlaying = true;
        bChallenge.setEnabled(isPlaying);
        tvText.setText("");
        if (userTurn) {
            tvStatus.setText(USER_TURN);
        } else {
            tvStatus.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.restart)
            onStart(null);
        else {
            String text = tvText.getText().toString();
            if (text.length() >= 4 && dictionary.isWord(text)) {
                tvStatus.setText("You Lose :(");
                bChallenge.setEnabled(false);
                score -= text.length();
                tvScore.setText("Score : " + score);
                isPlaying = false;
                return;
            }
            computerTurn();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("saving", "data");
        outState.putString("status", tvStatus.getText().toString());
        outState.putString("text", tvText.getText().toString());
        outState.putBoolean("userTurn", userTurn);
        outState.putBoolean("isPlaying", isPlaying);
        outState.putInt("score", score);
    }
}
