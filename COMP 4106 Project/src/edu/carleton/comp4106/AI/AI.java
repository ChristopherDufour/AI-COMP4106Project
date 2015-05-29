package edu.carleton.comp4106.AI;

import edu.carleton.comp4106.game.ConnectFour;

// AI interface - Sets standard public functions that all AI need to use
public interface AI {
	// Takes a game object and returns a new game with their move performed on
	// it
	public ConnectFour determineMove(ConnectFour game);

	// Reset the AI so it can play a new game
	public void reinitilize();

	// Return the current node count
	public int nodeCount();

	// Return the total node count
	public int totalNodeCount();

	// Return the name of the AI
	public String getName();
}
