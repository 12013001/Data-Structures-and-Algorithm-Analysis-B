import java.util.ArrayList;
import java.util.LinkedList;

public class KdTree {

    // 节点类，其中 rect 成员表示该节点所分割的平面，
    // 即它的左右孩子所表示的空间之和，该成员用于判断
    // 最邻近点
    private class Node {
        Cell cell;

        Rect rect;
        Node left;
        Node right;
        Node (Cell c) {
            cell = c;
            rect = new Rect(c);
            left = null;
            right = null;
        }
    }

    // 根节点
    private Node root;
    // 构造函数
    public KdTree() {
        root = null;
    }

    // 插入， 用同名私有方法递归实现， 默认根节点是纵向分割
    public void insert(Cell cell) {
        root = insert(cell, root, false, 0.0, 0.0, 1.0, 1.0);
    }

    private Node insert(Cell cell, Node node, boolean isVertical,
                        double x0, double y0, double x1, double y1) {

        if (node == null) {
            return new Node(cell);
        }

        // 改变分割方向
        isVertical = !isVertical;

        // 判断要插入的点在当前点的左/下还是右/上
        double value0 = isVertical ? cell.getRx() : cell.getRy();
        double value1 = isVertical ? node.cell.getRx(): node.cell.getRy();
        if (value0 < value1) {
            node.left = insert(cell, node.left, isVertical,
                    x0, y0, isVertical ? node.cell.getRx() : x1, isVertical ? y1 : node.cell.getRy());
        } else {
            node.right = insert(cell, node.right, isVertical,
                    isVertical ? node.cell.getRx() : x0, isVertical ? y0 : node.cell.getRy(), x1, y1);
        }
        return node;
    }

    // 判断是否包含该点， 用同名私有方法递归实现
    public boolean contains(Cell cell) {
        return contains(cell, root, false);
    }

    private boolean contains(Cell cell, Node node, boolean isVertical) {
        if (node == null) return false;

        if (node.cell.equals(cell)) return true;

        // 改变分割方向
        isVertical = !isVertical;

        // 判断要查询的点在当前点的左/下还是右/上
        double value1 = isVertical ? cell.getRx() : cell.getRy();
        double value2 = isVertical ? node.cell.getRx() : node.cell.getRy();
        if (value1 < value2) {
            return contains(cell, node.left, isVertical);
        } else {
            return contains(cell, node.right, isVertical);
        }
    }

    // 返回矩形范围内的所有点， 用同名私有方法递归实现
    public Iterable<Cell> range(Cell cell) {
        ArrayList<Cell> result = new ArrayList<Cell>();
        Rect rect = new Rect(cell);
        range(rect, root, false, result);
        result.remove(cell);
        return result;
    }

    private void range(Rect rect, Node node, boolean isVertical, ArrayList<Cell> bag) {
        if (node == null) return;

        // 改变分割方向
        isVertical = !isVertical;
        Cell cell = node.cell;
        if (rect.checkOverlap(cell)) bag.add(cell);

        // 判断当前点所分割的两个空间是否与矩形相交
        double value = isVertical ? cell.getRx() : cell.getRy();
        double min = isVertical ? rect.minX - cell.getRadius() : rect.minY - cell.getRadius();
        double max = isVertical ? rect.maxX + cell.getRadius() : rect.maxY + cell.getRadius();
        if (min < value) {
            range(rect, node.left, isVertical, bag);
        }
        if (max >= value) {
            range(rect, node.right, isVertical, bag);
        }
    }

    // 返回距离该点最近的点， 用同名私有方法递归实现
    public Cell nearest(Cell target) {
        return nearest(target, root, null, false);
    }

    private Cell nearest(Cell target, Node node, Cell currentBest, boolean isVertical) {
        if (node == null) return currentBest;
        isVertical = !isVertical;
        double value1 = isVertical ? target.getRx() : target.getRy();
        double value2 = isVertical ? node.cell.getRx() : node.cell.getRy();

        // 继续搜索目标点所在的半区
        Node next = value1 < value2 ? node.left : node.right;
        Node other = value1 < value2 ? node.right : node.left;
        Cell nextBest = nearest(target, next, node.cell, isVertical);
        double currentDistance = 0;
        double nextDistance = nextBest.distanceSquareTo(target);
        if (currentBest == null) {
            currentBest = nextBest;
            currentDistance = nextDistance;
        } else {
            currentDistance = currentBest.distanceSquareTo(target);
            if (nextDistance < currentDistance) {
                currentBest = nextBest;
                currentDistance = nextDistance;
            }
        }
        // 判断另一半区是否可能包含更近的点
        if ((other != null) && (other.rect.distanceSquareToPoint(target) < currentDistance)) {
            currentBest = nearest(target, other, currentBest, isVertical);
        }
        return currentBest;
    }
}



