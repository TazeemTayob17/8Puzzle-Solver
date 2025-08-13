import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class test {
    public static void main(String[] args) {
        String board = "1857#3462";
        String goal = "78651432#";
        /*
         * 1 8 5
         * 7 # 3
         * 4 6 2
         */
        
         /*ArrayList<String> test = new ArrayList<>();
        test.add(board);

        // ArrayList<String> moves = AvailableMoves.getMoves(test.get(0));
        // int count = 0;

        outerLoop: for (int i = 0; i < test.size(); i++) {

            ArrayList<String> moves = AvailableMoves.getMoves(test.get(i));// up down left right

            for (String move : moves) {
                int newIndex = ImplementMoves.getIndex(test.get(i), move);
                String newBoard = ImplementMoves.implementMove(test.get(i), newIndex);
                test.add(newBoard);
                if (newBoard.equals(goal)) {
                    break outerLoop;
                }
            }
            test.remove(0);
        }

        // print test
        for (String res : test) {
            System.out.println(res);
        }*/

        Queue<String> queue = new LinkedList<>();
        Queue<String> temp = new LinkedList<>();
        queue.add(board);
        int counter = 0;
        System.out.println("Beginning of program, the queue: " + queue);

        while(!queue.isEmpty()){
            /*ArrayList<String> moves = AvailableMoves.getMoves(queue.peek());
            for(String move : moves){
                int newIndex = ImplementMoves.getIndex(queue.peek(), move);
                String newBoard = ImplementMoves.implementMove(queue.peek(), newIndex);
                queue.add(newBoard);
            }
            queue.poll();
            //Iterate over entire queue to check if goal is found
            for(String element : queue){
                if(element.equals(goal)){
                    System.out.println("Goal found: " + element);
                    queue.clear();
                }
            }*/
            int queueSize = queue.size();
            for(int i = 0; i < queueSize; i++){
                ArrayList<String> moves = AvailableMoves.getMoves(queue.peek());
                for(String move : moves){
                    int newIndex = ImplementMoves.getIndex(queue.peek(), move);
                    String newBoard = ImplementMoves.implementMove(queue.peek(), newIndex);
                    temp.add(newBoard);
                }
                queue.poll();
            }

            //populate queue with temp
            while(!temp.isEmpty()){
                queue.add(temp.poll());
            }

            //Iterate over entire queue to check if goal is found
            for(String element : queue){
                if(element.equals(goal)){
                    System.out.println("Goal found: " + element);
                    queue.clear();
                    //break mainLoop; // Exit the main loop if goal is found
                }
            }

            counter++;
        }
        System.out.println("End of program, the queue: " + queue);
        System.out.println("Total iterations: " + counter);
    }
}
