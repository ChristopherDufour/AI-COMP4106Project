package edu.carleton.comp4106;

import java.util.Scanner;

import edu.carleton.comp4106.AI.*;
import edu.carleton.comp4106.game.*;
import edu.carleton.comp4106.game.ConnectFour.*;

public class Main {
	// Main function - Runs the program
	// Gives a selection between running for statistics (the primary function)
	// or playing as a human against the AI
	public static void main(String[] args) {
		System.out.println("Welcome to Connect Four With AI");
		System.out.println("1 - Play with AI");
		System.out.println("2 - Run for statistics");
		System.out.println("q - Quit");
		int input = 0;
		while (input == 0) {
			System.out.print("Make your selection: ");
			input = getInput();
			if (input != 1 && input != 2){
				System.out.print(input);
			}
			if (input != -1 && input != 1 && input != 2) {
				input = 0;
			}
		}
		if (input == 1) {
			playConnectFour();
		} else if (input == 2) {
			getStats();
		}
	}

	// Gets an integer from standard input. Treats q or quit as -1, allowing for
	// those to quit the program
	@SuppressWarnings("resource")
	private static int getInput() {
		Scanner scanner = new Scanner(System.in);
		String input = null;
		Integer number = null;

		while (number == null) {
			input = scanner.nextLine();
			if (input != null) {
				if (input.equalsIgnoreCase("q")
						|| input.equalsIgnoreCase("quit")) {
					return -1;
				}
				number = parseInteger(input);
				if (number == null) {
					System.out.println("INVALID INPUT: " + input);
				}
			}
		}

		return number;
	}

