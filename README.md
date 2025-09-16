# 8-Puzzle Solver (Java)

A clean, fast implementation of the classic **8-puzzle**: action dynamics, legal-move enumeration, and multiple search strategies (**BFS**, **Uniform-Cost Search**, **Greedy Best-First**, **A\***). Includes an experiment runner to study time/space complexity under different heuristics.

---

## Overview

The 8-puzzle uses a 3×3 grid with tiles `1..8` and a blank `#`. A legal action moves the **blank** one cell `UP`, `DOWN`, `LEFT`, or `RIGHT` when in bounds.

Each program in this repo reads from **stdin** and prints a single result to **stdout**, which makes them easy to pipe/automate.

### Components

- **ImplementMoves.java** — Apply a single action to a board string. Illegal actions leave the board unchanged.  
- **AvailableMoves.java** — Print all legal actions for a given board in the fixed order `UP`, `DOWN`, `LEFT`, `RIGHT`.  
- **bfs.java** — **Breadth-First Search** with unit costs; prints the **optimal number of moves** from start to goal.  
- **UCS.java** — **Uniform-Cost Search / Dijkstra** with a configurable cost model. Default: `UP = 5`, others `= 1` (toggle to uniform if desired).  
- **GreedyBFS.java** — **Greedy Best-First Search** using **Manhattan** heuristic (sum of tile distances to goal; not guaranteed optimal).  
- **AStar.java** — **A\*** with **f = g + h** and the **Manhattan** heuristic (optimal under unit-cost moves).  
- **Experiments** — This folder contains 2 experiments, with reports, that I performed on the different search algorithms

> State encoding: a 9-character string in row-major order, e.g., `12345678#`.  
> Programs read the **start** and **goal** states on two separate lines unless otherwise noted.

---

## File Structure

```
.
├── ImplementMoves.java       # Apply a single move to the board
├── AvailableMoves.java       # List legal actions in order
├── bfs.java                  # BFS (unit-cost) shortest-path cost
├── UCS.java                  # Uniform-Cost Search (UP can be cost 5; toggleable)
├── GreedyBFS.java            # Greedy Best-First (priority = h only, Manhattan)
├── AStar.java                # A* (f = g + h, Manhattan)
├── Experiments/                  # PDFs/plots generated from experiments 
└── README.md                 # This file
```

---

## Input / Output

### ImplementMoves.java

**Input (two lines)**  
1. Board string (digits `1..8` and `#`), e.g., `1857#3462`  
2. Action: `UP`, `DOWN`, `LEFT`, or `RIGHT`

**Output**  
The resulting board (or the original if the action is illegal).

**Example**
```
In:
1857#3462
LEFT
Out:
185#73462
```

---

### AvailableMoves.java

**Input**  
Board string (digits `1..8` and `#`).

**Output**  
Each legal action on its own line in the fixed order: `UP`, `DOWN`, `LEFT`, `RIGHT`.

**Example**
```
In:
1857#3462
Out:
UP
DOWN
LEFT
RIGHT
```

---

### bfs.java (Breadth-First Search)

**Input (two lines)**  
1. Start board  
2. Goal board

**Output**  
Integer **cost** of the optimal plan (fewest moves). If unreachable (parity mismatch), prints `-1`.

**Example**
```
In:
78651#432
12345678#
Out:
25
```

---

### UCS.java (Uniform-Cost Search)

- **Cost model:** default is `UP = 5`, `DOWN/LEFT/RIGHT = 1`. (You can toggle the constant in the file to make all moves cost 1.)  
- **Input/Output:** same format as BFS; prints the **minimum path cost** under the configured costs.

**Example**
```
In:
1857#3462
1#5783462
Out:
2
```

---

### GreedyBFS.java (Greedy Best-First)

- **Heuristic:** sum of **Manhattan distances** of tiles `1..8` to their positions in the input goal (ignore `#`).  
- **Priority:** `h` only; **not guaranteed optimal**.  
- **Input/Output:** two lines (start, goal) → prints the returned path length.

**Example**
```
In:
5#1742638
524316#87
Out:
31
```

---

### AStar.java (A\*)

- **Heuristic:** sum of **Manhattan distances** (admissible & consistent for 4-connected moves).  
- **Priority:** `f = g + h`; **optimal** under unit-cost moves.  
- **Input/Output:** two lines (start, goal) → prints optimal cost.

**Example**
```
In:
5#1742638
524316#87
Out:
19
```

---

## Build & Run (Java 8+)

```bash
# Compile core tools & BFS
javac ImplementMoves.java AvailableMoves.java bfs.java

# Compile additional solvers
javac UCS.java GreedyBFS.java AStar.java

# Compile experiment (optional)
javac Experiment2.java
```

**Run — ImplementMoves**
```bash
echo -e "1857#3462\nLEFT" | java ImplementMoves
```

