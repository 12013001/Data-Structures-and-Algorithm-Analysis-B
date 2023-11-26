import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Stopwatch;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class NCMap {

    public static void main(String[] args) throws IOException {
        //读取文件
        for (String s : args)
            System.out.printf("|%s|\n", s);
        Scanner input = new Scanner(System.in);
        FileReader fileReader = new FileReader(input.nextLine());
        Scanner in = new Scanner(fileReader);

        //选择gui或terminal
        String type = input.nextLine();
        if (type.equals("--terminal")) {
            test2(in);
        }
        else {
            test1(in);
        }
    }

    public static void test1(Scanner in) {
        try ( PrintWriter fout1 = new PrintWriter("./inputs/output.txt") ) {
            final double dt = 1.0 / 15;//设定间隔时间
            double width = in.nextDouble();
            double height = in.nextDouble();

            if (height > width) {
                StdDraw.setCanvasSize((int) (width * 512 / height), 512);
            } else {
                StdDraw.setCanvasSize(512, (int) (height * 512 / width));
            }

            Cell.setSideLength(width, height);
            int N = in.nextInt();
            StdDraw.show(0);
            StdDraw.setXscale(0, +width);
            StdDraw.setYscale(0, +height);
            Cell[] cells = new Cell[N];
            for (int i = 0; i < N; i++) {
                double rx = in.nextDouble();
                double ry = in.nextDouble();
                double radius = in.nextDouble();
                double range = in.nextDouble();
                Color color = null;
                String colorType = in.next();
                char colorType1 = colorType.charAt(0);
                if (colorType1 == 'r') {
                    color = Color.red;
                }
                if (colorType1 == 'g') {
                    color = Color.green;
                }
                if (colorType1 == 'b') {
                    color = Color.blue;
                }
                if (colorType1 == 'y') {
                    color = Color.yellow;
                }
                cells[i] = new Cell(i, radius, rx, ry, 0.0, 0.0, range, color);
                cells[i].setVelocity();
            }

            int n = in.nextInt();
            double[] time = new double[n];
            int[] index = new int[n];
            for (int i = 0; i < n; i++) {
                time[i] = in.nextDouble();
                index[i] = in.nextInt();
            }

            ArrayList<Double> changeColorTime = new ArrayList<Double>();
            for (double t1 = 0; true; t1++) {
                for (double t2 = 0; t2 < 15; t2++) {
                    Map map = new Map(0, 0, width * 2, height * 2);
                    CellBHTree tree = new CellBHTree(map);

                    //命令行输出结果
                    for (int k = 0; k < n; k++) {
                        if ((0 <= time[k] * 15 - (t1 * 15 + t2)) && (time[k] * 15 - (t1 * 15 + t2) < 1)) {
                            double x = cells[index[k]].getRx();
                            double y = cells[index[k]].getRy();
                            char colorChar = cells[index[k]].getColorType();
                            fout1.println(x + " " + y + " " + colorChar);
                            System.out.println(x + " " + y + " " + colorChar);
                            if (k == n - 1) {
                                double runtime = 0.0;
                                for (int p = 0; p < changeColorTime.size(); p++) {
                                    runtime = runtime + changeColorTime.get(p);
                                }
                                System.out.print("The average time of all cell to move and change color in a round is: ");
                                System.out.printf("%.20f\n", runtime / (double) changeColorTime.size());
                                return;//搞完数据停止
                            }
                        }
                    }

                    for (int i = 0; i < N; i++) {
                        if (cells[i].in(map)) {
                            tree.insert(cells[i]);
                        }
                    }

                    //开启计时器
                    Stopwatch timer = new Stopwatch();

                    //move cell[i] to new position.
                    for (int i = 0; i < N; i++) {
                        ArrayList<Cell> hitCells = new ArrayList<Cell>();
                        for (int j = 0; j < N; j++) {
                            if ((cells[i].getId() != j) && (cells[i].willHit(cells[j]))) {
                                hitCells.add(cells[j]);
                            }
                        }
                        double distant = 1.0 / 15;
                        for (int k = 0; k < hitCells.size(); k++) {
                            double cellDistance = Math.max(0.0, cells[i].rundistance(hitCells.get(k)));
                            if ((cells[i].getId() != hitCells.get(k).getId()) && (
                                    cellDistance < distant)) {
                                distant = cellDistance;
                            }
                        }
                        if (cells[i].judgeHitWall()) {
                            if (cells[i].distantToWall() < distant) {
                                distant = cells[i].distantToWall();
                            }
                        }
                        if ((hitCells.size() == 0) && (!cells[i].judgeHitWall())) {
                            cells[i].update(dt);
                        } else {
                            if (cells[i].getColor().equals(Color.red)) {
                                cells[i].runUp(+distant);
                            }
                            if (cells[i].getColor().equals(Color.green)) {
                                cells[i].runUp(-distant);
                            }
                            if (cells[i].getColor().equals(Color.blue)) {
                                cells[i].runRight(-distant);
                            }
                            if (cells[i].getColor().equals(Color.yellow)) {
                                cells[i].runRight(+distant);
                            }
                        }
                    }

                    //scans cell[i]'s perception range, calculate the number of rgb cells.将周围对应颜色数量存入自身数组中
                    for (int i = 0; i < N; i++) {
                        int red = 0;
                        int green = 0;
                        int blue = 0;
                        int yellow = 0;
                        int[] colors = new int[5];
                        for (int j = 0; j < N; j++) {
                            if ((i != j) && (cells[i].inPerceptionRange(cells[j]))) {
                                if (cells[j].getColor().equals(Color.red)) {
                                    red++;
                                }
                                if (cells[j].getColor().equals(Color.green)) {
                                    green++;
                                }
                                if (cells[j].getColor().equals(Color.blue)) {
                                    blue++;
                                }
                                if (cells[j].getColor().equals(Color.yellow)) {
                                    yellow++;
                                }
                            }
                        }
                        colors[0] = red + green + blue + yellow;
                        colors[1] = red;
                        colors[2] = green;
                        colors[3] = blue;
                        colors[4] = yellow;
                        cells[i].setColors(colors);
                    }

                    //change cell[i]'s color with the rules if necessary.
                    for (int i = 0; i < N; i++) {
                        if (cells[i].getColor().equals(Color.red)) {
                            if ((cells[i].getColors()[1] >= 3) && (
                                    ((double) cells[i].getColors()[1] / cells[i].getColors()[0]) > 0.7)) {
                                cells[i].setColor(Color.green);
                            } else if ((cells[i].getColors()[4] >= 1) && (
                                    ((double) cells[i].getColors()[4] / cells[i].getColors()[0]) < 0.1)) {
                                cells[i].setColor(Color.yellow);
                            }
                        }
                        if (cells[i].getColor().equals(Color.green)) {
                            if ((cells[i].getColors()[2] >= 3) && (
                                    ((double) cells[i].getColors()[2] / cells[i].getColors()[0]) > 0.7)) {
                                cells[i].setColor(Color.blue);

                            } else if ((cells[i].getColors()[1] >= 1) && (
                                    ((double) cells[i].getColors()[1] / cells[i].getColors()[0]) < 0.1)) {
                                cells[i].setColor(Color.red);
                            }
                        }
                        if (cells[i].getColor().equals(Color.blue)) {
                            if ((cells[i].getColors()[3] >= 3) && (
                                    ((double) cells[i].getColors()[3] / cells[i].getColors()[0]) > 0.7)) {
                                cells[i].setColor(Color.yellow);
                            } else if ((cells[i].getColors()[2] >= 1) && (
                                    ((double) cells[i].getColors()[2] / cells[i].getColors()[0]) < 0.1)) {
                                cells[i].setColor(Color.green);
                            }
                        }
                        if (cells[i].getColor().equals(Color.yellow)) {
                            if ((cells[i].getColors()[4] >= 3) && (
                                    ((double) cells[i].getColors()[4] / cells[i].getColors()[0]) > 0.7)) {
                                cells[i].setColor(Color.red);
                            } else if ((cells[i].getColors()[3] >= 1) && (
                                    ((double) cells[i].getColors()[3] / cells[i].getColors()[0]) < 0.1)) {
                                cells[i].setColor(Color.blue);
                            }
                        }
                        cells[i].setVelocity();
                    }

                    //关闭计时器，记录一个细胞检测并变色所需时间
                    double changeColorTime1 = timer.elapsedTime();
                    changeColorTime.add(changeColorTime1);

                    StdDraw.clear(StdDraw.BLACK);
                    for (int i = 0; i < N; i++) {
                        cells[i].draw();
                    }

                    StdDraw.show(66);//图动的快慢？？（我也不知道的其实）
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public static void test2(Scanner in) {
        try ( PrintWriter fout2 = new PrintWriter("./inputs/output.txt") ) {
            final double dt = 1.0 / 15;//设定间隔时间
            double width = in.nextDouble();
            double height = in.nextDouble();
            Cell.setSideLength(width, height);
            int N = in.nextInt();
            Cell[] cells = new Cell[N];
            for (int i = 0; i < N; i++) {
                double rx = in.nextDouble();
                double ry = in.nextDouble();
                double radius = in.nextDouble();
                double range = in.nextDouble();
                Color color = null;
                String colorType = in.next();
                char colorType1 = colorType.charAt(0);
                if (colorType1 == 'r') {
                    color = Color.red;
                }
                if (colorType1 == 'g') {
                    color = Color.green;
                }
                if (colorType1 == 'b') {
                    color = Color.blue;
                }
                if (colorType1 == 'y') {
                    color = Color.yellow;
                }
                cells[i] = new Cell(i, radius, rx, ry, 0.0, 0.0, range, color);
                cells[i].setVelocity();
            }

            int n = in.nextInt();
            double[] time = new double[n];
            int[] index = new int[n];
            for (int i = 0; i < n; i++) {
                time[i] = in.nextDouble();
                index[i] = in.nextInt();
            }

            ArrayList<Double> changeColorTime = new ArrayList<Double>();

            for (double t1 = 0; true; t1++) {
                for (double t2 = 0; t2 < 15; t2++) {
                    Map map = new Map(0, 0, width * 2, height * 2);
                    CellBHTree tree = new CellBHTree(map);

                    //命令行输出结果
                    for (int k = 0; k < n; k++) {
                        if ((0 <= time[k] * 15 - (t1 * 15 + t2)) && (time[k] * 15 - (t1 * 15 + t2) < 1)) {
                            double x = cells[index[k]].getRx();
                            double y = cells[index[k]].getRy();
                            char colorChar = cells[index[k]].getColorType();
                            fout2.println(x + " " + y + " " + colorChar);
                            System.out.println(x + " " + y + " " + colorChar);
                            if (k == n - 1) {
                                double runtime = 0.0;
                                for (int p = 0; p < changeColorTime.size(); p++) {
                                    runtime = runtime + changeColorTime.get(p);
                                }
                                System.out.print("The average time of all cell to move and change color in a round is: ");
                                System.out.printf("%.20f\n", runtime / (double) changeColorTime.size());
                                return;//搞完数据停止
                            }
                        }
                    }

                    for (int i = 0; i < N; i++) {
                        if (cells[i].in(map)) {
                            tree.insert(cells[i]);
                        }
                    }

                    //开启计时器
                    Stopwatch timer = new Stopwatch();

                    //move cell[i] to new position.
                    for (int i = 0; i < N; i++) {
                        ArrayList<Cell> hitCells = new ArrayList<Cell>();
                        for (int j = 0; j < N; j++) {
                            if ((cells[i].getId() != j) && (cells[i].willHit(cells[j]))) {
                                hitCells.add(cells[j]);
                            }
                        }
                        double distant = 1.0 / 15;
                        for (int k = 0; k < hitCells.size(); k++) {
                            double cellDistance = Math.max(0.0, cells[i].rundistance(hitCells.get(k)));
                            if ((cells[i].getId() != hitCells.get(k).getId()) && (
                                    cellDistance < distant)) {
                                distant = cellDistance;
                            }
                        }
                        if (cells[i].judgeHitWall()) {
                            if (cells[i].distantToWall() < distant) {
                                distant = cells[i].distantToWall();
                            }
                        }
                        if ((hitCells.size() == 0) && (!cells[i].judgeHitWall())) {
                            cells[i].update(dt);
                        } else {
                            if (cells[i].getColor().equals(Color.red)) {
                                cells[i].runUp(+distant);
                            }
                            if (cells[i].getColor().equals(Color.green)) {
                                cells[i].runUp(-distant);
                            }
                            if (cells[i].getColor().equals(Color.blue)) {
                                cells[i].runRight(-distant);
                            }
                            if (cells[i].getColor().equals(Color.yellow)) {
                                cells[i].runRight(+distant);
                            }
                        }
                    }

                    //scans cell[i]'s perception range, calculate the number of rgb cells.将周围对应颜色数量存入自身数组中
                    for (int i = 0; i < N; i++) {
                        int red = 0;
                        int green = 0;
                        int blue = 0;
                        int yellow = 0;
                        int[] colors = new int[5];
                        for (int j = 0; j < N; j++) {
                            if ((i != j) && (cells[i].inPerceptionRange(cells[j]))) {
                                if (cells[j].getColor().equals(Color.red)) {
                                    red++;
                                }
                                if (cells[j].getColor().equals(Color.green)) {
                                    green++;
                                }
                                if (cells[j].getColor().equals(Color.blue)) {
                                    blue++;
                                }
                                if (cells[j].getColor().equals(Color.yellow)) {
                                    yellow++;
                                }
                            }
                        }
                        colors[0] = red + green + blue + yellow;
                        colors[1] = red;
                        colors[2] = green;
                        colors[3] = blue;
                        colors[4] = yellow;
                        cells[i].setColors(colors);
                    }

                    //change cell[i]'s color with the rules if necessary.
                    for (int i = 0; i < N; i++) {
                        if (cells[i].getColor().equals(Color.red)) {
                            if ((cells[i].getColors()[1] >= 3) && (
                                    ((double) cells[i].getColors()[1] / cells[i].getColors()[0]) > 0.7)) {
                                cells[i].setColor(Color.green);
                            } else if ((cells[i].getColors()[4] >= 1) && (
                                    ((double) cells[i].getColors()[4] / cells[i].getColors()[0]) < 0.1)) {
                                cells[i].setColor(Color.yellow);
                            }
                        }
                        if (cells[i].getColor().equals(Color.green)) {
                            if ((cells[i].getColors()[2] >= 3) && (
                                    ((double) cells[i].getColors()[2] / cells[i].getColors()[0]) > 0.7)) {
                                cells[i].setColor(Color.blue);

                            } else if ((cells[i].getColors()[1] >= 1) && (
                                    ((double) cells[i].getColors()[1] / cells[i].getColors()[0]) < 0.1)) {
                                cells[i].setColor(Color.red);
                            }
                        }
                        if (cells[i].getColor().equals(Color.blue)) {
                            if ((cells[i].getColors()[3] >= 3) && (
                                    ((double) cells[i].getColors()[3] / cells[i].getColors()[0]) > 0.7)) {
                                cells[i].setColor(Color.yellow);
                            } else if ((cells[i].getColors()[2] >= 1) && (
                                    ((double) cells[i].getColors()[2] / cells[i].getColors()[0]) < 0.1)) {
                                cells[i].setColor(Color.green);
                            }
                        }
                        if (cells[i].getColor().equals(Color.yellow)) {
                            if ((cells[i].getColors()[4] >= 3) && (
                                    ((double) cells[i].getColors()[4] / cells[i].getColors()[0]) > 0.7)) {
                                cells[i].setColor(Color.red);
                            } else if ((cells[i].getColors()[3] >= 1) && (
                                    ((double) cells[i].getColors()[3] / cells[i].getColors()[0]) < 0.1)) {
                                cells[i].setColor(Color.blue);
                            }
                        }
                        cells[i].setVelocity();
                    }

                    //关闭计时器，记录一个细胞检测并变色所需时间
                    double changeColorTime1 = timer.elapsedTime();
                    changeColorTime.add(changeColorTime1);
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
    //优化变色部分
    public static void test3(Scanner in) throws FileNotFoundException {
        try ( PrintWriter fout3 = new PrintWriter("./inputs/output.txt") ) {
            final double dt = 1.0 / 15;//设定间隔时间
            double width = in.nextDouble();
            double height = in.nextDouble();

            ArrayList<Double> changeColorTime = new ArrayList<Double>();

            if (height > width) {
                StdDraw.setCanvasSize((int) (width * 512 / height), 512);
            } else {
                StdDraw.setCanvasSize(512, (int) (height * 512 / width));
            }

            Cell.setSideLength(width, height);
            int N = in.nextInt();
            StdDraw.show(0);
            StdDraw.setXscale(0, +width);
            StdDraw.setYscale(0, +height);
            Cell[] cells = new Cell[N];
            for (int i = 0; i < N; i++) {
                double rx = in.nextDouble();
                double ry = in.nextDouble();
                double radius = in.nextDouble();
                double range = in.nextDouble();
                Color color = null;
                String colorType = in.next();
                char colorType1 = colorType.charAt(0);
                if (colorType1 == 'r') {
                    color = Color.red;
                }
                if (colorType1 == 'g') {
                    color = Color.green;
                }
                if (colorType1 == 'b') {
                    color = Color.blue;
                }
                if (colorType1 == 'y') {
                    color = Color.yellow;
                }
                cells[i] = new Cell(i, radius, rx, ry, 0.0, 0.0, range, color);
                cells[i].setVelocity();
            }

            int n = in.nextInt();
            double[] time = new double[n];
            int[] index = new int[n];
            for (int i = 0; i < n; i++) {
                time[i] = in.nextDouble();
                index[i] = in.nextInt();
            }

            for (double t1 = 0; true; t1++) {
                for (double t2 = 0; t2 < 15; t2++) {
                    Map map = new Map(0, 0, width * 2, height * 2);

                    //命令行输出结果
                    for (int k = 0; k < n; k++) {
                        if ((0 <= time[k] * 15 - (t1 * 15 + t2)) && (time[k] * 15 - (t1 * 15 + t2) < 1)) {
                            double x = cells[index[k]].getRx();
                            double y = cells[index[k]].getRy();
                            char colorChar = cells[index[k]].getColorType();
                            fout3.println(x + " " + y + " " + colorChar);
                            System.out.println(x + " " + y + " " + colorChar);
                            if (k == n - 1) {
                                double runtime = 0.0;
                                for (int p = 0; p < changeColorTime.size(); p++) {
                                    runtime = runtime + changeColorTime.get(p);
                                }
                                System.out.print("The average time of all cell to move and change color in a round is: ");
                                System.out.printf("%.20f\n", runtime / (double) changeColorTime.size());
                                return;//搞完数据停止
                            }
                        }
                    }
                    //生成KdTree
                    KdTree kdTree = new KdTree();
                    for (int i = 0; i < N; i++) {
                        if (cells[i].in(map)) {
                            kdTree.insert(cells[i]);
                        }
                    }

                    //开启计时器
                    Stopwatch timer = new Stopwatch();

                    //move cell[i] to new position.
                    for (int i = 0; i < N; i++) {
                        ArrayList<Cell> hitCells = new ArrayList<Cell>();
                        for (int j = 0; j < N; j++) {
                            if ((cells[i].getId() != j) && (cells[i].willHit(cells[j]))) {
                                hitCells.add(cells[j]);
                            }
                        }
                        double distant = 1.0 / 15;
                        for (int k = 0; k < hitCells.size(); k++) {
                            double cellDistance = Math.max(0.0, cells[i].rundistance(hitCells.get(k)));
                            if ((cells[i].getId() != hitCells.get(k).getId()) && (
                                    cellDistance < distant)) {
                                distant = cellDistance;
                            }
                        }
                        if (cells[i].judgeHitWall()) {
                            if (cells[i].distantToWall() < distant) {
                                distant = cells[i].distantToWall();
                            }
                        }
                        if ((hitCells.size() == 0) && (!cells[i].judgeHitWall())) {
                            cells[i].update(dt);
                        } else {
                            if (cells[i].getColor().equals(Color.red)) {
                                cells[i].runUp(+distant);
                            }
                            if (cells[i].getColor().equals(Color.green)) {
                                cells[i].runUp(-distant);
                            }
                            if (cells[i].getColor().equals(Color.blue)) {
                                cells[i].runRight(-distant);
                            }
                            if (cells[i].getColor().equals(Color.yellow)) {
                                cells[i].runRight(+distant);
                            }
                        }
                    }
                    //变色
                    for (Cell i : cells) {
                        changeColor(kdTree, i);
                    }
                    changeAllColor(cells);

                    //关闭计时器，记录一个细胞检测并变色所需时间
                    double changeColorTime1 = timer.elapsedTime();
                    changeColorTime.add(changeColorTime1);

                    StdDraw.clear(StdDraw.BLACK);
                    for (int i = 0; i < N; i++) {
                        cells[i].draw();
                    }

                    StdDraw.show(66);

                }
            }
        }
    }
    //变色计数函数，计算周边Cell颜色并存入cell属性中
    public static void changeColor(KdTree tree, Cell cell){
        if(!tree.contains(cell)) return;
        Iterable<Cell> sur = new ArrayList<Cell>();
        sur = tree.range(cell);
        int count = 0;
        int cnt1 = 0;
        int cnt2 = 0;
        //获取链表的第i个元素并统计颜色数据
        for(Cell i:sur){
            if(cell.getColor().equals(Color.red)){
                if(i.getColor().equals(Color.red)){cnt1++;}
                if(i.getColor().equals(Color.yellow)){cnt2++;}
            }
            else if(cell.getColor().equals(Color.green)){
                if(i.getColor().equals(Color.green)){cnt1++;}
                if(i.getColor().equals(Color.red)){cnt2++;}
            }
            else if(cell.getColor().equals(Color.blue)){
                if(i.getColor().equals(Color.blue)){cnt1++;}
                else if(i.getColor().equals(Color.green)){cnt2++;}
            }
            else if(cell.getColor().equals(Color.yellow)){
                if(i.getColor().equals(Color.yellow)){cnt1++;}
                else if(i.getColor().equals(Color.blue)){cnt2++;}
            }
            count++;
        }
        cell.setCnt1(cnt1);
        cell.setCnt2(cnt2);
        cell.setCount(count);
    }
    //如果颜色数据满足条件，则改变颜色
    public static void changeAllColor(Cell[] cells){
        for(Cell cell:cells){
            if(cell.getCnt1()>=3&&((double)cell.getCnt1()/cell.getCount())>0.7){
                if(cell.getColor().equals(Color.red)){cell.setColor(Color.green);}
                else if(cell.getColor().equals(Color.green)){cell.setColor(Color.blue);}
                else if(cell.getColor().equals(Color.blue)){cell.setColor(Color.yellow);}
                else if(cell.getColor().equals(Color.yellow)){cell.setColor(Color.red);}
            }
            else if(cell.getCnt2()>=1&&((double)cell.getCnt2()/cell.getCount())<0.1){
                if(cell.getColor().equals(Color.red)){cell.setColor(Color.yellow);}
                else if(cell.getColor().equals(Color.green)){cell.setColor(Color.red);}
                else if(cell.getColor().equals(Color.blue)){cell.setColor(Color.green);}
                else if(cell.getColor().equals(Color.yellow)){cell.setColor(Color.blue);}
            }
        }
    }
}
