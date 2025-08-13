import java.io.*;
import java.util.*;

/**
 * 8-Puzzle BFS Complexity Experiment (Java)
 * - Generates states at exact depth k via self-avoiding random walk from the goal.
 * - Solves with BFS, measures time (wall clock) and nodes expanded.
 * - Repeats trials per k, writes raw + aggregate CSVs.
 *
 * Requires:
 *   ImplementMoves.java  (apply move: getIndex + implementMove)
 *   AvailableMoves.java  (getMoves in order: UP, DOWN, LEFT, RIGHT)
 */
public class BFSExperiment {

    private static final String GOAL = "12345678#";
    private static final Random RNG = new Random(42L); // reproducible

    // ---------- Utility: parity check (inversion parity ignoring '#') ----------
    private static int invParity(String s) {
        char[] a = new char[8];
        int k = 0;
        for (int i = 0; i < 9; i++) {
            char ch = s.charAt(i);
            if (ch != '#') a[k++] = ch;
        }
        int inv = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = i + 1; j < 8; j++) {
                if (a[i] > a[j]) inv++;
            }
        }
        return inv & 1;
    }

    // ---------- BFS that returns cost, expanded, and elapsed time (ns) ----------
    static final class BFSStats {
        final int cost;            // -1 if unreachable
        final int expanded;        // nodes popped from frontier
        final long elapsedNanos;   // wall-clock

        BFSStats(int cost, int expanded, long elapsedNanos) {
            this.cost = cost;
            this.expanded = expanded;
            this.elapsedNanos = elapsedNanos;
        }
    }

    public static BFSStats bfsWithStats(String start, String goal) {
        if (start.equals(goal)) return new BFSStats(0, 0, 0L);
        if (invParity(start) != invParity(goal)) return new BFSStats(-1, 0, 0L);

        long t0 = System.nanoTime();
        ArrayDeque<String> q = new ArrayDeque<>();
        HashMap<String, Integer> dist = new HashMap<>(100_000);
        q.addLast(start);
        dist.put(start, 0);
        int expanded = 0;

        while (!q.isEmpty()) {
            String cur = q.removeFirst();
            expanded++;
            int d = dist.get(cur);

            // Expand using your AvailableMoves ordering (UP,DOWN,LEFT,RIGHT)
            ArrayList<String> moves = AvailableMoves.getMoves(cur);
            for (String mv : moves) {
                int idx = ImplementMoves.getIndex(cur, mv); // valid here
                if (idx == -1) continue; // defensive (shouldn't happen)
                String nxt = ImplementMoves.implementMove(cur, idx);
                if (dist.containsKey(nxt)) continue;

                int nd = d + 1;
                if (nxt.equals(goal)) {
                    long t1 = System.nanoTime();
                    return new BFSStats(nd, expanded, t1 - t0);
                }
                dist.put(nxt, nd);
                q.addLast(nxt);
            }
        }
        long t1 = System.nanoTime();
        return new BFSStats(-1, expanded, t1 - t0);
    }

    // ---------- Generate a state at EXACT depth k via self-avoiding random walk ----------
    public static String generateStateAtExactDepth(int k) {
        final int MAX_RESTARTS = 300;
        for (int attempt = 0; attempt < MAX_RESTARTS; attempt++) {
            String s = GOAL;
            HashSet<String> seen = new HashSet<>();
            seen.add(s);

            boolean restart = false;
            for (int step = 0; step < k; step++) {
                // build candidate moves that don't revisit
                ArrayList<String> legal = AvailableMoves.getMoves(s);
                ArrayList<String> candidates = new ArrayList<>();
                for (String mv : legal) {
                    int idx = ImplementMoves.getIndex(s, mv);
                    if (idx == -1) continue;
                    String nxt = ImplementMoves.implementMove(s, idx);
                    if (!seen.contains(nxt)) {
                        candidates.add(nxt);
                    }
                }
                if (candidates.isEmpty()) {
                    restart = true;
                    break; // dead-end; restart
                }
                // uniformly sample next state
                s = candidates.get(RNG.nextInt(candidates.size()));
                seen.add(s);
            }
            if (restart) continue;

            // Verify exact depth back to GOAL
            BFSStats st = bfsWithStats(s, GOAL);
            if (st.cost == k) return s;
        }
        // Fallback: best-effort (should be rare for k ≤ 20)
        return GOAL;
    }

    // ---------- Aggregation helpers ----------
    static final class RunningStats {
        int n = 0;
        double sum = 0.0;
        double sumsq = 0.0;

        void add(double x) { n++; sum += x; sumsq += x * x; }
        double mean() { return sum / n; }
        double popStd() {
            if (n == 0) return 0.0;
            double mu = mean();
            return Math.sqrt(Math.max(0.0, (sumsq / n) - mu * mu));
        }
    }

    // ---------- Main: run experiment, write CSVs ----------
    public static void main(String[] args) throws Exception {
        // Configure depths and trials
        int[] kValues = new int[] {2,4,6,8,10,12,14,16,18,20};
        int trials = 5;

        // Output files (relative to working directory)
        File rawCsv = new File("experiment_results.csv");
        File aggCsv = new File("experiment_aggregate.csv");

        try (PrintWriter raw = new PrintWriter(new FileWriter(rawCsv))) {
            raw.println("k,trial,state,cost_found,nodes_expanded,time_sec");

            // For aggregation: k -> stats
            Map<Integer, RunningStats> timeStats = new HashMap<>();
            Map<Integer, RunningStats> nodeStats = new HashMap<>();
            Map<Integer, RunningStats> costStats = new HashMap<>();

            for (int k : kValues) {
                timeStats.put(k, new RunningStats());
                nodeStats.put(k, new RunningStats());
                costStats.put(k, new RunningStats());

                for (int t = 1; t <= trials; t++) {
                    String state = generateStateAtExactDepth(k);
                    BFSStats st = bfsWithStats(state, GOAL);

                    double timeSec = st.elapsedNanos / 1e9;
                    raw.printf(Locale.US, "%d,%d,%s,%d,%d,%.9f%n",
                            k, t, state, st.cost, st.expanded, timeSec);

                    // accumulate
                    timeStats.get(k).add(timeSec);
                    nodeStats.get(k).add(st.expanded);
                    costStats.get(k).add(st.cost);
                }
            }

            // Write aggregate CSV
            try (PrintWriter agg = new PrintWriter(new FileWriter(aggCsv))) {
                agg.println("k,time_mean,time_2std,nodes_mean,nodes_2std,cost_mean");
                for (int k : kValues) {
                    RunningStats ts = timeStats.get(k);
                    RunningStats ns = nodeStats.get(k);
                    RunningStats cs = costStats.get(k);
                    double tMean = ts.mean();
                    double t2std = 2.0 * ts.popStd();
                    double nMean = ns.mean();
                    double n2std = 2.0 * ns.popStd();
                    double cMean = cs.mean();

                    agg.printf(Locale.US, "%d,%.9f,%.9f,%.3f,%.3f,%.3f%n",
                            k, tMean, t2std, nMean, n2std, cMean);
                }
            }
        }

        System.out.println("Wrote:");
        System.out.println(" - " + rawCsv.getAbsolutePath());
        System.out.println(" - " + aggCsv.getAbsolutePath());
        System.out.println("Import the CSVs into Excel/Sheets to plot mean ± 2σ for time and nodes.");
    }
}

