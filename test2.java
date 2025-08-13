import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class test2 {

    
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String start = br.readLine();
        String goal  = br.readLine();
        if (start == null || goal == null) return;
        start = start.trim();
        goal  = goal.trim();

        // Trivial case
        if (start.equals(goal)) {
            System.out.println(0);
            return;
        }

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
                int index = ImplementMoves.getIndex(cur, move);  // returns -1 if illegal (shouldn't happen here)
                if (index == -1) continue;
                String next = ImplementMoves.implementMove(cur, index);

                if (!dist.containsKey(next)) {
                    dist.put(next, d + 1);
                    if (next.equals(goal)) {
                        System.out.println(d + 1); // shortest cost found
                        return;
                    }
                    queue.add(next);
                }
            }
        }

        // If exhausted (shouldn't happen for solvable pairs), report -1
        System.out.println(-1);
    }
}
    

