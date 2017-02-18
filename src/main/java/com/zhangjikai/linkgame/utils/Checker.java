package com.zhangjikai.linkgame.utils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 连连看游戏规则的类<br/>
 * <p>
 * 这个类中有如下静态方法开放给外部调用：<br/>
 * newGame:给定棋盘的实际大小和图案的种数，返回一个可走的棋盘;
 * move:用于某些模式中，自动移动方块的方法，传入移动方向，返回移动后的棋盘，执行此方法后不保证有路可走; canMove:判断棋局是否有路可走;
 * canRemove:判断传入的两个点能否被消去; getPath:用于返回路径，在执行完canRemove且其返回值为true时;
 * help:传入棋局，由一个长度为4的一维数组返回可以被消去的x1,y1,x2,y2;
 * reset：打乱棋局，保证原来每种图案数量不变。传入一个二维数组，返回一个新的二维数组; 另外还有一个静态变量WAY表示图中空的位置，默认为0.<br/>
 * 此外还有四个私有方法：<br/>
 * hasWay:判断一条直线上的两个点是否连通（不包括端点）; turnZero:判断两个点能否在拐0次的情况下被消去。他要求：hasWay&两端点值相同;
 * turnOne(Two):与上面类似，但要求hasWay&两端点值相同&拐点为WAY.
 *
 * @author andong
 * @version 0.5.1 Finished on 5/10/2013<br/>
 *          修改了判断通路的方法，因为原来的方法与动画效果同用会有bug 修正了移动棋盘时的bug 修改了打散数组的算法，使其离散度增加
 *          修复了路径显示错误的bug since 0.4 修复了生成棋盘为长方形时出错的bug，修改了打乱棋盘的方式 since 0.3
 *          加入生成棋盘和移动棋盘的方法，修复了不能消去边界的方块的bug
 */
public class Checker {

    private static final int WAY = 0; // 通路
    private static int[] path = new int[9]; // 记录消去时经过的路径
    private static Mode mode; // 设定棋盘的移动方式，取值为(0,1,2,3,4)，其余值为不移动

    // 给出提示，返回一个长度为4的数组={x1,y1,x2,y2}。若不存在返回全0的数组
    public static int[] help(int[][] arr) {
        for (int i = 0; i < arr.length * arr[0].length; i++) {
            for (int j = 0; j < arr.length * arr[0].length; j++) {
                int x1 = i / arr[0].length;
                int y1 = i % arr[0].length;
                int x2 = j / arr[0].length;
                int y2 = j % arr[0].length;
                if (canRemove(arr, x1, y1, x2, y2)) {
                    return new int[]{x1, y1, x2, y2};
                }
            }
        }
        return null;
    }

    /**
     * 随机产生一个有路可走的棋盘，每种图案的个数均为偶数个。
     * 棋盘的大小为实际大小，如要创建10*10的有效棋盘，出来的是12*12的数组，其中有图案的部分是10*10。
     * width和height不能全为奇数，否则无解。
     *
     * @param width      棋盘的宽度，不包括最外层的0
     * @param height     棋盘的高度，同样不包括最外层的0
     * @param numOfShape 指定有多少种图案可以使用，以便产生随机数
     * @param md         根据模式来决定生成的棋盘的效果，详见Mode.java
     * @return 返回一个最外层被0包裹的数组
     */
    public static int[][] newGame(int width, int height, int numOfShape, Mode md) {
        mode = md;
        int[][] arr = new int[height + 2][width + 2];
        do {
            int[] t = new int[height * width];
            int len = md == Mode.blank ? width * height * 3 / 4 : width
                    * height;
            for (int i = 0; i < len; i += 2) {
                t[i] = t[i + 1] = mode == Mode.blank ? (int) (Math.random() * (numOfShape + 1))
                        : (int) (Math.random() * numOfShape) + 1;
            }
            int times = t.length;
            for (int k = 0; k < times; k++) {
                for (int i = t.length - 1; i >= 1; i--) {
                    int ran = (int) (Math.random() * i);
                    int temp = t[ran];
                    t[ran] = t[i];
                    t[i] = temp;
                }
            }
            for (int i = 0; i < t.length; i++) {
                int y = i % width + 1;
                int x = i / width + 1;
                arr[x][y] = t[i];
            }
        } while (!canMove(arr));
        return arr;
    }

