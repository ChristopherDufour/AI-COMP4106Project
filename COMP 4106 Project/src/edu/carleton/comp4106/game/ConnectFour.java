package edu.carleton.comp4106.game;

import java.util.Arrays;

public class ConnectFour {
	// The game board, 7x6 and can either have red or blue game pieces in it or
	// be empty at each position
	private Player[][] board;
	// The number of pieces in each column, to make calculations faster
	private int[] colHeight;
	// Which player's turn it is
	private Player currentTurn;
	// The current state of the game
	private GameState gameState;
	// The number of turns that have passes, also shows how many pieces are in
	// play
	private int movesNumber;

	// Returns the board
	public Player[][] getBoard() {
		return board;
	}

	// Returns the number of pieces in each row
	public int getColumnHeight(int col) throws InvalidBoardLocationException {
		if (col >= colHeight.length || col < 0) {
			throw new InvalidBoardLocationException();
		}
		return colHeight[col];
	}

	// Returns the player type for who's turn it is
	public Player getCurrentTurn() {
		return currentTurn;
	}

	// Returns the current game state
	public GameState getCurrentGameState() {
		return gameState;
	}

	// Returns the number of pieces in play
	public int getMovesNumber() {
		return movesNumber;
	}

	// Enum for different board position states
	public enum Player {
		RED, BLUE, EMPTY;
	}

	// Enum for the different states the game can be in
	public enum GameState {
		RUNNING, RED_WIN, BLUE_WIN, TIE;
	}

	// Initialize a new game
	public ConnectFour() {
		board = new Player[7][6];
		colHeight = new int[7];
		for (Player[] c : board) {
			Arrays.fill(c, Player.EMPTY);
		}

		Arrays.fill(colHeight, 0);
		currentTurn = Player.RED;
		gameState = GameState.RUNNING;
		movesNumber = 0;
	}

	// Performs a deep copy of the game, for AI tree purposes
	public ConnectFour deepCopy() {
		return new ConnectFour(board, colHeight, currentTurn, gameState,
				movesNumber);
	}

	// Copy contstructor used in deep copy
	private ConnectFour(Player[][] board, int[] colHeight, Player currentTurn,
			GameState gameState, int movesNumber) {
		this.board = new Player[7][6];
		this.colHeight = new int[7];
		for (int x = 0; x < 7; x++) {
			this.colHeight[x] = colHeight[x];
			for (int y = 0; y < 6; y++) {
				this.board[x][y] = board[x][y];
			}
		}
		this.currentTurn = currentTurn;
		this.gameState = gameState;
		this.movesNumber = movesNumber;
	}

	// Play a piece, changing the board state, player and determines if the game
	// state changed
	public void playPiece(int col) throws InvalidBoardLocationException,
			FullColumnException {
		// Check if the game is running, if it's not don't do anything
		if (gameState != GameState.RUNNING) {
			return;
		}
		// If the input is out of bounds
		if (col >= board.length || col < 0) {
			throw new InvalidBoardLocationException();
		}
		// If the input is in a column that is full
		if (colHeight[col] > 5) {
			throw new FullColumnException();
		}
		// Selection is valid, place the piece and check if it triggered a
		// victory condition
		board[col][colHeight[col]] = currentTurn;
		// Check for victory condidtion
		checkVictoryCondition(col);
		// Change the player and incement the column height variable
		changePlayer();
		colHeight[col]++;
		movesNumber++;

		if (gameState == GameState.RUNNING) {
			boolean allRowsAreFull = true;
			for (int height : colHeight) {

				if (height < 6)
					allRowsAreFull = false;
			}
			if (allRowsAreFull) {
				gameState = GameState.TIE;
			}
		}

	}

