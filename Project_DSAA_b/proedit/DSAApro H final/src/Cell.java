import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.*;

public class Cell {
    private static double WIDTH, HEIGHT;//Walls边界
    private static final double INFINITY = Double.POSITIVE_INFINITY;

    private final int id;//Identity
    private final double radius;//Radius
    private double rx, ry;//Position
    private double vx, vy;// velocity
    private final double range;//Perception range
    private Color color;//color
    private int[] colors = null;//周边存在的颜色对应细胞个数

    private int cnt1 = 0;

    private int cnt2 = 0;

    private int count = 0;
    /*
    初始化细胞参数
     */
    public Cell(int id, double radius, double rx, double ry, double vx, double vy, double range, Color color){
        this.id = id;
        this.radius = radius;
        this.rx = rx;
        this.ry = ry;
        this.vx = vx;
        this.vy = vy;
        this.range = range;
        this.color = color;
    }

    /*
    初始化边界参数
     */
    public static void setSideLength(double width, double height) {
        WIDTH = width;
        HEIGHT = height;
    }

    public void setCount(int count) {this.count = count;}

    public void setCnt1(int cnt1) {this.cnt1 = cnt1;}

    public void setCnt2(int cnt2) {this.cnt2 = cnt2;}

    public int getCnt1() {return cnt1;}

    public int getCnt2() {return cnt2;}

    public int getCount() {return count;}

    public static double getSideWidth() {
        return WIDTH;
    }

    public static double getSideHeight() {
        return HEIGHT;
    }

    public double getRadius(){
        return this.radius;
    }

    public int getId(){
        return this.id;
    }

    public double getRange(){
        return this.range;
    }

    public double getRx(){
        return this.rx;
    }

    public double getRy(){
        return this.ry;
    }

    public void setRx(double rx){
        this.rx = rx;
    }

    public void setRy(double ry){
        this.ry = ry;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }//增设速度

    public void setVy(double vy) {
        this.vy = vy;
    }

    public Color getColor(){
        return this.color;
    }

    public void setColor(Color color){
        this.color = color;
    }

