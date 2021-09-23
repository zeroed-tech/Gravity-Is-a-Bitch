package tech.zeroed.jamgame.gab;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import space.earlygrey.shapedrawer.ShapeDrawer;
import tech.zeroed.jamgame.gab.Entities.Block;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GravitysABitch extends ApplicationAdapter {
    public static final float TILE_SIZE = 32f;
    public static ShapeDrawer shapeDrawer;
    public static SpriteBatch spriteBatch;
    public static SpriteBatch hudBatch;
    public static OrthographicCamera camera;
    public static OrthographicCamera hudCamera;
    Viewport hudViewport;
    public static Physics physics;
    public Stage stage;

    public boolean paused = false;

    public static BitmapFont font;

    public static AssetLoader assetManager;
    public static RoomManager roomManager;
    private CameraManager cameraManager;

    @Override
    public void create() {
        super.create();
        Gdx.app.log("Launched", "Create was called");
        assetManager = new AssetLoader();
        assetManager.loadAssets();

        //assetManager.load("Sprites.atlas", TextureAtlas.class);
        assetManager.finishLoading();

        VisUI.load(assetManager.get("Skins/Skin.json", Skin.class));

        //atlas = assetManager.get("Sprites.atlas");

        hudCamera = new OrthographicCamera(1920, 1080);
        hudCamera.position.z = 1;
        hudCamera.update();

        hudViewport = new FitViewport(1920, 1080, hudCamera);
        hudBatch = new SpriteBatch();
        hudBatch.setProjectionMatrix(hudCamera.combined);
        stage = new Stage(hudViewport);
        Gdx.input.setInputProcessor(stage);

        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f;
        spriteBatch.setProjectionMatrix(camera.combined);

        physics = new Physics();

        roomManager = new RoomManager();

        roomManager.addTemplate(new RoomTemplate(){
            {
                layout = "BBBBBBBBBBBBBBBBB\n" +
                         "                 \n" +
                         "                 \n" +
                         "                 \n" +
                         "                 \n" +
                         "                 \n" +
                         "                 \n" +
                         "   P             \n" +
                         "BBBBBBBBBBBBBBBBB\n";
                containsSpawnPoint = true;
                entrances = 0;
                exits = 1;
                gravityType = Room.GravityType.VERTICAL;
            }
        });

        roomManager.addTemplate(new RoomTemplate(){
            {
                layout = "BBBBBBBBBBBBBBBBB\n" +
                         "                 \n" +
                         "                 \n" +
                         "                 \n" +
                         "                 \n" +
                         "                 \n" +
                         "                 \n" +
                         "                 \n" +
                         "BBBBBBBBBBBBBBBBB\n";
                gravityType = Room.GravityType.VERTICAL;
                entrances = 1;
                exits = 1;
            }
        });


        roomManager.addTemplate(new RoomTemplate(){
            {
                layout = "B       B\n" +
                        "B       B\n" +
                        "B       B\n" +
                        "B       B\n" +
                        "B       B\n" +
                        "B       B\n" +
                        "B       B\n" +
                        "B       B\n" +
                        "B       B\n" +
                        "B       B\n";
                entrances = 1;
                exits = 1;
                gravityType = Room.GravityType.HORIZONTAL;
            }
        });

        roomManager.generateLevel(250);

        cameraManager = new CameraManager(camera, roomManager);

        shapeDrawer = new ShapeDrawer(spriteBatch, new TextureRegion(new Texture(Gdx.files.internal("pixel.png"))));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Pixel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 25;
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    public void render() {
        super.render();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            if(Gdx.graphics.isFullscreen()){
                Gdx.graphics.setWindowedMode(1280, 720);
            }else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
            paused = !paused;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && Gdx.app.getType() != Application.ApplicationType.WebGL){
            Dialog dialog = new Dialog("", VisUI.getSkin()) {
                public void result(Object obj) {
                    if((boolean) obj)
                        Gdx.app.exit();
                }
            };
            dialog.text("Are you sure you want to quit?");
            dialog.button("Yes", true); //sends "true" as the result
            dialog.button("No", false);  //sends "false" as the result
            dialog.show(stage);
        }

        float delta = paused ? 0 : Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(camera.combined);
        stage.act();
        stage.draw();

        spriteBatch.begin();
        physics.render(delta);
        roomManager.update(delta);
        spriteBatch.end();
        cameraManager.update(delta);
    }



    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        hudViewport.update(width, height);
    }
}