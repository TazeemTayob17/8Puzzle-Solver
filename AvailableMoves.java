//Submission 2
import java.util.ArrayList;
import java.util.Scanner;

public class AvailableMoves {

    //Function to get all possible moves
    public static ArrayList<String> getMoves(String board){
        ArrayList<String> possibleMoves = new ArrayList<>();
        //Get index of #
        int hashIndex = board.indexOf("#");

        if(hashIndex > 2){
            possibleMoves.add("UP");
        }
        if(hashIndex < 6){
            possibleMoves.add("DOWN");
        }
        if(hashIndex % 3 != 0){
            possibleMoves.add("LEFT");
        }
        if(hashIndex % 3 != 2){
            possibleMoves.add("RIGHT");
        }

        return possibleMoves;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String board = scanner.nextLine();

        ArrayList<String> moves = getMoves(board);
        
        for(int i = 0; i < moves.size(); i++){
            System.out.println(moves.get(i));
        }

        scanner.close();
    }
}
