package tech.zeroed.jamgame.gab;

public class RoomTemplate {
    public String layout;
    public boolean containsSpawnPoint = false;
    public Room.GravityType gravityType = Room.GravityType.VERTICAL;
    public int entrances, exits;

    public Room build(){
        return new Room(layout).setGravityType(gravityType);
    }

    public JoinerRoom buildJoiner(){
        return new JoinerRoom(layout);
    }
}
