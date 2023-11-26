public class Rect {
    // 分别表示左下顶点和右上顶点
    public final double minX;
    public final double minY;
    public final double maxX;
    public final double maxY;

    public Rect(Cell cell){
        minX = cell.getRx() - cell.getRange();
        minY = cell.getRy() - cell.getRange();
        maxX = cell.getRx() + cell.getRange();
        maxY = cell.getRy() + cell.getRange();
    }
    // 计算矩形到某一点的最近距离（以平方和的形式）
    public double distanceSquareToPoint(Cell cell) {
        double dx = 0.0;
        double dy = 0.0;
        if (cell.getRx() < minX) dx = minX - cell.getRx();
        else if (cell.getRx() > maxX) dx = cell.getRx() - maxX;
        if (cell.getRy() < minY) dy = minY - cell.getRy();
        else if (cell.getRy() > maxY) dy = cell.getRy() - maxY;
        return dx * dx + dy * dy ;
    }
    //查看矩形与圆是否相交
    public boolean checkOverlap(Cell cell) {
        double dx = minX > cell.getRx() ? minX - cell.getRx() : maxX < cell.getRx() ? cell.getRx() - maxX : 0;
        double dy = minY > cell.getRy() ? minY - cell.getRy() : maxY < cell.getRy() ? cell.getRy() - maxY : 0;
        return dx * dx + dy * dy <= cell.getRadius() * cell.getRadius();
    }




}
