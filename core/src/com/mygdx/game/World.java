package com.mygdx.game;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;

import datastructure.ReorderableArrayList;

public class World implements Disposable{
    private OrthographicCamera camera;
    private AssetManager assetManager;
    private SpriteBatch batch;
    // private ReorderableArrayList<IDK> renderList;

    private TiledMap tiledMap;
    private IsometricTiledMapRenderer mapRenderer;
    private Texture backgroundTexture;

    private float zoom = 1.0f; // Initial zoom level
    private float maxZoom = 10.0f; // Maximum zoom out level (see more of the world)
    private float minZoom = 0.80f; // Minimum zoom in level (1.0 means original size)


    private float worldWidth;
    private float worldHeight;

    private float viewLimit = 2.0f; // during development othervise =1

    private static float tileWidthInPixels;
    private static float tileHeightInPixels;

    private static float TILE_WIDTH_HALF;
	private static float TILE_HEIGHT_HALF;

    private int mapWidthInTiles;
    private int mapHeightInTiles;
    
    private static Vector2 topCenter;

    public World(OrthographicCamera camera, AssetManager assetManager, SpriteBatch batch, String map, String baseTileLayerName, String background, float maxZoom, float minZoom, float viewLimit, float worldWidth) {
        this.camera = camera;
        this.assetManager = assetManager;
        this.batch = batch;
        this.maxZoom = maxZoom;
        this.minZoom = minZoom;
        this.viewLimit = viewLimit;
        setProperties(map, background, baseTileLayerName, worldWidth);

    }

    private void setProperties(String map, String background, String baseTileLayerName, float worldWidth) {
        setupMap(map, background,baseTileLayerName);
    }

    private void setupMap(String map, String background, String baseTileLayerName) {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());
        assetManager.load(map, TiledMap.class);
        assetManager.finishLoading();

        tiledMap = assetManager.get(map);
        mapRenderer = new IsometricTiledMapRenderer(tiledMap, batch, baseTileLayerName);

        // Background
        setupBackgroundInitial(background);

        // Set map properties
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("tiles");
        tileWidthInPixels = layer.getTileWidth();
        tileHeightInPixels = layer.getTileHeight();
        TILE_WIDTH_HALF = tileWidthInPixels/2;
        TILE_HEIGHT_HALF = tileHeightInPixels/2;
        mapWidthInTiles = layer.getWidth();
        mapHeightInTiles = layer.getHeight();


