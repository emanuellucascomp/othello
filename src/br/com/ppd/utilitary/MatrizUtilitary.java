package br.com.ppd.utilitary;

public class MatrizUtilitary {
    public static final int[][] AREA_1 =
            new int[][] {
                    {3, 4},
                    {4, 3}
            };

    public static final int[][] AREA_4 =
            new int[][] {
                    {3, 3},
                    {4, 4}
            };

    public static int[][] getArea(int area) {
        if (area == 1) {
            return AREA_4;
        } else {
            return AREA_1;
        }
    }

    public static int[][] getOponentArea(int area) {
        if (area == 1) {
            return AREA_4;
        } else {
            return AREA_1;
        }
    }

}
