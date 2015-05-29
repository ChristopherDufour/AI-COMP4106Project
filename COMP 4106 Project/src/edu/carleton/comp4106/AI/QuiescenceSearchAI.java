package edu.carleton.comp4106.AI;

import java.util.Random;

import edu.carleton.comp4106.game.ConnectFour;
import edu.carleton.comp4106.game.ConnectFour.GameState;
import edu.carleton.comp4106.game.ConnectFour.Player;
import edu.carleton.comp4106.game.FullColumnException;
import edu.carleton.comp4106.game.InvalidBoardLocationException;

public class QuiescenceSearchAI implements AI {
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
	private QuiescenceSearchAI() {
	}

	// Create an AI for a some player, using some heuristic
	public QuiescenceSearchAI(Player player, Heuristic heuristic) {
		this.player = player;
		this.heuristic = heuristic;
		currentNode = null;
		maxDepth = 3;
	}

	// Same as above, but allows for custom max depth
	public QuiescenceSearchAI(Player player, Heuristic heuristic, int maxDepth) {
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
		currentNode = currentNode.getChildForState(state);
		int bestMoveIndex = -1;
		int value = Integer.MIN_VALUE;
		Random r = new Random();
		for (int i = 0; i < currentNode.getChildrenLength(); i++) {
			int currentValue = miniMax(currentNode.getChild(i), maxDepth, false);
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
		int bestMove = currentNode.getMove(bestMoveIndex);
		currentNode = currentNode.getChild(bestMoveIndex);

		try {
			state.playPiece(bestMove);
		} catch (InvalidBoardLocationException | FullColumnException e) {
			e.printStackTrace();
		}
		return state;
	}

	private int miniMax(Node node, int depth, boolean givenExtra) {
		if (node.getData().getCurrentGameState() != GameState.RUNNING)
			return node.getValue();
		if (node.getChildrenLength() < 1) {
			return node.getValue();
		}

		if (depth == 0) {
			// If a node is noisy then evaluate it at greater depth
			// Main purpose of Quiescence Search
			if ((node.getValue() < 1000 && node.getValue() > -1000)
					|| node.getValue() >= 10000 || node.getValue() <= -10000) {
				return node.getValue();
			} else {
				if (!givenExtra) {
					depth += 2;
					givenExtra = true;
				} else {
					return node.getValue();
				}
			}
		}

		int bestValue = 0;
		// Get the state with the best value for this player
		if (node.getData().getCurrentTurn() == player) {
			bestValue = Integer.MIN_VALUE;

			for (int i = 0; i < node.getChildrenLength(); i++) {
				int value = miniMax(node.getChild(i), depth - 1, givenExtra);
				bestValue = Math.max(value, bestValue);
			}
			return bestValue;
		}
		// Evaluate the state with the best value for the opponent player
		// This is the min (the state with the worst value for the current
		// player)
		bestValue = Integer.MAX_VALUE;
		for (int i = 0; i < node.getChildrenLength(); i++) {
			int value = miniMax(node.getChild(i), depth - 1, givenExtra);
			bestValue = Math.min(value, bestValue);
		}
		return bestValue;
	}

	@Override
	public int nodeCount() {
		return currentNode.nodeCount();
	}

	@Override
	public int totalNodeCount() {
		return originalNode.nodeCount();
	}

	@Override
	public void reinitilize() {
		currentNode = null;
		originalNode = null;
	}

	@Override
	public String getName() {
		return "Quiescence Search AI";
	}

}
