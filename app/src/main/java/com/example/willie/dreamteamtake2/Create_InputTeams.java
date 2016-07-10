package com.example.willie.dreamteamtake2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.GridLayout.LayoutParams;
import android.util.TypedValue;
import android.content.Context;
import android.util.Log;

/**
 * Team input list for creating a new tournament.
 *
 * @author Anatolie Diordita (Code + UI)
 * @author Willem Kowal (Code)
 * @author Willie Ausrotas (UI)
 */
public class Create_InputTeams extends AppCompatActivity {

    private String name, type;
    private int amountOfTeams;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_teams);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent extras = getIntent();

        name = extras.getStringExtra("tourName");
        type = extras.getStringExtra("tourType");
        amountOfTeams = extras.getIntExtra("tourTeams", 0);

        // Get the container for team name EditText objects
        LinearLayout TeamInputList = (LinearLayout) findViewById(R.id.teamListContainer);

        // Object containing visual settings for EditText inputs to be generated below
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 45);

        // Generate x amount of EditText inputs on the team name entry page
        for (int i = 0; i < amountOfTeams; i++) {

            EditText temp = new EditText(this);

            temp.setHint("Team " + (i + 1));
            temp.setId(i);
            temp.setLayoutParams(layoutParams);
            temp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            // Push the EditText object into the list
            TeamInputList.addView(temp);

        }

    }

    /**
     * Listener for every time user submits the create form.
     *
     * @param view The window's view
     */
    public void onViewTournament(View view) {

        // Initialize view variables
        String[] teamNames = new String[amountOfTeams];
        LinearLayout container = (LinearLayout) findViewById(R.id.teamListContainer);

        // Loop through and gather values of all team names from EditText elements
        for (int i = 0; i < container.getChildCount(); i++) {

            View theView = container.getChildAt(i);

            if (theView instanceof EditText) {
                EditText editText = (EditText) theView;

                String value = editText.getText().toString();

                int id = editText.getId();

                // If no team name entered, default to hint value on input
                if (value.matches(""))
                    value = editText.getHint().toString();

                // Verify team name is in bounds for addition to array
                if (id < teamNames.length)
                    teamNames[id] = value;

            }

        }

        // Create and save the new tournament
        Tournament createdTournament = createTournament(name, teamNames, type);
        createdTournament.saveTournament(context);

        // Move on to home screen
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivityForResult(intent, 0);

    }

    /**
     * Returns a new Tournament with a knockout layout.
     *
     * @param name
     * @param teamNames
     * @return
     */
    private Tournament createKnockout(String name, String[] teamNames) {
        int numRounds = (int) Math.ceil(Math.log10((double) teamNames.length) / Math.log10(2)), numMatches = teamNames.length / 2;

        Log.i("CREATE_KNOCK", "Start creation of knockout (rounds: " + numRounds + " ~ matches: " + numMatches);

        if (teamNames.length % 2 == 1 || teamNames.length % 4 == 1) {
            numRounds++;
            Log.i("CREATE_KNOCK", "Odd number of teams, adding an extra round");
        }

        Tournament tournament = new Tournament(name, "knockout", teamNames.length, numRounds);

        // Add teams to tournaments from inputs
        for (int i = 0; i < teamNames.length; i++) {
            tournament.addTeam(new Team(teamNames[i]));
            Log.i("CREATE_KNOCK", "Added team " + teamNames[i] + " to position " + i);
        }

        do {//Create empty rounds of play
            tournament.addRound(numMatches);
            numMatches = numMatches / 2;
            Log.i("CREATE_KNOCK", "Added round " + numMatches);
        } while (numMatches > 0);

        if (teamNames.length % 2 != 0 || teamNames.length % 4 != 0) {
            Log.i("CREATE_KNOCK", "Odd number of teams, adding extra round to end");
            tournament.addRound(1);
        }

        Log.i("CREATE_KNOCK", "Randomizing tournament...");
        tournament.randomStart();

        return tournament;
    }

    /**
     * Returns a new Tournament with a round robbin layout.
     *
     * @param name
     * @param teamNames
     * @return
     */
    private Tournament createRoundRobin(String name, String[] teamNames) {

        Log.i("CREATE_ROBIN", "Start creation of knockout (rounds: " + teamNames.length + ")");

        Tournament tournament = new Tournament(name, "roundrobin", teamNames.length, teamNames.length - 1);
// Add teams to tournaments from inputs
        for (int i = 0; i < teamNames.length; i++) {
            tournament.addTeam(new Team(teamNames[i]));
            Log.i("CREATE_ROBIN", "Added team " + teamNames[i] + " to position " + i);
        }
//Create empty rounds
        for (int i = 0; i < teamNames.length - 1; i++) {
            tournament.addRound(teamNames.length - i - 1);
            Log.i("CREATE_ROBIN", "Added team " + teamNames[i] + " to position " + i);
        }
//Fill each round with the required teams
        for (int i = 0; i < teamNames.length - 1; i++) {//round loop

            Log.i("CREATE_ROBIN", "Added round " + i + " with " + tournament.getRound(i).getNumMatches() + " matches");

            for (int j = i + 1; j < teamNames.length; j++) {//match loop
                Log.i("CREATE_ROBIN", "Added match at round " + i + " between " + tournament.getTeam(i).getName() + " and " + tournament.getTeam(i + 1).getName());
                tournament.addMatch(i, tournament.getTeam(i), tournament.getTeam(j));
            }

        }
        return tournament;
    }

    /**
     * Returns a new Tournament with a combo layout.
     * @param name
     * @param teamNames
     * @return
     */
    private Tournament createCombo(String name, String[] teamNames) {

        Log.i("CREATE_COMBO", "Start creation of combo (rounds: " + teamNames.length + ")");

        //Determine number of rounds
        int numRounds = teamNames.length - 1;
        int numKnockoutRounds = 0;
        numKnockoutRounds = ((int) Math.floor(Math.log(numRounds) / Math.log(2)));
        numRounds += numKnockoutRounds;

        //Load teams
        Tournament tournament = new Tournament(name, "combo", teamNames.length, numRounds);
        for (int i = 0; i < teamNames.length; i++) {
            tournament.addTeam(new Team(teamNames[i]));
            Log.i("CREATE_COMBO", "Add team: " + teamNames[i] + "  to position: " + i + "\n");
        }

        //Create the round robin rounds
        for (int i = 0; i < teamNames.length - 1; i++) {
            tournament.addRound(teamNames.length - i - 1);
            Log.i("CREATE_COMBO", "Added team " + teamNames[i] + " to position " + i);
        }

        //Assign round robin rounds
        for (int i = 0; i < teamNames.length - 1; i++) {// round loop
            Log.i("CREATE_COMBO", "Creating round " + i + " with " + tournament.getRound(i).getNumMatches() + " matches");

            for (int j = i + 1; j < teamNames.length; j++) {// match loop
                Log.i("CREATE_COMBO", "Added match at round " + i + " between " + tournament.getTeam(i).getName() + " and " + tournament.getTeam(i + 1).getName());
                tournament.addMatch(i, tournament.getTeam(i), tournament.getTeam(j));
            }

        }

        for (int i = numKnockoutRounds - 1; i >= 0; i--) {
            tournament.addRound((int) Math.pow(2, i));
        }

        Log.i("CREATE_COMBO", "Combo tournament has " + numRounds + " rounds");
        return tournament;

    }

    /**
     * A factory to create the required type of tournament.
     * @param name
     * @param teamNames
     * @param type
     * @return
     */
    private Tournament createTournament(String name, String[] teamNames, String type) {

        int numTeams = teamNames.length;
        Team[] teamList = new Team[numTeams];

        Tournament tournament;

        // Defaults to combo
        if (type.equals("Round Robin"))
            tournament = createRoundRobin(name, teamNames);
        else if (type.equals("Knockout"))
            tournament = createKnockout(name, teamNames);
        else
            tournament = createCombo(name, teamNames);

        return tournament;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
            return true;

        else if (id == R.id.action_howtouse) {
            Intent intent = new Intent(getApplicationContext(), HowToUse.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

}
