package com.google.engedu.ghost;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    TextView tvStatus, tvText;
    Button bRestart, bChallenge;

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
        try {
            InputStream is = getAssets().open("words.txt");
            dictionary = new SimpleDictionary(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        onStart(null);
    }

    private void computerTurn() {
        String text = tvText.getText().toString();

        String nextWord = dictionary.getAnyWordStartingWith(text);
        if (nextWord == null) {
            tvStatus.setText("You Lose :(");
            userTurn = true;
            return;
        }
        char nextLetter = nextWord.charAt(text.length());
        text += nextLetter;
        tvText.setText(text);
        if (text.length() >= 4 && dictionary.isWord(text)) {
            tvStatus.setText("You Win :)");
            bChallenge.setEnabled(false);
            return;
        }
        userTurn = true;
        tvStatus.setText(USER_TURN);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (Character.isLetter(event.getUnicodeChar())) {
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
        bChallenge.setEnabled(true);
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
                return;
            }
            computerTurn();
        }
    }
}