    // 将传入的数组打乱顺序，并返回一个有路可走的棋局
    public static int[][] reset(int[][] arr) {
        Queue<Integer> q = new LinkedList<Integer>();
        do {
            for (int i = 1; i < arr.length - 1; i++) {
                for (int j = 1; j < arr[0].length - 1; j++) {
                    if (arr[i][j] != WAY) {
                        q.add(arr[i][j]);
                        arr[i][j] = WAY;
                    }
                }
            }
            while (!q.isEmpty()) {
                int x, y;
                do {
                    x = (int) ((arr.length - 2) * Math.random() + 1);
                    y = (int) ((arr[0].length - 2) * Math.random() + 1);
                } while (arr[x][y] != WAY);
                arr[x][y] = q.remove();
            }
        } while (!canMove(arr));
        return arr;
    }

    // 判断当前棋局是否有路可走
    public static boolean canMove(int[][] arr) {
        for (int i = 0; i < arr.length * arr[0].length; i++) {
            for (int j = 0; j < arr.length * arr[0].length; j++) {
                int x1 = i / arr[0].length;
                int y1 = i % arr[0].length;
                int x2 = j / arr[0].length;
                int y2 = j % arr[0].length;
                if (canRemove(arr, x1, y1, x2, y2))
                    return true;
            }
        }
        return false;
    }

    // 判断选中的两个点能否被消去
    public static boolean canRemove(int[][] arr, int x1, int y1, int x2, int y2) {
        if (x1 == x2 && y1 == y2) {
            return false;
        }
        int m1 = arr[x1][y1];
        int m2 = arr[x2][y2];
        if (m1 != m2 || m1 == WAY || m2 == WAY) {
            return false;
        }
        return turnZero(arr, x1, y1, x2, y2) || turnOne(arr, x1, y1, x2, y2)
                || turnTwo(arr, x1, y1, x2, y2);
    }

    /**
     * 如果要达到每次消去方块之后其余方块自动移动的效果，请注意： 因为执行完此方法后可能会无路可走，所以先执行本方法，再判断是否canMove.
     *
     * @param arr 移动前的棋盘
     * @return 移动后的棋盘
     */
    public static int[][] move(int[][] arr) {
        if (mode == Mode.center) {
            for (int x = 1; x <= arr.length / 2 - 2; x++)
                for (int y = 1; y <= arr[0].length - 2; y++)
                    if (arr[x][y] <= 0)
                        for (int t = x; t >= 1; t--)
                            arr[t][y] = arr[t - 1][y];
            for (int x = arr.length - 2; x >= arr.length / 2 - 1; x--)
                for (int y = 1; y <= arr[0].length - 2; y++)
                    if (arr[x][y] <= 0)
                        for (int t = x; t <= arr.length - 2; t++)
                            arr[t][y] = arr[t + 1][y];
        } else if (mode == Mode.up) {
            for (int x = arr.length - 2; x >= 1; x--)
                for (int y = 1; y <= arr[0].length - 2; y++)
                    if (arr[x][y] <= 0)
                        for (int t = x; t <= arr.length - 2; t++)
                            arr[t][y] = arr[t + 1][y];
        } else if (mode == Mode.down) {
            for (int x = 1; x <= arr.length - 2; x++)
                for (int y = 1; y <= arr[0].length - 2; y++)
                    if (arr[x][y] <= 0)
                        for (int t = x; t >= 1; t--)
                            arr[t][y] = arr[t - 1][y];
        } else if (mode == Mode.left) {
            for (int x = 1; x <= arr.length - 2; x++)
                for (int y = arr[0].length - 2; y >= 1; y--)
                    if (arr[x][y] <= 0)
                        for (int t = y; t <= arr[0].length - 2; t++)
                            arr[x][t] = arr[x][t + 1];
        } else if (mode == Mode.right) {
            for (int x = 1; x <= arr.length - 2; x++)
                for (int y = 1; y <= arr[0].length - 2; y++)
                    if (arr[x][y] <= 0)
                        for (int t = y; t >= 1; t--)
                            arr[x][t] = arr[x][t - 1];
        }
        return arr;
    }