	// Checks if the piece at row/col creates a winning sequence for that player
	private void checkVictoryCondition(int col) {
		// If the place we are checking is blank return null
		if (board[col][colHeight[col]] == Player.EMPTY) {
			return;
		}
		// Checks if that player won the game. We do not have to check above the
		// given piece as
		// Directions are clockwise from top, -1 indicates invalid directions,
		// or one that didn't win
		int[] directions = new int[8];
		int[][] playerPieces = new int[4][7];
		int i = 1;
		int invalidDirections = 0;

		Arrays.fill(directions, 0);
		for (int c = 0; c < 4; c++) {
			Arrays.fill(playerPieces[c], 0);
			playerPieces[c][3] = 1;
		}

		while (i < 4 && invalidDirections < 7) {
			// Going up
			if (colHeight[col] + i > 5) {
				if (directions[0] != -1) {
					directions[0] = -1;
					invalidDirections++;
				}

				if (directions[6] != -1) {
					directions[6] = -1;
					invalidDirections++;
				}

			}
			// Going right
			if (col + i > 6) {
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
			if (colHeight[col] - i < 0) {
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
			if (col - i < 0) {
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

			// Check if each direction is valid
			// Up and Right
			if (directions[0] != -1) {
				if (board[col + i][colHeight[col] + i] != board[col][colHeight[col]]) {
					directions[0] = -1;
					invalidDirections++;
				} else {
					playerPieces[1][3 + i] = 1;
				}
			}
			// Right
			if (directions[1] != -1) {
				if (board[col + i][colHeight[col]] != board[col][colHeight[col]]) {
					directions[1] = -1;
					invalidDirections++;
				} else {
					playerPieces[2][3 + i] = 1;
				}
			}
			// Right down
			if (directions[2] != -1) {
				if (board[col + i][colHeight[col] - i] != board[col][colHeight[col]]) {
					directions[2] = -1;
					invalidDirections++;
				} else {
					playerPieces[3][3 + i] = 1;
				}
			}
			// Down
			if (directions[3] != -1) {
				if (board[col][colHeight[col] - i] != board[col][colHeight[col]]) {
					directions[3] = -1;
					invalidDirections++;
				} else {
					playerPieces[0][3 + i] = 1;
				}
			}

			if (directions[4] != -1) {
				if (board[col - i][colHeight[col] - i] != board[col][colHeight[col]]) {
					directions[4] = -1;
					invalidDirections++;
				} else {
					playerPieces[1][3 - i] = 1;
				}
			}

			if (directions[5] != -1) {
				if (board[col - i][colHeight[col]] != board[col][colHeight[col]]) {
					directions[5] = -1;
					invalidDirections++;
				} else {
					playerPieces[2][3 - i] = 1;
				}
			}
			if (directions[6] != -1) {
				if (board[col - i][colHeight[col] + i] != board[col][colHeight[col]]) {
					directions[6] = -1;
					invalidDirections++;
				} else {
					playerPieces[3][3 - i] = 1;
				}
			}
			i++;
		}
		// Determines if, from the last played peice, if a 4 in a row was
		// created
		for (int dirs = 0; dirs < 4; dirs++) {
			for (int shift = 0; shift < 4; shift++) {
				int value = (playerPieces[dirs][0 + shift]
						+ playerPieces[dirs][1 + shift]
						+ playerPieces[dirs][2 + shift] + playerPieces[dirs][3 + shift]);
				if (value == 4) {
					if (board[col][colHeight[col]] == Player.RED) {
						gameState = GameState.RED_WIN;
					} else if (board[col][colHeight[col]] == Player.BLUE) {
						gameState = GameState.BLUE_WIN;
					}
				}
			}
		}

	}

	// Change the player to the other player
	private void changePlayer() {
		if (currentTurn == Player.RED) {
			currentTurn = Player.BLUE;
		} else {
			currentTurn = Player.RED;
		}
	}

	// Generates a hash code based on the variables - allows a state to be
	// represneted as an int
	// Used in comparing states and the transposition table
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(board);
		result = prime * result + Arrays.hashCode(colHeight);

		if (currentTurn == null)
			result = prime * result + 2;
		else if (currentTurn == Player.RED)
			result = prime * result + 3;
		else if (currentTurn == Player.BLUE)
			result = prime * result + 5;
		else if (currentTurn == Player.EMPTY)
			result = prime * result + 7;

		if (gameState == null)
			result = prime * result + 11;
		else if (gameState == GameState.RED_WIN)
			result = prime * result + 13;
		else if (gameState == GameState.BLUE_WIN)
			result = prime * result + 19;
		else if (gameState == GameState.TIE)
			result = prime * result + 23;

		result = prime * result + movesNumber;
		return result;
	}

	// Compare two states for equality
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectFour other = (ConnectFour) obj;
		if (!Arrays.deepEquals(board, other.board))
			return false;
		if (!Arrays.equals(colHeight, other.colHeight))
			return false;
		if (currentTurn != other.currentTurn)
			return false;
		if (gameState != other.gameState)
			return false;
		if (movesNumber != other.movesNumber)
			return false;
		return true;
	}
}
