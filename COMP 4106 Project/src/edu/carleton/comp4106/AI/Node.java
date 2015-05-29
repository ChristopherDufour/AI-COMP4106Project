package edu.carleton.comp4106.AI;

import java.util.ArrayList;

import edu.carleton.comp4106.game.ConnectFour;
import edu.carleton.comp4106.game.ConnectFour.Player;
import edu.carleton.comp4106.game.FullColumnException;
import edu.carleton.comp4106.game.InvalidBoardLocationException;

// Node used to store and manipulate the state data that AI's use
public class Node {
	// This node's parent
	private Node parent;
	// The state this node contatins
	private ConnectFour data;
	// An array of this node's children
	private Node[] children;
	// A list of which moves are possible to make from this state
	private ArrayList<Integer> possibleMoves;
	// The heuristic function used to evaluate the states
	private Heuristic heuristic;
	// The value of this state
	private int value;
	// The player that this node's AI belongs to
	private Player player;

	// Create a new node, used in declaring the root node of a tree
	public Node(ConnectFour data, Heuristic heuristic, Player player) {
		parent = null;
		this.data = data;
		this.heuristic = heuristic;
		this.player = player;
		possibleMoves = new ArrayList<Integer>();
		// determine possible moves
		for (int i = 0; i < 7; i++) {
			try {
				if (data.getColumnHeight(i) < 6) {
					possibleMoves.add(i);
				}
			} catch (InvalidBoardLocationException e) {
				e.printStackTrace();
			}
		}
		children = new Node[possibleMoves.size()];
		value = heuristic.evaluateGame(data, player);

	}

	// Create a node with a value, used to compare to other nodes,
	// and in finding the largest/smallest value nodes
	public Node(int i) {
		value = i;
	}

	// Create a node based off of a move made from it's parent
	private Node(Node parent, int move) {
		this.parent = parent;
		player = parent.player;
		// Create a new copy of the game to maipulate
		ConnectFour newGame = parent.getData().deepCopy();
		try {
			// play the piece to know what a possible future state is
			newGame.playPiece(move);

			data = newGame;

			possibleMoves = new ArrayList<Integer>();
			// Determine possible moves
			for (int i = 0; i < 7; i++) {

				if (data.getColumnHeight(i) < 6) {
					possibleMoves.add(i);
				}
			}
		} catch (InvalidBoardLocationException e1) {
			e1.printStackTrace();
		} catch (FullColumnException e1) {
			e1.printStackTrace();
		}
		children = new Node[possibleMoves.size()];

		heuristic = parent.heuristic;
		value = heuristic.evaluateGame(newGame, player);
	}

	// Return's this node's parent
	public Node getParent() {
		return parent;
	}

	// Return's this node's data
	public ConnectFour getData() {
		return data;
	}

	// Return's this node's heuristic value
	public int getValue() {
		return value;
	}

	// Return's this node's number of children
	public int getChildrenLength() {
		return children.length;
	}

	// Get the move at intex i, if it exists
	public Integer getMove(int i) {
		if (i >= possibleMoves.size()) {
			return null;
		}
		return possibleMoves.get(i);
	}

	// Get the child at index i, by default children are null to conserve space
	// So the child node may need to be generated
	public Node getChild(int i) {
		if (i >= children.length || i < 0) {
			return null;
		}

		if (children[i] == null) {
			children[i] = new Node(this, possibleMoves.get(i));
		}
		return children[i];
	}

	// Get the total number of all children and their children under the given
	// node
	public int getChildrenNumber(Node node) {
		int count = 1;
		for (Node child : children) {
			if (child != null) {
				count += getChildrenNumber(child);
			}
		}
		return count;
	}

	// Uses recursion to find the child for this given state
	public Node getChildForState(ConnectFour state) {
		if (data.equals(state))
			return this;
		if (state.getMovesNumber() < getData().getMovesNumber()) {
			return null;
		}

		for (int i = 0; i < children.length; i++) {
			if (getChild(i).getData().equals(state)) {
				return getChild(i);
			}
		}
		for (int i = 0; i < children.length; i++) {
			Node foundState = getChild(i).getChildForState(state);
			if (foundState != null) {
				return foundState;
			}
		}
		return null;
	}

	// Returns the total number of nodes under this node
	public int nodeCount() {
		int count = 1;
		for (int i = 0; i < children.length; i++) {
			Node childNode = children[i];
			if (childNode != null) {
				count += childNode.nodeCount();
			}
		}
		return count;
	}

	// Get the move that the parent made to get to this node
	public Integer getParentMove(Node node) {
		Node currentNode = this;

		while (currentNode != null && (currentNode.parent.data != node.data)) {
			currentNode = currentNode.parent;
		}
		for (int i = 0; i < node.children.length; i++) {
			if (node.getChild(i) == currentNode) {
				return node.possibleMoves.get(i);
			}
		}

		return null;
	}
}
