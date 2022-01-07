package lielietea.mirai.plugin.core.game.zeppelin.data;

public class Coordinate {
    public int x;
    public int y;

    public Coordinate(int x,int y){
        this.x=x;
        this.y=y;
    }

    public static boolean equals(Coordinate c1, Coordinate c2){
        return (c1.x==c2.x && c1.y==c2.y);
    }
}
