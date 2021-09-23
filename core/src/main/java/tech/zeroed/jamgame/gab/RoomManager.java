package tech.zeroed.jamgame.gab;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import tech.zeroed.jamgame.gab.Entities.Player;
import text.formic.Stringf;

public class RoomManager {
    private SnapshotArray<Room> rooms;

    private SnapshotArray<RoomTemplate> templates;

    private Array<RoomTemplate> spawnPoints;

    private Array<RoomTemplate> oneEntrance;
    private Array<RoomTemplate> twoEntrance;

    private Array<RoomTemplate> oneExit;
    private Array<RoomTemplate> twoExit;

    public Room currentRoom = null;
    Player player;

    RoomTemplate downRight;
    RoomTemplate leftDown;
    RoomTemplate upLeft;
    RoomTemplate rightUp;

    public static final float BASE_HOLDING_GRAVITY = 25;
    public static final float BASE_MOVING_GRAVITY = 1;

    public RoomManager() {
        setupBaseTemplates();
        rooms = new SnapshotArray<>(true, 500);
        templates = new SnapshotArray<>();
        spawnPoints = new Array<>();
        oneEntrance = new Array<>();
        twoEntrance = new Array<>();
        oneExit = new Array<>();
        twoExit = new Array<>();
    }

    private void setupBaseTemplates(){
        rightUp = new RoomTemplate(){
            {
                layout =
                        "B       B\n" +
                        "        B\n" +
                        "        B\n" +
                        "        B\n" +
                        "        B\n" +
                        "        B\n" +
                        "        B\n" +
                        "        B\n" +
                        "BBBBBBBBB\n";
            }
        };

        upLeft = new RoomTemplate(){
            {
                layout =
                        "BBBBBBBBB\n" +
                        "        B\n" +
                        "        B\n" +
                        "        B\n" +
                        "        B\n" +
                        "        B\n" +
                        "        B\n" +
                        "        B\n" +
                        "B       B\n";
            }
        };

        leftDown = new RoomTemplate(){
            {
                layout =
                        "BBBBBBBBB\n" +
                        "B        \n" +
                        "B        \n" +
                        "B        \n" +
                        "B        \n" +
                        "B        \n" +
                        "B        \n" +
                        "B        \n" +
                        "B       B\n";
            }
        };


        downRight = new RoomTemplate(){
            {
                layout =
                        "B       B\n" +
                        "B        \n" +
                        "B        \n" +
                        "B        \n" +
                        "B        \n" +
                        "B        \n" +
                        "B        \n" +
                        "B        \n" +
                        "BBBBBBBBB\n";
            }
        };
    }

    public void addTemplate(RoomTemplate template){
        templates.add(template);
        if(template.containsSpawnPoint) spawnPoints.add(template);

        if(template.exits == 1) oneExit.add(template);
        if(template.exits == 2) twoExit.add(template);

        if(template.entrances == 1) oneEntrance.add(template);
        if(template.entrances == 2) twoEntrance.add(template);
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
            if(i > 0 && i < rooms.size){
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
        for(int i = index; i < index+5; i++){
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
                    Gdx.app.log("RoomManager", "Changing rooms "+room.getMoveDirection()+" ID: "+room.id);
                    break;
                }
            }
            if(currentRoom == previousRoom){
                Gdx.app.error("RoomManager", Stringf.format("Didn't change rooms for some reason. Player is at %f,%f", player.x, player.y));
            }
            rooms.end();
        }
        currentRoom.update();

