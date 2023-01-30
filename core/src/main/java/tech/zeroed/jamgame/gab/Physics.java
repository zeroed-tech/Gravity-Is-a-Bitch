package tech.zeroed.jamgame.gab;

import com.badlogic.gdx.utils.SnapshotArray;
import com.dongbat.jbump.World;
import tech.zeroed.jamgame.gab.Entities.Entity;

import static tech.zeroed.jamgame.gab.GravitysABitch.TILE_SIZE;

public class Physics {

    public static SnapshotArray<Entity> entities;

    public World<Entity> world;

    public Physics() {
        world = new World<>(TILE_SIZE);
        entities = new SnapshotArray<>();
    }

    public void addEntity(Entity entity){
        world.add(entity.item, entity.x + entity.boundingBoxX, entity.y + entity.boundingBoxY, entity.boundingBoxWidth, entity.boundingBoxHeight);
        entities.add(entity);
    }

    public void render(float delta){
        if(delta != 0) {
            Object[] entitySnapshot = entities.begin();
            for (int i = 0, n = entities.size; i < n; i++) {
                Entity entity = (Entity) entitySnapshot[i];
                entity.act(delta);
            }
            entities.end();
        }

        //draw all entities
        for (Entity entity : entities) {
            entity.draw(delta);
        }
    }

    public void removeEntity(Entity entity) {
        world.remove(entity.item);
        entities.removeValue(entity, true);
    }
}
