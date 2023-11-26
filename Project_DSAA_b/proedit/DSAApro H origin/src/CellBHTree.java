public class CellBHTree {

    private final double theta = 0.5;
    private Cell cell;
    private Map map;
    private CellBHTree NW; // tree representing northwest quadrant
    private CellBHTree NE; // tree representing northeast quadrant
    private CellBHTree SW; // tree representing southwest quadrant
    private CellBHTree SE; // tree representing southeast quadrant

    public CellBHTree(Map m) {
        this.map = m;
        this.cell = null;
        this.NW = null;
        this.NE = null;
        this.SW = null;
        this.SE = null;
    }

    public void insert(Cell c) {

        if (cell == null) {
            cell = c;
            return;
        }

        else {
            NE = new CellBHTree(map.NE());
            NW = new CellBHTree(map.NW());
            SW = new CellBHTree(map.SW());
            SE = new CellBHTree(map.SE());

            putCell(this.cell);
            putCell(c);
        }
    }

    private void putCell(Cell c) {
        if (c.in(map.NE())) NE.insert(c);
        else if (c.in(map.NW())) NW.insert(c);
        else if (c.in(map.SW())) SW.insert(c);
        else if (c.in(map.SE())) SE.insert(c);
    }

    private boolean isExternal() {
        return (NE == null && NW == null && SW == null && SE == null);
    }

}
