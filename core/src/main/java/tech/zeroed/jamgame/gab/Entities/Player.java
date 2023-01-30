package tech.zeroed.jamgame.gab.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.dongbat.jbump.*;
import tech.zeroed.jamgame.gab.GravitysABitch;
import tech.zeroed.jamgame.gab.Room;
import text.formic.Stringf;

import static tech.zeroed.jamgame.gab.GravitysABitch.*;

public class Player extends Entity{

    float maxSpeed = 0;

    public static final float ACCELERATION = 10f;
    public static final float MAX_SPEED = 8f;

    PlayerCollisionFilter PLAYER_COLLISION_FILTER = new PlayerCollisionFilter();
    private Collisions projectedCollisions = new Collisions();
    private float deltaX;
    private float deltaY;

    public Player(){
        boundingBoxWidth = 16;
        boundingBoxHeight = 25;
        item = new Item<>(this);
    }

    @Override
    public void act(float delta) {
        boolean left  = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean up    = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean down  = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);

        boolean shoot = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        deltaX = 0;
        deltaY = 0;

        if(left) deltaX += -ACCELERATION * delta;
        else if(right) deltaX += ACCELERATION * delta;

        if(up) deltaY += ACCELERATION * delta;
        else if(down) deltaY += -ACCELERATION * delta;

        if(deltaX > 0 && velocityX < 0 || deltaX < 0 && velocityX > 0) deltaX*=4;
        if(deltaY > 0 && velocityY < 0 || deltaY < 0 && velocityY > 0) deltaY*=4;

        velocityX += deltaX;
        velocityY += deltaY;

        velocityX = MathUtils.clamp(velocityX, -MAX_SPEED, MAX_SPEED);
        velocityY = MathUtils.clamp(velocityY, -MAX_SPEED, MAX_SPEED);

        // Update final position
        x += velocityX;
        y += velocityY;

        // Check and handle collisions
        Response.Result results = GravitysABitch.physics.world.move(item, x + boundingBoxX, y + boundingBoxY, PLAYER_COLLISION_FILTER);
        for(int i = 0; i < results.projectedCollisions.size(); i++){
            Collision collision = results.projectedCollisions.get(i);
            Entity other = (Entity) collision.other.userData;
            if(other instanceof Block){
                if (collision.normal.x == 1 || collision.normal.x == -1) velocityX = 0;
                if (collision.normal.y == 1 || collision.normal.y == -1) velocityY = 0;
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
    public void draw(float delta){
        shapeDrawer.setColor(Color.BLUE);
        shapeDrawer.setDefaultLineWidth(1.0f);
        shapeDrawer.filledRectangle(x, y, boundingBoxWidth, boundingBoxHeight);
        shapeDrawer.circle(x + boundingBoxWidth/2, y + boundingBoxHeight/2, 5);
//        spriteBatch.end();
//        hudBatch.begin();
//        Room current = roomManager.currentRoom;
//        font.draw(hudBatch, Stringf.format(
//                 "X:               %.2f\n" +
//                 "Y:               %.2f\n" +
//                 "Speed X:         %.2f\n" +
//                 "Speed Y:         %.2f\n" +
//                 "Delta X:         %.2f\n" +
//                 "Delta Y:         %.2f\n" +
//                 "MaxSpeed:        %.2f\n" +
//                 "Gravity:           %s\n" +
//                 "Gravity X:       %.2f\n" +
//                 "Gravity Y:       %.2f\n"
//        , x, y, velocityX, velocityY, deltaX, deltaY, maxSpeed, (current.getInitialMoveDirection() == current.getFinalMoveDirection() ? current.getInitialMoveDirection() : ""+current.getInitialMoveDirection()+"->"+current.getFinalMoveDirection()), gravityX, gravityY),
//        -hudCamera.viewportWidth/2+10,  hudCamera.viewportHeight/2-10 - font.getLineHeight());
//        hudBatch.end();
//        spriteBatch.begin();
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
