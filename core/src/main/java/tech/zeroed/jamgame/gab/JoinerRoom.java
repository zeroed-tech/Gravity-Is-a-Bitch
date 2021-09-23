package tech.zeroed.jamgame.gab;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import tech.zeroed.jamgame.gab.Entities.Entity;
import tech.zeroed.jamgame.gab.Entities.Player;

import static tech.zeroed.jamgame.gab.GravitysABitch.TILE_SIZE;
import static tech.zeroed.jamgame.gab.GravitysABitch.shapeDrawer;

public class JoinerRoom extends Room{
    private MoveDirection initialDirection;
    private MoveDirection finalDirection;

    private boolean directionFlipped = false;

    Vector2 a = new Vector2();
    Vector2 b = new Vector2();
    Vector2 c = new Vector2();

    public JoinerRoom(String layout) {
        super(layout);
    }

    public void setDirections(MoveDirection initialDirection, MoveDirection finalDirection){
        this.initialDirection = initialDirection;
        this.finalDirection = finalDirection;
        if(initialDirection == MoveDirection.RIGHT && finalDirection == MoveDirection.DOWN){
            a.set(roomOriginX, roomOriginY);
            b.set(roomOriginX + roomWidth, roomOriginY);
            c.set(roomOriginX + roomWidth, roomOriginY + roomHeight);
        }else if(initialDirection == MoveDirection.RIGHT && finalDirection == MoveDirection.UP){
            a.set(roomOriginX + roomWidth, roomOriginY + roomHeight);
            b.set(roomOriginX, roomOriginY + roomHeight + TILE_SIZE/4);
            c.set(roomOriginX + roomWidth - TILE_SIZE/4, roomOriginY);
        }else if (initialDirection == MoveDirection.DOWN && finalDirection == MoveDirection.RIGHT){
            a.set(roomOriginX, roomOriginY + TILE_SIZE/2);
            b.set(roomOriginX + roomWidth, roomOriginY);
            c.set(roomOriginX + roomWidth, roomOriginY + roomHeight - TILE_SIZE/4);
        }else if (initialDirection == MoveDirection.DOWN && finalDirection == MoveDirection.LEFT){
            a.set(roomOriginX, roomOriginY + roomHeight - TILE_SIZE/4);
            b.set(roomOriginX + roomWidth, roomOriginY + TILE_SIZE/4);
            c.set(roomOriginX, roomOriginY);
        }else if (initialDirection == MoveDirection.LEFT && finalDirection == MoveDirection.DOWN){
            a.set(roomOriginX + roomWidth, roomOriginY);
            b.set(roomOriginX, roomOriginY + roomHeight);
            c.set(roomOriginX, roomOriginY);
        }else if (initialDirection == MoveDirection.LEFT && finalDirection == MoveDirection.UP){
            a.set(roomOriginX, roomOriginY);
            b.set(roomOriginX, roomOriginY + roomHeight);
            c.set(roomOriginX + roomWidth, roomOriginY + roomHeight);
        }else if (initialDirection == MoveDirection.UP && finalDirection == MoveDirection.RIGHT){
            a.set(roomOriginX + roomWidth, roomOriginY + roomHeight);
            b.set(roomOriginX, roomOriginY + roomHeight - TILE_SIZE/4);
            c.set(roomOriginX + roomWidth, roomOriginY + TILE_SIZE/4);
        }else if (initialDirection == MoveDirection.UP && finalDirection == MoveDirection.LEFT){
            a.set(roomOriginX, roomOriginY + TILE_SIZE/4);
            b.set(roomOriginX, roomOriginY + roomHeight);
            c.set(roomOriginX + roomWidth, roomOriginY + roomHeight - TILE_SIZE/4);
        }
    }

    @Override
    public MoveDirection getMoveDirection() {
        return directionFlipped ? finalDirection : initialDirection;
    }

    @Override
    public void enterRoom(Player player){
        this.player = player;
    }

    @Override
    public void update() {
        super.update();
        if(player != null && !directionFlipped){
            Vector2 playerCenter = new Vector2(player.x + player.boundingBoxWidth/2, player.y + player.boundingBoxHeight/2);
            if(Intersector.isPointInTriangle(playerCenter, a, b, c)){
                Gdx.app.log("JoinerRoom", "Player has entered the second half, flipping direction");
                player.setMoveDirection(finalDirection);
                directionFlipped = true;
            }
        }
    }

    @Override
    public void render(){
        super.render();
        if(!addedToWorld)return;
        shapeDrawer.setColor(debugDrawColour);
        shapeDrawer.setDefaultLineWidth(1.0f);
        shapeDrawer.triangle(a, b, c);
    }
}
