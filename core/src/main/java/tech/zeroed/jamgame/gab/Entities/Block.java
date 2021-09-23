package tech.zeroed.jamgame.gab.Entities;

import com.dongbat.jbump.Item;

import static tech.zeroed.jamgame.gab.GravitysABitch.TILE_SIZE;

public class Block extends Entity{
    public Block(){
        boundingBoxWidth = TILE_SIZE;
        boundingBoxHeight = TILE_SIZE;
        item = new Item<>(this);
    }


    @Override
    public void act(float delta) {

    }
}
