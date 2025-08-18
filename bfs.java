import java.util.Scanner;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class bfs {

    public static int search(String start, String goal) {
        // Trivial cases
        if (start == null || goal == null)
            return -1;
        if (start.equals(goal))
            return 0;

        // Standard BFS
        ArrayDeque<String> queue = new ArrayDeque<>();
        HashMap<String, Integer> dist = new HashMap<>(100_000);
        queue.add(start);
        dist.put(start, 0);

        while (!queue.isEmpty()) {
            String cur = queue.poll();
            int d = dist.get(cur);

            // Expand in the required order via AvailableMoves (UP, DOWN, LEFT, RIGHT)
            ArrayList<String> moves = AvailableMoves.getMoves(cur);

            for (String move : moves) {
                // Use your ImplementMoves to generate the child state
                int index = ImplementMoves.getIndex(cur, move); // returns -1 if illegal (shouldn't happen here)
                if (index == -1)
                    continue;
                String next = ImplementMoves.implementMove(cur, index);

                if (!dist.containsKey(next)) {
                    dist.put(next, d + 1);
                    if (next.equals(goal)) {
                        return d + 1; // shortest cost found
                        // System.out.println(d + 1);
                        // return;
                    }
                    queue.add(next);
                }
            }
        }

        // If exhausted (shouldn't happen for solvable pairs), report -1
        return -1;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String start = scanner.nextLine().trim();
        String goal = scanner.nextLine().trim();

        int result = search(start, goal);
        System.out.println(result);

        scanner.close();
    }
}
