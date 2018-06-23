import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    private final Picture mPicture;
    private final EnergyDigraph eDAG;
    private int mWidth;
    private int mHeight;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        // allocate an internal picture data
        mPicture = new Picture(picture);

        // initialize an internal picture data
        mWidth = mPicture.width();
        mHeight = mPicture.height();

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

    private boolean isOnBoundary (int x, int y) {
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
        double res;
        if (!isOnBoundary(x, y)) {
            double xGradientSquared = calculateGradientSquared(x, y, true/*isXGradient*/);
            double yGradientSquared = calculateGradientSquared(x, y, false/*isXGradient*/);
            res = Math.sqrt(xGradientSquared + yGradientSquared);
        }
        else {
            res = 1000.0;
        }
        return res;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return eDAG.findVerticalSeam();
    }

    // sequence of indices for horizontal seam
    // public   int[] findHorizontalSeam() {;}

    // remove vertical seam from current picture
    // public    void removeVerticalSeam(int[] seam) {;}

    // remove horizontal seam from current picture
    // public    void removeHorizontalSeam(int[] seam) {;}

    public static void main(String[] args) {

        Picture picture = new Picture(args[0]);
        StdOut.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());

        SeamCarver sc = new SeamCarver(picture);

        /* Test energy
        StdOut.printf("Printing energy calculated for each pixel.\n");

        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                StdOut.printf("%.4f ", sc.energy(col, row));
            StdOut.println();
        }
        */

        /* Test vertical seam */
        StdOut.printf("Printing vertical seam.\n");
        int seam[] = sc.findVerticalSeam();
        for (int i = 0; i < sc.height(); i++)
            StdOut.printf("%d ", seam[i]);
        StdOut.println();
    }
}
