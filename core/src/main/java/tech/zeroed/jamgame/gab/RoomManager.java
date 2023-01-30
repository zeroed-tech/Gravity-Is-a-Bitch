package tech.zeroed.jamgame.gab;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.SnapshotArray;
import tech.zeroed.jamgame.gab.Entities.Player;
import text.formic.Stringf;

import static tech.zeroed.jamgame.gab.GravitysABitch.shapeDrawer;

public class RoomManager {
    private SnapshotArray<Room> rooms;

    private SnapshotArray<RoomTemplate> templates;

    public Room currentRoom = null;
    Player player;

    RoomTemplate spawnPoint;

    RoomTemplate eastToNorth;
    RoomTemplate eastToSouth;
    RoomTemplate westToSouth;
    RoomTemplate westToNorth;

    public RoomManager() {
        rooms = new SnapshotArray<>(true, 500);
        templates = new SnapshotArray<>();

        // Load all rooms
        for(FileHandle map : Gdx.files.internal("Rooms").list((dir, name) -> name.startsWith("Room") && name.endsWith(".tmx"))){
            addTemplate(new RoomTemplate(map.nameWithoutExtension()));
        }

        // Load joiners
        eastToNorth = new RoomTemplate("EN");
        eastToSouth = new RoomTemplate("ES");
        westToSouth = new RoomTemplate("WS");
        westToNorth = new RoomTemplate("WN");
        spawnPoint = new RoomTemplate("Spawn");
        spawnPoint.orientation = Room.Orientation.HORIZONTAL;
    }

    public void addTemplate(RoomTemplate template){
        templates.add(template);
    }

    private void addRoom(Room room){
        rooms.add(room);
        if(currentRoom == null) {
            currentRoom = room;
            player = room.player;
            if(player == null){
                Gdx.app.error("RoomManager", "First room did not contain a player spawn point");
            }
            changeRoom(room);
        }
    }

    public void changeRoom(Room newRoom){
        newRoom.enterRoom(player);
        currentRoom = newRoom;
        int index = rooms.indexOf(currentRoom, true);
        Object[] iterator = rooms.begin();
        //unload earlier rooms
        for(int i = index-5; i < index-2; i++){
            if(i >= 0 && i < rooms.size){
                Room r = (Room) iterator[i];
                if(r == null)continue;
                if(r.addedToWorld){
                    r.removeFromWorld();
                    rooms.removeValue(r, true);
                }
            }
        }
        rooms.end();
        //Load future rooms
        for(int i = index; i < index+3; i++){
            if(i > 0 && i < rooms.size){
                Room r = rooms.get(i);
                if(r == null)continue;
                if(!r.addedToWorld){
                    r.addToWorld();
                }
            }
        }
    }

    public void update(float delta){
        if(!currentRoom.containsPlayer(player.x, player.y)){
            Room previousRoom = currentRoom;
            // Player has left the current room, work out where they went
            Object[]  _rooms = rooms.begin();
            for (int i = 0, n = rooms.size; i < n; i++) {
                Room room = (Room)_rooms[i];
                if(room.containsPlayer(player.x, player.y)){
                    changeRoom(room);
                    Gdx.app.log("RoomManager", "Changing rooms "+room.getInitialMoveDirection()+"/"+room.getFinalMoveDirection()+" ID: "+room.getId());
                    break;
                }
            }
            if(currentRoom == previousRoom){
                Gdx.app.error("RoomManager", Stringf.format("Didn't change rooms for some reason. Player is at %f,%f", player.x, player.y));
            }
            rooms.end();
        }

        for(Room room : rooms){
            room.render(delta);
        }
    }

