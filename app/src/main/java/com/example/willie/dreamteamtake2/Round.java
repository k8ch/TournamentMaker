package com.example.willie.dreamteamtake2;

/**
 * Stores a round of matches within a tournament. Example: semi-finals.
 * 
 * @author Willem Kowal
 *
 */
public class Round {
	private int numMatches;
	private Match matches[];
	private boolean active = false;

	/**
	 * Create a new round with the specified number of matches.
	 * 
	 * @param numMatches
	 */
	public Round(int numMatches) {
		this.numMatches = numMatches;
		matches = new Match[numMatches];
	}

	/**
	 * Getter
	 * 
	 * @return The number of matches in this round.
	 */
	public int getNumMatches() {
		return numMatches;
	}

	/**
	 * Retrieves the match at the specified index.
	 * 
	 * @param index
	 * @return The specified Match.
	 */
	public Match getMatch(int index) {
		if (index < 0 || index > numMatches)
			throw new IndexOutOfBoundsException("Real games only please.");
		return matches[index];
	}

	/**
	 * Adds the match to the round in the first available slot.
	 * 
	 * @param match
	 * @return True if successful, false if not.
	 */
	public boolean addMatch(Match match) {
		if (active)
			return false;
		for (int i = 0; i < numMatches; i++) {
			if (matches[i] == null) {
				matches[i] = match;
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds the match at the specified index.
	 * 
	 * @param match
	 * @param index
	 * @return True if successful, false if not. If a match already exists at the specified index, the method
	 *         returns false.
	 */
	public boolean addMatch(Match match, int index) {
		if (index < 0 || index > numMatches - 1)
			throw new IndexOutOfBoundsException("That is not a valid index.");
		if (active || matches[index] != null)
			return false;
		matches[index] = match;
		return true;
	}

	/**
	 * Replaces the match at the specified index.
	 * 
	 * @param newMatch
	 * @param index
	 * @return True if successful, false if not. If a match does not already exists at the specified index,
	 *         the method returns false.
	 */
	public boolean replaceMatch(Match newMatch, int index) {
		if (index < 0 || index > numMatches - 1)
			throw new IndexOutOfBoundsException("That is not a valid index.");
		if (active || matches[index] == null)
			return false;
		matches[index] = newMatch;
		return true;
	}

	/**
	 * Make the round 'active' this disables editing of matches.
	 * 
	 * @return True if successful, false if not.
	 */
	public boolean setActive() {
		if (active)
			return false;
		active = true;
		return true;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isComplete() {
		for (int i = 0; i < numMatches; i++) {
			if (matches[i] == null || matches[i].getTeam1Goals() == -1) {
				return false;
			}
		}
		return true;
	}

	public boolean canAdd() {
		for (int i = 0; i < numMatches; i++ ) {
			if ( matches[i] == null )
					return true;
		}
		return false;
	}

	public String toString() {

		String output = "";
		output += "Round with " + numMatches + " matches\n";
		for (int i = 0; i < numMatches; i++) {

			if (matches[i] != null) {
				output += matches[i].toString() + "\n\n";
			}
		}

		return output;
	}
}
