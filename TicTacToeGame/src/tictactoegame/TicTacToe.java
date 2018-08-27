package tictactoegame;

import java.util.Random;
import java.util.Scanner;

public class TicTacToe {

    private static final char SYMBOL1 = 'x';
    private static final char SYMBOL2 = 'o';

    private final char[][] board = {{'-', '-', '-'},
    {'-', '-', '-'},
    {'-', '-', '-'}};

    private final Scanner scanner;
    private final TicTacToePlayer[] players;

    private int winner;
    private int currentPlayer;
    private int moveCounter;

    public TicTacToe(Scanner scanner, String name1, String name2) {
        TicTacToePlayer player1 = new TicTacToePlayer(name1, SYMBOL1);
        TicTacToePlayer player2 = new TicTacToePlayer(name2, SYMBOL2);
        this.players = new TicTacToePlayer[]{player1, player2};

        this.winner = 0;
        this.currentPlayer = 0;
        this.scanner = scanner;
    }

    /**
     * Starts the game.
     */
    public void play() {
        // While there is no winner and no tie...
        while (getWinner() == 0) {
            // Display board and start next turn
            displayBoard();
            nextTurn();
        }

        // Display the board one final time
        displayBoard();

        // Display end message
        switch (getWinner()) {
            case -1:
                System.out.println("The game is a tie!");
                break;
            case 1:
                System.out.println(players[0].getName() + " has won the game!");
                break;
            case 2:
                System.out.println(players[1].getName() + " has won the game!");
                break;
        }
    }

    /**
     * Returns the winner:
     * <ul>
     * <li>If the game is in progress, 0 is returned.</li>
     * <li>If the player 1 won, 1 is returned.</li>
     * <li>If the player 2 won, 2 is returned.</li>
     * <li>If the game is a tie, -1 is returned.</li>
     * </ul>
     *
     * @return the winner of the game, or a special code if there is none.
     */
    public int getWinner() {
        return winner;
    }

    /**
     * Displays the current board to standard output.
     */
    public void displayBoard() {
        System.out.println();

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }

        System.out.print("\n\n");
    }

    /**
     * Prompts the next player to make a move, and then checks if this a tie or
     * a win.
     */
    public void nextTurn() {
        // Get current player
        TicTacToePlayer player = players[currentPlayer];

        int x, y;
        if (player.getName().equals("AI")) {
            // AI move

            Random random = new Random();
            do {
                x = random.nextInt(3);
                y = random.nextInt(3);
            } while (!isValidMove(x, y));
        } else {
            // Player move

            // Send prompt
            System.out.println(player.getName() + ", enter the coordinates of your next move (eg: 1 2 is middle-right): ");

            // Get coordinates
            x = scanner.nextInt();
            y = scanner.nextInt();

            // Validate coordinates
            while (!isValidMove(x, y)) {
                System.out.println("Invalid coordinate, enter the coordinates of your next move (eg: 1 2 is middle-right): ");
                x = scanner.nextInt();
                y = scanner.nextInt();
            }
        }

        // Update board
        this.board[x][y] = player.getSymbol();

        // Update move counter
        ++this.moveCounter;

        // Check for winner
        checkWinner();

        // Select next player
        this.currentPlayer ^= 1;
    }

    /**
     * Checks if a player won the game or if a tie occurred, and updates the
     * {@link #winner} field accordingly.
     */
    private void checkWinner() {
        // Test diagonals
        if (board[1][1] != '-') {
            if (board[1][1] == board[0][0] && board[1][1] == board[2][2]) {
                this.winner = 1 + currentPlayer;
                return;
            }

            if (board[1][1] == board[0][2] && board[2][0] == board[1][1]) {
                this.winner = 1 + currentPlayer;
                return;
            }
        }

        // Test rows
        for (int i = 0; i < 3; ++i) {
            if (board[i][0] != '-' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                this.winner = 1 + currentPlayer;
                return;
            }
        }

        // Test columns
        for (int j = 0; j < 3; ++j) {
            if (board[0][j] != '-' && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                this.winner = 1 + currentPlayer;
                return;
            }
        }

        // Check for a tie
        if (moveCounter >= 9) {
            this.winner = -1;
            return;
        }
    }

    /**
     * Checks if a given move is valid. A move is valid if (1) both coordinates
     * are 0, 1, or 2; and (2) if the board is empty at the given location.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @return {@code true} if the given move is valid, {@code false} otherwise.
     */
    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < 3 && y >= 0 && y < 3 && board[x][y] == '-';
    }

    private static class TicTacToePlayer {

        private final String name;
        private final char symbol;

        public TicTacToePlayer(String name, char symbol) {
            this.name = name;
            this.symbol = symbol;
        }

        public String getName() {
            return name;
        }

        public char getSymbol() {
            return symbol;
        }

    }
}