	// Parses a string to an integer, allowing for no integers to be present
	private static Integer parseInteger(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	// Prints out the game board for a human player to read
	private static void printBoard(ConnectFour game) {
		Player[][] board = game.getBoard();
		System.out.println();
		for (int y = 5; y > -1; y--) {

			for (int x = 0; x < 7; x++) {
				Player current = board[x][y];
				if (current == Player.RED) {
					System.out.print('R');
				} else if (current == Player.BLUE) {

					System.out.print('B');
				} else {
					System.out.print('-');
				}

			}

			System.out.println();

		}
		for (int x = 0; x < 7; x++) {
			System.out.print(x);
		}
		System.out.println();
	}

	// Play connect four against an AI opponent
	private static void playConnectFour() {
		// Initilize game
		ConnectFour game = new ConnectFour();
		// Get AIs
		// AI player1 = new QuiescenceSearchAI(Player.RED,new
		// CloseToWinHeuristic());
		// AI player1 = new MinMaxAI(Player.RED, new CloseToWinHeuristic());
		AI player1 = new HumanTextInputAI(Player.RED);
		// AI player1 = new TranspositionTablesAI(Player.RED, new
		// CloseToWinHeuristic());

		AI player2 = new MinMaxAI(Player.BLUE, new CloseToWinHeuristic());
		// AI player2 = new QuiescenceSearchAI(Player.BLUE, new
		// CloseToWinHeuristic());
		// AI player2 = new HumanTextInputAI(Player.BLUE);
		// AI player2 = new TranspositionTablesAI(Player.BLUE, new
		// CloseToWinHeuristic());

		// Main game loop
		while (game.getCurrentGameState() == GameState.RUNNING) {
			printBoard(game);
			if (game.getCurrentTurn() == Player.RED) {
				System.out.println("RED's Turn");
				player1.determineMove(game);
			} else if (game.getCurrentTurn() == Player.BLUE) {
				System.out.println("BLUE's Turn");
				player2.determineMove(game);
			}
		}
		// The game is over, display the results
		printBoard(game);
		System.out.println("Game is over!");
		if (game.getCurrentGameState() == GameState.RED_WIN) {
			System.out.println("RED WINS!");
		} else if (game.getCurrentGameState() == GameState.BLUE_WIN) {
			System.out.println("BLUE WINS!");
		} else if (game.getCurrentGameState() == GameState.TIE) {
			System.out.println("DRAW!");
		} else {
			System.out.println("ERROR - GAME IS NOT OVER");
		}
		System.out
				.println("Red Total Node Count = " + player1.totalNodeCount());
		System.out.println("Blue Total Node Count = "
				+ player2.totalNodeCount());
		// If the AI used was the transposition table save the new moves it
		// learned
		if (player1 instanceof TranspositionTablesAI) {
			TranspositionTablesAI p1 = (TranspositionTablesAI) player1;
			p1.saveTranspostitionTable();
		}
		if (player2 instanceof TranspositionTablesAI) {
			TranspositionTablesAI p2 = (TranspositionTablesAI) player2;
			p2.saveTranspostitionTable();
		}
	}

	// Run for statistics
	// Runs the AIs against eachother to determine average, minimum and maximum
	// statistical values
	private static void getStats() {
		AI[] redAIs = new AI[4];

		redAIs[0] = new MinMaxAI(Player.RED, new CloseToWinHeuristic());
		redAIs[1] = new QuiescenceSearchAI(Player.RED,
				new CloseToWinHeuristic());
		redAIs[2] = new AlphaBetaAI(Player.RED, new CloseToWinHeuristic());
		redAIs[3] = new TranspositionTablesAI(Player.RED,
				new CloseToWinHeuristic());

		AI[] blueAIs = new AI[4];
		blueAIs[0] = new MinMaxAI(Player.BLUE, new CloseToWinHeuristic());
		blueAIs[1] = new QuiescenceSearchAI(Player.BLUE,
				new CloseToWinHeuristic());
		blueAIs[2] = new AlphaBetaAI(Player.BLUE, new CloseToWinHeuristic());
		blueAIs[3] = new TranspositionTablesAI(Player.BLUE,
				new CloseToWinHeuristic());
		int numberOfTests = 20;
		// The forst time we run it is for the variable AI as the first player
		for (int x = 0; x < redAIs.length; x++) {
			// Get the AI we will be using
			AI redAI = redAIs[x];
			// Get the opponent AI
			AI blueAI = blueAIs[0];
			// Count the number of wins for each player and ties
			int redWins = 0;
			int blueWins = 0;
			int ties = 0;
			// Keep track of total node counts and total calcualtion times
			// For each round
			long[] redNodeCount = new long[numberOfTests];
			long[] blueNodeCount = new long[numberOfTests];
			long[] redCalcTime = new long[numberOfTests];
			long[] blueCalcTime = new long[numberOfTests];
			System.out.println(redAI.getName() + " vs. " + blueAI.getName());
			// Run the game with a psudo loading bar to show how many games has
			// passes
			System.out.print("[");
			for (int i = 0; i < numberOfTests; i++) {
				redAI.reinitilize();
				blueAI.reinitilize();

				ConnectFour game = new ConnectFour();
				while (game.getCurrentGameState() == GameState.RUNNING) {
					if (game.getCurrentTurn() == Player.RED) {
						long timeMilliSec = System.currentTimeMillis();
						redAI.determineMove(game);
						redCalcTime[i] += System.currentTimeMillis()
								- timeMilliSec;
					} else if (game.getCurrentTurn() == Player.BLUE) {
						long timeMilliSec = System.currentTimeMillis();
						blueAI.determineMove(game);
						blueCalcTime[i] += System.currentTimeMillis()
								- timeMilliSec;
					}
				}
				if (game.getCurrentGameState() == GameState.RED_WIN) {
					redWins++;
				} else if (game.getCurrentGameState() == GameState.BLUE_WIN) {
					blueWins++;
				} else if (game.getCurrentGameState() == GameState.TIE) {
					ties++;
				} else {
					System.out.println("ERROR - GAME IS NOT OVER");
				}
				redNodeCount[i] = redAI.totalNodeCount();
				blueNodeCount[i] = blueAI.totalNodeCount();
				if (redAI instanceof TranspositionTablesAI) {
					TranspositionTablesAI p1 = (TranspositionTablesAI) redAI;
					p1.saveTranspostitionTable();
				}
				if (blueAI instanceof TranspositionTablesAI) {
					TranspositionTablesAI p2 = (TranspositionTablesAI) blueAI;
					p2.saveTranspostitionTable();
				}
				System.out.print("/");
			}
			System.out.println("]");
			// Report results
			System.out.println("Red wins: "
					+ (((float) redWins / numberOfTests) * 100) + "%");
			System.out.println("Blue wins: "
					+ (((float) blueWins / numberOfTests) * 100) + "%");
			System.out.println("Ties: "
					+ (((float) ties / numberOfTests) * 100) + "%");

			// Red node count average
			System.out.println("--- Total Node Count --- ");
			System.out.println("- RED - ");
			System.out.println("Average node count: " + average(redNodeCount));
			// Red node count min
			System.out.println("Min node count: " + min(redNodeCount));
			// Red node count max
			System.out.println("Max node count: " + max(redNodeCount));
			System.out.println("- BLUE - ");
			// Blue node count average
			System.out.println("Average node count: " + average(blueNodeCount));
			// Blue node count min
			System.out.println("Min node count: " + min(blueNodeCount));
			// Blue node count max
			System.out.println("Max node count: " + max(blueNodeCount));

			System.out
					.println("--- Total Calculation Time in miliseconds --- ");
			System.out.println("- RED - ");
			System.out.println("Average calculation time: "
					+ average(redCalcTime) + "ms");
			// Red node count min
			System.out.println("Min calculation time: " + min(redCalcTime)
					+ "ms");
			// Red node count max
			System.out.println("Max calculation time: " + max(redCalcTime)
					+ "ms");
			System.out.println("- BLUE - ");
			// Blue node count average
			System.out.println("Average calculation time: "
					+ average(blueCalcTime) + "ms");
			// Blue node count min
			System.out.println("Min calculation time: " + min(blueCalcTime)
					+ "ms");
			// Blue node count max
			System.out.println("Max calculation time: " + max(blueCalcTime)
					+ "ms");

		}
		// Same as before, but with the second player (blue) as the variable
		for (int x = 0; x < blueAIs.length; x++) {
			AI redAI = redAIs[0];
			AI blueAI = blueAIs[x];
			int redWins = 0;
			int blueWins = 0;
			int ties = 0;
			long[] redNodeCount = new long[numberOfTests];
			long[] blueNodeCount = new long[numberOfTests];
			long[] redCalcTime = new long[numberOfTests];
			long[] blueCalcTime = new long[numberOfTests];
			System.out.println(redAI.getName() + " vs. " + blueAI.getName());

			System.out.print("[");
			for (int i = 0; i < numberOfTests; i++) {
				redAI.reinitilize();
				blueAI.reinitilize();

				ConnectFour game = new ConnectFour();
				while (game.getCurrentGameState() == GameState.RUNNING) {
					if (game.getCurrentTurn() == Player.RED) {
						long timeMilliSec = System.currentTimeMillis();
						redAI.determineMove(game);
						redCalcTime[i] += System.currentTimeMillis()
								- timeMilliSec;
					} else if (game.getCurrentTurn() == Player.BLUE) {
						long timeMilliSec = System.currentTimeMillis();
						blueAI.determineMove(game);
						blueCalcTime[i] += System.currentTimeMillis()
								- timeMilliSec;
					}
				}
				if (game.getCurrentGameState() == GameState.RED_WIN) {
					redWins++;
				} else if (game.getCurrentGameState() == GameState.BLUE_WIN) {
					blueWins++;
				} else if (game.getCurrentGameState() == GameState.TIE) {
					ties++;
				} else {
					System.out.println("ERROR - GAME IS NOT OVER");
				}
				redNodeCount[i] = redAI.totalNodeCount();
				blueNodeCount[i] = blueAI.totalNodeCount();
				if (redAI instanceof TranspositionTablesAI) {
					TranspositionTablesAI p1 = (TranspositionTablesAI) redAI;
					p1.saveTranspostitionTable();
				}
				if (blueAI instanceof TranspositionTablesAI) {
					TranspositionTablesAI p2 = (TranspositionTablesAI) blueAI;
					p2.saveTranspostitionTable();
				}
				System.out.print("/");
			}
			System.out.println("]");
			// Report results
			System.out.println("Red wins: "
					+ (((float) redWins / numberOfTests) * 100) + "%");
			System.out.println("Blue wins: "
					+ (((float) blueWins / numberOfTests) * 100) + "%");
			System.out.println("Ties: "
					+ (((float) ties / numberOfTests) * 100) + "%");

			// Red node count average
			System.out.println("--- Total Node Count --- ");
			System.out.println("- RED - ");
			System.out.println("Average node count: " + average(redNodeCount));
			// Red node count min
			System.out.println("Min node count: " + min(redNodeCount));
			// Red node count max
			System.out.println("Max node count: " + max(redNodeCount));
			System.out.println("- BLUE - ");
			// Blue node count average
			System.out.println("Average node count: " + average(blueNodeCount));
			// Blue node count min
			System.out.println("Min node count: " + min(blueNodeCount));
			// Blue node count max
			System.out.println("Max node count: " + max(blueNodeCount));

			System.out
					.println("--- Total Calculation Time in miliseconds --- ");
			System.out.println("- RED - ");
			System.out.println("Average calculation time: "
					+ average(redCalcTime) + "ms");
			// Red node count min
			System.out.println("Min calculation time: " + min(redCalcTime)
					+ "ms");
			// Red node count max
			System.out.println("Max calculation time: " + max(redCalcTime)
					+ "ms");
			System.out.println("- BLUE - ");
			// Blue node count average
			System.out.println("Average calculation time: "
					+ average(blueCalcTime) + "ms");
			// Blue node count min
			System.out.println("Min calculation time: " + min(blueCalcTime)
					+ "ms");
			// Blue node count max
			System.out.println("Max calculation time: " + max(blueCalcTime)
					+ "ms");

		}

	}

	// Calculate the average of an array of longs
	private static long average(long[] array) {
		long total = 0;
		for (long val : array) {
			total += val;
		}
		return total / array.length;
	}

	// Calculate the smallest number in an array of longs
	private static long min(long[] array) {
		long result = Integer.MAX_VALUE;
		for (long val : array) {
			if (result > val) {
				result = val;
			}
		}
		return result;
	}

	// Calculate the largest number in an array of longs
	private static long max(long[] array) {
		long result = 0;
		for (long val : array) {
			if (result < val) {
				result = val;
			}
		}
		return result;
	}

}
