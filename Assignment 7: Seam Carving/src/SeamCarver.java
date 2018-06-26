import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

/**
 *  SeamCarver class datatype which implements algorithm of smart image resizing.
 *  This datatype uses maximum ~O(12*W*H) auxilary memory, where W and H are dimensions of an input image.
 */
public class SeamCarver {

    private final EnergyDigraph eDAG; //< 
    private Picture mPicture;         //<
    private int mWidth;               //<
    private int mHeight;              //<
    private boolean isTransposed;     //<
    private boolean isOperHoriz;      //<

    /**
     * Auxilary datatype which represents an energy digraph associated with the given image.
     * This is acyclic graph of special structure: each pixel (except boundary pixel) has a path to 3 lower neighbors.
     * It is destined to find a vertical seam (shortest pixel energy path from the top to the bottom of an image).
     * Problem of finding a seam is equal to the SP problem in acyclic graph and could be solved using topological sort algorithm.
     */
    private class EnergyDigraph {
        private int mHeight;             //<
        private int mWidth;              //<
        private int mDims;               //<
        private double[] mEnergy;        //<

        public EnergyDigraph(int height, int width) {
            mHeight = height;
            mWidth = width;
            mDims = mHeight * mWidth;
            mEnergy = new double[mDims];
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

        private void relax(int pixel, int sourcePixel, double[] distTo, int[] edgeTo) {
            if (isInRange(pixel) && distTo[pixel] > distTo[sourcePixel] + mEnergy[pixel]) {
                distTo[pixel] = distTo[sourcePixel] + mEnergy[pixel];
                edgeTo[pixel] = sourcePixel;
            }
        }

        // sequence of indices for vertical seam
        public int[] findVerticalSeam() {
            int[] seam = new int[mHeight];
            int[] edgeTo = new int[mDims];
            double[] distTo = new double[mDims];
            double seamDistance = Double.POSITIVE_INFINITY;
            int leftBottomPixel = coordsToPixel(0, mHeight-1);

            for (int s = 0; s < mWidth; s++) {
                // Initialize distances from s to any vertex (pixel)
                for (int p = 0; p < mDims; p++) {
                    distTo[p] = Double.POSITIVE_INFINITY;
                    edgeTo[p] = -1;
                }
                distTo[s] = 0.0;

                // Relax adjacent pixels in topological order to find SP
                for (int p : topologicalOrder(s)) {
                    int x = pixelToCoordX(p);
                    int y = pixelToCoordY(p);
                    if (y < mHeight - 1) {
                        // relax down-left
                        if (x > 0) relax(p + mWidth - 1, p, distTo, edgeTo);
                        // relax down-left
                        if (x < mWidth - 1) relax(p + mWidth + 1, p, distTo, edgeTo);
                        relax(p + mWidth, p, distTo, edgeTo);
                    }
                }
                // Find min distance from top to bottom and appropriate bottom pixel
                int minBottomPixel = leftBottomPixel;
                double minDistance = Double.POSITIVE_INFINITY;
                for (int w = leftBottomPixel; w < mDims; w++) {
                    if (minDistance > distTo[w]) {
                        minBottomPixel = w;
                        minDistance = distTo[w];
                    }
                }
                if (seamDistance > minDistance) {
                    // Update seam weight
                    seamDistance = minDistance;
                    // Update seam
                    int ind = mHeight - 1;
                    seam[ind--] = pixelToCoordX(minBottomPixel);
                    for (int p = edgeTo[minBottomPixel]; ind >= 0; p = edgeTo[p], ind--) {
                        seam[ind] = pixelToCoordX(p);
                    }
                }
            }

            return seam;
        }

        public void transposeEnergy() {
            double[] newEnergy = new double[mDims];
            for (int row = 0; row < mHeight; row++) {
                for (int col = 0; col < mWidth; col++) {
                    newEnergy[coordsToPixelTransposed(row, col)] = mEnergy[coordsToPixel(col, row)];
                }
            }
            mEnergy = newEnergy;
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
        // note: this pattern of pixel traverse is optimal (!!!)
        for (int x = 0; x < mWidth; x++) {
            for (int y = 0; y < mHeight; y++) {
                eDAG.setEnergy(x, y, validEnergy(x, y));
            }
        }
    }

    // current picture
    public Picture picture() {
        return isTransposed ? transposedPicture() : new Picture(mPicture);        
    }

    // width of current picture
    public int width() {
        return isTransposed ? mHeight : mWidth;
    }

    // height of current picture
    public int height() {
        return isTransposed ? mWidth : mHeight;
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
        if (isTransposed) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        if (!validateCoords(x, y))
            throw new java.lang.IllegalArgumentException();
        return validEnergy(x, y);
    }

    private double validEnergy(int x, int y) {
        double res;
        if (!isOnBoundary(x, y)) {
            double xGradientSquared = calculateGradientSquared(x, y, true/* isXGradient */);
            double yGradientSquared = calculateGradientSquared(x, y, false/* isXGradient */);
            res = Math.sqrt(xGradientSquared + yGradientSquared);
        }
        else {
            res = 1000.0;
        }
        return res;
    }

    private Picture transposedPicture() {
        Picture newPicture = new Picture(mHeight, mWidth);
        // note: this pattern of pixel traverse is optimal (!!!)
        for (int col = 0; col < mWidth; col++) {
            for (int row = 0; row < mHeight; row++) {
                int rgb = mPicture.getRGB(col, row);
                newPicture.setRGB(row, col, rgb);
            }
        }
        return newPicture;
    }
    
    private void transposePicture() {
        mPicture = transposedPicture();
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
        if (isTransposed && !isOperHoriz) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        isOperHoriz = false;

        if ((mWidth <= 1) || !validateSeam(seam, mHeight))
            throw new java.lang.IllegalArgumentException();

        // Update internal picture
        Picture newPicture = new Picture(mWidth - 1, mHeight);
        // note: this pattern of pixel traverse is optimal (!!!)
        for (int col = 0; col < mWidth; col++) {
            for (int row = 0; row < mHeight; row++) {
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
            if (seamCol > 0)          eDAG.setEnergy(seamCol-1, row, validEnergy(seamCol-1, row));
            if (seamCol < mWidth - 1) eDAG.setEnergy(seamCol, row, validEnergy(seamCol, row));
            if (mWidth == 1)          eDAG.setEnergy(0, row, validEnergy(0, row));
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!isTransposed) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        isOperHoriz = true;
        return findVerticalSeam();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (!isTransposed) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        isOperHoriz = true;
        removeVerticalSeam(seam);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            StdOut.println("Usage:\njava ResizeDemo [image filename] [num cols to remove] [num rows to remove]");
            return;
        }

        Picture inputImg = new Picture(args[0]);
        int removeColumns = Integer.parseInt(args[1]);
        int removeRows = Integer.parseInt(args[2]);

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