    public void generateLevel(int roomCount) {
        // Generate a spawn point
        RoomTemplate previousRoomTemplate = spawnPoint;
        Room previousRoom;

        Room spawnPoint = previousRoomTemplate.build().setMoveDirection(Room.MoveDirection.RIGHT, Room.MoveDirection.RIGHT);
        addRoom(spawnPoint);
        previousRoom = spawnPoint;

        // Generate some more rooms
        for(int i = 1; i <= roomCount; i++){
            RoomTemplate template;

            template = templates.random();

            Room.MoveDirection moveDirection = previousRoom.getFinalMoveDirection();
            if(template.orientation != previousRoomTemplate.orientation){
                // Randomise the gravity direction for the new room
                boolean moveEast = MathUtils.randomBoolean();
                boolean moveNorth = MathUtils.randomBoolean();

                // The rooms are switching gravity directions, add a joiner room
                RoomTemplate joinerRoomTemplate = null;

                Room.MoveDirection initialDirection = previousRoom.getFinalMoveDirection();
                Room.MoveDirection finalDirection = Room.MoveDirection.RIGHT;
                if(previousRoomTemplate.orientation == Room.Orientation.HORIZONTAL){
                    // We are transitioning into a vertical room
                    if(previousRoom.getFinalMoveDirection() == Room.MoveDirection.RIGHT){
                        if(moveNorth){
                            joinerRoomTemplate = westToNorth;
                            moveDirection = Room.MoveDirection.UP;
                            finalDirection = Room.MoveDirection.UP;
                        }else{
                            // We will be moving down
                            joinerRoomTemplate = westToSouth;
                            moveDirection = Room.MoveDirection.DOWN;
                            finalDirection = Room.MoveDirection.DOWN;
                        }
                    }else if(previousRoom.getFinalMoveDirection() == Room.MoveDirection.LEFT){
                        if(moveNorth){
                            // We will be moving up
                            joinerRoomTemplate = eastToNorth;
                            moveDirection = Room.MoveDirection.UP;
                            finalDirection = Room.MoveDirection.UP;
                        }else{
                            // We will be moving down
                            joinerRoomTemplate = eastToSouth;
                            moveDirection = Room.MoveDirection.DOWN;
                            finalDirection = Room.MoveDirection.DOWN;
                        }
                    }
                }
                else
                {
                    // We are transitioning to a horizontal room
                    if(previousRoom.getFinalMoveDirection() == Room.MoveDirection.UP){
                        if(moveEast){
                            // We will be moving to the right
                            joinerRoomTemplate = eastToSouth;
                            moveDirection = Room.MoveDirection.RIGHT;
                            finalDirection = Room.MoveDirection.RIGHT;
                        }else{
                            // We will be moving to the left
                            joinerRoomTemplate = westToSouth;
                            moveDirection = Room.MoveDirection.LEFT;
                            finalDirection = Room.MoveDirection.LEFT;
                        }
                    }else if(previousRoom.getFinalMoveDirection() == Room.MoveDirection.DOWN){
                        if(moveEast){
                            // We will be moving to the right
                            joinerRoomTemplate = eastToNorth;
                            moveDirection = Room.MoveDirection.RIGHT;
                            finalDirection = Room.MoveDirection.RIGHT;
                        }else{
                            // We will be moving to the left
                            joinerRoomTemplate = westToNorth;
                            moveDirection = Room.MoveDirection.LEFT;
                            finalDirection = Room.MoveDirection.LEFT;
                        }
                    }


                }

                if(joinerRoomTemplate != null){
                    Room joinerRoom = joinerRoomTemplate.build().setMoveDirection(initialDirection, finalDirection);
                    // Determine where the previous room will be exited
                    previousRoom.attachRoom(joinerRoom);

                    addRoom(joinerRoom);
                    previousRoom = joinerRoom;
                }else{
                    Gdx.app.error("", "Couldn't find joiner template");
                }
            }

            Room newRoom = template.build().setMoveDirection(moveDirection, moveDirection);
            previousRoom.attachRoom(newRoom);

            addRoom(newRoom);

            previousRoom = newRoom;
            previousRoomTemplate = template;
        }

        changeRoom(spawnPoint);
        spawnPoint.addToWorld();
    }
}
