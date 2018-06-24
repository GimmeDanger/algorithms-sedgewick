import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    private final EnergyDigraph eDAG;
    private Picture mPicture;
    private int mWidth;
    private int mHeight;
    private boolean isTransposed;
    private boolean isOperHoriz;

    private class EnergyDigraph {
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

        private int coordsToPixelTransposed(int x, int y) {
            return y * mHeight + x;
        }

        private int pixelToCoordX(int p) {
            return p % mWidth;
        }

        private int pixelToCoordY(int p) {
            return p / mWidth;
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

        private void relax(int pixel, int sourcePixel) {
            if (isInRange(pixel) && mDistTo[pixel] > mDistTo[sourcePixel] + mEnergy[pixel]) {
                mDistTo[pixel] = mDistTo[sourcePixel] + mEnergy[pixel];
                mEdgeTo[pixel] = sourcePixel;
            }
        }

        // sequence of indices for vertical seam
        public int[] findVerticalSeam() {
            int[] seam = new int[mHeight];
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
                        if (x > 0) relax(p + mWidth - 1, p);
                        // relax down-left
                        if (x < mWidth - 1) relax(p + mWidth + 1, p);
                        relax(p + mWidth, p);
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

            return seam;
        }

        public void transposeEnergy() {
            for (int row = 0; row < mHeight; row++) {
                for (int col = row + 1; col < mWidth; col++) {
                    double tmp = mEnergy[coordsToPixel(col, row)];
                    mEnergy[coordsToPixel(col, row)] = mEnergy[coordsToPixelTransposed(row, col)];
                    mEnergy[coordsToPixelTransposed(row, col)] = tmp;
                }
            }
            int tmp = mWidth;
            mWidth = mHeight;
            mHeight = tmp;
        }

        // remove vertical seam from current picture
        public void removeVerticalSeam(int[] seam) {
            int pixelCopyBegin = coordsToPixel(seam[0], 0);
            for (int row = 0; row < mHeight; row++) {
                int pixelCopyEnd = row < mHeight - 1 ? coordsToPixel(seam[row+1], row+1) : mDims;
                System.arraycopy(mEnergy, pixelCopyBegin+1,
                        mEnergy, pixelCopyBegin-row, pixelCopyEnd - pixelCopyBegin - 1);
                pixelCopyBegin = pixelCopyEnd;
            }
            mWidth--;
            mDims = mHeight * mWidth;
        }
    }

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new java.lang.IllegalArgumentException();

        // allocate an internal picture data
        mPicture = new Picture(picture);

        // initialize an internal picture data
        mWidth = mPicture.width();
        mHeight = mPicture.height();
        isTransposed = false;
        isOperHoriz = false;

        // allocate an energy digraph data
        eDAG = new EnergyDigraph(mHeight, mWidth);

        // initialize an energy digraph data
        for (int y = 0; y < mHeight; y++) {
            for (int x = 0; x < mWidth; x++) {
                eDAG.setEnergy(x, y, energy(x, y));
            }
        }
    }

    // current picture
    public Picture picture() {
        if (isTransposed) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        return mPicture;
    }

    // width of current picture
    public int width() {
        return mWidth;
    }

    // height of current picture
    public int height() {
        return mHeight;
    }

    private boolean validateCoords(int x, int y) {
        return (x >= 0 && x < mWidth && y >= 0 && y < mHeight);
    }

    private boolean validateSeam(int[] seam, int assumedLength) {
        boolean isValid = true;
        if (seam == null || seam.length != assumedLength)
            isValid = false;
        else {
            boolean isVertical = (assumedLength == mHeight);
            int maxLength = isVertical ? mWidth : mHeight;
            int prevSeamInd = seam[0];
            for (int i = 0; i < assumedLength; i++) {
                int seamInd = seam[i];
                if (seamInd < 0 || seamInd >= maxLength || Math.abs(prevSeamInd - seamInd) > 1) {
                    isValid = false;
                    break;
                }
                prevSeamInd = seamInd;
            }
        }
        return isValid;
    }

    private boolean isOnBoundary(int x, int y) {
        return (x == 0) || (x == mWidth - 1) || (y == 0) || (y == mHeight - 1);
    }

    private double calculateGradientSquared(int x, int y, boolean isXGradient) {
        int rgbPlus = isXGradient ? mPicture.getRGB(x + 1, y) : mPicture.getRGB(x, y + 1);
        int rgbMinus = isXGradient ? mPicture.getRGB(x - 1, y) : mPicture.getRGB(x, y - 1);

        // RGB integer-representation to (R, G, B):
        // int red = (rgb >> 16) & 0xFF;
        // int green = (rgb >> 8) & 0xFF;
        // int blue = rgb & 0xFF;

        double deltaR = ((rgbPlus >> 16) & 0xFF) - ((rgbMinus >> 16) & 0xFF);
        double deltaG = ((rgbPlus >> 8) & 0xFF) - ((rgbMinus >> 8) & 0xFF);
        double deltaB = (rgbPlus & 0xFF) - (rgbMinus & 0xFF);

        return deltaR * deltaR + deltaG * deltaG + deltaB * deltaB;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (!validateCoords(x, y))
            throw new java.lang.IllegalArgumentException();
        double res;
        if (!isOnBoundary(x, y)) {
            double xGradientSquared = calculateGradientSquared(x, y,true/*isXGradient*/);
            double yGradientSquared = calculateGradientSquared(x, y,false/*isXGradient*/);
            res = Math.sqrt(xGradientSquared + yGradientSquared);
        }
        else {
            res = 1000.0;
        }
        return res;
    }

    private void transposePicture() {
        Picture newPicture = new Picture(mHeight, mWidth);
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                int rgb = mPicture.getRGB(col, row);
                newPicture.setRGB(row, col, rgb);
            }
        }
        mPicture = newPicture;
        mWidth = mPicture.width();
        mHeight = mPicture.height();
        isTransposed = !isTransposed;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (isTransposed && !isOperHoriz) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        isOperHoriz = false;
        return eDAG.findVerticalSeam();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if ((mWidth <= 1) || !validateSeam(seam, !isOperHoriz ? mHeight : mWidth))
            throw new java.lang.IllegalArgumentException();
        if (isTransposed && !isOperHoriz) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        isOperHoriz = false;

        // Update internal picture
        Picture newPicture = new Picture(mWidth - 1, mHeight);
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                if (col == seam[row])
                    continue;
                int rgb = mPicture.getRGB(col, row);
                newPicture.setRGB(col < seam[row] ? col : col - 1, row, rgb);
            }
        }
        mPicture = newPicture;
        mWidth = mPicture.width();
        mHeight = mPicture.height();
        // Update energy digraph
        eDAG.removeVerticalSeam(seam);
        for (int row = 0; row < mHeight; row++) {
            int seamCol = seam[row];
            if (seamCol > 0) eDAG.setEnergy(seamCol-1, row, energy(seamCol-1, row));
            eDAG.setEnergy(seamCol, row, energy(seamCol, row));
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!isTransposed) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        isOperHoriz = true;
        return eDAG.findVerticalSeam();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (mHeight <= 1 || !validateSeam(seam, mWidth))
            throw new java.lang.IllegalArgumentException();
        if (!isTransposed) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        isOperHoriz = true;
        removeVerticalSeam(seam);
    }

    public static void main(String[] args) {
        /* Test energy, vertical seam
        Picture picture = new Picture(args[0]);
        StdOut.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());

        SeamCarver sc = new SeamCarver(picture);

        StdOut.printf("Printing energy calculated for each pixel.\n");

        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                StdOut.printf("%.4f ", sc.energy(col, row));
            StdOut.println();

        StdOut.printf("Printing vertical seam.\n");
        int seam[] = sc.findVerticalSeam();
        for (int i = 0; i < sc.height(); i++)
            StdOut.printf("%d ", seam[i]);
        StdOut.println()
        }
        */

        /* Test remove vertical seam */
        Picture inputImg = new Picture(args[0]);
        int removeColumns = 0;
        int removeRows = 1;

        StdOut.printf("image is %d columns by %d rows\n", inputImg.width(), inputImg.height());
        SeamCarver sc = new SeamCarver(inputImg);

        // Stopwatch sw = new Stopwatch();

        for (int i = 0; i < removeRows; i++) {
            int[] horizontalSeam = sc.findHorizontalSeam();
            sc.removeHorizontalSeam(horizontalSeam);
        }

        for (int i = 0; i < removeColumns; i++) {
            int[] verticalSeam = sc.findVerticalSeam();
            sc.removeVerticalSeam(verticalSeam);
        }
        Picture outputImg = sc.picture();

        StdOut.printf("new image size is %d columns by %d rows\n", sc.width(), sc.height());

        // StdOut.println("Resizing time: " + sw.elapsedTime() + " seconds.");
        inputImg.show();
        outputImg.show();
    }
}
