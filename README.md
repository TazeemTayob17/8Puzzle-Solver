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
  Integer **cost** of the optimal plan (fewest moves). If unreachable (parity mismatch), prints `-1`.

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
echo -e "1857#3462
LEFT" | java ImplementMoves
```

### Run — AvailableMoves
```bash
echo "1857#3462" | java AvailableMoves
```

### Run — bfs (BFS)
```bash
echo -e "78651#432
12345678#" | java bfs
# -> 25
```

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
