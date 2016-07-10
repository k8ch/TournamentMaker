package com.example.willie.dreamteamtake2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;
import android.content.Context;
import java.io.InputStreamReader;
import android.util.Log;

/**
 * Tournament object containing all necessary methods for creating, managing and editing a
 * tournament complete with rounds, matches and teams.
 * 
 * @author Willem Kowal
 * @author Anatolie Diordita (modifications to write/save methods)
 *
 */
public class Tournament {
	private int numTeams, numRounds;
	private Team teamList[];
	private Round rounds[];
	private String name;
	private String type;

	private Team oddOneOut;

	/**
	 * Create an empty Tournament with the specified name, number of teams, and number of rounds.
	 * 
	 * @param name
	 * @param numTeams
	 * @param numRounds
	 */
	public Tournament(String name, String type, int numTeams, int numRounds) {
		this.name = name;
		this.type = type;
		this.numTeams = numTeams;
		this.numRounds = numRounds;
		teamList = new Team[numTeams];
		rounds = new Round[numRounds];
	}

	/**
	 * Loads a pre-existing tournament from the designated .txt file.
	 * 
	 * @param fileLocation
	 */
	public Tournament(String fileLocation, Context context) {

		try {

			int lineNum, numMatches, score1, score2;
			Team team1, team2;

			Log.i("READ_FILE", "Starting file read...");
			FileInputStream reader = context.openFileInput(fileLocation);

			InputStreamReader inputStreamReader = new InputStreamReader(reader);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			StringBuilder sb = new StringBuilder();
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line + "\n");
			}

			Log.i("READ_FILE", "File read successful.");
			Log.i("READ_FILE", "------------------------------------------------------");
			//Log.i("READ_FILE", sb.toString());

			String[] lines = sb.toString().split("\n");

			name = lines[0];
			type = lines[1];
			numTeams = Integer.parseInt(lines[2]);
			numRounds = Integer.parseInt(lines[3]);

			Log.i("READ_TOUR", "Loading \""+type+"\" tournament \""+name+"\" ("+numTeams+" teams, "+numRounds+" rounds)");

			teamList = new Team[numTeams];
			rounds = new Round[numRounds];

			// Reading teams (start at line 5)
			lineNum = 4;

			for ( int i = 0; i < numTeams; i++ ) {

				Log.i("READ_TOUR", "Loaded team ["+i+"]");

				teamList[i] = new Team(lines[lineNum]);
				lineNum++;
			}

			// Reading matches
			for ( int i = 0; i < numRounds; i++ ) {
				numMatches = Integer.parseInt(lines[lineNum]);
				lineNum++;

				Log.i("READ_TOUR", "Loading match ["+numMatches + "]...");

				rounds[i] = new Round(numMatches);

				for ( int j = 0; j < numMatches; j++ ) {

					int temp1 = Integer.parseInt(lines[lineNum]);
					lineNum++;

					Log.i("READ_TOUR", "  Team 1 with ID "+temp1);

					if ( temp1 != -1 ) {

						int temp2 = Integer.parseInt(lines[lineNum]);
						lineNum++;

						Log.i("READ_TOUR", "  Team 2 with ID "+temp2);

						team1 = teamList[temp1];
						team2 = teamList[temp2];

						score1 = Integer.parseInt(lines[lineNum]);
						lineNum++;

						Log.i("READ_TOUR", "  Score 1: "+score1);

						if ( score1 == -1 )
							rounds[i].addMatch(new Match(team1, team2));
						else {
							score2 = Integer.parseInt(lines[lineNum]);
							lineNum++;

							Log.i("READ_TOUR", "  Score 2: "+score2);

							rounds[i].addMatch(new Match(team1, team2, score1, score2));
						}

					}

				}

			}

