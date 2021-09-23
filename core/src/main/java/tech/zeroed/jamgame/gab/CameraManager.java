package tech.zeroed.jamgame.gab;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import tech.zeroed.jamgame.gab.Entities.Player;

import static tech.zeroed.jamgame.gab.Room.GravityType.*;

public class CameraManager {
    private OrthographicCamera camera;
    private RoomManager rm;

    private float velocityX, velocityY;

    public CameraManager(OrthographicCamera camera, RoomManager rm) {
        this.camera = camera;
        this.rm = rm;
        lookAt(450, rm.currentRoom.roomOriginY + rm.currentRoom.roomHeight/2);
    }

    private void lookAt(float x, float y) {
        camera.position.set(x, y, 0);
        camera.update();
    }

    public void update(float delta){
        Room currentRoom = rm.currentRoom;
//        velocityX = MathUtils.clamp(velocityX + currentRoom.gravityX * delta, 0, currentRoom.maxSpeed);
//        velocityY = MathUtils.clamp(velocityY + currentRoom.gravityY * delta, 0, currentRoom.maxSpeed);
//
//        if(currentRoom.gravityType == VERTICAL){
//            velocityY = 0;
//        }else{
//            velocityX = 0;
//        }

        //camera.position.add(velocityX, velocityY, 0);
        camera.position.set(currentRoom.player.x, currentRoom.player.y, 1);
//        if(currentRoom.gravityType == VERTICAL){
//            camera.position.y = camera.position.lerp(new Vector3(450, currentRoom.roomOriginY + currentRoom.roomHeight/2, 1), 0.5f).y;
//        }else{
//            camera.position.x = camera.position.lerp(new Vector3(currentRoom.roomOriginX + currentRoom.roomWidth/2, camera.position.y , 1), 0.5f).x;
//        }
        camera.update();

        Player player = currentRoom.player;

        if(!camera.frustum.boundsInFrustum(player.x, player.y, 0, player.boundingBoxWidth/2, player.boundingBoxHeight/2, 1)){
            Gdx.app.log("CameraManager", "Player left the camera");
        }
    }
}