    /*
    用细胞颜色来获取其速度
     */
    public void setVelocity(){
        if (color == Color.red){
            vx = 0.0;
            vy = 1;
        }
        if (color == Color.green){
            vx = 0.0;
            vy = -1;
        }
        if (color == Color.blue){
            vx = -1;
            vy = 0.0;
        }
        if (color == Color.yellow){
            vx = 1;
            vy = 0.0;
        }
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public int[] getColors() {
        return colors;
    }

    /*
    更新细胞移动一格的位置
     */
    public void update(double dt) {
        rx += vx * dt;
        ry += vy * dt;
    }

    //左右移动
    public void runRight(double d){
        rx += d;
    }

    //上下移动
    public void runUp(double d){
        ry += d;
    }

    //判断另一个细胞b是否在探测范围
    public boolean inPerceptionRange(Cell b){
        if ((b.rx>=rx-range-b.radius && b.rx<=rx+range+b.radius && b.ry>=ry-range && b.ry<=ry+range)
                ||(b.ry>=ry-range-b.radius && b.ry<=ry+range+b.radius && b.rx>=rx-range && b.rx<=rx+range)
                ||(Math.sqrt((b.rx-(rx+range))*(b.rx-(rx+range))+(b.ry-(ry+range))*(b.ry-(ry+range)))<=b.radius)
                ||(Math.sqrt((b.rx-(rx-range))*(b.rx-(rx-range))+(b.ry-(ry-range))*(b.ry-(ry-range)))<=b.radius)
                ||(Math.sqrt((b.rx-(rx-range))*(b.rx-(rx-range))+(b.ry-(ry+range))*(b.ry-(ry+range)))<=b.radius)
                ||(Math.sqrt((b.rx-(rx+range))*(b.rx-(rx+range))+(b.ry-(ry-range))*(b.ry-(ry-range)))<=b.radius)){
            return true;
        }
        else {
            return false;
        }
//        return Math.max(Math.abs(this.rx - b.rx), Math.abs(this.ry - b.ry))
//            <= this.range / 2 + b.radius;
    }

    //画出当前细胞并更新颜色
    public void draw() {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(rx, ry, radius);
    }

    //判断细胞是否在对应象限内
    public boolean in(Map m) {
        return m.contains(this.rx, this.ry);
    }

    //将当前细胞颜色转换为小写字母标号
    public char getColorType(){
        char colorType = 'n';
        if (color == Color.red){
            colorType = 'r';
        }
        if (color == Color.green){
            colorType = 'g';
        }
        if (color == Color.blue){
            colorType = 'b';
        }
        if (color == Color.yellow){
            colorType = 'y';
        }
        return colorType;
    }

    //判断这一个1/15s内是否会碰到
    public boolean willHit(Cell c){
        boolean judge = false;
        if (color == Color.red){
            if (
                    (Math.sqrt((rx-c.rx) * (rx-c.rx) + ((ry+1.0/15)-c.ry) * ((ry+1.0/15)-c.ry)) < radius + c.radius)||
                    ((c.rx > rx - radius - c.radius)&&(c.rx < rx + radius + c.radius)&&(c.ry > ry)&&(c.ry < ry+1.0/15))){
                judge = true;
            }
        }
        if (color == Color.green){
            if (
                    (Math.sqrt((rx-c.rx) * (rx-c.rx) + ((ry-1.0/15)-c.ry) * ((ry-1.0/15)-c.ry)) < radius + c.radius)||
                    ((c.rx > rx - radius - c.radius)&&(c.rx < rx + radius + c.radius)&&(c.ry > ry - 1.0/15)&&(c.ry < ry))){
                judge = true;
            }
        }
        if (color == Color.blue){
            if (
                    (Math.sqrt(((rx-1.0/15)-c.rx) * ((rx-1.0/15)-c.rx) + (ry-c.ry) * (ry-c.ry)) < radius + c.radius)||
                    ((c.ry > ry - radius - c.radius)&&(c.ry < ry + radius + c.radius)&&(c.rx > rx - 1.0/15)&&(c.rx < rx))){
                judge = true;
            }
        }
        if (color == Color.yellow){
            if (
                    (Math.sqrt(((rx+1.0/15)-c.rx) * ((rx+1.0/15)-c.rx) + (ry-c.ry) * (ry-c.ry)) < radius + c.radius)||
                    ((c.ry > ry - radius - c.radius)&&(c.ry < ry + radius + c.radius)&&(c.rx > rx)&&(c.rx < rx + 1.0/15))){
                judge = true;
            }
        }
        return judge;
    }

    //计算撞到另一个球走了多远
    public double rundistance(Cell c){
        double d = 0;
        if (color.equals(Color.red)){
            double rSum = radius + c.radius;
            double dx = rx - c.rx;
            d = c.ry - ry - Math.sqrt((rSum * rSum) - (dx * dx));
        }
        if (color.equals(Color.green)){
            double rSum = radius + c.radius;
            double dx = rx - c.rx;
            d = ry - c.ry - Math.sqrt((rSum * rSum) - (dx * dx));
        }
        if (color.equals(Color.blue)){
            double rSum = radius + c.radius;
            double dy = ry - c.ry;
            d = rx - c.rx - Math.sqrt((rSum * rSum) - (dy * dy));
        }
        if (color.equals(Color.yellow)){
            double rSum = radius + c.radius;
            double dy = ry - c.ry;
            d = c.rx - rx - Math.sqrt((rSum * rSum) - (dy * dy));
        }
//        System.out.println(id+ " " + d);
        return d;
    }

    //检查穿模用两球间距离
    public double distantBetween(Cell c){
        double dx = rx - c.rx;
        double dy = ry - c.ry;
        double d = Math.sqrt(dx*dx+dy*dy);
        return d;
    }

    //判断此回合移动后是否会撞墙
    public boolean judgeHitWall(){
        if ((color.equals(Color.red))&&((HEIGHT - ry - radius) < 1.0/15)){
            return true;
        }
        else if ((color.equals(Color.green))&&(ry - radius < 1.0/15)){
            return true;
        }
        else if ((color.equals(Color.blue))&&(rx - radius < 1.0/15)){
            return true;
        }
        else if ((color.equals(Color.yellow))&&((WIDTH - rx - radius) < 1.0/15)){
            return true;
        }
        else {
            return false;
        }
    }

    //撞墙还需走多远
    public double distantToWall(){
        if (color.equals(Color.red)){
            return HEIGHT - ry - radius;
        }
        else if (color.equals(Color.green)){
            return ry - radius;
        }
        else if (color.equals(Color.blue)){
            return rx - radius;
        }
        else if (color.equals(Color.yellow)){
            return WIDTH - rx - radius;
        }
        else {
            return 0.0;
        }
    }

    public double distanceSquareTo(Cell that) {
        double dx = that.getRx() - this.rx;
        double dy = that.getRy() - this.ry;
        return dx * dx + dy * dy;
    }

}
