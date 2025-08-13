//Submission 1
import java.util.Scanner;

public class ImplementMoves {

    //Function to get index of the new position for the #
    public static int getIndex(String board, String move) {
        // Find position of #
        int hashIndex = board.indexOf("#");

        // LEFT move
        if (move.equals("LEFT")) {
            // Check if LEFT is possible
            if (hashIndex == 0 || hashIndex == 3 || hashIndex == 6) {
                System.out.println("Invalid move");
                return -1; // Invalid move
            } else { // LEFT is valid
                int newIndex = hashIndex - 1;
                //char currentNum = board.charAt(newIndex);
                return newIndex;
            }
        }else if(move.equals("RIGHT")){ //RIGHT move
            //Check if RIGHT is possible
            if(hashIndex == 2 || hashIndex == 5 || hashIndex == 8){
                System.out.println("Invalid move");
                return -1; // Invalid move
            }else{ //Right is valid
                int newIndex = hashIndex + 1;
                return newIndex;
            }
        }else if(move.equals("UP")){ //UP move
            //Check if UP is possible
            if(hashIndex < 3){
                System.out.println("Invalid move");
                return -1; // Invalid move
            }else{ //UP is valid
                int newIndex = hashIndex - 3;
                return newIndex;
            }
        }else if(move.equals("DOWN")){ //DOWN move
            //Check if DOWN is possible
            if(hashIndex > 5){
                System.out.println("Invalid move");
                return -1; // Invalid move
            }else{ //DOWN is valid
                int newIndex = hashIndex + 3;
                return newIndex;
            }
        }

        return -1;
    }

    //Function to change the position of # on the board
    public static String implementMove(String board, int moveIndex){
        //Get # position
        int hashIndex = board.indexOf("#");
        //Get number at the new index
        char newNum = board.charAt(moveIndex);

        //Change the board
        StringBuilder builder = new StringBuilder(board);
        builder.setCharAt(moveIndex, '#');
        builder.setCharAt(hashIndex, newNum);
        String newBoard = builder.toString();

        return newBoard;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String board = scanner.nextLine();
        String move = scanner.nextLine();

        int index = getIndex(board, move);
        String outputBoard = implementMove(board, index);
        System.out.println(outputBoard);

        scanner.close();
    }
}