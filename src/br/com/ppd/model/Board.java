package br.com.ppd.model;

import br.com.ppd.utilitary.SquareUtilitary;
import br.com.ppd.utilitary.MatrizUtilitary;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int BOARD_WIDTH = 8;

    public static final int BOARD_HEIGHT = 8;

    private Square[][] boardMatrix;

    private int[][] possibleNeighborPositions;


    public Board() {
        this.boardMatrix = new Square[BOARD_HEIGHT][BOARD_WIDTH];
        this.possibleNeighborPositions = new int[][] {{-1, -1},
                {-1, 0},
                {0, -1},
                {0, +1},
                {1, -1},
                {1, 0}};
    }

    public Square[][] getBoardMatrix() {
        return boardMatrix;
    }

    public void createBoard() {
        createRectangle();
    }

    private void createRectangle(){
        int quantity = 8;
        int offset = 0;

        double baseX = SquareUtilitary.CENTER_X - (6 * SquareUtilitary.SQUARE_WIDTH);
        double baseY = SquareUtilitary.CENTER_Y + (3 * SquareUtilitary.SQUARE_HEIGHT);

        for (int i = 7; i >= 0 ; i--) {
            for (int j = offset; j < quantity; j++) {
                Square tileSquare = new Square(new Coordinate(baseX+(j* SquareUtilitary.SQUARE_WIDTH), baseY - ((12-i)* SquareUtilitary.SQUARE_HEIGHT *3/4)), i, j);
                this.boardMatrix[i][j] = tileSquare;
            }
        }
    }

    public List<Square> getAdjacentTo(Square square) {
        List<Square> squares = new ArrayList<>();
        Square neighborSquare = null;
        for (int i = 0; i < 6; i++) {
            try {

                if (square.getMatrixIndexRow()%2 == 0)
                    neighborSquare = this.boardMatrix[square.getMatrixIndexRow() + this.possibleNeighborPositions[i][0]]
                            [square.getMatrixIndexColumn() + this.possibleNeighborPositions[i][1]];
                else if (i < 2 || i > 3)
                    neighborSquare = this.boardMatrix[square.getMatrixIndexRow() + this.possibleNeighborPositions[i][0]]
                            [square.getMatrixIndexColumn() + this.possibleNeighborPositions[i][1]+1];
                else
                    neighborSquare = this.boardMatrix[square.getMatrixIndexRow() + this.possibleNeighborPositions[i][0]]
                            [square.getMatrixIndexColumn() + this.possibleNeighborPositions[i][1]];

                if (neighborSquare != null) {
                    squares.add(neighborSquare);
                }
            } catch (ArrayIndexOutOfBoundsException e) {}
        }
        return squares;
    }

    public boolean winCondition(Player player) {
        for (int[] position : MatrizUtilitary.getOponentArea(player.getArea())) {
            Square square = this.boardMatrix[position[0]][position[1]];
            if (square.isEmpty() || !square.getOwner().equals(player))
                return false;
        }
        return true;
    }

}
