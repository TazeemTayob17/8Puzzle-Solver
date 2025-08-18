import java.util.Scanner;

public class ImplementMoves {

    // Validate that the board is exactly 9 chars and contains digits 1..8 and exactly one '#'
    private static boolean isValidBoard(String board) {
        if (board == null) return false;
        if (board.length() != 9) return false;

        boolean[] seen = new boolean[128];
        int digits = 0, blanks = 0;

        for (int i = 0; i < 9; i++) {
            char ch = board.charAt(i);
            if (ch == '#') {
                blanks++;
            } else if (ch >= '1' && ch <= '8') {
                digits++;
            } else {
                return false; // invalid character
            }
            if (ch < 128) {
                if (seen[ch]) return false; // duplicate tile or blank
                seen[ch] = true;
            }
        }
        // Must have exactly one of each 1..8 and exactly one '#'
        return (digits == 8 && blanks == 1);
    }

    // Return target index for moving the blank in the given direction; -1 if illegal
    public static int getIndex(String board, String move) {
        int hashIndex = board.indexOf('#');
        if (hashIndex < 0) return -1; // malformed board

        int row = hashIndex / 3;
        int col = hashIndex % 3;

        switch (move) {
            case "LEFT":
                // Standard left if not in col 0
                if (col > 0) return hashIndex - 1;
                // SPECIAL RULE (LEFT ONLY): if at col 0 but not in the top row,
                // allow wrapping to the previous row's last column (index - 1).
                // Example: row2,col1 (1-indexed) -> row1,col3  (zero-based: (1,0) -> (0,2))
                if (row > 0) return hashIndex - 1;
                // top-left cell can't wrap further
                return -1;

            case "RIGHT":
                // No wrapping for RIGHT
                return (col == 2) ? -1 : hashIndex + 1;

            case "UP":
                return (row == 0) ? -1 : hashIndex - 3;

            case "DOWN":
                return (row == 2) ? -1 : hashIndex + 3;

            default:
                return -1; // unknown action
        }
    }

    // Swap the blank with the tile at moveIndex; if invalid index, return original
    public static String implementMove(String board, int moveIndex) {
        int hashIndex = board.indexOf('#');
        if (hashIndex < 0 || moveIndex < 0 || moveIndex >= 9) {
            return board; // echo original if something is off
        }
        char[] arr = board.toCharArray();
        char tmp = arr[moveIndex];
        arr[moveIndex] = '#';
        arr[hashIndex] = tmp;
        return new String(arr);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read first non-empty line for board
        String board = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) { board = line; break; }
        }

        // Read first non-empty line for action
        String move = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) { move = line; break; }
        }
        scanner.close();

        // If inputs missing, do nothing (spec always supplies two lines)
        if (board == null || move == null) return;

        String normalizedMove = move.trim().toUpperCase();

        // If board malformed or action invalid/illegal, print original board unchanged
        if (!isValidBoard(board)) {
            System.out.println(board);
            return;
        }

        int idx = getIndex(board, normalizedMove);
        if (idx == -1) {
            System.out.println(board);
            return;
        }

        String result = implementMove(board, idx);
        System.out.println(result);
    }
}
