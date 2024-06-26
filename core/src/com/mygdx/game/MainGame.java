package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainGame extends Game implements Disposable{
    private OrthographicCamera camera;
    private Viewport viewport;
    private AssetManager assetManager;
    private SpriteBatch batch;

    private TiledMap tiledMap;
    private IsometricTiledMapRenderer mapRenderer;
    private Texture backgroundTexture;

    private MainScreen mainScreen;
    private World world;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        // viewport.apply();
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        // assetManager.setLoader(TiledMap.class, new TmxMapLoader());

        world = new World(camera, assetManager, batch, "isofield.tmx", "tiles", "backgroundgrey.png", 10.0f, 0.5f, 2.0f, 8192);
        mainScreen = new MainScreen(camera, viewport, assetManager, world);
        setScreen(mainScreen);
    }

    @Override
    public void dispose() {
        super.dispose();
        mainScreen.dispose();
        world.dispose();
        batch.dispose();
    }
    
}
