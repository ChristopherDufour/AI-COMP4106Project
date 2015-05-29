package edu.carleton.comp4106.AI;

import java.util.Scanner;

import edu.carleton.comp4106.game.ConnectFour;
import edu.carleton.comp4106.game.FullColumnException;
import edu.carleton.comp4106.game.InvalidBoardLocationException;
import edu.carleton.comp4106.game.ConnectFour.*;

// Human text input - gets moves from command line input
public class HumanTextInputAI implements AI {
	Player currentPlayer;

	public HumanTextInputAI(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	@Override
	public ConnectFour determineMove(ConnectFour game) {
		// You can't move it it's not your turn or the game is over
		if (game.getCurrentTurn() != currentPlayer) {
			System.out.println("It is not your turn!");
			return game;
		}
		if (game.getCurrentGameState() != GameState.RUNNING) {
			System.out.println("Game is over!");
			return game;
		}
		// Get a move from the user and perform it
		StringBuilder selectText = new StringBuilder();
		selectText.append("Human player ");
		if (currentPlayer == Player.RED) {
			selectText.append("Red, ");
		} else {
			selectText.append("Blue, ");
		}
		selectText.append("input your move: ");
		boolean validMove = false;
		while (!validMove) {
			System.out.print(selectText);
			try {
				game.playPiece(getInt());
				validMove = true;
			} catch (InvalidBoardLocationException e) {
				System.out.println("INVALID MOVE - OUT OF BOUNDS");
			} catch (FullColumnException e) {
				System.out.println("INVALID MOVE - COLUMN IS FULL");
			}

		}

		return game;
	}

	// Get an integer from the command line
	@SuppressWarnings("resource")
	private int getInt() {
		Scanner in = new Scanner(System.in);

		while (!in.hasNextInt())
			in.next();
		int num = in.nextInt();
		System.out.println(num);
		return num;
	}

	// Humans don't use nodes!
	@Override
	public int nodeCount() {
		return 0;
	}

	@Override
	public int totalNodeCount() {
		return 0;
	}

	// Humans arn't robots and don't need to reinitialize
	@Override
	public void reinitilize() {
	}

	@Override
	public String getName() {
		return "Human text input AI";
	}

}
