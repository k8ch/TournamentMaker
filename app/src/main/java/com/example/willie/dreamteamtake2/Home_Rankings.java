package com.example.willie.dreamteamtake2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.util.TypedValue;
import android.view.Gravity;

/**
 * Displays team rankings, organized by best team to worst team.
 *
 * @author Daniel Bloor (Code)
 * @author Willie Ausrotas (UI)
 *
 */
public class Home_Rankings extends Fragment {

    private View container;
    private LayoutInflater inflater;
    private ViewGroup parentView;
    private TableLayout table;

    private Tournament tournament;

    private String teams[];
    private int teamsStats[][];

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Store view parent ViewGroup container for later use
        this.parentView = container;

        // Inflate tab overview layout
        this.container = inflater.inflate(R.layout.home_tab_rankings, container, false);
        this.inflater = inflater;
        this.context = this.container.getContext();

        // Load tournament data
        this.tournament = new Tournament("tournamentData.txt", context);

        // Initializes teams, sort them and display them
        initializeVariables();
        sortTeams();
        displayTeams();

        return this.container;
    }

    // Initializes teams & team stats from database
    private void initializeVariables() {

        table = (TableLayout) this.container.findViewById(R.id.statTable);

        teamsStats = new int[tournament.getNumTeams()][4];
        teams = new String[tournament.getNumTeams()];

        // Fetch every team + team stats from database
        for (int i = 0; i < tournament.getNumTeams(); i++) {
            teamsStats[i] = tournament.getTeamStats(i);
            teams[i] = tournament.getTeam(i).getName();
        }

    }

    // Works out the points from the wins and losses for each team for when they're printed into the table
    private int calculatePoints(int[] teamStats) {
        int points = 0;

        points = ((teamStats[2] * 3) + teamStats[3]);

        return points;
    }

    /**
     * Since the teams aren't sorted for points beforehand (since the points
     * are being calculated in this file instead), the teams then need to
     * be sorted into descending order of points
     */
    public void sortTeams() {
        int temp[]; // For the statistics
        String temp2; // For the team name
        for (int i = 1; i < tournament.getNumTeams(); i++) {
            for (int j = i; j > 0; j--) {
                if (calculatePoints(teamsStats[j]) > calculatePoints(teamsStats[j - 1])) { // Calculates the points from the wins and losses
                    temp = teamsStats[j]; // Temporarily stores the team statistics
                    temp2 = teams[j]; // Temporarily stores the team name
                    teamsStats[j] = teamsStats[j-1]; // Shifts the higher point team statistics across towards the beginning
                    teams[j] = teams[j-1]; // Shifts the team name across towards the beginning to match the statistics
                    teamsStats[j-1] = temp; // Puts the team statistics across to where the other team statistics moved from
                    teams[j-1] = temp2; // Puts the team name across to where the other team name moved from
                }
            }
        }
    }

    private void displayTeams() {

        // For each team, create and display a table row
        for (int i = 0; i < tournament.getNumTeams(); i++) {

            // Initialize layout objects
            TableRow row = new TableRow(context);

            TextView name = new TextView(context);
            TextView wins = new TextView(context);
            TextView loss= new TextView(context);
            TextView points = new TextView(context);

            // Create separator
            TableRow separator = new TableRow(context);
            View line = new View(context);
            int separatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
            TableRow.LayoutParams separatorLayoutParams = new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, separatorHeight);
            separatorLayoutParams.setMargins(0, 40, 0, 0);
            separatorLayoutParams.span = 4;
            line.setBackgroundColor(0xFFdddddd);
            separator.addView(line, separatorLayoutParams);

            // Add margin to rows
            TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
            tableRowParams.setMargins(0, 40, 0, 0);
            row.setLayoutParams(tableRowParams);

            // Team name + rank
            name.setText((i + 1) + ". " + teams[i]);
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            name.setTextColor(0xFF000000);
            name.setLayoutParams(new TableRow.LayoutParams(0));
            row.addView(name);

            // Number of wins
            wins.setText(teamsStats[i][2] + "");
            wins.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            wins.setTextColor(0xFF000000);
            wins.setGravity(Gravity.CENTER);
            wins.setLayoutParams(new TableRow.LayoutParams(1));
            row.addView(wins);

            // Number of losses
            loss.setText(teamsStats[i][3] + "");
            loss.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            loss.setTextColor(0xFF000000);
            loss.setGravity(Gravity.CENTER);
            loss.setLayoutParams(new TableRow.LayoutParams(2));
            row.addView(loss);

            // Number of points
            points.setText(String.valueOf(calculatePoints(teamsStats[i])));
            points.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            points.setTextColor(0xFF000000);
            points.setGravity(Gravity.CENTER);
            points.setLayoutParams(new TableRow.LayoutParams(3));
            row.addView(points);

            // Add & display row to user
            table.addView(row);
            table.addView(separator);

        }

    }

}