package br.com.ppd.utilitary;

import br.com.ppd.model.Coordinate;

public class SquareUtilitary {

    public static final double SQUARE_SIZE = 20;

    public static final double SQUARE_WIDTH = Math.sqrt(3) * SQUARE_SIZE;

    public static final double SQUARE_HEIGHT = SQUARE_SIZE * 2;

    public static final double HEX_OFFSET = 0;

    public static final double CENTER_X = 400;

    public static final double CENTER_Y = 270;

    public static Coordinate calculateCoordinateOfHexByIndex(Coordinate center, int i) {
        double angle_deg = 90 * i - 45;
        double angle_rad = Math.PI / 180 * angle_deg;

        return new Coordinate(center.getX() + SQUARE_SIZE * Math.cos(angle_rad),
                center.getY() + SQUARE_SIZE * Math.sin(angle_rad));

    }
}
