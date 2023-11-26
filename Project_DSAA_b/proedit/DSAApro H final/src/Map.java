import edu.princeton.cs.algs4.StdDraw;

public class Map {
    private double width;
    private double height;
    private double xmid;
    private double ymid;

    // create a new square with the given parameters (assume it is square)
    public Map(double xmid, double ymid, double width, double height) {
        this.xmid = xmid;
        this.ymid = ymid;
        this.width = width;
        this.height = height;
    }

    // return the length of one side of the square quadrant
    public double width() {
        return width;
    }

    public double height(){
        return height;
    }

    // does quadrant contain (x, y)?
    public boolean contains(double x, double y) {
        boolean xflag = xmid - width / 2 <= x && x <= xmid + width / 2.0;
        boolean yflag = ymid - height / 2 <= y && y <= ymid + height / 2.0;
        return xflag && yflag;
    }

    public Map NW() {
        return new Map(xmid - width / 4, ymid + width / 4, width / 2, height / 2);
    }

    public Map NE() {
        return new Map(xmid + width / 4, ymid + width / 4, width / 2, height / 2);
    }

    public Map SW() {
        return new Map(xmid - height / 4, ymid - height / 4, height / 2, height / 2);
    }

    public Map SE() {
        return new Map(xmid + height / 4, ymid - height / 4, height / 2, height / 2);
    }
}
