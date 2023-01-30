package tech.zeroed.jamgame.gab;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import tech.zeroed.jamgame.gab.Entities.Block;
import tech.zeroed.jamgame.gab.Entities.Bomb;
import tech.zeroed.jamgame.gab.Entities.Entity;
import tech.zeroed.jamgame.gab.Entities.Player;

import java.util.HashMap;
import java.util.function.Supplier;

import static tech.zeroed.jamgame.gab.GravitysABitch.*;

public class Room {
    public boolean addedToWorld;
    private int id;
    private String mapName;

    // The axis towards the ground (VERTICAL=pulling up/down)
    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public enum MoveDirection {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    HashMap<String, Supplier<Entity>> types = new HashMap<>();

    Array<Entity> roomEntities = new Array<>();
    Player player = null;

    private MoveDirection finalDirection = MoveDirection.RIGHT;
    private Room.MoveDirection initialDirection = Room.MoveDirection.RIGHT;

    public float roomOriginX, roomOriginY, roomWidth, roomHeight;

    public Vector2 north = new Vector2();
    public Vector2 east = new Vector2();
    public Vector2 south = new Vector2();
    public Vector2 west = new Vector2();

    public boolean containsSpawnPoint = false;

    public Room(String mapName, int id){
        this.mapName = mapName;
        types.put("Block", Block::new);
        types.put("Spawn", Player::new);
        types.put("Bomb", Bomb::new);
        this.id = id;
        loadMap(mapName);
    }

    public Room setOrigin(float x, float y){
        this.roomOriginX = x;
        this.roomOriginY = y;
        north.add(x,y);
        east.add(x,y);
        south.add(x,y);
        west.add(x,y);
        for(Entity entity : roomEntities) {
            entity.setX(entity.x + roomOriginX);
            entity.setY(entity.y + roomOriginY);
        }
        return this;
    }

    public void attachRoomToNorth(Room newRoom){
        newRoom.setOrigin(north.x - newRoom.south.x, north.y + TILE_SIZE - newRoom.south.y);
    }

    public void attachRoomToEast(Room newRoom){
        newRoom.setOrigin(east.x  + TILE_SIZE - newRoom.west.x, east.y - newRoom.west.y);
    }

    public void attachRoomToSouth(Room newRoom){
        newRoom.setOrigin(south.x - newRoom.north.x, south.y -TILE_SIZE - newRoom.north.y);
    }

    public void attachRoomToWest(Room newRoom){
        newRoom.setOrigin(west.x -TILE_SIZE - newRoom.east.x, west.y - newRoom.east.y);
    }

    public void attachRoom(Room newRoom) {
        switch (finalDirection){
            case UP:
                attachRoomToNorth(newRoom);
                break;
            case RIGHT:
                attachRoomToEast(newRoom);
                break;
            case DOWN:
                attachRoomToSouth(newRoom);
                break;
            case LEFT:
                attachRoomToWest(newRoom);
                break;
        }
    }

    public Room setMoveDirection(MoveDirection initialDirection, MoveDirection finalDirection){
        this.initialDirection = initialDirection;
        this.finalDirection = finalDirection;
        //Gdx.app.log("Direction-"+id, "\n"+mapName+ " " +initialDirection+"->"+finalDirection+"\n");
        return this;
    }

    public MoveDirection getInitialMoveDirection(){
        return initialDirection;
    }

    public MoveDirection getFinalMoveDirection(){
        return finalDirection;
    }

    public int getId(){
        return id;
    }

    public void addToWorld(){
        for(Entity entity : roomEntities) {
            physics.addEntity(entity);
        }
        addedToWorld = true;
    }

    public void removeFromWorld(){
        for(Entity entity : roomEntities) {
            if(entity.equals(player))continue;
            physics.removeEntity(entity);
        }
        addedToWorld = false;
    }

    public void enterRoom(Player player){
        this.player = player;
    }

    public boolean containsPlayer(float playerX, float playerY){
        return playerX >= roomOriginX && playerX < roomOriginX + roomWidth && playerY >= roomOriginY && playerY < roomOriginY + roomHeight;
    }

    public void render(float delta){
        if(!addedToWorld)return;
        for (Entity entity : roomEntities) {
            entity.draw(delta);
        }
    }

    private void loadMap(String mapName) {
        TiledMap tilemap = new TmxMapLoader().load("Rooms/"+mapName+".tmx");

        for(MapLayer layer : tilemap.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tl = (TiledMapTileLayer) layer;
                roomWidth = tl.getWidth() * tl.getTileWidth();
                roomHeight = tl.getHeight() * tl.getTileHeight();

                for (int x = 0; x < tl.getWidth(); x++) {
                    for (int y = 0; y < tl.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = tl.getCell(x, y);

                        if (cell == null) continue;

                        TiledMapTile tile = cell.getTile();
                        MapProperties tileProperties = tile.getProperties();
                        String tileType = tileProperties.get("Type", "Unknown", String.class);

                        Supplier<Entity> constructor = types.get(tileType);
                        if (constructor == null) {
                            // Handle special tiles
                            switch (tileType) {
                                case "Entrance_North":
                                    north.set(x * TILE_SIZE, y * TILE_SIZE);
                                    break;
                                case "Entrance_East":
                                    east.set(x * TILE_SIZE, y * TILE_SIZE);
                                    break;
                                case "Entrance_South":
                                    south.set(x * TILE_SIZE, y * TILE_SIZE);
                                    break;
                                case "Entrance_West":
                                    west.set(x * TILE_SIZE, y * TILE_SIZE);
                                    break;
                                default:
                                    Gdx.app.log("LoadMap", "Tried to load unimplemented item "+tileType);
                            }
                            continue;
                        }

                        Entity entity = constructor.get();
                        entity.init(tileProperties);
                        entity.setX(x * TILE_SIZE);
                        entity.setY(y * TILE_SIZE);

                        roomEntities.add(entity);

                        if (entity instanceof Player) {
                            player = (Player) entity;
                            containsSpawnPoint = true;
                        }
                    }
                }
            }else{
                for(MapObject object : layer.getObjects()){
                    MapProperties properties = object.getProperties();
                    float x = properties.get("x", Float.class);
                    float y = properties.get("y", Float.class);

                    String tileType = properties.get("type", "Unknown", String.class);
                    Supplier<Entity> constructor = types.get(tileType);
                    if(constructor == null){
                        Gdx.app.log("LoadMap", "Tried to load unimplemented item "+tileType);
                        continue;
                    }

                    Entity entity = constructor.get();
                    entity.init(properties);
                    entity.setX(x);
                    entity.setY(y);

                    roomEntities.add(entity);

                }
            }
        }
    }
}

