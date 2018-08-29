package com.devtomermoran.tictactoe;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Main extends JFrame {

	private JPanel contentPane;

	private GameBoard gameBoard;
	private PreGameHeader preGameHeader;
	private PostGameFooter postGameFooter;

	private Main() {
		setSize(740, 680);
		setTitle("TicTacToe Game");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initGUI() {
		// Instantiate elements
		this.contentPane = new JPanel(new GridBagLayout());

		this.preGameHeader = new PreGameHeader();
		this.postGameFooter = new PostGameFooter();
		this.gameBoard = new GameBoard();

		// Construct GUI
		gameBoard.initGUI();
		preGameHeader.initGUI();

		postGameFooter.setVisible(false);

		Insets zero = new Insets(0, 0, 0, 0);
		contentPane.add(preGameHeader, constraints(0, 0, 1, zero));
		contentPane.add(gameBoard, constraints(0, 1, 1, zero));
		contentPane.add(postGameFooter, constraints(0, 2, 1, zero));
		this.setContentPane(contentPane);
	}

	private void startGame() {
		preGameHeader.setVisible(false);
		gameBoard.startGame(preGameHeader.getPlayer1(), preGameHeader.getPlayer2());
	}

	private void endGame(int winner) {
		System.out.println("End of game! " + winner);

		String winnerName;
		switch (winner) {
			case -1:
				winnerName = null;
				break;
			case 1:
				winnerName = preGameHeader.getPlayer1();
				break;
			case 2:
				winnerName = preGameHeader.getPlayer2();
				break;
			default:
				throw new AssertionError();
		}

		postGameFooter.onWin(winnerName);

		preGameHeader.setVisible(true);
		postGameFooter.setVisible(true);
	}

	private class PreGameHeader extends JPanel {

		private final JLabel label1, label2;
		private final JTextField field1, field2;
		private final JButton startButton;

		private PreGameHeader() {
			super(new GridBagLayout());

			this.label1 = new JLabel("Player 1:");
			this.label2 = new JLabel("Player 2:");
			this.field1 = new JTextField(15);
			this.field2 = new JTextField(15);
			this.startButton = new JButton("Start Game");
		}

		private void initGUI() {
			// 1. Add child elements to this pane
			this.add(label1, constraints(0, 0, 1, new Insets(15, 0, 0, 5)));
			this.add(field1, constraints(1, 0, 1, new Insets(15, 0, 0, 20)));
			this.add(label2, constraints(2, 0, 1, new Insets(15, 0, 0, 5)));
			this.add(field2, constraints(3, 0, 0, new Insets(15, 0, 0, 0)));
			this.add(startButton, constraints(0, 1, 4, new Insets(20, 0, 20, 0)));

			// 2. Style child elements
			Font boldFont = new Font("Monospace", Font.BOLD, 15);
			Font plainFont = new Font("Monospace", Font.PLAIN, 15);
			label1.setFont(boldFont);
			label2.setFont(boldFont);
			field1.setFont(plainFont);
			field2.setFont(plainFont);
			startButton.setFont(boldFont);

			// 3. Set up event handlers
			startButton.setEnabled(false);
			startButton.addActionListener(e -> startGame());

			DocumentListener listener = new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent de) {
					onChange();
				}

				@Override
				public void removeUpdate(DocumentEvent de) {
					onChange();
				}

				@Override
				public void changedUpdate(DocumentEvent de) {
					onChange();
				}

				void onChange() {
					startButton.setEnabled(!field1.getText().isEmpty() && !field2.getText().isEmpty());
				}
			};

			field1.getDocument().addDocumentListener(listener);
			field2.getDocument().addDocumentListener(listener);
		}

		private String getPlayer1() {
			return field1.getText();
		}

		private String getPlayer2() {
			return field2.getText();
		}
	}

	private static final char SYMBOLS[] = {'X', 'O'};
	private static final Font NAME_FONTS[] = {
		new Font("monospace", Font.BOLD, 35),
		new Font("monospace", Font.PLAIN, 30)
	};

	private class GameBoard extends JPanel {

		private final JLabel playerName1, playerName2;

		private final JButton buttons[][];
		private final char board[][] = new char[3][3];

		private int moveCounter;

		private GameBoard() {
			super(new GridBagLayout());

			this.playerName1 = new JLabel();
			this.playerName2 = new JLabel();

			this.buttons = new JButton[3][3];

		}

		private void initGUI() {
			// 1. Initialize player names
			playerName1.setFont(NAME_FONTS[0]);
			playerName2.setFont(NAME_FONTS[1]);
			playerName2.setHorizontalAlignment(SwingConstants.LEFT);
			playerName2.setHorizontalAlignment(SwingConstants.RIGHT);

			this.add(playerName1, constraints(0, 0, 1, new Insets(0, 0, 0, 0)));
			this.add(playerName2, constraints(1, 0, 2, new Insets(0, 0, 0, 0)));

			// 2. Initialize the buttons grid
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 3; ++j) {
					JButton button = new JButton("X");
					button.setEnabled(false);
					button.setFont(new Font("monospace", Font.BOLD, 90));
					button.setPreferredSize(new Dimension(150, 150));

					GridBagConstraints constr = new GridBagConstraints();
					constr.gridx = j;
					constr.gridy = i + 1;
					this.add(button, constr);

					button.addActionListener(clickListener(button, i, j));

					this.buttons[i][j] = button;
				}
			}
		}

		private ActionListener clickListener(JButton button, int i, int j) {
			return (ActionEvent ae) -> {
				if (board[i][j] != 0) {
					return;
				}
				
				// Update character
				int player = moveCounter % 2;
				board[i][j] = SYMBOLS[player];
				button.setText(String.valueOf(SYMBOLS[player]));

				playerName1.setFont(NAME_FONTS[player ^ 1]);
				playerName2.setFont(NAME_FONTS[player]);

				// Increment move counter to easily detect ties
				++moveCounter;

				// Check for end of game
				int winner = checkWinner(player);
				if (winner != 0) {
					endGame(winner);
					playerName1.setVisible(false);
					playerName2.setVisible(false);
				}
			};
		}

		private void startGame(String name1, String name2) {
			playerName1.setText(name1 + " (" + SYMBOLS[0] + ")");
			playerName2.setText(name2 + " (" + SYMBOLS[1] + ")");

			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 3; ++j) {
					board[i][j] = 0;

					JButton button = buttons[i][j];

					button.setText("");
					button.setEnabled(true);
				}
			}
		}

		private int checkWinner(int currentPlayer) {
			// Test diagonals
			if (board[1][1] != 0) {
				if (board[1][1] == board[0][0] && board[1][1] == board[2][2]) {
					return 1 + currentPlayer;
				}

				if (board[1][1] == board[0][2] && board[2][0] == board[1][1]) {
					return 1 + currentPlayer;
				}
			}

			// Test rows
			for (int i = 0; i < 3; ++i) {
				if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
					return 1 + currentPlayer;
				}
			}

			// Test columns
			for (int j = 0; j < 3; ++j) {
				if (board[0][j] != 0 && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
					return 1 + currentPlayer;
				}
			}

			// Check for a tie
			if (moveCounter >= 9) {
				return -1;
			}

			return 0;
		}
	}

	private class PostGameFooter extends JPanel {

		private final JLabel label;

		private PostGameFooter() {
			this.label = new JLabel();

			label.setFont(new Font("monospace", Font.BOLD, 20));

			this.add(label);
		}

		private void onWin(String winner) {
			if (winner == null) {
				label.setText("The game is a tie!");
			} else {
				label.setText("The winner is " + winner + "!");
			}

		}
	}

	public static void main(String[] args) {
		Main main = new Main();
		main.initGUI();
		main.setVisible(true);
	}

	private static GridBagConstraints constraints(int x, int y, int width, Insets insets) {
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridx = x;
		constr.gridy = y;
		constr.gridwidth = width;
		constr.gridheight = 1;
		constr.insets = insets;
		constr.fill = GridBagConstraints.BOTH;
		return constr;
	}
}
