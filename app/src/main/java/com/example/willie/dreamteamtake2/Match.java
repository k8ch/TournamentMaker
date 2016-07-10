package com.example.willie.dreamteamtake2;

/**
 * Stores a single 'game' that consists of two teams and their eventual scores.
 * 
 * @author Willem Kowal
 *
 */
public class Match {
	private Team team1, team2;
	private int team1Goals, team2Goals;

	/**
	 * Creates a new Match between team1 and team2. Initializes the teams' scores to -1.
	 * 
	 * @param team1
	 * @param team2
	 */
	public Match(Team team1, Team team2) {
		this.team1 = team1;
		this.team2 = team2;
		team1Goals = -1;
		team2Goals = -1;
	}

	/**
	 * Creates a new Match between team1 and team2 and records their scores.
	 * 
	 * @param team1
	 * @param team2
	 */
	public Match(Team team1, Team team2, int team1Goals, int team2Goals) {
		this.team1 = team1;
		this.team2 = team2;
		this.team1Goals = team1Goals;
		this.team2Goals = team2Goals;
	}

	/**
	 * Getter
	 * 
	 * @return Team1
	 */
	public Team getTeam1() {
		return team1;
	}

	/**
	 * Getter
	 * 
	 * @return Teams2
	 */
	public Team getTeam2() {
		return team2;
	}

	/**
	 * Getter
	 * 
	 * @return The number of goals scored by team1.
	 */
	public int getTeam1Goals() {
		return team1Goals;
	}

	/**
	 * Sets the number of goals scored by team1.
	 * 
	 * @param team1Goals
	 */
	public void setTeam1Goals(int team1Goals) {
		if (team1Goals < 0)
			throw new IndexOutOfBoundsException("Positive scores only please.");
		this.team1Goals = team1Goals;
	}

	/**
	 * Getter
	 * 
	 * @return The number of goals scored by team2.
	 */
	public int getTeam2Goals() {
		return team2Goals;
	}

	/**
	 * Sets the number of goals scored by team2.
	 * 
	 * @param team2Goals
	 */
	public void setTeam2Goals(int team2Goals) {
		if (team2Goals < 0)
			throw new IndexOutOfBoundsException("Positive scores only please.");
		this.team2Goals = team2Goals;
	}

	/**
	 * Returns the winner of the match. If the game has not been played, and outOfBounds exception is thrown.
	 * If the match was a tie, null if returned.
	 * 
	 * @return winning team or null if a tie.
	 */
	public Team getWinner() {
		if (team1Goals < 0 || team2Goals < 0)
			throw new IndexOutOfBoundsException("No scores have been entered.");
		else if (team1Goals > team2Goals)
			return team1;
		else if (team2Goals > team1Goals)
			return team2;
		else
			return null;
	}

	public String toString() {

		return team1.toString() + " goals;  " + getTeam1Goals() + "\n" + team2.toString()
				+ " goals;  " + getTeam2Goals() + "\n\n";

	}
}
