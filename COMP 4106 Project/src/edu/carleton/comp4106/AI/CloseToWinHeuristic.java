package edu.carleton.comp4106.AI;

import java.util.Arrays;

import edu.carleton.comp4106.game.ConnectFour;
import edu.carleton.comp4106.game.ConnectFour.GameState;
import edu.carleton.comp4106.game.ConnectFour.Player;
import edu.carleton.comp4106.game.InvalidBoardLocationException;

// A heuristic function that returns value based on how close the current player is to winning 
public class CloseToWinHeuristic implements Heuristic {

	@Override
	public int evaluateGame(ConnectFour game, Player player) {
		// If the opponent wins the game is a tie
		if (game.getCurrentGameState() != GameState.RUNNING) {
			// If the player wins award points
			if ((game.getCurrentGameState() == GameState.RED_WIN && player == Player.RED)
					|| (game.getCurrentGameState() == GameState.BLUE_WIN && player == Player.BLUE)) {
				// If this current player won the game return high positive
				// value - to be seeken out
				return 10000;
			}

			else {
				// If the opponent won the game or it is a tie return high
				// negative value - to be avoided
				return -10000;
			}
		}

		// Winning states will already be marked by the Connect4 object, so
		// check for 1 away from winning states
		try {
			int[] playerValues = checkForNumberAwayFromWin(game, player);
			int[] opponentValues = checkForNumberAwayFromWin(game,
					getOtherPlayer(player));

			int playerValue = 0;
			// Players get points for how close adding new pieces would pring
			// them to 4 in a rows
			playerValue = 1000 * playerValues[3] + 100 * playerValues[2] + 10
					* playerValues[1] + playerValues[0];

			int opponentValue = 0;
			if (opponentValues[3] > 0) {
				return -1000;
			}
			opponentValue = 100 * opponentValues[2] + 10 * opponentValues[1]
					+ opponentValues[0];

			return (playerValue - opponentValue);
		} catch (InvalidBoardLocationException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public void printEvaluateGame(ConnectFour game, Player player) {
		// Winning states will already be marked by the Connect4 object, so
		// check for 1 away from winning states
		try {
			int[] playerValues = checkForNumberAwayFromWin(game, player);
			int[] opponentValues = checkForNumberAwayFromWin(game,
					getOtherPlayer(player));
			System.out.println("US: " + playerValues[3] + " " + playerValues[2]
					+ " " + playerValues[1] + " " + playerValues[0]);
			System.out.println("THEM: " + opponentValues[3] + " "
					+ opponentValues[2] + " " + opponentValues[1] + " "
					+ opponentValues[0]);

		} catch (InvalidBoardLocationException e) {
			e.printStackTrace();
		}
	}

	// Return how close possible move pieces are to a four in a row
	private int[] checkForNumberAwayFromWin(ConnectFour game, Player player)
			throws InvalidBoardLocationException {
		int[] results = new int[4];
		Arrays.fill(results, 0);

		for (int x = 0; x < 7; x++) {
			if (game.getColumnHeight(x) > 5) {
				continue;
			}
			int[] directions = new int[8];
			int[][] playerPieces = new int[4][7];
			int i = 1;
			int invalidDirections = 0;

			Arrays.fill(directions, 0);
			for (int c = 0; c < 4; c++) {
				Arrays.fill(playerPieces[c], -100);
				playerPieces[c][3] = 1;
			}

			while (i < 4 && invalidDirections < 7) {
				// Going up
				if (game.getColumnHeight(x) + i > 5) {
					if (directions[0] != -1) {
						directions[0] = -1;
						invalidDirections++;
					}
					if (directions[6] != -1) {
						directions[6] = -1;
						invalidDirections++;
					}
					if (directions[7] != -1) {
						directions[7] = -1;
						invalidDirections++;
					}

				}
				// Going right
				if (x + i > 6) {
					if (directions[0] != -1) {
						directions[0] = -1;
						invalidDirections++;
					}
					if (directions[1] != -1) {
						directions[1] = -1;
						invalidDirections++;
					}
					if (directions[2] != -1) {
						directions[2] = -1;
						invalidDirections++;
					}
				}
				// Going down
				if (game.getColumnHeight(x) - i < 0) {
					if (directions[2] != -1) {
						directions[2] = -1;
						invalidDirections++;
					}
					if (directions[3] != -1) {
						directions[3] = -1;
						invalidDirections++;
					}
					if (directions[4] != -1) {
						directions[4] = -1;
						invalidDirections++;
					}
				}

				// Going left
				if (x - i < 0) {
					if (directions[4] != -1) {
						directions[4] = -1;
						invalidDirections++;
					}
					if (directions[5] != -1) {
						directions[5] = -1;
						invalidDirections++;
					}
					if (directions[6] != -1) {
						directions[6] = -1;
						invalidDirections++;
					}
				}

				// Check if there is a piece there, an opponent's piece or an
				// empty space

				// Up and right
				if (directions[0] != -1) {
					if (game.getBoard()[x + i][(game.getColumnHeight(x) + i)] == Player.EMPTY) {
						playerPieces[1][i + 3] = 0;
					} else if (game.getBoard()[x + i][(game.getColumnHeight(x) + i)] == player) {
						playerPieces[1][i + 3] = 1;
					} else {
						directions[0] = -1;
						invalidDirections++;
					}
				}

				// Right
				if (directions[1] != -1) {
					if (game.getBoard()[x + i][(game.getColumnHeight(x))] == Player.EMPTY) {
						playerPieces[2][i + 3] = 0;
					} else if (game.getBoard()[x + i][(game.getColumnHeight(x))] == player) {
						playerPieces[2][i + 3] = 1;
					} else {
						directions[1] = -1;
						invalidDirections++;
					}
				}

				// Down and Right
				if (directions[2] != -1) {
					if (game.getBoard()[x + i][(game.getColumnHeight(x) - 1)] == Player.EMPTY) {
						playerPieces[3][i + 3] = 0;
					} else if (game.getBoard()[x + i][(game.getColumnHeight(x) - 1)] == player) {
						playerPieces[3][i + 3] = 1;
					} else {
						directions[2] = -1;
						invalidDirections++;
					}
				}

				// Down
				if (directions[3] != -1) {
					if (game.getBoard()[x][(game.getColumnHeight(x) - 1)] == player) {
						playerPieces[0][i + 3] = 1;
					} else {
						directions[3] = -1;
						invalidDirections++;
					}
				}
				// Down and Left
				if (directions[4] != -1) {
					if (game.getBoard()[x - i][(game.getColumnHeight(x) - 1)] == Player.EMPTY) {
						playerPieces[1][3 - i] = 0;
					} else if (game.getBoard()[x - i][(game.getColumnHeight(x) - 1)] == player) {
						playerPieces[1][3 - i] = 1;
					} else {
						directions[4] = -1;
						invalidDirections++;
					}
				}

				// Left
				if (directions[5] != -1) {
					if (game.getBoard()[x - i][(game.getColumnHeight(x))] == Player.EMPTY) {
						playerPieces[2][3 - i] = 0;
					} else if (game.getBoard()[x - i][(game.getColumnHeight(x))] == player) {
						playerPieces[2][3 - i] = 1;
					} else {
						directions[5] = -1;
						invalidDirections++;
					}
				}

				// Left and Up
				if (directions[6] != -1) {
					if (game.getBoard()[x - i][(game.getColumnHeight(x) + 1)] == Player.EMPTY) {
						playerPieces[3][3 - i] = 0;
					} else if (game.getBoard()[x - i][(game.getColumnHeight(x) + 1)] == player) {
						playerPieces[3][3 - i] = 1;
					} else {
						directions[6] = -1;
						invalidDirections++;
					}
				}

				// Up (nothing can be above this piece)
				if (directions[7] != -1) {
					if (game.getBoard()[x][(game.getColumnHeight(x) + i)] == Player.EMPTY) {
						playerPieces[0][3 - i] = 0;
					}
				}

				i++;
			}
			// Check if this would create a 4, 3, 2 or 1 in a row, with room to
			// expant to a 4 in a row and return the results
			for (int dirs = 0; dirs < 4; dirs++) {
				for (int shift = 0; shift < 4; shift++) {
					int value = (playerPieces[dirs][0 + shift]
							+ playerPieces[dirs][1 + shift]
							+ playerPieces[dirs][2 + shift] + playerPieces[dirs][3 + shift]);
					if (value == 4) {
						results[3]++;
					} else if (value == 3) {
						results[2]++;
					} else if (value == 2) {
						results[1]++;
					} else if (value == 1) {
						results[0]++;
					}

				}
			}

		}

		return results;
	}

	private Player getOtherPlayer(Player p) {
		if (p == Player.RED)
			return Player.BLUE;
		if (p == Player.BLUE)
			return Player.RED;
		return null;

	}
}
