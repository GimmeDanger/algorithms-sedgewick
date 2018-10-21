import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double[] x;
    private int time;

    // perform t independent experiments on an n-by-n grid
    public PercolationStats(int n, int t) {
        if (n <= 0 || t <= 0)
            throw new IllegalArgumentException("Illeagal Argument");
        x = new double[t];
        time = t;
        for (int i = 0; i < t; i++) {
            Percolation perc = new Percolation(n);
            int posX, posY;
            while (true) {
                do {
                    posX = StdRandom.uniform(n) + 1;
                    posY = StdRandom.uniform(n) + 1;
                } while (perc.isOpen(posX, posY));
                perc.open(posX, posY);
                x[i] += 1;
                if (perc.percolates())
                    break;
            }
            x[i] = x[i] / (n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(x);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(x);
    }


    // low  endpoint of 95% confidence interval
    public double confidenceLo() {
        double mu = mean();
        double sigma = stddev();
        return mu - 1.96 * sigma / Math.sqrt(time);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        double mu = mean();
        double sigma = stddev();
        return mu + 1.96 * sigma / Math.sqrt(time);
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats percStats = new PercolationStats(n, t);
        StdOut.printf("mean                     = %f\n", percStats.mean());
        StdOut.printf("stddev                   = %f\n", percStats.stddev());
        StdOut.printf("95%% confidence interval = %f, %f\n", percStats.confidenceLo(), percStats.confidenceHi());
    }
}
