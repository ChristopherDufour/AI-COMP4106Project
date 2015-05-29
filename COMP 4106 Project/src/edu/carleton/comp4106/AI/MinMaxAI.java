package edu.carleton.comp4106.AI;

import java.util.Random;

import edu.carleton.comp4106.game.ConnectFour;
import edu.carleton.comp4106.game.ConnectFour.GameState;
import edu.carleton.comp4106.game.ConnectFour.Player;
import edu.carleton.comp4106.game.FullColumnException;
import edu.carleton.comp4106.game.InvalidBoardLocationException;

// Basic MiniMax AI
public class MinMaxAI implements AI {
	// The node we are currently evaluating
	private Node currentNode;
	// The root node, used to determine the total number of nodes
	private Node originalNode;
	// The max depth a node can search
	private int maxDepth;
	// The colour of the player
	private Player player;
	// The heuristic function used to evaluate states
	private Heuristic heuristic;

	// To prevent empty construction
	@SuppressWarnings("unused")
	private MinMaxAI() {
	}

	// Create an AI for a some player, using some heuristic
	public MinMaxAI(Player player, Heuristic heuristic) {
		this.player = player;
		this.heuristic = heuristic;
		currentNode = null;
		maxDepth = 3;
	}

	// Same as above, but allows for custom max depth
	public MinMaxAI(Player player, Heuristic heuristic, int maxDepth) {
		this.player = player;
		this.heuristic = heuristic;
		currentNode = null;
		this.maxDepth = maxDepth;
	}

	// Main function that performs the next move on the game object
	@Override
	public ConnectFour determineMove(ConnectFour state) {
		// Check if this is the first node
		if (currentNode == null) {
			currentNode = new Node(state, heuristic, player);
			originalNode = currentNode;
		}
		// If the opponent made a move we will find that state
		currentNode = currentNode.getChildForState(state);

		int bestMoveIndex = -1;
		int value = Integer.MIN_VALUE;
		Random r = new Random();
		// Determine which of the available direction allows for the best move
		for (int i = 0; i < currentNode.getChildrenLength(); i++) {
			int currentValue = miniMax(currentNode.getChild(i), maxDepth);
			if (currentValue > value) {
				bestMoveIndex = i;
				value = currentValue;
			} else if (currentValue == value) {
				if (r.nextInt(2) == 0) {
					bestMoveIndex = i;
					value = currentValue;
				}
			}
		}
		// Get the row number of the best move
		int bestMove = currentNode.getMove(bestMoveIndex);
		currentNode = currentNode.getChild(bestMoveIndex);

		// Play the move and return the new state
		try {
			state.playPiece(bestMove);
		} catch (InvalidBoardLocationException | FullColumnException e) {
			e.printStackTrace();
		}
		return state;
	}

	// Minimax function that returns how valuable a node is
	private int miniMax(Node node, int depth) {
		// if the node has no children, has hit max depth or the game isn't
		// running return this node's value
		if (depth == 0
				|| node.getData().getCurrentGameState() != GameState.RUNNING)
			return node.getValue();
		if (node.getChildrenLength() < 1) {
			return node.getValue();
		}

		int bestValue = 0;
		// Get the state with the best value for this player
		if (node.getData().getCurrentTurn() == player) {
			bestValue = Integer.MIN_VALUE;

			for (int i = 0; i < node.getChildrenLength(); i++) {
				int value = miniMax(node.getChild(i), depth - 1);
				bestValue = Math.max(value, bestValue);
			}
			return bestValue;
		}
		// Evaluate the state with the best value for the opponent player
		// This is the min (the state with the worst value for the current
		// player)
		bestValue = Integer.MAX_VALUE;
		for (int i = 0; i < node.getChildrenLength(); i++) {
			int value = miniMax(node.getChild(i), depth - 1);
			bestValue = Math.min(value, bestValue);
		}
		return bestValue;
	}

	// Return the current node count
	@Override
	public int nodeCount() {
		return currentNode.nodeCount();
	}

	// Return the total node count
	@Override
	public int totalNodeCount() {
		return originalNode.nodeCount();
	}

	// Reset the AI to play a different game
	@Override
	public void reinitilize() {
		currentNode = null;
		originalNode = null;
	}

	// Returns the AIs name
	@Override
	public String getName() {
		return "Mini Max AI";
	}
}
