package tech.zeroed.jamgame.gab.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.dongbat.jbump.Item;
import tech.zeroed.jamgame.gab.Room;
import text.formic.Stringf;

import static tech.zeroed.jamgame.gab.GravitysABitch.*;

public class Bomb extends Entity{

    private boolean moveVertical;
    private float speed = 200;
    private float range = 6 * TILE_SIZE;
    private float progress = 0;
    private float baseX = 0;
    private float baseY = 0;
    private boolean reverse = false;
    private float alpha;
    public static int next = 0;
    public int id;

    public Bomb(){
        id = next++;
        boundingBoxWidth = TILE_SIZE;
        boundingBoxHeight = TILE_SIZE;
        item = new Item<>(this);
        sprite = atlas.createSprite("Bomb");
    }

    @Override
    public void init(MapProperties tileProperties) {
        super.init(tileProperties);
        moveVertical = tileProperties.get("MoveVertical", true, Boolean.class);
        progress = tileProperties.get("Progress", 0.0f, Float.class) * range;
    }

    @Override
    public void act(float delta) {
        progress += (reverse ? -1 : 1) * speed * delta;
        alpha = Interpolation.pow4.apply(Math.min(1f, progress / range));
        y = MathUtils.clamp(baseY + range*alpha, baseY, baseX+range);
        if(progress >= range)
            reverse = !reverse;
        if(progress < 0){
            reverse = !reverse;
        }
        progress = MathUtils.clamp(progress, 0, range);

    }

    @Override
    public void draw(float delta) {
        super.draw(delta);
//        hudBatch.begin();
//        font.draw(hudBatch, Stringf.format(
//                "X:                %.2f\n" +
//                "Y:                %.2f\n" +
//                "Progress:     %.2f\n" +
//                "Alpha:         %.2f\n"
//                , x, y, progress, alpha), -hudCamera.viewportWidth/2+10,  hudCamera.viewportHeight/2-10 - font.getLineHeight());
//        hudBatch.end();
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        this.baseX = x;
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        this.baseY = y;
    }
}
