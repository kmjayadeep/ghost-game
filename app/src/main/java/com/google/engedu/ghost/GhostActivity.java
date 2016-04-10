package com.google.engedu.ghost;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    TextView tvStatus, tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        tvText = (TextView) findViewById(R.id.ghostText);
        tvStatus = (TextView) findViewById(R.id.gameStatus);
        findViewById(R.id.restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStart(null);
            }
        });
        try {
            InputStream is = getAssets().open("words.txt");
            dictionary = new SimpleDictionary(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        onStart(null);
    }

    private void computerTurn() {
        // Do computer turn stuff then make it the user's turn again
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
        tvText.setText("");
        if (userTurn) {
            tvStatus.setText(USER_TURN);
        } else {
            tvStatus.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }
}
