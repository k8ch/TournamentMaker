package com.example.willie.dreamteamtake2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

/**
 * Schedule for upcoming and completed games in tournament.
 *
 * @author Anatolie Diordita (Code + UI)
 * @author Willem Kowal (Code)
 */
public class Home_Schedule extends Fragment {

    private Context context;
    private View container;
    private LayoutInflater inflater;
    private ViewGroup parentView;

    private Tournament tournament;
    public int roundCurrent = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Store view parent ViewGroup container for later use
        this.parentView = container;

        // Inflate tab overview layout
        this.container = inflater.inflate(R.layout.home_tab_schedule, container, false);
        this.inflater = inflater;
        this.context = this.container.getContext();

        // Load tournament data + populate next rounds with matches
        tournament = new Tournament("tournamentData.txt", context);
        populateNextRound(tournament);

        // Check if a request is being made to load a specific round
        Intent extras = getActivity().getIntent();
        int goToRound = extras.getIntExtra("goToRound", 0);

        // Display specific round (else default round)
        if (goToRound != 0) {
            loadRound(goToRound - 1);
            roundCurrent = goToRound - 1;
        } else
            loadRound(0);

        // Previous round button click listener
        FrameLayout btnPrevRound = (FrameLayout) this.container.findViewById(R.id.clickPrev);
        btnPrevRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadRound(roundCurrent - 1))
                    roundCurrent--;
            }
        });

        ImageView chevron_left = (ImageView) this.container.findViewById(R.id.chevron_left);
        chevron_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadRound(roundCurrent - 1))
                    roundCurrent--;
            }
        });

        // Next round button click listener
        FrameLayout btnNextRound = (FrameLayout) this.container.findViewById(R.id.clickNext);
        btnNextRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadRound(roundCurrent + 1))
                    roundCurrent++;
            }
        });

        ImageView chevron_right = (ImageView) this.container.findViewById(R.id.chevron_right);
        chevron_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadRound(roundCurrent - 1))
                    roundCurrent--;
            }
        });

        return this.container;

    }

    private boolean loadRound(int roundNum) {

        final int roundIndex = roundNum;

        // Do not allow to go out of array bounds
        if (roundIndex > (tournament.getNumRounds() - 1) || roundIndex < 0)
            return false;

        // The round doesn't exist or hasn't started yet
        if (tournament.getRound(roundIndex) == null || tournament.getRound(roundIndex).getMatch(0) == null) {

            // Display an error dialog telling the user the round hasn't started yet
            new AlertDialog.Builder(context)
                    .setTitle("Round Not Started")
                    .setMessage("Round " + (roundIndex + 1) + " has not started yet!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .show();

            return false;

        } else { // Round exists/has started, carry on

            int numMatches = tournament.getRound(roundIndex).getNumMatches();
            Round round = tournament.getRound(roundIndex);

            LinearLayout parent = (LinearLayout) this.container.findViewById(R.id.overviewGameList);

            // Clear the parent container from previous round data
            parent.removeAllViews();

            // Set round # textview
            TextView text = (TextView) this.container.findViewById(R.id.roundNum);
            text.setText("Round " + (roundIndex + 1));

            FrameLayout btnNext = (FrameLayout) this.container.findViewById(R.id.clickNext);
            FrameLayout btnPrev = (FrameLayout) this.container.findViewById(R.id.clickPrev);

            // Hide next button if no further rounds available
            if ((roundIndex + 1) > (tournament.getNumRounds() - 1))
                btnNext.setVisibility(View.INVISIBLE);
            else btnNext.setVisibility(View.VISIBLE);

            // Hide prev button if no previous rounds available
            if ((roundIndex - 1) < 0) btnPrev.setVisibility(View.INVISIBLE);
            else btnPrev.setVisibility(View.VISIBLE);

            View container;

            // Display each match from the round
            for (int i = 0; i < numMatches; i++) {

                Match match = round.getMatch(i);

                container = inflater.inflate(R.layout.game_item, null);
                container.setId(i + 1);

                TextView matchNum = (TextView) container.findViewById(R.id.match);
                TextView team1 = (TextView) container.findViewById(R.id.team1);
                TextView team2 = (TextView) container.findViewById(R.id.team2);
                TextView score = (TextView) container.findViewById(R.id.score);

                String score1, score2, name1, name2;

                score1 = score2 = "-";
                name1 = name2 = "";

                // If the match has been completed, insert the match results
                if (match != null) {
                    score1 = match.getTeam1Goals() + "";
                    score2 = match.getTeam2Goals() + "";
                    name1 = match.getTeam1().getName();
                    name2 = match.getTeam2().getName();
                }

                // If the match hasn't been completed, set the score values to dashes
                if (match == null || match.getTeam1Goals() == -1)
                    score1 = score2 = "-";

                // Insert match info into the template
                matchNum.setText("Match " + (i + 1));
                team1.setText(name1);
                team2.setText(name2);
                score.setText(score1 + " : " + score2);

                // Add onClick listener that leads to enter score view
                final int roundIndexTemp = roundIndex;
                final int matchNumTemp = i;

                // If a user clicks the round, take them to an edit page
                container.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if ( tournament.getType().equals("knockout") && roundIndex != (tournament.getNumRounds()-1) && tournament.getRound(roundIndex + 1).isComplete() ) {
                            // Display an error dialog telling the user the round has already finished
                            new AlertDialog.Builder(context)
                                    .setTitle("Round Already Complete")
                                    .setMessage("You cannot edit this match because the round has already completed.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    })
                                    .show();
                        }
                        else {
                            Intent intent = new Intent(context, Match_EditScore.class);
                            intent.putExtra("round", roundIndexTemp);
                            intent.putExtra("match", matchNumTemp);
                            startActivityForResult(intent, 0);
                        }

                    }

                });

                parent.addView(container);

            }

        }

        return true;

    }

    /**
     * Iterates through each round of play, checking if a round of play can be populated with the winners of the previous round.
     *
     * @param tournament
     * @return
     */
    private boolean populateNextRound(Tournament tournament) {

        Log.i("POPULATE", "Populating next round...");

        if (tournament.getType().equals("knockout")) {

            for (int i = 1; i < tournament.getNumRounds(); i++) {

                if (!tournament.getRound(i).isComplete() && tournament.getRound(i - 1).isComplete()) {
                    Log.i("POPULATE", "Populating round " + i + " with teams");
                    knock(i - 1, tournament);
                    return true;
                }
            }
        } else if (tournament.getType().equals("combo")) {

            //Check if ALL round robbins rounds are complete
            for (int i = 0; i < tournament.getNumTeams() - 1; i++) {
                if (!tournament.getRound(i).isComplete()) {
                    Log.i("POPULATE", "Problem: all round robin rounds aren't complete yet!");
                    return false;
                }
            }

            //Check if the first knockout round has been assigned
            if (tournament.getRound(tournament.getNumTeams() - 1).getMatch(0) == null) {//first knock

                Log.i("POPULATE", "Generating first knockout");

                //Sort the teams based on their standings
                Team[] teams = new Team[tournament.getNumTeams()];
                int score[] = new int[tournament.getNumTeams()];
                for (int i = 0; i < tournament.getNumTeams(); i++) {
                    teams[i] = tournament.getTeam(i);
                    score[i] += tournament.getTeamStats(i)[2] * 3;
                    score[i] += tournament.getTeamStats(i)[3];
                }
                for (int i = 0; i < tournament.getNumTeams(); i++) {
                    for (int j = 0; j < tournament.getNumTeams(); j++) {
                        if (score[i] > score[j]) {
                            int temp = score[i];
                            Team tempT = teams[i];
                            score[i] = score[j];
                            teams[i] = teams[j];
                            score[j] = temp;
                            teams[j] = tempT;
                        }
                    }
                }

                //Assign matches
                int currRound = tournament.getNumTeams() - 1, j = 0;
                for (int i = 0; i < tournament.getRound(currRound).getNumMatches(); i++) {
                    Log.i("POPULATE", "Adding math between " + teams[j].toString() + " and " + teams[j + 1] + "in round " + currRound);

                    tournament.addMatch(currRound, teams[j], teams[j + 1]);
                    j = j + 2;
                }

                tournament.saveTournament(context);
                return true;

            } else { //continue knock

                for (int i = tournament.getNumTeams() - 1; i < tournament.getNumRounds(); i++) {
                    if (!tournament.getRound(i).isComplete() && tournament.getRound(i - 1).isComplete()) {
                        Log.i("POPULATE", "Populating round " + i + "with teams");
                        knock(i - 1, tournament);
                        return true;
                    }
                }

            }
        }
        return false;

    }

    /**
     * Populates the 'currRound' with the winning teams from the previous round.
     *
     * @param currRound
     * @param tournament
     */
    private void knock(int currRound, Tournament tournament) {
        int numMatches = tournament.getRound(currRound + 1).getNumMatches();
        int j = 0;

        Log.i("KNOCK", "Populating round " + currRound + " with " + numMatches + " matches");

        if (tournament.getRound(currRound).getNumMatches() != 1) {//Checks if this is the final round of play
            for (int i = 0; i < numMatches; i++) {
                Log.i("KNOCK", "Adding math between " + tournament.getWinner(currRound, j).toString() + " and " + tournament.getWinner(currRound, j + 1));

                tournament.addMatch(currRound + 1, tournament.getWinner(currRound, j), tournament.getWinner(currRound, j + 1));
                j = j + 2;
            }
        } else if (tournament.getRound(currRound - 1).getNumMatches() == 3) {//Check s the special case where there are 3 teams left in the tournament
            Log.i("KNOCK", "Creating final round with oddOut");
            tournament.addMatch(currRound + 1, tournament.getWinner(currRound, 0), tournament.getRound(currRound - 1).getMatch(2).getWinner());
        }

        tournament.saveTournament(context);

    }

}