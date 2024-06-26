package com.mygdx.game;

import javax.swing.SwingUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import datastructure.ReorderableArrayList;

public class MainScreen implements Screen{
    private OrthographicCamera camera;
    private Viewport viewport;
    private AssetManager assetManager;
    private SpriteBatch batch;
    private TiledMap tiledMap;
    private IsometricTiledMapRenderer mapRenderer;
    private World world;

    private float worldWidth;
    private float worldHeight;
    private float zoom = 1.0f; // Initial zoom level
    private float maxZoom = 10.0f; // Maximum zoom out level (see more of the world)
    private float minZoom = 0.80f; // Minimum zoom in level (1.0 means original size)
    ShapeRenderer shapeRenderer;
    CharacterActor character;
    MyStage stage; //fuck you for making me use this
    ReorderableArrayList<CharacterActor> characters;


    public MainScreen(OrthographicCamera camera, Viewport viewport, AssetManager assetManager, World world) {
        this.camera = camera;
        this.viewport = viewport;
        viewport.apply();
        this.world = world;
        batch = world.getBatch();
        this.assetManager = assetManager;
        characters = new ReorderableArrayList<>();
        assetManager.load("badlogic.jpg", Texture.class);
        assetManager.load("defredgreen.png", Texture.class);
        assetManager.load("defgreen.png", Texture.class);
        assetManager.load("t/lLegTemplate.png", Texture.class);
        assetManager.load("t/rLegTemplate.png", Texture.class);
        assetManager.load("t/lArmTemplate.png", Texture.class);
        assetManager.load("t/rArmTemplate.png", Texture.class);
        assetManager.load("t/headTemplate.png", Texture.class);
        assetManager.load("t/bodyTemplate.png", Texture.class);
        assetManager.finishLoading();
        tiledMap = world.getTiledMap();
        mapRenderer = world.getMapRenderer();
        worldWidth = world.getWorldWidth();
        worldHeight = world.getWorldHeight();
        zoom = world.getZoom();
        
        stage = new MyStage(viewport,batch, characters);
        character = new CharacterActor(assetManager, stage.getCharacters());
        stage.addActor(character);
        
        // Create and show the Swing GUI
        SwingUtilities.invokeLater(() -> {
            new GuiThingy(characters).setVisible(true);
        });
        
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        stage.plexer.addProcessor(new InputAdapter() {
            @Override 
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                System.out.println("clickDepth: "+calcClickDepth(screenX, screenY));
                return super.touchUp(screenX, screenY, pointer, button);
            }
            @Override
            public boolean scrolled(float amountX, float amountY) {
                zoom += amountY * 0.1f;
                zoom = MathUtils.clamp(zoom, maxZoom, minZoom);
                camera.zoom = zoom;
                System.out.println("Zoom: " + zoom);
                clampCamera(camera, world.getViewLimit());
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                float deltaX = -Gdx.input.getDeltaX() * camera.zoom;
                float deltaY = Gdx.input.getDeltaY() * camera.zoom;
                camera.translate(deltaX, deltaY);
                clampCamera(camera, world.getViewLimit());
                System.out.println(
                        "Camera Position in touchDragged X: " + camera.position.x + " Y: " + camera.position.y);
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.NUMPAD_ADD) {
                    zoom += 0.5;

                } else if (keycode == Input.Keys.NUMPAD_SUBTRACT) {
                    zoom -= 0.5;

                }
                zoom = MathUtils.clamp(zoom, minZoom, maxZoom);
                camera.zoom = zoom;
                System.out.println("Zoom: " + zoom);
                clampCamera(camera, world.getViewLimit());
                return true;
            }

        });
        Gdx.input.setInputProcessor(stage.plexer);
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        camera.update();

        mapRenderer.getBatch().begin();
        mapRenderer.setView(camera.combined, 0, 0, worldWidth, worldHeight);
        world.drawBackground();

        mapRenderer.getBatch().end();
        stage.draw();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        // shapeRenderer.rect(mapRenderer.getViewBounds().getX(), mapRenderer.getViewBounds().getY(), mapRenderer.getViewBounds().getWidth(), mapRenderer.getViewBounds().getHeight());
        Vector2 point = World.mapToScreen(0, 0);
        Vector2 point1 = World.mapToScreen(5, 2);
        shapeRenderer.circle(point.x, point.y, 64);
        // shapeRenderer.circle(topCenter.x-64, topCenter.y-64, 64);
        shapeRenderer.circle(point1.x, point1.y, 64);
        shapeRenderer.end();

    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height,true);
        stage.getViewport().update(width, height, true);
        camera.update();
    }

    @Override
    public void pause() {
        System.out.println("Bye");
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resume'");
    }

    @Override
    public void hide() {
        System.out.println("HIDE!! they are coming!!!!");
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        
    }

    private void clampCamera(OrthographicCamera camera, float viewLimit) {
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;
        float minX;
        float minY;

        if (viewLimit == 1.0f) {
            minX = (int) (effectiveViewportWidth / 2);
            minY = (int) (effectiveViewportHeight / 2);
        } else {
            minX = effectiveViewportWidth / 2 - (worldWidth * viewLimit);
            minY = effectiveViewportHeight / 2 - (worldHeight * viewLimit);
        }
        float maxX = (int) (worldWidth * viewLimit) - (int) (effectiveViewportWidth / 2);
        float maxY = (int) (worldHeight * viewLimit) - (int) (effectiveViewportHeight / 2);

        camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
        camera.update();
    }

    public float calcClickDepth(float X, float Y){
        Vector3 clickCoordinates = new Vector3(X, Y, 0);
        clickCoordinates = camera.unproject(clickCoordinates);
        Vector2 isoCoords = World.screenToMap(clickCoordinates.x, clickCoordinates.y);
        float depth = World.calculateDepth(isoCoords.x, isoCoords.y);
        return depth;
    }

}