    /**
     * 因为java只能返回一个值，所以为了获取路径，需要在用户选中两个图案时，若执行canRemove返回真，说明可以消去，
     * 此时调用此方法可以获取一条路径 方法返回所有的端点和拐点，因为通过这些点就可以画出路径
     *
     * @return 返回一个一维数组，这个数组的第一个位置表示有几个这样的点。后面每两个数字组成一个点的x、y坐标。
     * 如｛3，0，2，2，2，2，0｝表示由三个点(0,2),(2,2),(2,0)组成的折线
     */
    public static int[] getPath() {
        return path;
    }

    // 判断位于一条直线（横或竖，不包括斜线）的两个点是否连通.如果连通，返回true。不连通或不是直线，返回false。
    private static boolean hasWay(int[][] arr, int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            if (y1 > y2) {
                int t = y1;
                y1 = y2;
                y2 = t;
            }
            for (int y = y1 + 1; y < y2; y++) {
                if (arr[x1][y] > WAY) {
                    return false;
                }
            }
            return true;
        } else if (y1 == y2) {
            if (x1 > x2) {
                int t = x1;
                x1 = x2;
                x2 = t;
            }
            for (int x = x1 + 1; x < x2; x++) {
                if (arr[x][y1] > WAY) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // 判断能否在转0次的情况下连通
    private static boolean turnZero(int[][] arr, int x1, int y1, int x2, int y2) {
        if (arr[x1][y1] == arr[x2][y2] && hasWay(arr, x1, y1, x2, y2)) {
            path[0] = 2;
            path[1] = x1;
            path[2] = y1;
            path[3] = x2;
            path[4] = y2;
            return true;
        }
        return false;
    }

    // 判断能否在转一次 的情况下连通
    private static boolean turnOne(int[][] arr, int x1, int y1, int x2, int y2) {
        if ((hasWay(arr, x1, y1, x1, y2) && hasWay(arr, x1, y2, x2, y2) && arr[x1][y2] == WAY)) {
            path[0] = 3;
            path[1] = x1;
            path[2] = y1;
            path[3] = x1;
            path[4] = y2;
            path[5] = x2;
            path[6] = y2;
            return true;
        }
        if ((hasWay(arr, x1, y1, x2, y1) && hasWay(arr, x2, y1, x2, y2) && arr[x2][y1] == WAY)) {
            path[0] = 3;
            path[1] = x1;
            path[2] = y1;
            path[3] = x2;
            path[4] = y1;
            path[5] = x2;
            path[6] = y2;
            return true;
        }
        return false;
    }

    // 判断能否在转两次的情况下连通
    private static boolean turnTwo(int[][] arr, int x1, int y1, int x2, int y2) {
        for (int x = 0; x < arr.length; x++) {
            if (hasWay(arr, x1, y1, x, y1) && hasWay(arr, x, y1, x, y2)
                    && hasWay(arr, x, y2, x2, y2) && arr[x][y1] == WAY
                    && arr[x][y2] == WAY) {
                path[0] = 4;
                path[1] = x1;
                path[2] = y1;
                path[3] = x;
                path[4] = y1;
                path[5] = x;
                path[6] = y2;
                path[7] = x2;
                path[8] = y2;
                return true;
            }
        }
        for (int y = 0; y < arr[0].length; y++) {
            if (hasWay(arr, x1, y1, x1, y) && hasWay(arr, x1, y, x2, y)
                    && hasWay(arr, x2, y, x2, y2) && arr[x1][y] == WAY
                    && arr[x2][y] == WAY) {
                path[0] = 4;
                path[1] = x1;
                path[2] = y1;
                path[3] = x1;
                path[4] = y;
                path[5] = x2;
                path[6] = y;
                path[7] = x2;
                path[8] = y2;
                return true;
            }
        }
        return false;
    }

}
