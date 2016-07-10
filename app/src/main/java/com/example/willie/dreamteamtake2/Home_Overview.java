package com.example.willie.dreamteamtake2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.content.Context;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.util.Log;

/**
 * Overview screen for tournament list.
 *
 * @author Anatolie Diordita (Code + UI)
 *
 */
public class Home_Overview extends Fragment {

    public Tournament tournament;
    private int roundCurrent = 0;

    private Context context;
    private View container;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate tab overview layout
        this.container = inflater.inflate(R.layout.home_tab_overview, container, false);
        this.inflater = inflater;
        this.context = this.container.getContext();

        // Load tournament data
        tournament = new Tournament("tournamentData.txt", context);

        TextView tournamentName = (TextView) this.container.findViewById(R.id.name);
        tournamentName.setText(tournament.getName());

        TextView tournamentComplete = (TextView) this.container.findViewById(R.id.complete);
        tournamentComplete.setText(tournament.getProgress() + "% Complete");

        ProgressBar tournamentProgress = (ProgressBar) this.container.findViewById(R.id.progress);
        tournamentProgress.setProgress(tournament.getProgress());

        // Set tournament type in header
        TextView tournamentType = (TextView) this.container.findViewById(R.id.type);
        String typeDisplay;

        // Load first round into overview
        loadRound(0);

        // Set value of "type" TextView accordingly
        switch (tournament.getType()) {
            case "knockout": typeDisplay = "Knockout";
                break;
            case "roundrobin": typeDisplay = "Round Robin";
                break;
            case "combo": typeDisplay = "Combination";
                break;
            default: typeDisplay = "Tournament";
                break;
        }
        tournamentType.setText(typeDisplay);

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

    private boolean loadRound( int roundIndex ) {

        // Do not allow to go out of array bounds
        if ( roundIndex > ( tournament.getNumRounds() - 1 ) || roundIndex < 0 )
            return false;

        // The round doesn't exist or hasn't started yet
        if ( tournament.getRound(roundIndex) == null || tournament.getRound(roundIndex).getMatch(0) == null ) {

            // Display an error dialog telling the user the round hasn't started yet
            new AlertDialog.Builder(context)
                    .setTitle("Round Not Started")
                    .setMessage("Round " + (roundIndex + 1) + " has not started yet!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) { }
                    })
                    .show();

            return false;

        } else { // Round exists/has started, carry on

            int numMatches = tournament.getRound(roundIndex).getNumMatches();
            Round round = tournament.getRound(roundIndex);

            LinearLayout parent = (LinearLayout) this.container.findViewById(R.id.overviewGameList);

            parent.removeAllViews();

            TextView text = (TextView) this.container.findViewById(R.id.roundNum);
            text.setText("Round " + (roundIndex + 1));

            FrameLayout btnNext = (FrameLayout) this.container.findViewById(R.id.clickNext);
            FrameLayout btnPrev = (FrameLayout) this.container.findViewById(R.id.clickPrev);

            // Hide next button if no further rounds available
            if ( (roundIndex + 1) > (tournament.getNumRounds() - 1) ) btnNext.setVisibility(View.INVISIBLE);
            else btnNext.setVisibility(View.VISIBLE);

            // Hide prev button if no previous rounds available
            if ( (roundIndex - 1) < 0 ) btnPrev.setVisibility(View.INVISIBLE);
            else btnPrev.setVisibility(View.VISIBLE);

            // Display each match from the round
            for ( int i = 0; i < numMatches; i++ ) {

                Match match = round.getMatch(i);

                // Inflate game item template
                View container = inflater.inflate(R.layout.game_item, null);
                LinearLayout gameTemplate = (LinearLayout) container.findViewById(R.id.gameTemplate);

                TextView matchNum = (TextView) container.findViewById(R.id.match);
                TextView team1 = (TextView) container.findViewById(R.id.team1);
                TextView team2 = (TextView) container.findViewById(R.id.team2);
                TextView score = (TextView) container.findViewById(R.id.score);

                // Set the template's ID to the game ID
                gameTemplate.setId(i);

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

                // Add match template into parent view
                parent.addView(gameTemplate);

            }

        }

        return true;

    }

}