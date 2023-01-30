package tech.zeroed.jamgame.gab;

public class RoomTemplate {
    public String map;
    public Room.Orientation orientation;
    private static int id = 0;

    public RoomTemplate(String map) {
        this.map = map;
        this.orientation = this.map.startsWith("RoomH") ? Room.Orientation.HORIZONTAL : Room.Orientation.VERTICAL;
    }

    public Room build(){
        return new Room(map, id++);
    }
}
