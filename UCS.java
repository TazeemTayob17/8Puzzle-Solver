import java.util.*;

public class UCS {

    // Toggle: true => UP=5, others=1. false => all=1 (uniform).
    private static final boolean HEAVY_UP = true;

    private static int stepCost(String move) {
        if (!HEAVY_UP)
            return 1;
        return "UP".equals(move) ? 5 : 1;
    }

    public static int uniformCostSearch(String start, String goal) {
        if (start == null || goal == null)
            return -1;
        if (start.equals(goal))
            return 0;

        // PriorityQueue entries: [gCost(Integer), tie(Integer), state(String)]
        Comparator<Object[]> cmp = (a, b) -> {
            int c = Integer.compare((Integer) a[0], (Integer) b[0]);
            if (c != 0)
                return c;
            return Integer.compare((Integer) a[1], (Integer) b[1]); // stable tie-break
        };
        PriorityQueue<Object[]> pq = new PriorityQueue<>(cmp);

        // best known cost to reach each state
        HashMap<String, Integer> best = new HashMap<>(100_000);

        int tie = 0;
        pq.add(new Object[] { 0, tie++, start });
        best.put(start, 0);

        while (!pq.isEmpty()) {
            Object[] top = pq.poll();
            int g = (Integer) top[0];
            String s = (String) top[2];

            // Skip stale entries (we've already found a better path to s)
            if (g > best.getOrDefault(s, Integer.MAX_VALUE))
                continue;

            // Goal test on POP (Dijkstra property)
            if (s.equals(goal))
                return g;

            // Expand neighbors in required order: UP, DOWN, LEFT, RIGHT
            for (String move : AvailableMoves.getMoves(s)) {
                int idx = ImplementMoves.getIndex(s, move);
                if (idx == -1)
                    continue;
                String nxt = ImplementMoves.implementMove(s, idx);

                int ng = g + stepCost(move);
                if (ng < best.getOrDefault(nxt, Integer.MAX_VALUE)) {
                    best.put(nxt, ng);
                    pq.add(new Object[] { ng, tie++, nxt });
                }
            }
        }
        return -1; // no path (shouldn't happen on valid inputs)
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String start = scanner.nextLine().trim();
        String goal = scanner.nextLine().trim();
        int result = uniformCostSearch(start, goal);
        System.out.println(result);
        scanner.close();
    }
}