        for(Room room : rooms){
            room.render();
        }
    }

    public void generateLevel(int roomCount) {
        int id = 0;
        // Generate a spawn point
        RoomTemplate previousRoomTemplate = spawnPoints.random();
        Room previousRoom;

        Room spawnPoint = previousRoomTemplate.build().setSpeedLevel(0).setMoveDirection(Room.MoveDirection.RIGHT);
        spawnPoint.id = id++;
        addRoom(spawnPoint);
        previousRoom = spawnPoint;

        // Generate some more rooms
        for(int i = 1; i <= roomCount; i++){
            RoomTemplate template;
            if(previousRoomTemplate.exits == 1){
                template = oneEntrance.random();
            }else{
                template = twoEntrance.random();
            }

            Room.MoveDirection moveDirection = previousRoom.getMoveDirection();
            if(template.gravityType != previousRoomTemplate.gravityType){
                // Randomise the gravity direction for the new room
                boolean gravPullRight = false;
                boolean gravPullDown = false;

                // The rooms are switching gravity directions, add a joiner room
                RoomTemplate joinerRoomTemplate = null;

                Room.MoveDirection initialDirection = Room.MoveDirection.RIGHT;
                Room.MoveDirection finalDirection = Room.MoveDirection.RIGHT;
                if(previousRoomTemplate.gravityType == Room.GravityType.VERTICAL){
                    // We are transitioning into a horizontal gravity room
                    if(previousRoom.getMoveDirection() == Room.MoveDirection.RIGHT){
                        if(gravPullDown){
                            joinerRoomTemplate = rightUp;
                            moveDirection = Room.MoveDirection.UP;
                            initialDirection = previousRoom.getMoveDirection();
                            finalDirection = Room.MoveDirection.UP;
                        }else{
                            // We will be moving down
                            joinerRoomTemplate = upLeft;
                            moveDirection = Room.MoveDirection.DOWN;
                            initialDirection = previousRoom.getMoveDirection();
                            finalDirection = Room.MoveDirection.DOWN;
                        }
                    }else if(previousRoom.getMoveDirection() == Room.MoveDirection.LEFT){
                        if(gravPullDown){
                            // We will be moving up
                            joinerRoomTemplate = downRight;
                            moveDirection = Room.MoveDirection.UP;
                            initialDirection = previousRoom.getMoveDirection();
                            finalDirection = Room.MoveDirection.UP;
                        }else{
                            // We will be moving down
                            joinerRoomTemplate = leftDown;
                            moveDirection = Room.MoveDirection.DOWN;
                            initialDirection = previousRoom.getMoveDirection();
                            finalDirection = Room.MoveDirection.DOWN;
                        }
                    }
                }else{
                    // We are transitioning to a vertical gravity room
                    if(previousRoom.getMoveDirection() == Room.MoveDirection.UP){
                        if(gravPullRight){
                            // We will be moving to the right
                            joinerRoomTemplate = leftDown;
                            moveDirection = Room.MoveDirection.RIGHT;
                            initialDirection = previousRoom.getMoveDirection();
                            finalDirection = Room.MoveDirection.RIGHT;
                        }else{
                            // We will be moving to the left
                            joinerRoomTemplate = upLeft;
                            moveDirection = Room.MoveDirection.LEFT;
                            initialDirection = previousRoom.getMoveDirection();
                            finalDirection = Room.MoveDirection.LEFT;
                        }
                    }else if(previousRoom.getMoveDirection() == Room.MoveDirection.DOWN){
                        if(gravPullRight){
                            // We will be moving to the right
                            joinerRoomTemplate = downRight;
                            moveDirection = Room.MoveDirection.RIGHT;
                            initialDirection = previousRoom.getMoveDirection();
                            finalDirection = Room.MoveDirection.RIGHT;
                        }else{
                            // We will be moving to the left
                            joinerRoomTemplate = rightUp;
                            moveDirection = Room.MoveDirection.LEFT;
                            initialDirection = previousRoom.getMoveDirection();
                            finalDirection = Room.MoveDirection.LEFT;
                        }
                    }
                }

                if(joinerRoomTemplate != null){
                    JoinerRoom joinerRoom = joinerRoomTemplate.buildJoiner();
                    joinerRoom.id = id++;
                    joinerRoom.setSpeedLevel(0);
                    // Determine where to place this room
                    float offsetX;
                    float offsetY;
                    switch (previousRoom.getMoveDirection()){
                        case LEFT:
                            offsetX = previousRoom.roomOriginX - joinerRoom.roomWidth;
                            offsetY = previousRoom.roomOriginY;
                            break;
                        case RIGHT:
                            offsetX = previousRoom.roomOriginX + previousRoom.roomWidth;
                            offsetY = previousRoom.roomOriginY;
                            break;
                        case UP:
                            offsetX = previousRoom.roomOriginX;
                            offsetY = previousRoom.roomOriginY + previousRoom.roomHeight;
                            break;
                        case DOWN:
                            offsetX = previousRoom.roomOriginX;
                            offsetY = previousRoom.roomOriginY - joinerRoom.roomHeight;
                            break;
                        default:
                            offsetX = 0;
                            offsetY = 0;
                    }

                    joinerRoom.setMoveDirection(moveDirection).setOrigin(offsetX, offsetY);
                    addRoom(joinerRoom);
                    joinerRoom.setDirections(initialDirection, finalDirection);
                    previousRoom = joinerRoom;
                }else{
                    Gdx.app.error("", "Couldn't find joiner template");
                }
            }

            Room newRoom = template.build().setSpeedLevel(0).setMoveDirection(moveDirection);
            newRoom.id = id++;
            float offsetX;
            float offsetY;

            switch (moveDirection){
                case LEFT:
                    offsetX = previousRoom.roomOriginX - newRoom.roomWidth;
                    offsetY = previousRoom.roomOriginY;

                    break;
                case RIGHT:
                    offsetX = previousRoom.roomOriginX + previousRoom.roomWidth;
                    offsetY = previousRoom.roomOriginY;

                    break;
                case UP:
                    offsetX = previousRoom.roomOriginX;
                    offsetY = previousRoom.roomOriginY + previousRoom.roomHeight;
                    break;
                case DOWN:
                    offsetX = previousRoom.roomOriginX;
                    offsetY = previousRoom.roomOriginY - newRoom.roomHeight;

                    break;
                default:
                    offsetX = 0;
                    offsetY = 0;
            }
            newRoom.setOrigin(offsetX, offsetY);

            addRoom(newRoom);

            previousRoom = newRoom;
            previousRoomTemplate = template;
        }

        changeRoom(spawnPoint);
        spawnPoint.addToWorld();
    }
}
