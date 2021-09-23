package tech.zeroed.jamgame.gab.Entities;

import com.dongbat.jbump.Item;

public abstract class Entity {
    public float x;
    public float y;
    public float boundingBoxX;
    public float boundingBoxY;
    public float boundingBoxWidth;
    public float boundingBoxHeight;
    public float rotation;
    public boolean flipX;
    public boolean flipY;
    public float velocityX;
    public float velocityY;
    public float gravityX;
    public float gravityY;
    public Item<Entity> item;

    public abstract void act(float delta);

    public void debugDraw() {

    }

    public void draw(){

    }
}
