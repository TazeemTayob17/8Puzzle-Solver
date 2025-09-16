import java.util.*;
//import java.io.*;

public class Experiment2 {

    // -------------------- Heuristics --------------------
    static int manhattan(String state, String goal) {
        int[] gRow = new int[128], gCol = new int[128];
        for (int i = 0; i < 9; i++) {
            char ch = goal.charAt(i);
            if (ch != '#') { gRow[ch] = i / 3; gCol[ch] = i % 3; }
        }
        int h = 0;
        for (int i = 0; i < 9; i++) {
            char ch = state.charAt(i);
            if (ch == '#') continue;
            int r = i / 3, c = i % 3;
            h += Math.abs(r - gRow[ch]) + Math.abs(c - gCol[ch]);
        }
        return h;
    }

    static double euclidean(String state, String goal) {
        int[] gRow = new int[128], gCol = new int[128];
        for (int i = 0; i < 9; i++) {
            char ch = goal.charAt(i);
            if (ch != '#') { gRow[ch] = i / 3; gCol[ch] = i % 3; }
        }
        double h = 0.0;
        for (int i = 0; i < 9; i++) {
            char ch = state.charAt(i);
            if (ch == '#') continue;
            int r = i / 3, c = i % 3;
            int dr = r - gRow[ch], dc = c - gCol[ch];
            h += Math.sqrt(dr*dr + dc*dc);
        }
        return h;
    }

    // -------------------- Utilities --------------------
    static List<String> neighborsUDLR(String s) {
        ArrayList<String> out = new ArrayList<>(4);
        int bi = s.indexOf('#');
        int r = bi / 3, c = bi % 3;
        if (r > 0) out.add(swap(s, bi, bi - 3)); // UP
        if (r < 2) out.add(swap(s, bi, bi + 3)); // DOWN
        if (c > 0) out.add(swap(s, bi, bi - 1)); // LEFT
        if (c < 2) out.add(swap(s, bi, bi + 1)); // RIGHT
        return out;
    }

    static String swap(String s, int i, int j) {
        char[] a = s.toCharArray();
        char t = a[i]; a[i] = a[j]; a[j] = t;
        return new String(a);
    }

    static boolean solvable(String a, String b) {
        return parity(a) == parity(b);
    }

    static int parity(String t) {
        int[] a = new int[8]; int k=0, inv=0;
        for (int i=0;i<9;i++) if (t.charAt(i)!='#') a[k++]=t.charAt(i)-'0';
        for (int i=0;i<8;i++) for (int j=i+1;j<8;j++) if (a[i]>a[j]) inv++;
        return inv & 1;
    }

    // Generate states at exact depth k from 'goal' using level-order BFS
    static List<String> sampleStatesAtDepthK(String goal, int k, int maxSamples, long seed) {
        if (k==0) return Collections.singletonList(goal);
        ArrayDeque<String> q = new ArrayDeque<>();
        HashMap<String,Integer> depth = new HashMap<>();
        q.add(goal); depth.put(goal,0);
        ArrayList<String> levelK = new ArrayList<>();
        Random rnd = new Random(seed);

        while(!q.isEmpty()){
            String s = q.poll();
            int d = depth.get(s);
            if (d == k) { levelK.add(s); continue; }
            List<String> nbrs = neighborsUDLR(s);
            for (int i = nbrs.size()-1; i>0; i--) {
                int j = rnd.nextInt(i+1);
                String tmp = nbrs.get(i); nbrs.set(i, nbrs.get(j)); nbrs.set(j, tmp);
            }
            for (String nxt: nbrs) {
                if (depth.containsKey(nxt)) continue;
                depth.put(nxt, d+1);
                q.add(nxt);
            }
        }
        Collections.shuffle(levelK, new Random(seed+1));
        if (levelK.size() > maxSamples) return levelK.subList(0, maxSamples);
        return levelK;
    }

    // -------------------- GBFS (priority = h only) --------------------
    static class GBFSResult {
        int cost; long ms; int nodesExpanded; int maxStored;
    }
    static GBFSResult greedyBFS(String start, String goal, boolean useEuclidean) {
        GBFSResult res = new GBFSResult();
        long t0 = System.nanoTime();

        Comparator<Object[]> cmp = (a,b)->{
            int c = Double.compare((Double)a[0], (Double)b[0]);
            if (c!=0) return c;
            return ((String)a[1]).compareTo((String)b[1]);
        };
        PriorityQueue<Object[]> pq = new PriorityQueue<>(cmp);
        HashSet<String> seen = new HashSet<>(100_000);

        double h0 = useEuclidean ? euclidean(start, goal) : manhattan(start, goal);
        pq.add(new Object[]{h0, start, 0});
        seen.add(start);

        int maxStored = pq.size() + seen.size();
        int expanded = 0;

        while(!pq.isEmpty()){
            Object[] top = pq.poll();
            String s = (String) top[1];
            int d = (Integer) top[2];
            expanded++;

            if (s.equals(goal)) {
                res.cost = d;
                res.ms = (System.nanoTime()-t0)/1_000_000;
                res.nodesExpanded = expanded;
                res.maxStored = Math.max(maxStored, pq.size()+seen.size());
                return res;
            }

            int bi = s.indexOf('#');
            int r = bi/3, c = bi%3;

            // local helper to push neighbor
            java.util.function.IntConsumer push = (j)->{
                char[] a = s.toCharArray();
                char t = a[bi]; a[bi]=a[j]; a[j]=t;
                String nxt = new String(a);
                if (seen.add(nxt)) {
                    double h = useEuclidean ? euclidean(nxt, goal) : manhattan(nxt, goal);
                    pq.add(new Object[]{h, nxt, d+1});
                }
            };

            if (r>0) push.accept(bi - 3);
            if (r<2) push.accept(bi + 3);
            if (c>0) push.accept(bi - 1);
            if (c<2) push.accept(bi + 1);

            maxStored = Math.max(maxStored, pq.size()+seen.size());
        }
        res.cost = -1;
        res.ms = (System.nanoTime()-t0)/1_000_000;
        res.nodesExpanded = expanded;
        res.maxStored = Math.max(maxStored, pq.size()+seen.size());
        return res;
    }

