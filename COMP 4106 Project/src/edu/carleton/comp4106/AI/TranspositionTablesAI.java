package edu.carleton.comp4106.AI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import edu.carleton.comp4106.game.ConnectFour;
import edu.carleton.comp4106.game.ConnectFour.GameState;
import edu.carleton.comp4106.game.ConnectFour.Player;
import edu.carleton.comp4106.game.FullColumnException;
import edu.carleton.comp4106.game.InvalidBoardLocationException;

// Transposition table class
public class TranspositionTablesAI implements AI {

	private Node currentNode;
	private Node originalNode;
	private int maxDepth;
	private Player player;
	private Heuristic heuristic;
	// Transpostion table, used to store the saved moves
	private HashMap<Integer, Integer> traspositionTable;

	@SuppressWarnings("unused")
	private TranspositionTablesAI() {
	}

	public TranspositionTablesAI(Player player, Heuristic heuristic) {
		this.player = player;
		this.heuristic = heuristic;
		currentNode = null;
		maxDepth = 3;
		// Load transpositionTable
		traspositionTable = loadTranspostitionTable();
	}

	public TranspositionTablesAI(Player player, Heuristic heuristic,
			int maxDepth) {
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
		boolean foundByTable = false;
		int bestMoveIndex = -1;
		// If we know a moves from the table perform it
		if (traspositionTable.containsKey(state.hashCode())) {
			bestMoveIndex = traspositionTable.get(state.hashCode());
			// If the move is illegal then ignore it, otherwise skip node
			// calculation
			if (currentNode.getMove(bestMoveIndex) != null) {
				foundByTable = true;
			}
		}
		if (!foundByTable) {

			int value = Integer.MIN_VALUE;
			for (int i = 0; i < currentNode.getChildrenLength(); i++) {
				int currentValue;
				// Standard Minimax to determine value
				currentValue = miniMax(currentNode.getChild(i), maxDepth);

				if (currentValue > value) {
					bestMoveIndex = i;
					value = currentValue;
				}

			}
		}

		int bestMove = currentNode.getMove(bestMoveIndex);
		currentNode = currentNode.getChild(bestMoveIndex);

		// If we didn't know this move then update the table with it
		if (!foundByTable) {
			traspositionTable.put(state.hashCode(), bestMove);
		}
		try {
			state.playPiece(bestMove);

		} catch (InvalidBoardLocationException | FullColumnException e) {
			e.printStackTrace();
		}
		return state;
	}

	private int miniMax(Node node, int depth) {
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
		traspositionTable = loadTranspostitionTable();
	}

	@Override
	public String getName() {
		return "Transposition Tables AI";
	}

	// Load the HashMap from the storage object
	private HashMap<Integer, Integer> loadTranspostitionTable() {
		HashMap<Integer, Integer> table = new HashMap<Integer, Integer>();
		File file = new File("src/edu/carleton/comp4106/AI/TranspositionTable");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));

			String l;
			while ((l = br.readLine()) != null) {
				String[] args = l.split("[,]", 2);
				if (args.length != 2)
					continue;

				Integer hash = Integer.parseInt(args[0]);
				Integer decision = Integer.parseInt(args[1]);
				table.put(hash, decision);
			}
			br.close();
			// System.out.println("Loaded Transposition table with "
			// + table.size() + " entries");

		} catch (FileNotFoundException e) {
			System.out
					.println("Trasposition Table AI - File not found, generatring new table");
			return new HashMap<Integer, Integer>();
		} catch (IOException e) {
			System.out
					.println("Trasposition Table AI - Error loading file, generatring new table");
			return new HashMap<Integer, Integer>();
		}
		return table;
	}

	// Merge this TT with the saved one, and save to the file
	public void saveTranspostitionTable() {
		if (traspositionTable == null)
			return;

		HashMap<Integer, Integer> loadedTable = loadTranspostitionTable();
		traspositionTable.putAll(loadedTable);
		File file = new File("src/edu/carleton/comp4106/AI/TranspositionTable");
		// System.out.println("Saving " + traspositionTable.size() +
		// " entries.");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (Integer hash : traspositionTable.keySet()) {
				if (hash == null)
					continue;
				bw.write(hash + "," + traspositionTable.get(hash) + "\n");

			}
			bw.flush();
			bw.close();
		} catch (FileNotFoundException e) {
			System.out
					.println("Trasposition Table AI - File not found, unable to save");
		} catch (IOException e) {
			System.out
					.println("Trasposition Table AI - Error saving file, save failed");
		}

	}
}
