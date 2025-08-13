
//Program to visualise board
import java.util.Scanner;

public class VisBoard {

    //Function to create 3x3 board
    public static String[][] createBoard(String board){
        String[][] matrix = new String[3][3];
        int count = 0;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                matrix[i][j] = String.valueOf(board.charAt(count));
                count++;
            }
        }

        return matrix;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String board = scanner.nextLine();
        String[][] matrix = createBoard(board);
        //Print the board
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

        scanner.close();
    }
}