    // -------------------- A* (f = g + h) --------------------
    static class AStarResult {
        int cost; long ms; int nodesExpanded; int maxStored;
    }
    static AStarResult aStar(String start, String goal, boolean useEuclidean) {
        AStarResult res = new AStarResult();
        long t0 = System.nanoTime();

        Comparator<Object[]> cmp = (a,b)->{
            int cf = Double.compare((Double)a[0], (Double)b[0]);
            if (cf!=0) return cf;
            int ch = Double.compare((Double)a[1], (Double)b[1]);
            if (ch!=0) return ch;
            return ((String)a[2]).compareTo((String)b[2]);
        };
        PriorityQueue<Object[]> pq = new PriorityQueue<>(cmp);
        HashMap<String,Integer> bestG = new HashMap<>(100_000);

        double h0 = useEuclidean ? euclidean(start, goal) : manhattan(start, goal);
        pq.add(new Object[]{ 0.0 + h0, h0, start, 0 });
        bestG.put(start, 0);

        int maxStored = pq.size() + bestG.size();
        int expanded = 0;

        while(!pq.isEmpty()){
            Object[] top = pq.poll();
            //double f = (Double) top[0];
            //double h = (Double) top[1];
            String s = (String) top[2];
            int g = (Integer) top[3];

            if (g > bestG.getOrDefault(s, Integer.MAX_VALUE)) continue;

            expanded++;
            if (s.equals(goal)) {
                res.cost = g;
                res.ms = (System.nanoTime()-t0)/1_000_000;
                res.nodesExpanded = expanded;
                res.maxStored = Math.max(maxStored, pq.size()+bestG.size());
                return res;
            }

            int bi = s.indexOf('#');
            int r = bi/3, c = bi%3;

            java.util.function.IntConsumer push = (j)->{
                char[] a = s.toCharArray();
                char t2 = a[bi]; a[bi]=a[j]; a[j]=t2;
                String nxt = new String(a);
                int ng = g + 1;
                if (ng < bestG.getOrDefault(nxt, Integer.MAX_VALUE)) {
                    double nh = useEuclidean ? euclidean(nxt, goal) : manhattan(nxt, goal);
                    bestG.put(nxt, ng);
                    pq.add(new Object[]{ ng + nh, nh, nxt, ng });
                }
            };

            if (r>0) push.accept(bi - 3);
            if (r<2) push.accept(bi + 3);
            if (c>0) push.accept(bi - 1);
            if (c<2) push.accept(bi + 1);

            maxStored = Math.max(maxStored, pq.size()+bestG.size());
        }
        res.cost = -1;
        res.ms = (System.nanoTime()-t0)/1_000_000;
        res.nodesExpanded = expanded;
        res.maxStored = Math.max(maxStored, pq.size()+bestG.size());
        return res;
    }

    // -------------------- Runner --------------------
    public static void main(String[] args) throws Exception {
        final String GOAL = "12345678#";
        int[] K_VALUES = new int[] { 5, 10, 15, 20, 25, 30 };
        int TRIALS = 5;

        System.out.println("algo,heuristic,k,trial,time_ms,nodes_expanded,max_stored,cost");

        long seedBase = 42;
        for (int k : K_VALUES) {
            List<String> pool = sampleStatesAtDepthK(GOAL, k, TRIALS*2, seedBase + k);
            if (pool.isEmpty()) continue;

            for (int t = 0; t < TRIALS; t++) {
                String start = pool.get(t % pool.size());
                if (!solvable(start, GOAL)) continue;

                GBFSResult g_m = greedyBFS(start, GOAL, false);
                System.out.printf(Locale.US, "GBFS,Manhattan,%d,%d,%.3f,%d,%d,%d%n",
                        k, t+1, (double)g_m.ms, g_m.nodesExpanded, g_m.maxStored, g_m.cost);

                GBFSResult g_e = greedyBFS(start, GOAL, true);
                System.out.printf(Locale.US, "GBFS,Euclidean,%d,%d,%.3f,%d,%d,%d%n",
                        k, t+1, (double)g_e.ms, g_e.nodesExpanded, g_e.maxStored, g_e.cost);

                AStarResult a_m = aStar(start, GOAL, false);
                System.out.printf(Locale.US, "AStar,Manhattan,%d,%d,%.3f,%d,%d,%d%n",
                        k, t+1, (double)a_m.ms, a_m.nodesExpanded, a_m.maxStored, a_m.cost);

                AStarResult a_e = aStar(start, GOAL, true);
                System.out.printf(Locale.US, "AStar,Euclidean,%d,%d,%.3f,%d,%d,%d%n",
                        k, t+1, (double)a_e.ms, a_e.nodesExpanded, a_e.maxStored, a_e.cost);
            }
        }
    }}
 

    

