package tech.zeroed.jamgame.gab.Entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.dongbat.jbump.Item;

import static tech.zeroed.jamgame.gab.GravitysABitch.shapeDrawer;
import static tech.zeroed.jamgame.gab.GravitysABitch.spriteBatch;

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
    public Sprite sprite;

    public abstract void act(float delta);

    public void draw(float delta){
        if(sprite != null){
            sprite.setX(x);
            sprite.setY(y);
            sprite.draw(spriteBatch);
        }else {
            shapeDrawer.setDefaultLineWidth(1.0f);
            shapeDrawer.filledRectangle(x, y, boundingBoxWidth, boundingBoxHeight);
        }
    }

    public void init(MapProperties tileProperties) {
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y){
        this.y = y;
    }
}
