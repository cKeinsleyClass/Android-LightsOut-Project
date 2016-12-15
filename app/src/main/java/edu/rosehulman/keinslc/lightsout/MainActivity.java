package edu.rosehulman.keinslc.lightsout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LightsOutGame mLightsOutGame = new LightsOutGame();
    private TextView mGameStateTextView;
    private Button[] mButtons = new Button[mLightsOutGame.DEFAULT_NUM_BUTTONS];
    private Button mNewGameButton;

    // Capture Views \ Buttons
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("LightsOut", "OnCreateCalled");

        // Capture the textView
        mGameStateTextView = (TextView) findViewById(R.id.textView);
        // Capture the new game button
        mNewGameButton = (Button) findViewById(R.id.newGame);
        // Capture the game buttons
        for (int i = 0; i < mButtons.length; i++) {
            // Builds the ID by looking up the Identifier name specified in xml
            int id = getResources().getIdentifier("button" + i, "id", getPackageName());
            mButtons[i] = (Button) findViewById(id);
            // Sets this as the listener
            mButtons[i].setOnClickListener(this);
        }
        // Sets this as the listener
        mNewGameButton.setOnClickListener(this);
        // Updates the buttons to match the gameState
        updateButtonState();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mNewGameButton.getId()) {
            mLightsOutGame = new LightsOutGame();
        } else {
            for (int i = 0; i < mButtons.length; i++) {
                if (view.getId() == mButtons[i].getId()) {
                    // If true there was a win
                    mLightsOutGame.pressedButtonAtIndex(i);
                }
            }
        }
        updateView();
    }

    /**
     * For udpating the view
     */
    private void updateView() {
        // Must have won
        updateButtonState();
        if (mLightsOutGame.checkForWin()) {
            changeClickableButtons(false);
            mGameStateTextView.setText(R.string.winState);
            return;
        }
        int numTimesClicked = mLightsOutGame.getNumPresses();
        // Must have been a new game
        if (numTimesClicked == 0) {
            mGameStateTextView.setText(R.string.game_state);
            changeClickableButtons(true);
            return;
        }
        // Must be in the middle of a game
        mGameStateTextView.setText(getResources().getQuantityString(R.plurals.numtimesPressed, mLightsOutGame.getNumPresses(), mLightsOutGame.getNumPresses()));
    }

    /**
     * Changes whether or not the buttons should be able to be clicked
     *
     * @param clickable - true if they should work, false otherwise
     */
    private void changeClickableButtons(boolean clickable) {
        for (int i = 0; i < mButtons.length; i++) {
            mButtons[i].setEnabled(clickable);
        }
    }

    /**
     * Loops over buttons updating the text on them
     */
    private void updateButtonState() {
        for (int i = 0; i < mButtons.length; i++) {
            String s = mLightsOutGame.getValueAtIndex(i) + "";
            mButtons[i].setText(s);
        }
    }

    //Use onSaveInstanceState(Bundle) and onRestoreInstanceState for rotation data persistence

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        Log.d("LightsOut", "Store into Instance State");
        savedInstanceState.putInt("numClicks", mLightsOutGame.getNumPresses());
        for (int i = 0; i < mButtons.length; i++) {
            savedInstanceState.putInt("button"+i,mLightsOutGame.getValueAtIndex(i));
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    //onRestoreInstanceState
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("LightsOut", "Restored from Instance State");
        // This bundle has also been passed to onCreate.
        int myInt = savedInstanceState.getInt("numClicks");
        int[] buttonValues = new int[mButtons.length];
        for (int i = 0; i < mButtons.length; i++) {
            buttonValues[i] = savedInstanceState.getInt("button"+i);
        }
        mLightsOutGame.setNumPresses(myInt);
        mLightsOutGame.setAllValues(buttonValues);
        updateView();
    }

}
