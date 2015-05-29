package edu.carleton.comp4106.AI;

import java.util.Random;

import edu.carleton.comp4106.game.ConnectFour;
import edu.carleton.comp4106.game.ConnectFour.GameState;
import edu.carleton.comp4106.game.ConnectFour.Player;
import edu.carleton.comp4106.game.FullColumnException;
import edu.carleton.comp4106.game.InvalidBoardLocationException;

public class AlphaBetaAI implements AI {

	private Node currentNode;
	private Node originalNode;
	private int maxDepth;
	private Player player;
	private Heuristic heuristic;
	private final Node MIN_NODE = new Node(Integer.MIN_VALUE);
	private final Node MAX_NODE = new Node(Integer.MAX_VALUE);

	@SuppressWarnings("unused")
	private AlphaBetaAI() {
	}

	public AlphaBetaAI(Player player, Heuristic heuristic) {
		this.player = player;
		this.heuristic = heuristic;
		currentNode = null;
		maxDepth = 5;
	}

	public AlphaBetaAI(Player player, Heuristic heuristic, int maxDepth) {
		this.player = player;
		this.heuristic = heuristic;
		currentNode = null;
		this.maxDepth = maxDepth;
	}

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
			// Get the value of this node using Alpha-Beta pruning
			int currentValue = miniMax(currentNode.getChild(i), maxDepth,
					MIN_NODE, MAX_NODE).getValue();
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

	// Using alpha - beta pruning, return the best value from the node
	private Node miniMax(Node node, int depth, Node alpha, Node beta) {
		if (depth == 0
				|| node.getData().getCurrentGameState() != GameState.RUNNING)
			return node;
		if (node.getChildrenLength() < 1) {
			return node;
		}

		// Alpha - If we are the player being evaluated only evaluate the node
		// with the highest score for us
		if (node.getData().getCurrentTurn() == player) {
			Node currentNode = MIN_NODE;
			for (int i = 0; i < node.getChildrenLength(); i++) {
				Node tempNode = miniMax(node.getChild(i), depth - 1, alpha,
						beta);

				if (tempNode.getValue() >= currentNode.getValue()) {
					currentNode = tempNode;
				}
				if (beta.getValue() <= currentNode.getValue()) {
					return currentNode;
				}
				if (alpha.getValue() < currentNode.getValue()) {
					alpha = currentNode;
				}
			}
			return currentNode;
		}
		// Beta - If the other player is being evaluated determine the node with
		// the worst score for us
		Node currentNode = MAX_NODE;
		for (int i = 0; i < node.getChildrenLength(); i++) {
			Node tempNode = miniMax(node.getChild(i), depth - 1, alpha, beta);
			if (tempNode.getValue() <= currentNode.getValue())
				currentNode = tempNode;

			if (alpha.getValue() >= currentNode.getValue()) {
				return currentNode;
			}
			if (beta.getValue() > currentNode.getValue()) {
				beta = currentNode;
			}
		}
		return currentNode;
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
		return "Mini Max AI with Alpha Beta pruning";
	}
}
