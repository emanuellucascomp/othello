package br.com.ppd.model;

public class Movement {

    private Square origin;

    private Square destination;

    public Movement(Square origin, Square destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public Square getOrigin() {
        return origin;
    }

    public void setOrigin(Square origin) {
        this.origin = origin;
    }

    public Square getDestination() {
        return destination;
    }

    public void setDestination(Square destination) {
        this.destination = destination;
    }


}