			reader.close();
			Log.i("READ_FILE", "------------------------------------------------------");

		} catch ( Exception e ) {}

	}

	/**
	 * Getter
	 *
	 * @return the number of teams.
	 */
	public int getNumTeams() {
		return numTeams;
	}

	/**
	 * Getter
	 *
	 * @return the tournament name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter
	 *
	 * @return the tournament type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Getter
	 * 
	 * @return the number of rounds.
	 */
	public int getNumRounds() {
		return numRounds;
	}

	/**
	 * Adds a team to the tournament. Stores in an array of size [numTeams] with placement in the first
	 * available slot.
	 * 
	 * @param team
	 * @return True if successful, false if not.
	 */
	public boolean addTeam(Team team) {
		if (team != null) {
			for (int i = 0; i < numTeams; i++) {
				if (teamList[i] == null) {
					teamList[i] = team;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Creates a new new with the provided name and adds it to the tournament. Stores in an array of size
	 * [numTeams] with placement in the first available slot.
	 * 
	 * @param name
	 * @return True if successful, false if not.
	 */
	public boolean addTeam(String name) {
		if (name != null)
			for (int i = 0; i < numTeams; i++) {
				if (teamList[i] == null) {
					teamList[i] = new Team(name);
					return true;
				}
			}
		return false;
	}

	/**
	 * Getter
	 * 
	 * @param index
	 * @return The team at the specified index.
	 */
	public Team getTeam(int index) {
		if (index < 0 || index > numTeams - 1)
			throw new IndexOutOfBoundsException("If you want a message, put it here");
		return teamList[index];
	}

	/**
	 * 
	 * @param team
	 * @return The team's position in the array.
	 */
	public int getTeamIndex(Team team) {
		return java.util.Arrays.asList(teamList).indexOf(team);
	}

	/**
	 * Replaces the team at the specified index with the new team. Requires that the index contains an
	 * existing team.
	 * 
	 * @param index
	 *            Integer value representing the target team's location in the array.
	 * @param team
	 *            The new team to be added to the tournament.
	 * @return True if successful, false if not.
	 */
	public boolean editTeam(int index, Team team) {
		if (teamList[index] == null)
			return false;
		if (index < 0 || index > numTeams - 1)
			throw new IndexOutOfBoundsException("If you want a message, put it here");
		teamList[index] = team;
		return true;
	}

	/**
	 * Saves the tournament to a .txt file. Will overwrite any existing tournaments with the same name.
	 * 
	 * @return True if successful, false if not.
	 */
	public boolean saveTournament(Context context) {
		//System.out.println(name);
		try {

			FileOutputStream write = context.openFileOutput("tournamentData.txt", Context.MODE_PRIVATE);

			//BufferedWriter write = new BufferedWriter(new FileWriter(name + ".txt"));

			write.write( (name+"\n").getBytes() );
			write.write( (type+"\n").getBytes() );
			write.write( (numTeams+"\n").getBytes() );
			write.write( (numRounds+"\n").getBytes() );

			for (int i = 0; i < numTeams; i++) {
				write.write( (teamList[i].getName()+"\n").getBytes() );
			}
			int score1;

			for (int i = 0; i < numRounds; i++) {

				write.write((rounds[i].getNumMatches()+"\n").getBytes());

				for (int j = 0; j < rounds[i].getNumMatches(); j++) {

					if ( rounds[i].getMatch(j) != null ) {

						write.write((java.util.Arrays.asList(teamList)
								.indexOf(rounds[i].getMatch(j).getTeam1()) + "\n").getBytes());

						write.write((java.util.Arrays.asList(teamList)
								.indexOf(rounds[i].getMatch(j).getTeam2()) + "\n").getBytes());

						score1 = rounds[i].getMatch(j).getTeam1Goals();

						if (score1 == -1) {
							write.write((-1 + "\n").getBytes());
						} else {
							write.write((score1 + "\n").getBytes());
							write.write((rounds[i].getMatch(j).getTeam2Goals() + "\n").getBytes());
						}
					} else
						write.write((-1 + "\n").getBytes());
				}
			}
			write.close();
		} catch (Exception e) {

		}
		return false;
	}

	/**
	 * Inserts the round into the next available space in the rounds list.
	 * 
	 * @param round
	 * @return True if successful, false if not.
	 */
	public boolean addRound(Round round) {
		if (round != null)
			for (int i = 0; i < numRounds; i++) {
				if (rounds[i] == null) {
					rounds[i] = round;
					return true;
				}
			}
		return false;
	}

	/**
	 * Creates a new Round and inserts it into the next available space in the rounds list.
	 * 
	 * @param numMatches
	 * @return True if successful, false if not.
	 */
	public boolean addRound(int numMatches) {
		if (numMatches > 0)
			for (int i = 0; i < numRounds; i++) {
				if (rounds[i] == null) {
					rounds[i] = new Round(numMatches);
					return true;
				}
			}
		return false;
	}

	/**
	 * Returns the round at given index.
	 *
	 * @param index
	 * @return Returns the round object
	 */
	public Round getRound(int index) {
		Round temp = rounds[index];
		return temp;
	}

	/**
	 * Calculates and returns statistics for the team at the specified index.
	 * 
	 * @param index
	 * @return An array of size 5. int[goals for, goals against, wins, losses, ties];
	 */

	public int[] getTeamStats(int index) {
		Team winner;// A temporary variable used to count the number of wins.
		if (index < 0 || index > numTeams - 1)
			throw new IndexOutOfBoundsException("If you want a message, put it here");
		Match match;
		int stats[] = new int[4];
		for (int i = 0; i < numRounds; i++) {
			for (int j = 0; j < rounds[i].getNumMatches(); j++) {
				if (rounds[i].getMatch(0) != null) {
					match = rounds[i].getMatch(j);
					if (match.getTeam1().equals(teamList[index])) {
						stats[0] += match.getTeam1Goals();
						stats[1] += match.getTeam2Goals();
						try {
							winner = match.getWinner();
							if (winner.equals(teamList[index])) {// If the team won
								stats[2]++;
							} else {// If the team lost
								stats[3]++;
							}
						} catch (IndexOutOfBoundsException e) {// A LOT of out of bounds exceptions will be
							// thrown.

						}
					} else if (match.getTeam2().equals(teamList[index])) {
						stats[0] += match.getTeam2Goals();
						stats[1] += match.getTeam1Goals();
						try {
							winner = match.getWinner();
							if (winner.equals(teamList[index])) {// If the team won
								stats[2]++;
							} else {// If the team lost
								stats[3]++;
							}
						} catch (IndexOutOfBoundsException e) {// A LOT of out of bounds exceptions will be
							// thrown.

						}
					}
				}
			}
		}
			return stats;
	}

	/**
	 * Makes the tournament active and populates the first round with the teams in a random order.
	 * 
	 * @return True if successful, false if not.
	 */
	public boolean randomStart() {
		if (rounds[0].getMatch(0) != null)
			return false;
		int randomizer[] = new int[numTeams];
		int temp, index;
		for (int i = 0; i < numTeams; i++) {
			randomizer[i] = i;
		}
		Random random = new Random();
		for (int i = numTeams - 1; i > 0; i--) {
			index = random.nextInt(i + 1);
			temp = randomizer[i];
			randomizer[i] = randomizer[index];
			randomizer[index] = temp;
		}
		for (int i = 0; i < rounds[0].getNumMatches() * 2; i = i + 2) {
			rounds[0].addMatch(new Match(teamList[randomizer[i]], teamList[randomizer[i + 1]]));
		}
		oddOneOut = teamList[randomizer[teamList.length - 1]];
		return true;
	}

	public Team getOddOneOut() {
		return oddOneOut;
	}

	/**
	 * Sets the score of the specified game.
	 * 
	 * @param roundNum
	 * @param matchNum
	 * @param team1Goals
	 * @param team2Goals
	 * @return True if successful, false if not.
	 */
	public boolean enterScore(int roundNum, int matchNum, int team1Goals, int team2Goals) {
		if (team1Goals < 0 || team2Goals < 0 || roundNum < 0 || roundNum > numRounds || matchNum < 0
				|| matchNum > rounds[roundNum].getNumMatches())
			return false;
		rounds[roundNum].getMatch(matchNum).setTeam1Goals(team1Goals);
		rounds[roundNum].getMatch(matchNum).setTeam2Goals(team2Goals);
		return true;
	}

	/**
	 * Returns the winner of the specified math.
	 * 
	 * @param roundNum
	 * @param matchNum
	 * @return Returns the victorious team. In the even the match was a tie, team1 is returned.
	 */
	public Team getWinner(int roundNum, int matchNum) {
		Team temp = rounds[roundNum].getMatch(matchNum).getWinner();
		if (temp == null)
			return rounds[roundNum].getMatch(matchNum).getTeam1();
		return temp;
	}

	/**
	 * Adds a new match between the two specified teams to the first available spot in the round.
	 * 
	 * @param roundNum
	 * @param team1
	 * @param team2
	 * @return True if successful, false if not.
	 */
	public boolean addMatch(int roundNum, int team1, int team2) {
		if (rounds[roundNum].canAdd()) {
			rounds[roundNum].addMatch(new Match(teamList[team1], teamList[team2]));
			return true;
		}

		return false;
	}

	/**
	 * Adds a new match between the two specified teams to the first available spot in the round.
	 * 
	 * @param roundNum
	 * @param team1
	 * @param team2
	 * @return True if successful, false if not.
	 */
	public boolean addMatch(int roundNum, Team team1, Team team2) {

		if (rounds[roundNum].canAdd()) {
			rounds[roundNum].addMatch(new Match(team1, team2));
			return true;
		}
		return false;

	}

	/**
	 * Gets a team's number of goals in a specific game.
	 * 
	 * @param numRound
	 * @param numMatch
	 * @param numTeam
	 * @return The score of the specified team in the specified match.
	 */
	public int getScore(int numRound, int numMatch, int numTeam) {
		if (teamList[numTeam].equals(rounds[numRound].getMatch(numMatch).getTeam1()))
			return rounds[numRound].getMatch(numMatch).getTeam1Goals();
		else if (teamList[numTeam].equals(rounds[numRound].getMatch(numMatch).getTeam2()))
			return rounds[numRound].getMatch(numMatch).getTeam2Goals();
		return -2;
	}

	/**
	 * Return the progress (out of 100) of a tournament
	 *
	 * @return Tournament progress (out of 100)
	 *
	 * @author Anatolie Diordita
	 */
	public int getProgress() {

		int numRounds = this.getNumRounds();
		double numMatches = 0;
		double numMatchesComplete = 0;

		Round tempRound;
		Match tempMatch;

		// Loop through each round
		for ( int i = 0; i < numRounds; i++ ) {
			tempRound = this.getRound(i);

			numMatches += tempRound.getNumMatches();

			// Loop through each match
			for ( int j = 0; j < tempRound.getNumMatches(); j++ ) {
				tempMatch = tempRound.getMatch(j);

				// Only count completed matches as those that exist/have score
				if (tempMatch != null && tempMatch.getTeam1Goals() != -1)
					numMatchesComplete++;

			}

		}

		return (int)( (numMatchesComplete / numMatches) * 100 );

	}

	public String toString() {
		StringBuffer out = new StringBuffer();
		out.append(name + "\n");
		for (int i = 0; i < numRounds; i++) {
			if (rounds[i] != null)
				out.append(rounds[i].toString());
		}
		return out.toString();
	}
}
