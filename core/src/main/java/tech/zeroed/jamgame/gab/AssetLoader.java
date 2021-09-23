package tech.zeroed.jamgame.gab;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;

public class AssetLoader extends AssetManager {
    public void loadAssets(){
        loadAssets("Skins/Skin.json");
    }

    public void loadAssets(String skinPath){
        //Register font loader
        FileHandleResolver resolver = new InternalFileHandleResolver();
        setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        finishLoading();

        ObjectMap<String, Object> fontMap = new ObjectMap<>();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Pixel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 20;fontMap.put("Label", generator.generateFont(parameter));
        parameter.size = 30;fontMap.put("Pixel", generator.generateFont(parameter));
        parameter.size = 40;fontMap.put("UpgradeLabel", generator.generateFont(parameter));

        generator.dispose();

        SkinLoader.SkinParameter param = new SkinLoader.SkinParameter(fontMap);

        load(skinPath, Skin.class, param);

    }
}
