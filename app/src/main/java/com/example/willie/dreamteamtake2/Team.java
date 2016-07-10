package com.example.willie.dreamteamtake2;

/**
 * A team within the tournament. To keep things simple, each team is assigned a name.
 * 
 * @author Willem Kowal
 *
 */
public class Team {
	private String name;

	/**
	 * Creates a new team with default values of color=0 and name ="Enter a name".
	 */
	public Team() {
		name = "Enter a name";
	}

	/**
	 * Creates a team with the specified name.
	 * 
	 * @param name
	 */
	public Team(String name) {
		this.name = name;
	}

	/**
	 * Getter
	 * 
	 * @return The team's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the team's name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns a string representation of the class.
	 */
	public String toString() {
		return "Team name: " + name;
	}

}
