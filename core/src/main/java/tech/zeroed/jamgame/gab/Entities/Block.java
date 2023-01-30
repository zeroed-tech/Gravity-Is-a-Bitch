package tech.zeroed.jamgame.gab.Entities;

import com.dongbat.jbump.Item;

import static tech.zeroed.jamgame.gab.GravitysABitch.TILE_SIZE;
import static tech.zeroed.jamgame.gab.GravitysABitch.atlas;

public class Block extends Entity{
    public Block(){
        boundingBoxWidth = TILE_SIZE;
        boundingBoxHeight = TILE_SIZE;
        item = new Item<>(this);
        sprite = atlas.createSprite("Blocks");
    }


    @Override
    public void act(float delta) {

    }
}
