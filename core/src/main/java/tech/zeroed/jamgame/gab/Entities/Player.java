package tech.zeroed.jamgame.gab.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.*;
import tech.zeroed.jamgame.gab.GravitysABitch;
import tech.zeroed.jamgame.gab.Room;
import tech.zeroed.jamgame.gab.RoomManager;
import text.formic.Stringf;

import static tech.zeroed.jamgame.gab.GravitysABitch.*;
import static tech.zeroed.jamgame.gab.Room.MoveDirection.*;

public class Player extends Entity{

    float maxSpeed = 0;

    PlayerCollisionFilter PLAYER_COLLISION_FILTER = new PlayerCollisionFilter();
    private Collisions projectedCollisions = new Collisions();
    private Room.MoveDirection moveDirection = Room.MoveDirection.RIGHT;

    public Player(){
        boundingBoxWidth = 16;
        boundingBoxHeight = 25;
        item = new Item<>(this);
        setGravity(RoomManager.BASE_MOVING_GRAVITY, -RoomManager.BASE_HOLDING_GRAVITY);
    }

    private void setGravity(float x, float y){
        this.gravityX = x;
        this.gravityY = y;
    }

    public void setMaxSpeed(float maxSpeed){
        this.maxSpeed = maxSpeed;
    }

    public void setMoveDirection(Room.MoveDirection moveDirection) {
        if(moveDirection == this.moveDirection) return;

        float oldVelocityX = Math.abs(velocityX);
        float oldVelocityY = Math.abs(velocityY);

        if(this.moveDirection == Room.MoveDirection.RIGHT && moveDirection == Room.MoveDirection.DOWN){
            if(gravityY > 0){
                // Currently on the roof
                velocityX = oldVelocityY;
                setGravity(RoomManager.BASE_HOLDING_GRAVITY, -RoomManager.BASE_MOVING_GRAVITY);
            }else{
                // Currently on the floor
                velocityX = -oldVelocityY;
                setGravity(-RoomManager.BASE_HOLDING_GRAVITY, -RoomManager.BASE_MOVING_GRAVITY);
            }
            velocityY = -oldVelocityX;
        }
        else if(this.moveDirection == Room.MoveDirection.RIGHT && moveDirection == Room.MoveDirection.UP){
            if(gravityY > 0){
                velocityX = -oldVelocityY;
                setGravity(-RoomManager.BASE_HOLDING_GRAVITY, RoomManager.BASE_MOVING_GRAVITY);
            }else{
                velocityX = oldVelocityY;
                setGravity(RoomManager.BASE_HOLDING_GRAVITY, RoomManager.BASE_MOVING_GRAVITY);
            }
            velocityY = oldVelocityX;
        }
        else if (this.moveDirection == Room.MoveDirection.DOWN && moveDirection == Room.MoveDirection.RIGHT){
            if(gravityX > 0){
                // On the right wall
                velocityY = oldVelocityX;
                setGravity(RoomManager.BASE_MOVING_GRAVITY, RoomManager.BASE_HOLDING_GRAVITY);
            }else{
                // On the left wall
                velocityY = -oldVelocityX;
                setGravity(RoomManager.BASE_MOVING_GRAVITY, -RoomManager.BASE_HOLDING_GRAVITY);
            }
            velocityX = oldVelocityY;
        }
        else if (this.moveDirection == Room.MoveDirection.DOWN && moveDirection == Room.MoveDirection.LEFT){
            if(gravityX > 0){
                velocityY = -oldVelocityX;
                setGravity(-RoomManager.BASE_MOVING_GRAVITY, -RoomManager.BASE_HOLDING_GRAVITY);
            }else{
                velocityY = oldVelocityX;
                setGravity(-RoomManager.BASE_MOVING_GRAVITY, RoomManager.BASE_HOLDING_GRAVITY);
            }
            velocityX = -oldVelocityY;
        }
        else if (this.moveDirection == Room.MoveDirection.LEFT && moveDirection == Room.MoveDirection.DOWN){
            if(gravityY > 0){
                velocityX = -oldVelocityY;
                setGravity(-RoomManager.BASE_HOLDING_GRAVITY, -RoomManager.BASE_MOVING_GRAVITY);
            }else{
                velocityX = -oldVelocityY;
                setGravity(RoomManager.BASE_HOLDING_GRAVITY, -RoomManager.BASE_MOVING_GRAVITY);
            }
             velocityY = -oldVelocityX;
        }
        else if (this.moveDirection == Room.MoveDirection.LEFT && moveDirection == Room.MoveDirection.UP){
            if(gravityY > 0){
                velocityX = oldVelocityY;
                setGravity(RoomManager.BASE_HOLDING_GRAVITY, RoomManager.BASE_MOVING_GRAVITY);
            }else{
                velocityX = -oldVelocityY;
                setGravity(-RoomManager.BASE_HOLDING_GRAVITY, RoomManager.BASE_MOVING_GRAVITY);
            }
            velocityY = oldVelocityX;
        }
        else if (this.moveDirection == Room.MoveDirection.UP && moveDirection == Room.MoveDirection.RIGHT){
            if(gravityX > 0){
                velocityY = -oldVelocityX;
                setGravity(RoomManager.BASE_MOVING_GRAVITY, -RoomManager.BASE_HOLDING_GRAVITY);
            }else{
                velocityY = oldVelocityX;
                setGravity(RoomManager.BASE_MOVING_GRAVITY, RoomManager.BASE_HOLDING_GRAVITY);
            }
            velocityX = oldVelocityY;
        }
        else if (this.moveDirection == Room.MoveDirection.UP && moveDirection == Room.MoveDirection.LEFT){
            if(gravityX > 0){
                velocityY = oldVelocityX;
                setGravity(-RoomManager.BASE_MOVING_GRAVITY, RoomManager.BASE_HOLDING_GRAVITY);
            }else{
                velocityY = -oldVelocityX;
                setGravity(-RoomManager.BASE_MOVING_GRAVITY, -RoomManager.BASE_HOLDING_GRAVITY);
            }
            velocityX = -oldVelocityY;
        }

        this.moveDirection = moveDirection;
    }