**Run — AvailableMoves**
```bash
echo "1857#3462" | java AvailableMoves
```

**Run — BFS**
```bash
echo -e "78651#432\n12345678#" | java bfs
```

**Run — UCS**
```bash
echo -e "1857#3462\n1#5783462" | java UCS
```

**Run — Greedy Best-First**
```bash
echo -e "5#1742638\n524316#87" | java GreedyBFS
```

**Run — A\***
```bash
echo -e "5#1742638\n524316#87" | java AStar
```

---

## Experiment 1: BFS Complexity vs Solution Depth (k)

This project includes an optional experiment that studies how the time and space usage of BFS scale with the optimal solution depth `k`.

### What it does
- **Generate start states at exact depth `k`** from the solved board (`12345678#`) using a **self‑avoiding random walk** (no revisits, uniform sampling among legal moves). This guarantees solvability and targets a specific depth.
- **Solve with BFS** back to the goal, recording:
  - **Time**: wall‑clock from BFS start to solution.
  - **Space**: number of **nodes expanded** (dequeued from the frontier).
- **Repeat 5 trials per `k`** and compute **mean ± 2σ** for both metrics.

### Java runner
- **`BFSExperiment.java`** — Generates instances for `k ∈ {2,4,6,8,10,12,14,16,18,20}` (configurable), runs BFS, and writes:
  - `experiment_results.csv` — raw records for every trial.
  - `experiment_aggregate.csv` — aggregated statistics (mean & 2σ for time and nodes per `k`).

**Build & run**
```bash
javac ImplementMoves.java AvailableMoves.java bfs.java BFSExperiment.java
java BFSExperiment
# -> writes experiment_results.csv and experiment_aggregate.csv
```


## Experiment 2: Complexity vs Solution Depth (k)

**Experiment2.java** explores how time/space scale as the **solution depth `k`** increases.

- **Start distribution:** generate states **exactly `k` moves** from the solved board (`12345678#`) via level-order BFS from the goal (guarantees correct depth and solvability).  
- **Algorithms & heuristics:** benchmark **GreedyBFS** and **A\*** with **Manhattan** and **Euclidean** heuristics.  
- **Metrics:**  
  - `time_ms` — wall-clock from first push to goal pop  
  - `nodes_expanded` — PQ pops (expansions)  
  - `max_stored` — peak memory proxy (`open + visited/bestG`)  
  - `cost` — path length returned  
- **Repetitions:** **5 trials** per `k`.

**Run**
```bash
javac Experiment2.java
java Experiment2 > Experiment2-results.csv
```

CSV schema:
```
algo,heuristic,k,trial,time_ms,nodes_expanded,max_stored,cost
```

Plot **mean ± 2σ** vs `k` for time and space (and optionally nodes expanded).

---

## Notes on Optimality & Heuristics

- **BFS** (unit costs) and **A\*** (with Manhattan) return **optimal** move counts.  
- **GreedyBFS** is **not optimal** in general: it expands by lowest `h` only and can be misled.  
- **Manhattan vs Euclidean:** on a 4-connected grid, **Manhattan** is typically the stronger heuristic (closer to true cost), so it tends to **reduce time/space** vs Euclidean for both GBFS and A\*.  
- **Solvability:** the 8-puzzle is solvable iff start and goal have the **same inversion parity** (ignoring `#`). Solvers short-circuit with `-1` when parity mismatches.

---

## Design Highlights

- **State encoding:** 9-char strings are immutable, hashable, and easy to print & compare.  
- **Search data structures:**  
  - BFS: `ArrayDeque` + `HashMap<String,Integer>` for distance  
  - UCS/GBFS/A\*: `PriorityQueue` with deterministic tie-breaking  
- **Heuristic precomputation:** goal positions pre-indexed for fast Manhattan/Euclidean evaluation.  
- **Determinism:** tie-breaking ensures stable runs and reproducible CSVs.

---

## Examples (I/O Recap)

```text
# BFS
start=78651#432, goal=12345678#  --> 25

# UCS (UP=5, others=1 by default)
start=1857#3462, goal=1#5783462  --> 2

# Greedy Best-First (Manhattan)
start=5#1742638, goal=524316#87  --> 31

# A* (Manhattan)
start=5#1742638, goal=524316#87  --> 19
```

---

## Reports

This repository includes **reports/plots** summarizing the experiment results (time, nodes expanded, peak space) with **mean ± 2σ** across depths `k`. Reproduce by running `Experiment2.java` and plotting `Experiment2-results.csv` in your tool of choice.

### Attribution
The Experiment programs (only) were generated with **ChatGPT 5** and is used **solely** to study time/space complexity and to test the new **GPT‑5** model. The core puzzle logic and various search algorithm solvers remain authored within this project.


