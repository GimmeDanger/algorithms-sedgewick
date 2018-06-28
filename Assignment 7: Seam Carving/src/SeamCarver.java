import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

/**
 *  SeamCarver class datatype which implements algorithm of smart image resizing.
 *  This datatype uses maximum ~O(12*W*H) auxilary memory, where W and H are dimensions of an input image.
 */
public class SeamCarver {

    private final EnergyDigraph eDAG; //< an energy digraph associated with the given image.
    private Picture mPicture;         //< an internal picture
    private int mWidth;               //< picture width
    private int mHeight;              //< picture height
    private boolean isTransposed;     //< true if picture is transposed with respect to the initial state
    private boolean isOperHoriz;      //< true if current operation is horizontal

    /**
     * Auxilary datatype which represents an energy digraph associated with the given image.
     * This is acyclic graph of special structure: each pixel (except boundary pixel) has a path to 3 lower neighbors.
     * It is destined to find a vertical seam (shortest pixel energy path from the top to the bottom of an image).
     * Problem of finding a seam is equal to the SP problem in acyclic graph and could be solved using topological sort algorithm.
     */
    private class EnergyDigraph {
        private int mHeight;             //< width
        private int mWidth;              //< height
        private int mDims;               //< width * height
        private double[] mEnergy;        //< energies array


        /**
         * Constructor
         * @param height
         * @param width
         */
        public EnergyDigraph(int height, int width) {
            mHeight = height;
            mWidth = width;
            mDims = mHeight * mWidth;
            mEnergy = new double[mDims];
        }

        /**
         * Convert pixel coords (x, y) to pixel index in range of [0, mDims-1]
         * @param x column index
         * @param y row index
         * @return pixel index in [0, mDims-1]
         */
        private int coordsToPixel(int x, int y) {
            return y * mWidth + x;
        }

        /**
         * Convert pixel coords (x, y) to pixel transposed index in range of [0, mDims-1]
         * @param x column index
         * @param y row index
         * @return pixel transposed index in [0, mDims-1]
         */
        private int coordsToPixelTransposed(int x, int y) {
            return y * mHeight + x;
        }

        /**
         * Find pixel`s x coord
         * @param p pixel index
         * @return x coordinate
         */
        private int pixelToCoordX(int p) {
            return p % mWidth;
        }

        /**
         * Check if pixel index is in range of [0, mDims-1]
         * @param p pixel index
         * @return true if pixel index is in range of [0, mDims-1]
         */
        private boolean isInRange(int p) {
            return p >= 0 && p < mDims;
        }

        /**
         * Set pixel`s energy
         * @param x column index
         * @param y row index
         * @param energy pixel energy
         */
        public void setEnergy(int x, int y, double energy) {
            mEnergy[coordsToPixel(x, y)] = energy;
        }

        /**
         * Relax a pixel to perform SP-algorithm
         * @param pixel current pixel`s index
         * @param sourcePixel source pixel`s index
         * @param distTo mDims array of distances
         * @param edgeTo mDims array of previous pixels
         */
        private void relax(int pixel, int sourcePixel, double[] distTo, int[] edgeTo) {
            if (isInRange(pixel) && distTo[pixel] > distTo[sourcePixel] + mEnergy[pixel]) {
                distTo[pixel] = distTo[sourcePixel] + mEnergy[pixel];
                edgeTo[pixel] = sourcePixel;
            }
        }

        /**
         * Find veritcal seam
         * @return vertical seam index sequence
         */
        public int[] findVerticalSeam() {
            int[] seam = new int[mHeight];

            int[] edgeTo = new int[mDims];
            double[] distTo = new double[mDims];
            for (int p = 0; p < mDims; p++) {
                distTo[p] = (p < mWidth) ? mEnergy[p] : Double.POSITIVE_INFINITY;
                edgeTo[p] = -1;
            }
            
            for (int row = 0; row < mHeight; row++) {
                for (int col = 0; col < mWidth; col++) {
                    int p = coordsToPixel(col, row);
                    if (row < mHeight - 1) {
                        // relax down-left
                        if (col > 0) relax(p + mWidth - 1, p, distTo, edgeTo);
                        // relax down-left
                        if (col < mWidth - 1) relax(p + mWidth + 1, p, distTo, edgeTo);
                        relax(p + mWidth, p, distTo, edgeTo);
                    }
                }
            }
            // Find min distance from top to bottom and appropriate bottom pixel
            int leftBottomPixel = coordsToPixel(0, mHeight-1);
            int minBottomPixel = leftBottomPixel;
            double minDistance = Double.POSITIVE_INFINITY;
            for (int w = leftBottomPixel; w < mDims; w++) {
                if (minDistance > distTo[w]) {
                    minBottomPixel = w;
                    minDistance = distTo[w];
                }
            }
            int ind = mHeight - 1;
            seam[ind--] = pixelToCoordX(minBottomPixel);
            for (int p = edgeTo[minBottomPixel]; ind >= 0; p = edgeTo[p], ind--) {
                seam[ind] = pixelToCoordX(p);
            }

            return seam;
        }

        /**
         * Transpose energy array and update class properties
         */
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

        /**
         * Remove vertical seam from current picture
         * @param seam index sequence of vertical seam
         */
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

    /**
     * Constructor which creates a seam carver object based on the given picture
     * @param picture
     */
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

    /**
     * Return current picture
     * @return current picture
     */
    public Picture picture() {
        return isTransposed ? transposedPicture() : new Picture(mPicture);        
    }

