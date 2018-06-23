import edu.princeton.cs.algs4.Queue;

public class EnergyDigraph {
    private int mHeight;
    private int mWidth;
    private int mDims;
    private final double[] mEnergy;
    private final double[] mDistTo;
    private final int[] mEdgeTo;

    public EnergyDigraph(int height, int width) {
        mHeight = height;
        mWidth = width;
        mDims = mHeight * mWidth;
        mEnergy = new double[mDims];
        mDistTo = new double[mDims];
        mEdgeTo = new int[mDims];
    }

    private int coordsToPixel(int x, int y) {
        return y * mWidth + x;
    }

    private int pixelToCoordX(int p) {
        return p % mWidth;
    }

    private int pixelToCoordY(int p) {
        return p / mWidth;
    }

    private boolean isInRange(int x, int y) {
        return x >= 0 && x < mWidth && y >= 0 && y < mHeight;
    }

    private boolean isInRange(int p) {
        return p >= 0 && p < mDims;
    }

    public void setEnergy(int x, int y, double energy) {
        mEnergy[coordsToPixel(x, y)] = energy;
    }

    private Iterable<Integer> topologicalOrder(int pixel) {
        // Calculate dfs reverse postorder == topological order
        Queue<Integer> topological = new Queue<>();

        int x = pixelToCoordX(pixel);
        int y = pixelToCoordY(pixel);
        int initialParity = (x + y) % 2;

        // It is easy to calculate dfs reverse postorder without dfs run for energy digraph
        for (int diagBegX = x, diagBegY = y; diagBegX >= 0 && diagBegY < mHeight;) {
            // Add diagonal pixels starting from (diagBegX, diagBegY)
            for (int diagX = diagBegX, diagY = diagBegY; diagX < mWidth && diagY < mHeight; diagX++, diagY++) {
                topological.enqueue(coordsToPixel(diagX, diagY));
            }
            // Set the beggining of the diaglonal which lies below
            if ((diagBegX + diagBegY) % 2 == initialParity || diagBegX == 0)
                diagBegY++;
            else
                diagBegX--;
        }

        return topological;
    }

    private void relax (int pixel, int sourcePixel) {
        if (isInRange(pixel) && mDistTo[pixel] > mDistTo[sourcePixel] + mEnergy[pixel]) {
            mDistTo[pixel] = mDistTo[sourcePixel] + mEnergy[pixel];
            mEdgeTo[pixel] = sourcePixel;
        }
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int seam[] = new int[mHeight];
        double seamDistance = Double.POSITIVE_INFINITY;
        int leftBottomPixel = coordsToPixel(0, mHeight-1);

        for (int s = 0; s < mWidth; s++) {
            // Initialize distances from s to any vertex (pixel)
            for (int p = 0; p < mDims; p++) {
                mDistTo[p] = Double.POSITIVE_INFINITY;
                mEdgeTo[p] = -1;
            }
            mDistTo[s] = 0.0;
            // Relax adjacent pixels in topological order to find SP
            for (int p : topologicalOrder(s)) {
                int x = pixelToCoordX(p);
                int y = pixelToCoordY(p);
                if (y < mHeight - 1) {
                    // relax down-left
                    if (x > 0) relax (p + mWidth - 1, p);
                    // relax down-left
                    if (x < mWidth - 1) relax (p + mWidth + 1, p);
                    relax (p + mWidth    , p);
                }
            }
            // Find min distance from top to bottom and appropriate bottom pixel
            int minBottomPixel = leftBottomPixel;
            double minDistance = Double.POSITIVE_INFINITY;
            for (int w = leftBottomPixel; w < mDims; w++) {
                if (minDistance > mDistTo[w]) {
                    minBottomPixel = w;
                    minDistance = mDistTo[w];
                }
            }
            if (seamDistance > minDistance) {
                // Update seam weight
                seamDistance = minDistance;
                // Update seam
                int ind = mHeight - 1;
                seam[ind--] = pixelToCoordX(minBottomPixel);
                for (int p = mEdgeTo[minBottomPixel]; p != s && ind >= 0; p = mEdgeTo[p], ind--) {
                    seam[ind] = pixelToCoordX(p);
                }
                seam[ind] = pixelToCoordX(s);
            }
        }

        return seamDistance < Double.POSITIVE_INFINITY ? seam : null;
    }

    // sequence of indices for horizontal seam
    // public   int[] findHorizontalSeam() {;}

    // remove vertical seam from current picture
    // public    void removeVerticalSeam(int[] seam) {;}

    // remove horizontal seam from current picture
    // public    void removeHorizontalSeam(int[] seam) {;}
}
