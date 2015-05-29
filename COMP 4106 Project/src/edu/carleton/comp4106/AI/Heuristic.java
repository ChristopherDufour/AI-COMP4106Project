package edu.carleton.comp4106.AI;

import edu.carleton.comp4106.game.ConnectFour;
import edu.carleton.comp4106.game.ConnectFour.Player;

// Heuristic interface - allows for multiple different heuristics to operate with AI objects
public interface Heuristic {
	// Return the int value of a game state relative to some player
	public int evaluateGame(ConnectFour game, Player player);
}
