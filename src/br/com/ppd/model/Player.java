package br.com.ppd.model;

import java.io.Serializable;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private String color;
    private String name;
    private int area;
    private int id;

    public Player(String color, int area, int id, String name) {
        this.color = color;
        this.area = area;
        this.id = id;
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCellStyle() {
        return "hex-" + this.color;
    }

    public String getCellActiveStyle() {
        return "hex-active-" + this.color;
    }

    @Override
    public boolean equals(Object other) {
        Player otherPlayer = (Player) other;
        return this.id == otherPlayer.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
