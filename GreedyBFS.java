import java.util.*;

public class GreedyBFS {

    private static int heuristic(String start, String goal) {
        // Initialize start and goal matrices
        char[][] goalMatrix = new char[3][3];
        char[][] startMatrix = new char[3][3];
        int temp = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                goalMatrix[i][j] = goal.charAt(temp);
                startMatrix[i][j] = start.charAt(temp);
                temp++;
            }
        }

        // Implement easy way to find position of each element in goal state
        HashMap<Character, Integer> goalRow = new HashMap<>();
        HashMap<Character, Integer> goalCol = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char val = goalMatrix[i][j];
                if (val == '#')
                    continue; // skip hashtag
                goalRow.put(val, i);
                goalCol.put(val, j);
            }
        }

        // Calculate heuristic value
        int heuristic = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char s = startMatrix[i][j];
                if (s == '#')
                    continue; // skip hashtag
                int targetRow = goalRow.get(s);
                int targetCol = goalCol.get(s);
                heuristic += Math.abs(i - targetRow) + Math.abs(j - targetCol);
            }
        }

        return heuristic;
    }

    // Greedy Best-First Search (priority = h only). Returns number of moves.
    public static int greedyBFS(String start, String goal) {
        if (start == null || goal == null)
            return -1;
        if (start.equals(goal))
            return 0;

        // PQ entries: [h, state, depth]; tie-break by lexicographic state
        Comparator<Object[]> cmp = (a, b) -> {
            int ha = (Integer) a[0], hb = (Integer) b[0];
            if (ha != hb)
                return Integer.compare(ha, hb);
            return ((String) a[1]).compareTo((String) b[1]);
        };
        PriorityQueue<Object[]> pq = new PriorityQueue<>(cmp);
        HashSet<String> seen = new HashSet<>(100_000);

        pq.add(new Object[] { heuristic(start, goal), start, 0 });
        seen.add(start);

        while (!pq.isEmpty()) {
            Object[] top = pq.poll();
            String s = (String) top[1];
            int d = (Integer) top[2];

            if (s.equals(goal))
                return d;

            // Generate neighbors inline (UDLR, no wrapping)
            int bi = s.indexOf('#');
            int r = bi / 3, c = bi % 3;

            // helper to push a neighbor (local to this method, not a class helper)
            java.util.function.IntConsumer push = (swapIdx) -> {
                char[] a = s.toCharArray();
                char tmp = a[bi];
                a[bi] = a[swapIdx];
                a[swapIdx] = tmp;
                String nxt = new String(a);
                if (seen.add(nxt)) { // mark seen on enqueue
                    pq.add(new Object[] { heuristic(nxt, goal), nxt, d + 1 });
                }
            };

            if (r > 0)
                push.accept(bi - 3); // UP
            if (r < 2)
                push.accept(bi + 3); // DOWN
            if (c > 0)
                push.accept(bi - 1); // LEFT
            if (c < 2)
                push.accept(bi + 1); // RIGHT
        }
        return -1;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String start = scanner.nextLine().trim();
        String goal = scanner.nextLine().trim();

        int result = greedyBFS(start, goal);
        System.out.println(result);

        scanner.close();
    }
}