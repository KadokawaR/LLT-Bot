package lielietea.mirai.plugin.core.game.zeppelin.data;

public class Coordinate {
    public Double x;
    public Double y;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static boolean equals(Coordinate c1, Coordinate c2) {
        return (c1.x == c2.x && c1.y == c2.y);
    }

    public boolean equals(Coordinate c){
            return (c.x.equals(this.x) && c.y.equals(this.y));
    }
}
