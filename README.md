# 8‑Puzzle Solver (Java)

A clean, fast implementation of the classic **8‑puzzle**: action dynamics (apply a move), available‑actions enumeration, and **Breadth‑First Search (BFS)** for shortest‑path cost.

## Overview

The 8‑puzzle consists of a 3×3 grid with tiles `1..8` and a blank `#`. A legal action moves the **blank** one cell `UP`, `DOWN`, `LEFT`, or `RIGHT` when in bounds.  
This project includes small, composable programs:

- **ImplementMoves.java** — Apply a single action to a board string. Illegal actions leave the board unchanged.
- **AvailableMoves.java** — Print all legal actions for a given board in the fixed order `UP`, `DOWN`, `LEFT`, `RIGHT`.
- **bfs.java** — Compute the **optimal number of moves** from a start state to a goal state using BFS (unit cost per move).

> Programs are designed for piping/automation: read from stdin, print one result, no extra text.

---

## File Structure

```
.
├── ImplementMoves.java     # Apply a single move to the board
├── AvailableMoves.java     # List legal actions in order
└── bfs.java          # BFS shortest‑path cost (search method + main)
```

The BFS solver reuses `ImplementMoves` and `AvailableMoves`. Core search logic is in `search(String start, String goal)` for easy testing.

---

## Input / Output

### ImplementMoves.java

- **Input (two lines):**
  1. Board string (9 chars using digits `1..8` and `#`), e.g., `1857#3462`
  2. Action: `UP`, `DOWN`, `LEFT`, or `RIGHT`

- **Output (one line):**  
  The resulting board. If the action is illegal, the original board is printed unchanged.

**Examples**
```
In:
1857#3462
LEFT
Out:
185#73462
```
```
In:
18573#462
UP
Out:
18#735462
```
```
In:
78651#432
DOWN
Out:
78651243#
```

### AvailableMoves.java

- **Input (one line):**  
  Board string (9 chars of `1..8` and `#`).

- **Output (0–4 lines):**  
  Each legal action on its own line in this fixed order: `UP`, `DOWN`, `LEFT`, `RIGHT`.

**Examples**
```
In:
1857#3462
Out:
UP
DOWN
LEFT
RIGHT
```
```
In:
18573#462
Out:
UP
DOWN
LEFT
```
```
In:
78651432#
Out:
UP
LEFT
```

### bfs.java

- **Input (two lines):**
  1. Start board
  2. Goal board

- **Output (one line):**  
  Integer **cost** of the optimal plan (fewest moves).

**Examples**
```
In:
78651#432
12345678#
Out:
25
```
```
In:
1857#3462
185#73462
Out:
1
```
```
In:
1857#3462
78651432#
Out:
20
```

---

## Build & Run (Java)

> Requires Java 8+ (JDK). On Windows, you can run `java ClassName` and paste inputs interactively.

### Compile
```bash
javac ImplementMoves.java AvailableMoves.java bfs.java
```

### Run — ImplementMoves
```bash
echo -e "1857#3462\nLEFT" | java ImplementMoves
```

### Run — AvailableMoves
```bash
echo "1857#3462" | java AvailableMoves
```

### Run — bfs (BFS)
```bash
echo -e "78651#432\n12345678#" | java bfs
# -> 25
```

---

## Experiment: BFS Complexity vs Solution Depth (k)

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

**Plotting**
- Import the CSVs into Excel/Google Sheets, and create error‑bar plots of **mean ± 2σ** for:
  - Time (seconds) vs `k`
  - Nodes expanded vs `k`

> Tip: The results should exhibit near‑exponential growth consistent with BFS complexity on unit‑cost graphs (≈ O(b^k)).

### Attribution
The **experiment driver code** (only) was generated with **ChatGPT 5** and is used **solely** to study time/space complexity and to test the new **GPT‑5** model. The core puzzle logic and BFS solver remain authored within this project.

---

## Design Notes

### State Encoding
- Board is a 9‑character string in row‑major order, e.g., `12345678#`; the blank is `#`.
- Strings are immutable and work well as keys in hash‑based structures.

### BFS Data Structures
- **Frontier:** `ArrayDeque<String>` — queue with amortized O(1) `addLast/removeFirst`, cache‑friendly.
- **Visited/Distance:** `HashMap<String, Integer>` — O(1) average membership and depth lookup.

### Solvability Check
- On a 3×3 board, a start can reach a goal iff both have the **same inversion parity** (inversions counted ignoring `#`).  
- The solver checks parity first; if mismatched, it returns `-1` immediately.

### Correctness & Complexity
- BFS on unit‑cost edges yields an **optimal** move count.  
- Complexity is `O(b^d)` in time and space (branching factor `b ≈ 2–3` on average).

---

## Testing Tips

- Match the I/O format exactly—no extra lines or messages.
- For illegal moves in `ImplementMoves`, print the **original** board unchanged.
- For `AvailableMoves`, always print actions in the order: `UP`, `DOWN`, `LEFT`, `RIGHT` when legal.

---
