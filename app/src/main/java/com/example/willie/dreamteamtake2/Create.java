package com.example.willie.dreamteamtake2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.util.Log;

/**
 * Main form for creating a new tournament.
 *
 * @author Anatolie Diordita (Code + UI)
 * @author Willie Ausrotas (UI)
 *
 */
public class Create extends AppCompatActivity {

    private int teamsSelected;

    // Minimum value for team number SeekBar
    private static int minTeams = 3;
    private static int maxTeams = 32;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tournament);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        teamsSelected = minTeams;

        // Initialize NumberPicker for teams, and set any variables
        NumberPicker numTeamPicker = (NumberPicker) findViewById(R.id.numTeamPicker);

        numTeamPicker.setValue(minTeams);
        numTeamPicker.setMinValue(minTeams);
        numTeamPicker.setMaxValue(maxTeams);
        numTeamPicker.setWrapSelectorWheel(false);

        // Change teamsSelected variable every time user changes value on numberPicker
        numTeamPicker.setOnValueChangedListener(new OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                teamsSelected = newVal;
            }
        });

    }

    /**
     * Listener for every time user submits the create form. Form requirements are as follows:
     *
     * 1. Tournament name must not be empty
     * 2. Tournament of type knockout is not allowed with odd teams (combo type only for odd knockout)
     *
     * @param view The window's view
     */
    public void onInputTeams(View view){

        // Whether or not to move onto the next screen (for validation)
        boolean validated = true;

        EditText inputName = (EditText) findViewById(R.id.inputTournamentName);
        String inputNameVal = (String) inputName.getText().toString();

        NumberPicker inputNumTeams = (NumberPicker) findViewById(R.id.numTeamPicker);
        int inputNumTeamsVal = (int) inputNumTeams.getValue();

        RadioGroup radioType = (RadioGroup) findViewById(R.id.radioType);
        String radioTypeVal = ((RadioButton) findViewById(radioType.getCheckedRadioButtonId())).getText().toString();

        // Validate if there is a tournament name entered
        if ( inputNameVal.matches("") ) {
            inputName.setError("Please enter a tournament name");
            validated = false;
        }

        // If knockout + odd number of teams, display notification to switch to combo type
        if ( radioTypeVal.matches("Knockout") && inputNumTeamsVal % 2 != 0 ) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder
                    .setTitle("Notice")
                    .setMessage("To have an odd number of teams, please select the \"Combination\" tournament type.")
                    .setCancelable(false)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {}
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            validated = false;
        }

        // No problems with form, continue to next screen
        if ( validated ) {
            Intent intent = new Intent(getApplicationContext(), Create_InputTeams.class);

            intent.putExtra("tourName", inputNameVal);
            intent.putExtra("tourTeams", inputNumTeamsVal);
            intent.putExtra("tourType", radioTypeVal);

            startActivityForResult(intent,0);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
            return true;

        else if ( id == R.id.action_howtouse) {
            Intent intent = new Intent(getApplicationContext(), HowToUse.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

}