    /**
     * Return width of current picture
     * @return width of current picture
     */
    public int width() {
        return isTransposed ? mHeight : mWidth;
    }

    /**
     * Return height of current picture
     * @return height of current picture
     */
    public int height() {
        return isTransposed ? mWidth : mHeight;
    }

    /**
     * Validate (x, y)
     * @param x a column index
     * @param y a row index
     * @return true if (x, y) is valid
     */
    private boolean validateCoords(int x, int y) {
        return (x >= 0 && x < mWidth && y >= 0 && y < mHeight);
    }

    /**
     * Validate a seam
     * @param seam sequence of seam indexes
     * @param assumedLength assumed length of a seam to determine a correct direction
     * @return true if seam is valid
     */
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

    /**
     * Check if (x, y) is on boundary
     * @param x a column index
     * @param y a row index
     * @return true if (x, y) is on boundary
     */
    private boolean isOnBoundary(int x, int y) {
        return (x == 0) || (x == mWidth - 1) || (y == 0) || (y == mHeight - 1);
    }

    /**
     * Find squared gradient value in x or y direction
     * Note: RGB integer-representation to (R, G, B):
     *       int red = (rgb >> 16) & 0xFF;
     *       int green = (rgb >> 8) & 0xFF;
     *       int blue = rgb & 0xFF;
     * @param x a column index
     * @param y a row index
     * @param isXGradient flag to check if x or y gradient is required
     * @return delta_x * delta_x or delta_y * delta_y
     */
    private double calculateGradientSquared(int x, int y, boolean isXGradient) {
        int rgbPlus = isXGradient ? mPicture.getRGB(x + 1, y) : mPicture.getRGB(x, y + 1);
        int rgbMinus = isXGradient ? mPicture.getRGB(x - 1, y) : mPicture.getRGB(x, y - 1);
        double deltaR = ((rgbPlus >> 16) & 0xFF) - ((rgbMinus >> 16) & 0xFF);
        double deltaG = ((rgbPlus >> 8) & 0xFF) - ((rgbMinus >> 8) & 0xFF);
        double deltaB = (rgbPlus & 0xFF) - (rgbMinus & 0xFF);
        return deltaR * deltaR + deltaG * deltaG + deltaB * deltaB;
    }

    /**
     * Find an energy of pixel(x, y)
     * @param x a column index
     * @param y a row index
     * @return energy of pixel (x, y)
     * Throw an IllegalArgumentException unless {coords are valid}
     */
    public double energy(int x, int y) {
        if (isTransposed) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        if (!validateCoords(x, y))
            throw new java.lang.IllegalArgumentException();
        return validEnergy(x, y);
    }

    /**
     * Find an energy of pixel, (x, y) is supposed to be valid
     * @param x a column index
     * @param y a row index
     * @return energy of pixel (x, y)
     */
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

    /**
     * Transpose internal picture and update class properties
     * @return transposed internal picture
     */
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

    /**
     * Transpose internal picture and update class properties
     */
    private void transposePicture() {
        mPicture = transposedPicture();
        mWidth = mPicture.width();
        mHeight = mPicture.height();
        isTransposed = !isTransposed;
    }

    /**
     * Find a sequence of indexes for vertical seam
     * @return mHeight array of vertical seam indexes
     */
    public int[] findVerticalSeam() {
        if (isTransposed && !isOperHoriz) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        isOperHoriz = false;
        return eDAG.findVerticalSeam();
    }

    /**
     * Remove vertical seam from current picture
     * @param seam a sequence of col indexes in a given vertical seam
     * Throw an IllegalArgumentException unless {seam is a valid}
     */
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

    /**
     * Find a sequence of indexes for horizontal seam
     * @return mWidth array of horizontal seam indexes
     */
    public int[] findHorizontalSeam() {
        if (!isTransposed) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        isOperHoriz = true;
        return findVerticalSeam();
    }

    /**
     * Remove horizontal seam from current picture
     * @param seam a sequence of row indexes in a given horizontal seam
     * Throw an IllegalArgumentException unless {seam is a valid}
     */
    public void removeHorizontalSeam(int[] seam) {
        if (!isTransposed) {
            transposePicture();
            eDAG.transposeEnergy();
        }
        isOperHoriz = true;
        removeVerticalSeam(seam);
    }

    /**
     * Unit testing of this class
     */
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        StdOut.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());

        SeamCarver sc = new SeamCarver(picture);

        /*
        StdOut.printf("Printing energy calculated for each pixel.\n");

        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                StdOut.printf("%.4f ", sc.energy(col, row));
            StdOut.println();
        }

        StdOut.printf("Printing vertical seam.\n");
        int[] seam = sc.findVerticalSeam();
        for (int i = 0; i < sc.width(); i++)
            StdOut.printf("%d ", seam[i]);
        StdOut.println();
        */

        int removeColumns = 1;
        int removeRows = 0;

        // Stopwatch sw = new Stopwatch();

        for (int i = 0; i < removeRows; i++) {
            int[] horizontalSeam = sc.findHorizontalSeam();
            sc.removeHorizontalSeam(horizontalSeam);
        }

        for (int i = 0; i < removeColumns; i++) {
            int[] verticalSeam = sc.findVerticalSeam();
            sc.removeVerticalSeam(verticalSeam);
        }
        // Picture outputImg = sc.picture();

        StdOut.printf("new image size is %d columns by %d rows\n", sc.width(), sc.height());
    }
}
