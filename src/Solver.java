import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;


/**
 * Created by spec on 02.05.2017.
 */
public final class Solver {
    private final Stack<Board> solutionBoards;


    public Solver(Board initial) {
        if (initial == null) throw new NullPointerException();

        solutionBoards = new Stack<>();
        MinPQ<SearchNode> searchNodes = new MinPQ<>();
        MinPQ<SearchNode> searchNodesMod = new MinPQ<>();

        searchNodes.insert(new SearchNode(initial, null));
        searchNodesMod.insert(new SearchNode(initial.twin(), null));

        SearchNode previousNode = null;
        SearchNode previousNodeMod = null;

        while (!searchNodes.min().board.isGoal() && !searchNodesMod.min().board.isGoal()) {
            SearchNode searchNode = searchNodes.delMin();
            for (Board board : searchNode.board.neighbors())
                if (previousNode == null || previousNode != null && !previousNode.equals(board))
                    searchNodes.insert(new SearchNode(board, searchNode));
            previousNode = searchNode;

            SearchNode searchNodeMod = searchNodesMod.delMin();
            for (Board board : searchNodeMod.board.neighbors())
                if (previousNodeMod == null || previousNodeMod != null && !previousNodeMod.equals(board))
                    searchNodesMod.insert(new SearchNode(board, searchNodeMod));
            previousNodeMod = searchNodeMod;
        }
        if (searchNodesMod.min().board.isGoal()) return;

        SearchNode current = searchNodes.min();
        while (current != null) {
            solutionBoards.push(current.board);
            current = current.prevNode;
        }
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In("8puzzle/puzzle07.txt");
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    public int moves() {
        return solutionBoards.size() - 1;
    }

    public Iterable<Board> solution() {
        if (isSolvable()) return solutionBoards;
        return null;
    }

    public boolean isSolvable() {
        return !solutionBoards.isEmpty();
    }

    private class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final SearchNode prevNode;
        private int moves;
        private int manhattan;

        public SearchNode(Board board, SearchNode prevNode) {
            this.board = board;
            this.prevNode = prevNode;
            this.manhattan = board.manhattan();
            if (prevNode != null) moves = prevNode.moves + 1;
            else moves = 0;
        }

        @Override
        public int compareTo(SearchNode that) {
            int priorityDiff = (this.manhattan + this.moves) - (that.manhattan + that.moves);
            return  priorityDiff == 0 ? this.manhattan - that.manhattan : priorityDiff;
        }
    }
}
