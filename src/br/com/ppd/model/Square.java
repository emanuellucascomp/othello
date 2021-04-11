package br.com.ppd.model;

import br.com.ppd.utilitary.SquareUtilitary;
import javafx.scene.shape.Polygon;

import java.io.Serializable;

public class Square implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean movable;
    private boolean empty;
    private Player owner;
    private Polygon hex;
    private Coordinate center;
    private int matrixIndexRow;
    private int matrixIndexColumn;


    public Square(Coordinate center, int i, int j) {
        this.center = center;
        createTile();
        this.matrixIndexRow = i;
        this.matrixIndexColumn = j;
        this.reset();
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public Polygon getHex() {
        return hex;
    }

    public void setHex(Polygon hex) {
        this.hex = hex;
    }

    public Coordinate getCenter() {
        return center;
    }

    public int getMatrixIndexRow() {
        return matrixIndexRow;
    }

    public int getMatrixIndexColumn() {
        return matrixIndexColumn;
    }

    public Player getOwner() {
        return owner;
    }

    private void createTile() {
        this.hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            this.hex.getPoints().addAll(SquareUtilitary.calculateCoordinateOfHexByIndex(this.center, i).asArray());
        }
    }

    public void setOwner(Player player) {
        this.owner = player;
        this.getHex().getStyleClass().add(player.getCellStyle());
        this.setEmpty(false);
    }

    public void reset() {
        this.owner = null;
        this.setMovable(false);
        this.getHex().getStyleClass().clear();
        this.getHex().getStyleClass().add("hex");
        this.getHex().setOnMouseClicked(null);
        this.setEmpty(true);
    }

    public void selectCell() {
        getHex().getStyleClass().clear();
        getHex().getStyleClass().add(owner.getCellActiveStyle());
    }

    public void deselectCell() {
        getHex().getStyleClass().clear();
        getHex().getStyleClass().add(owner.getCellStyle());
    }


    @Override
    public boolean equals(Object obj) {
        Square squareObj = (Square) obj;
        if (obj == null)
            return false;
        else
            return this.center.equals(squareObj.getCenter());
    }

}
