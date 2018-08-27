package tictactoegame;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Get scanner
        Scanner scanner = new Scanner(System.in);
        
        boolean play = true;
        while (play) {
            // Get player names
            String[] names = getPlayerNames(scanner);
            
            // Init and start game
            TicTacToe game = new TicTacToe(scanner, names[0], names[1]);
            game.play();
            
            System.out.println("Do you want to play again? [y/n]");
            play = scanner.next().toLowerCase().startsWith("y");
        }
    }
    
    private static String[] getPlayerNames(Scanner scanner) {
        System.out.print("Enter name for player 1: ");
        String name1 = scanner.next();
        System.out.print("Enter name for player 2: ");
        String name2 = scanner.next();
        
        return new String[]{name1, name2};
    }
}
