package com.devtomermoran.tictactoe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Main extends JFrame {

	private JPanel contentPane;

	private PreGameHeader preGameHeader;
	private GameHeader gameHeader;
	private GameBoard gameBoard;
	private PostGameFooter postGameFooter;

	private Main() {
		setSize(720, 640);
		setTitle("TicTacToe Game");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initGUI() {
		// Instantiate elements
		this.contentPane = new JPanel(new GridBagLayout());

		this.preGameHeader = new PreGameHeader();
		this.gameHeader = new GameHeader();
		this.postGameFooter = new PostGameFooter();
		this.gameBoard = new GameBoard(endGameListener());

		// Construct GUI
		gameHeader.initGUI();
		preGameHeader.initGUI((ActionEvent ae) -> {
			gameHeader.updateNames(preGameHeader.getPlayer1(), preGameHeader.getPlayer2());

			preGameHeader.setVisible(false);
			gameHeader.setVisible(true);

			gameBoard.startGame();
		});

		gameHeader.setVisible(false);
		postGameFooter.setVisible(false);

		Insets zero = new Insets(0, 0, 0, 0);
		contentPane.add(preGameHeader, constraints(0, 0, 1, zero));
		contentPane.add(gameHeader, constraints(0, 0, 1, zero));
		contentPane.add(gameBoard, constraints(0, 1, 1, zero));
		contentPane.add(postGameFooter, constraints(0, 2, 1, zero));
		this.setContentPane(contentPane);
	}

	private Consumer<Integer> endGameListener() {
		return (Integer winner) -> {
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
			gameHeader.setVisible(false);
			postGameFooter.setVisible(true);
		};
	}

	private static class PreGameHeader extends JPanel {

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

		private void initGUI(ActionListener onStart) {
			this.add(label1, constraints(0, 0, 1, new Insets(15, 0, 0, 5)));
			this.add(field1, constraints(1, 0, 1, new Insets(15, 0, 0, 20)));
			this.add(label2, constraints(2, 0, 1, new Insets(15, 0, 0, 5)));
			this.add(field2, constraints(3, 0, 0, new Insets(15, 0, 0, 0)));
			this.add(startButton, constraints(0, 1, 4, new Insets(20, 0, 20, 0)));

			Font boldFont = new Font("Monospace", Font.BOLD, 15);
			Font plainFont = new Font("Monospace", Font.PLAIN, 15);
			label1.setFont(boldFont);
			label2.setFont(boldFont);
			field1.setFont(plainFont);
			field2.setFont(plainFont);
			startButton.setFont(boldFont);

			startButton.setEnabled(false);
			startButton.addActionListener(onStart);

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

	private static class GameHeader extends JPanel {

		private final JLabel label1, label2;

		private GameHeader() {
			super(new GridBagLayout());

			this.label1 = new JLabel();
			this.label2 = new JLabel();
			this.label2.setAlignmentX(RIGHT_ALIGNMENT);

			Font font = new Font("monospace", Font.BOLD, 30);
			this.label1.setFont(font);
			this.label2.setFont(font);
		}

		private void initGUI() {
			this.add(label1, constraints(0, 0, 1, new Insets(0, 0, 20, 150)));
			this.add(label2, constraints(1, 0, 1, new Insets(0, 150, 20, 0)));
		}

		private void updateNames(String name1, String name2) {
			label1.setText(name1);
			label2.setText(name2);
		}
	}

	private static class GameBoard extends JPanel {

		private static final char SYMBOLS[] = {'X', 'O'};

		private final JButton buttons[][];
		private final char board[][] = new char[3][3];

		private int moveCounter;
		private final Consumer<Integer> endGameListener;

		private GameBoard(Consumer<Integer> endgameListener) {
			super(new GridBagLayout());
			this.endGameListener = endgameListener;

			this.buttons = new JButton[3][3];
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 3; ++j) {
					JButton button = new JButton("X");
					button.setEnabled(false);
					button.setFont(new Font("monospace", Font.BOLD, 90));
					button.setPreferredSize(new Dimension(150, 150));

					GridBagConstraints constr = new GridBagConstraints();
					constr.gridx = j;
					constr.gridy = i;
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

				++moveCounter;
				int winner = checkWinner(player);
				if (winner != 0) {
					endGameListener.accept(winner);
				}
			};
		}

		private void startGame() {
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

	private static class PostGameFooter extends JPanel {

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
		return constr;
	}
}