        Vector2 pos;
        try {
            topCenter = calculateTopCenterCorner(worldWidth, worldHeight, tileHeightInPixels);
            pos = calculateBottomLeftCorner(topCenter.x, topCenter.y, mapWidthInTiles, mapHeightInTiles, tileWidthInPixels, tileHeightInPixels);
            System.out.println("topCenter"+topCenter);
            System.out.println("topCenterIso"+screenToMap(topCenter.x,topCenter.y));
            // System.out.println("pos.x: "+pos.x+" pos.y: "+pos.y);
            
            tiledMap.getLayers().forEach((layer2) -> {
                layer2.setOffsetX(pos.x);
                layer2.setOffsetY(-pos.y);
            });
        } catch (Exception e) {
            System.err.println("wrong worldSize and tile size can't calculate map position correctly");
            e.printStackTrace();
        }
        // Set initial camera position to center
        camera.position.set(worldWidth*viewLimit / 2, worldHeight*viewLimit / 2, 0);
        backgroundTexture = saveBaseLayerImage("combined_image.png", tiledMap, "tiles", worldWidth, worldHeight, 4096);

        
    }

    private void setupBackgroundInitial(String background) {
        backgroundTexture = new Texture(background);
        worldWidth = backgroundTexture.getWidth();
        worldHeight = backgroundTexture.getHeight();
    }
    public void drawBackground() {
        drawBackground(backgroundTexture, worldWidth, worldHeight);
    }
    private void drawBackground(Texture backgroundTexture, float worldWidth, float worldHeight) {
        batch.disableBlending();
        batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        batch.enableBlending();
    }

    private Texture saveBaseLayerImage(String fileName, TiledMap tiledMap, String mapLayerName, float worldWidth, float worldHeight, int resultWidth) {
        Texture returnTexture;
        // System.out.println("Max Buffer size: " + getMaxTextureSize());

        // Define the framebuffer dimensions
        int fbWidth = Math.min(4096, getMaxTextureSize());
        int fbHeight = fbWidth / 2; // Maintain 2:1 aspect ratio

        // Create the FrameBuffer
        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, fbWidth, fbHeight, false);
        frameBuffer.begin();

        // Set up the viewport and camera for the framebuffer
        Camera fbCamera = new OrthographicCamera(worldWidth, worldHeight);
        fbCamera.position.set(worldWidth / 2f, worldHeight / 2f, 0);
        fbCamera.update();

        // Clear the frame buffer
        Gdx.gl.glClearColor(1, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render the background
        batch.setProjectionMatrix(fbCamera.combined);
        batch.begin();
        mapRenderer.setView(fbCamera.combined, 0, 0, worldWidth, worldHeight);
        batch.disableBlending();
        batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        batch.enableBlending();
        mapRenderer.renderTileLayer((TiledMapTileLayer) (tiledMap.getLayers().get(mapLayerName)));
        batch.end();

        // Save the frame buffer to an image
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, fbWidth, fbHeight);
        pixmap = flipPixmapVertically(pixmap);
        if (fbWidth != resultWidth) {
            // Save the image as a smaller version
            Pixmap scaledPixmap = new Pixmap(resultWidth, resultWidth / 2, pixmap.getFormat());
            scaledPixmap.drawPixmap(pixmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(), 0, 0, resultWidth,
                    resultWidth / 2);
            PixmapIO.writePNG(Gdx.files.local(fileName), scaledPixmap);
            returnTexture = new Texture(scaledPixmap);
            scaledPixmap.dispose();
        } else {
            PixmapIO.writePNG(Gdx.files.local(fileName), pixmap);
            returnTexture = new Texture(pixmap);
        }

        // End the framebuffer
        frameBuffer.end();

        // Clean up
        pixmap.dispose();
        frameBuffer.dispose();
        return returnTexture;
    }

    private Pixmap flipPixmapVertically(Pixmap pixmap) {
        Pixmap flipped = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                flipped.drawPixel(x, pixmap.getHeight() - 1 - y, pixmap.getPixel(x, y));
            }
        }
        pixmap.dispose();
        return flipped;
    }
        
    /**
     * Calculate the world coordinates of the bottom-left corner of the
     * IsometricMapRenderer's
     * bounds origin based on the top center corner's coordinates.
     *
     * @param topCenterX       The x-coordinate of the top center corner in world
     *                         coordinates.
     * @param topCenterY       The y-coordinate of the top center corner in world
     *                         coordinates.
     * @param mapWidthInTiles  The width of the map in tiles.
     * @param mapHeightInTiles The height of the map in tiles.
     * @param tileWidth        The width of a single tile in pixels.
     * @param tileHeight       The height of a single tile in pixels.
     * @return A Vector2 containing the x and y coordinates of the bottom-left
     *         corner.
     */
    private static Vector2 calculateBottomLeftCorner(float topCenterX, float topCenterY, int mapWidthInTiles,
    int mapHeightInTiles, float tileWidth, float tileHeight) {
        int[] mapSize = calculateIsometricDimensions(mapWidthInTiles, mapHeightInTiles, (int) tileWidth,
        (int) tileHeight);
        // Calculate the width and height of the map in pixels
        float mapWidthInPixels = mapSize[0];
        float mapHeightInPixels = mapSize[1];
        
        // Calculate the position of the bottom left of the map
        float bottomLeftX = topCenterX - (mapWidthInPixels / 2);
        float bottomLeftY;
        
        if (tileWidth == 128 && tileHeight == 64) {
            bottomLeftY = topCenterY - mapHeightInPixels - (tileHeight / 4);
            return new Vector2(bottomLeftX, bottomLeftY + mapHeightInPixels / 2);
        } else {
            bottomLeftY = topCenterY / 2 - mapHeightInPixels + (tileHeight / 2); // bigger
        }
                
        return new Vector2(bottomLeftX, bottomLeftY + mapHeightInPixels);// bigger
    }
    private static Vector2 calculateTopCenterCorner(float worldWidth, float worldHeight, float tileHeightInPixels) throws Exception {

        Vector2 pos = new Vector2();
        pos.x = worldWidth / 2;
        
        if (worldWidth == 12288 && worldHeight == 6144) {
            // System.out.println("backgroundWidth == 12288 && backgroundHeight == 6144 " + worldWidth + " x " + worldHeight);
            pos.y = (float) ((worldHeight * 2 / 3) + (2 * tileHeightInPixels));
        } else if(worldWidth == 8192 && worldHeight == 4096){
            // System.out.println("worldWidth == 8192 && worldHeight == 4096 " + worldWidth + " x " + worldHeight);
            pos.y = (float) ((worldHeight * 2 / 3) + (1 * tileHeightInPixels) + 6);
        }else{
            System.out.println("ELSE: worldWidth != 8092 && worldHeight != 4096 " + worldWidth + " x " + worldHeight);
            throw new Exception("Unsupported size parameters");
        }
                
        return pos;
    }
                
    public static int getMaxTextureSize() {
        IntBuffer buffer = BufferUtils.newIntBuffer(16);
        Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, buffer);
        return buffer.get(0);
    }
        
    private static int[] calculateIsometricDimensions(int mapWidthInTiles, int mapHeightInTiles,
            int tileWidthInPixels, int tileHeightInPixels) {
        int mapWidthInPixels = (mapWidthInTiles + mapHeightInTiles) * (tileWidthInPixels / 2);
        int mapHeightInPixels = (mapWidthInTiles + mapHeightInTiles) * (tileHeightInPixels / 2);
        return new int[] { mapWidthInPixels, mapHeightInPixels };
    }
    
    


    
    //finally works
    public static Vector2 screenToMap(float screenX, float screenY) {
        Vector2 mapPos = carToIso(screenX-topCenter.x, topCenter.y - screenY);

        return new Vector2(mapPos.x, mapPos.y);
    }


    //finally works
    public static Vector2 mapToScreen(float mapX, float mapY) {
        Vector2 screenPos = isoToCar(mapX, mapY);
        // System.out.println("mapToScreen"+screenPos);
        return new Vector2(topCenter.x + screenPos.x, topCenter.y-screenPos.y);
    }


    //finally works
    private static Vector2 carToIso(float screenX, float screenY) {
        float mapX = screenX / tileWidthInPixels + screenY / tileHeightInPixels;
        float mapY = screenY / tileHeightInPixels - screenX / tileWidthInPixels;
        return new Vector2(mapX, mapY);
    }
    
    
    //finally works
    private static Vector2 isoToCar(float mapX, float mapY) {
        float screenX = (mapX - mapY) * TILE_WIDTH_HALF;;
        float screenY = (mapX + mapY) * TILE_HEIGHT_HALF;

        return new Vector2(screenX, screenY);
    }

    

    @Override
    public void dispose() {
        mapRenderer.dispose();
        tiledMap.dispose();
        backgroundTexture.dispose();
    }

    
    
    
    public TiledMap getTiledMap() {
        return tiledMap;
    }
    public IsometricTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }
    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }
    
    public SpriteBatch getBatch() {
        return batch;
    }

            
            
    public float getMaxZoom() {
        return maxZoom;
    }
    
    public float getMinZoom() {
        return minZoom;
    }
    public float getWorldWidth() {
        return worldWidth;
    }
    public float getWorldHeight() {
        return worldHeight;
    }

    public float getViewLimit() {
        return viewLimit;
    }

    public float getTileWidthInPixels() {
        return tileWidthInPixels;
    }

    public float getTileHeightInPixels() {
        return tileHeightInPixels;
    }

    public float getTILE_WIDTH_HALF() {
        return TILE_WIDTH_HALF;
    }

    public float getTILE_HEIGHT_HALF() {
        return TILE_HEIGHT_HALF;
    }

    public int getMapWidthInTiles() {
        return mapWidthInTiles;
    }

    public int getMapHeightInTiles() {
        return mapHeightInTiles;
    }
    public float getZoom() {
        return zoom;
    }
    public static float calculateDepth(float X, float Y) {
        float normalizedVectorX = 1 / (float) Math.sqrt(2);
        float normalizedVectorY = 1 / (float) Math.sqrt(2);

        return (X * normalizedVectorX + Y * normalizedVectorY);
    }
}
