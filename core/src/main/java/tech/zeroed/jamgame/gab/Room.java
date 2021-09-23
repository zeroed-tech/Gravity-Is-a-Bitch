package tech.zeroed.jamgame.gab;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import tech.zeroed.jamgame.gab.Entities.Block;
import tech.zeroed.jamgame.gab.Entities.Entity;
import tech.zeroed.jamgame.gab.Entities.Player;

import java.util.HashMap;
import java.util.function.Supplier;

import static tech.zeroed.jamgame.gab.GravitysABitch.*;

public class Room {
    public boolean addedToWorld;
    public int id;
    // The axis towards the ground (VERTICAL=pulling up/down)
    public enum GravityType {
        HORIZONTAL,
        VERTICAL
    }

    public enum MoveDirection {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public float maxSpeed;

    HashMap<Character, Supplier<Entity>> types = new HashMap<>();

    Array<Entity> roomEntities = new Array<>();
    Player player = null;

    public GravityType gravityType = GravityType.VERTICAL;
    private MoveDirection moveDirection = MoveDirection.RIGHT;

    public float roomOriginX, roomOriginY, roomWidth, roomHeight;


    Color debugDrawColour = new Color(MathUtils.random(0f,1f), MathUtils.random(0f,1f), MathUtils.random(0f,1f), 1);
    Color debugDrawColour2 = new Color(MathUtils.random(0f,1f), MathUtils.random(0f,1f), MathUtils.random(0f,1f), 1);

    public Room(String layout){
        types.put('B', Block::new);
        types.put('P', Player::new);

        layoutRoom(layout);
    }


    public Room setOrigin(float x, float y){
        this.roomOriginX = x;
        this.roomOriginY = y;
        return this;
    }

    public Room setSpeedLevel(int speedLevel) {
        maxSpeed = 5 + (5 * speedLevel * 0.75f);
        return this;
    }

    public Room setGravityType(GravityType gravityType){
        this.gravityType = gravityType;
        return this;
    }

    public Room setMoveDirection(MoveDirection moveDirection){
        this.moveDirection = moveDirection;
        return this;
    }

    public MoveDirection getMoveDirection(){
        return moveDirection;
    }

    private void layoutRoom(String layout){
        float x = 0;
        float y = 0;

        String[] reversed = layout.split("\n");
        for(int i = reversed.length-1; i >= 0; i--){
            String row = reversed[i];
            for(char c : row.toCharArray()){
                Supplier<Entity> constructor = types.get(c);
                if(constructor == null){
                    x += TILE_SIZE;
                    continue;
                }
                Entity entity = constructor.get();
                entity.x = x;
                entity.y = y;

                roomEntities.add(entity);

                if(entity instanceof Player){
                    player = (Player) entity;
                    player.x += 10;
                }

                x += TILE_SIZE;
            }

            if(x > roomWidth)
                roomWidth = x;
            x = 0;

            y += TILE_SIZE;

            if(y > roomHeight)
                roomHeight = y;
        }

        //Gdx.app.log("Room", Stringf.format("Loaded room with origin X: %f, Y: %f, Width: %f, Height: %f", roomOriginX, roomOriginY, roomWidth, roomHeight));
    }

    public void addToWorld(){
        for(Entity entity : roomEntities) {
            entity.x += roomOriginX;
            entity.y += roomOriginY;
            physics.addEntity(entity);
        }
        addedToWorld = true;
    }

    public void removeFromWorld(){
        for(Entity entity : roomEntities) {
            physics.removeEntity(entity);
        }
        addedToWorld = false;
    }

    public void enterRoom(Player player){
        this.player = player;
        //setPlayerGravity();
        setPlayerMaxSpeed();
        setPlayerMoveDirection();
    }

    public void setPlayerMaxSpeed(){
        player.setMaxSpeed(maxSpeed);
    }

    private void setPlayerMoveDirection() {
        player.setMoveDirection(moveDirection);
    }

    public boolean containsPlayer(float playerX, float playerY){
        return playerX >= roomOriginX && playerX < roomOriginX + roomWidth && playerY >= roomOriginY && playerY < roomOriginY + roomHeight;
    }

    public void render(){
        if(!addedToWorld)return;
        for (Entity entity : roomEntities) {
            shapeDrawer.setDefaultLineWidth(1.0f);
            shapeDrawer.filledRectangle(entity.x, entity.y, entity.boundingBoxWidth, entity.boundingBoxHeight, debugDrawColour, debugDrawColour2);
        }
    }

    public void update(){

    }
}