    @Override
    public void act(float delta) {
        boolean flipGravity  = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        boolean shoot = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if(flipGravity){
            if(moveDirection == LEFT || moveDirection == RIGHT){
                gravityY *= -1;
            }else{
                gravityX *= -1;
            }
        }

        // Add gravity
        float deltaX = delta * gravityX;
        float deltaY = delta * gravityY;

        velocityX += deltaX;
        velocityY += deltaY;

        switch (moveDirection){
            case LEFT:
                velocityX = MathUtils.clamp(velocityX, -maxSpeed, 0);
                break;
            case RIGHT:
                velocityX = MathUtils.clamp(velocityX, 0, maxSpeed);
                break;
            case UP:
                velocityY = MathUtils.clamp(velocityY, 0, maxSpeed);
                break;
            case DOWN:
                //velocityY = MathUtils.clamp(velocityY, -maxSpeed, 0);
                if(velocityY < -maxSpeed)
                    velocityY += 0.001f;
                break;
        }

        // Update final position
        x += velocityX;
        y += velocityY;

        // Check and handle collisions
        Response.Result results = GravitysABitch.physics.world.move(item, x + boundingBoxX, y + boundingBoxY, PLAYER_COLLISION_FILTER);
        for(int i = 0; i < results.projectedCollisions.size(); i++){
            Collision collision = results.projectedCollisions.get(i);
            Entity other = (Entity) collision.other.userData;
            if(other instanceof Block){
                if(moveDirection == UP || moveDirection == DOWN){
                    if (collision.normal.x == 1 || collision.normal.x == -1)
                        velocityX = 0;
                }else{
                    if (collision.normal.y == 1 || collision.normal.y == -1)
                        velocityY = 0;
                }
            }
        }

        // Update position based on collision results
        Rect rect = GravitysABitch.physics.world.getRect(item);
        if(rect != null) {
            x = rect.x - boundingBoxX;
            y = rect.y - boundingBoxY;
        }
    }

    @Override
    public void draw(){
        shapeDrawer.setColor(Color.BLUE);
        shapeDrawer.setDefaultLineWidth(1.0f);
        shapeDrawer.circle(x + boundingBoxWidth/2, y + boundingBoxHeight/2, 5);
        spriteBatch.end();
        hudBatch.begin();
        font.draw(hudBatch, Stringf.format(
                 "X:                %.2f\n" +
                 "Y:                %.2f\n" +
                 "Speed X:       %.2f\n" +
                 "Speed Y:       %.2f\n" +
                 "MaxSpeed:      %.2f\n" +
                 "Gravity:      %s\n" +
                 "Gravity X:       %.2f\n" +
                 "Gravity Y:       %.2f\n"
        , x, y, velocityX, velocityY, maxSpeed, roomManager.currentRoom.getMoveDirection(), gravityX, gravityY),
        -hudCamera.viewportWidth/2+10,  hudCamera.viewportHeight/2-10 - font.getLineHeight());
        hudBatch.end();
        spriteBatch.begin();
        Gdx.app.log("Player", Stringf.format("X: %.2f Y: %.2f Speed X: %.2f Speed Y: %.2f MaxSpeed: %.2f Gravity: %s Gravity X: %.2f Gravity Y: %.2f", x, y, velocityX, velocityY, maxSpeed, roomManager.currentRoom.getMoveDirection(), gravityX, gravityY));
    }


    public static class PlayerCollisionFilter implements CollisionFilter {
        @Override
        public Response filter(Item item, Item other) {
            if(other.userData instanceof Block){
                return Response.slide;
            }
//            if(other.userData instanceof Traps){
//                return Response.cross;
//            }
            return null;
        }
    }
}
