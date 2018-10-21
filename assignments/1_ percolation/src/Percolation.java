import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[] grid;
    private int row, col;
    private int openSitesnumber;
    private WeightedQuickUnionUF wqUF;
    private boolean alreadyPercolates;

    // create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        if (n < 1) throw new IllegalArgumentException("Illeagal Argument");
        wqUF = new WeightedQuickUnionUF(n * n + 2);
        alreadyPercolates = false;
        row = n;
        col = n;
        openSitesnumber = 0;
        grid = new boolean[n * n];
    }

    // number of open sites
    public int numberOfOpenSites() {
        return openSitesnumber;
    }

    private void validate(int i, int j) {
        if (i < 1 || i > row)
            throw new IndexOutOfBoundsException("row index i out of bounds");
        if (j < 1 || j > col)
            throw new IndexOutOfBoundsException("col index j out of bounds");
    }

    // open site (row i, column j) if it is not open already
    public void open(int i, int j) {
        validate(i, j);
        int curIdx = (i - 1) * col + (j - 1);

        if (grid[curIdx]) return; // curIdx element is already opened

        grid[curIdx] = true;
        openSitesnumber++;

        if (i == 1)   wqUF.union(curIdx + 1, 0);              // connect fst element with a new opened element on top
        if (i == row) wqUF.union(curIdx + 1, row * col + 1);  // connect lst element with a new opened elem on button

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        // connect cur element with opened element nearby
        for (int dir = 0; dir < 4; dir++) {
            int posX = i + dx[dir];
            int posY = j + dy[dir];
            if (posX <= row && posX >= 1 && posY <= row && posY >= 1 && isOpen(posX, posY)) {
                wqUF.union(curIdx + 1, (posX - 1) * col + (posY - 1) + 1);
            }
        }
    }

    // is site (row i, column j) open?
    public boolean isOpen(int i, int j) {
        validate(i, j);
        return grid[(i - 1) * col + (j - 1)];
    }

    // is site (row i, column j) full?
    public boolean isFull(int i, int j) {
        validate(i, j);
        int curIdx = (i - 1) * col + (j - 1);
        if (wqUF.find(curIdx + 1) == wqUF.find(0)) return true;
        return false;
    }

    // does the system percolate?
    public boolean percolates() {
        if (alreadyPercolates) return true;
        if (wqUF.find(0) == wqUF.find(row * col + 1)) {
            alreadyPercolates = true;
            return true;
        }
        return false;
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation perc = new Percolation(3);
        perc.open(1, 1);
        System.out.println(perc.percolates());
        perc.open(1, 2);
        System.out.println(perc.percolates());
        perc.open(2, 1);
        System.out.println(perc.percolates());
        perc.open(2, 2);
        System.out.println(perc.numberOfOpenSites());
        System.out.println(perc.percolates());
        perc.open(3, 2);
        System.out.println(perc.percolates());
    }
}
