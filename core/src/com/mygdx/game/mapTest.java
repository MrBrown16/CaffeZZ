package com.mygdx.game;


import java.util.Comparator;

import datastructure.ReorderableArrayList;


public class mapTest<T> {
	public static void main(String[] args) {
		ReorderableArrayList<Float> list = new ReorderableArrayList<>();
		for (float i = 0; i < 10; i++) {
			list.add(i);
		}

		list.add(5f);
        list.add(1f);
        list.add(8f);
        list.add(3f);
        list.add(7f);

		System.out.println("Original list: " + list);

		list.moveElement(7, 2);
		System.out.println("Modified list after moving element from index 7 to 2: " + list);

		// Testing moving an element backward
		list.moveElement(2, 7);
		System.out.println("Modified list after moving element from index 2 to 7: " + list);
		// Comparator to sort in ascending order
        Comparator<Float> comparator = Float::compare;

        list.moveToSortedPosition(10, comparator); 
        System.out.println("Modified list after moving element at index 10 to sorted position: " + list);

        list.moveToSortedPosition(11, comparator); 
        System.out.println("Modified list after moving element at index 11 to sorted position: " + list);

        list.sort(comparator); 
        System.out.println("Sorted list: " + list);
    }
}

// MainGame mainGame;

// public mapTest(){
// mainGame = new MainGame();
// mainGame.create();

// }

// public class mapTest extends ApplicationAdapter {
// private OrthographicCamera camera;
// private Viewport viewport;
// private AssetManager assetManager;

// private TiledMap tiledMap;
// private IsometricTiledMapRenderer mapRenderer;
// private Texture backgroundTexture;

// private float zoom = 1.0f; // Initial zoom level
// private float maxZoom = 10.0f; // Maximum zoom out level (see more of the
// world)
// private float minZoom = 0.80f; // Minimum zoom in level (1.0 means original
// size)

// private float worldWidth;
// private float worldHeight;

// private float viewLimit = 2.0f; // during development othervise =1

// private float tileWidthInPixels;
// private float tileHeightInPixels;

// private float TILE_WIDTH_HALF;
// private float TILE_HEIGHT_HALF;

// private int mapWidthInTiles;
// private int mapHeightInTiles;

// Vector2 topCenter;

// ShapeRenderer shapeRenderer;

// public void setProperties(String map, String background, String
// baseTileLayerName, float worldWidth) {
// setupMap(map, background,baseTileLayerName);
// }

// public void setupMap(String map, String background, String baseTileLayerName)
// {
// assetManager.setLoader(TiledMap.class, new TmxMapLoader());
// assetManager.load(map, TiledMap.class);
// assetManager.finishLoading();
// tiledMap = assetManager.get(map);
// mapRenderer = new IsometricTiledMapRenderer(tiledMap,baseTileLayerName);

// // Background
// setupBackgroundInitial(background);

// // Set map properties
// TiledMapTileLayer layer = (TiledMapTileLayer)
// tiledMap.getLayers().get("tiles");
// tileWidthInPixels = layer.getTileWidth();
// tileHeightInPixels = layer.getTileHeight();
// TILE_WIDTH_HALF = tileWidthInPixels/2;
// TILE_HEIGHT_HALF = tileHeightInPixels/2;
// mapWidthInTiles = layer.getWidth();
// mapHeightInTiles = layer.getHeight();

// Vector2 pos;
// try {
// topCenter = calculateTopCenterCorner(worldWidth, worldHeight,
// tileHeightInPixels);
// pos = calculateBottomLeftCorner(topCenter.x, topCenter.y, mapWidthInTiles,
// mapHeightInTiles, tileWidthInPixels, tileHeightInPixels);
// // System.out.println("pos.x: "+pos.x+" pos.y: "+pos.y);

// tiledMap.getLayers().forEach((layer2) -> {
// layer2.setOffsetX(pos.x);
// layer2.setOffsetY(-pos.y);
// });
// } catch (Exception e) {
// System.err.println("wrong worldSize and tile size can't calculate map
// position correctly");
// e.printStackTrace();
// }

// }

// public void setupBackgroundInitial(String background) {
// backgroundTexture = new Texture(background);
// worldWidth = backgroundTexture.getWidth();
// worldHeight = backgroundTexture.getHeight();
// }

// @Override
// public void create() {
// shapeRenderer = new ShapeRenderer();
// camera = new OrthographicCamera();
// viewport = new ScreenViewport(camera);
// viewport.apply();

// assetManager = new AssetManager();
// setProperties("isofield.tmx", "backgroundgrey.png", "tiles", 8192);

// Gdx.input.setInputProcessor(new InputAdapter() {
// @Override
// public boolean scrolled(float amountX, float amountY) {
// zoom += amountY * 0.1f;
// zoom = MathUtils.clamp(zoom, maxZoom, minZoom);
// camera.zoom = zoom;
// System.out.println("Zoom: " + zoom);
// clampCamera(camera, viewLimit);
// return true;
// }

// @Override
// public boolean touchDragged(int screenX, int screenY, int pointer) {
// float deltaX = -Gdx.input.getDeltaX() * camera.zoom;
// float deltaY = Gdx.input.getDeltaY() * camera.zoom;
// camera.translate(deltaX, deltaY);
// clampCamera(camera, viewLimit);
// System.out.println(
// "Camera Position in touchDragged X: " + camera.position.x + " Y: " +
// camera.position.y);
// return true;
// }

// @Override
// public boolean keyUp(int keycode) {
// if (keycode == Input.Keys.NUMPAD_ADD) {
// zoom += 0.5;

// } else if (keycode == Input.Keys.NUMPAD_SUBTRACT) {
// zoom -= 0.5;

// }
// zoom = MathUtils.clamp(zoom, minZoom, maxZoom);
// camera.zoom = zoom;
// System.out.println("Zoom: " + zoom);
// clampCamera(camera, viewLimit);
// return true;
// }

// });
// // Set initial camera position to center
// camera.position.set(worldWidth*viewLimit / 2, worldHeight*viewLimit / 2, 0);
// backgroundTexture = saveBaseLayerImage("combined_image.png", tiledMap,
// "tiles", worldWidth, worldHeight, 4096);

// }

// public Texture saveBaseLayerImage(String fileName, TiledMap tiledMap, String
// mapLayerName, float worldWidth, float worldHeight, int resultWidth) {
// Texture returnTexture;
// // System.out.println("Max Buffer size: " + getMaxTextureSize());

// // Define the framebuffer dimensions
// int fbWidth = Math.min(4096, getMaxTextureSize());
// int fbHeight = fbWidth / 2; // Maintain 2:1 aspect ratio

// // Create the FrameBuffer
// FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, fbWidth,
// fbHeight, false);
// frameBuffer.begin();

// // Set up the viewport and camera for the framebuffer
// Camera fbCamera = new OrthographicCamera(worldWidth, worldHeight);
// fbCamera.position.set(worldWidth / 2f, worldHeight / 2f, 0);
// fbCamera.update();

// // Clear the frame buffer
// Gdx.gl.glClearColor(1, 0, 1, 1);
// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

// // Render the background
// mapRenderer.getBatch().setProjectionMatrix(fbCamera.combined);
// mapRenderer.getBatch().begin();
// mapRenderer.setView(fbCamera.combined, 0, 0, worldWidth, worldHeight);
// mapRenderer.getBatch().disableBlending();
// mapRenderer.getBatch().draw(backgroundTexture, 0, 0, worldWidth,
// worldHeight);
// mapRenderer.getBatch().enableBlending();
// mapRenderer.renderTileLayer((TiledMapTileLayer)
// (tiledMap.getLayers().get(mapLayerName)));
// mapRenderer.getBatch().end();

// // Save the frame buffer to an image
// Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, fbWidth, fbHeight);
// pixmap = flipPixmapVertically(pixmap);
// if (fbWidth != resultWidth) {
// // Save the image as a smaller version
// Pixmap scaledPixmap = new Pixmap(resultWidth, resultWidth / 2,
// pixmap.getFormat());
// scaledPixmap.drawPixmap(pixmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(),
// 0, 0, resultWidth,
// resultWidth / 2);
// PixmapIO.writePNG(Gdx.files.local(fileName), scaledPixmap);
// returnTexture = new Texture(scaledPixmap);
// scaledPixmap.dispose();
// } else {
// PixmapIO.writePNG(Gdx.files.local(fileName), pixmap);
// returnTexture = new Texture(pixmap);
// }

// // End the framebuffer
// frameBuffer.end();

// // Clean up
// pixmap.dispose();
// frameBuffer.dispose();
// return returnTexture;
// }

// private void clampCamera(OrthographicCamera camera, float viewLimit) {
// float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
// float effectiveViewportHeight = camera.viewportHeight * camera.zoom;
// float minX;
// float minY;

// if (viewLimit == 1.0f) {
// minX = (int) (effectiveViewportWidth / 2);
// minY = (int) (effectiveViewportHeight / 2);
// } else {
// minX = effectiveViewportWidth / 2 - (worldWidth * viewLimit);
// minY = effectiveViewportHeight / 2 - (worldHeight * viewLimit);
// }
// float maxX = (int) (worldWidth * viewLimit) - (int) (effectiveViewportWidth /
// 2);
// float maxY = (int) (worldHeight * viewLimit) - (int) (effectiveViewportHeight
// / 2);

// camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
// camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
// camera.update();
// }

// private void drawBackground(Texture backgroundTexture, float worldWidth,
// float worldHeight) {
// mapRenderer.getBatch().disableBlending();
// mapRenderer.getBatch().draw(backgroundTexture, 0, 0, worldWidth,
// worldHeight);
// mapRenderer.getBatch().enableBlending();
// }

// @Override
// public void render() {
// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

// camera.update();

// mapRenderer.getBatch().begin();
// mapRenderer.setView(camera.combined, 0, 0, worldWidth, worldHeight);
// drawBackground(backgroundTexture, worldWidth, worldHeight);
// mapRenderer.getBatch().end();
// shapeRenderer.setProjectionMatrix(camera.combined);

// shapeRenderer.begin(ShapeType.Line);
// shapeRenderer.setColor(Color.RED);
// // shapeRenderer.rect(mapRenderer.getViewBounds().getX(),
// mapRenderer.getViewBounds().getY(), mapRenderer.getViewBounds().getWidth(),
// mapRenderer.getViewBounds().getHeight());
// Vector2 point = mapToScreen(5, 2);
// shapeRenderer.circle(topCenter.x, topCenter.y, 64);
// // shapeRenderer.circle(topCenter.x-64, topCenter.y-64, 64);
// shapeRenderer.circle(point.x, point.y, 64);
// shapeRenderer.end();

// }

// @Override
// public void resize(int width, int height) {
// viewport.update(width, height,true);
// System.out.println("viewPort size: Width: "+camera.viewportWidth+"
// Height:"+camera.viewportHeight);
// System.out.println("Zoom: "+zoom);

// camera.update();
// }

// @Override
// public void dispose() {
// mapRenderer.dispose();
// tiledMap.dispose();
// assetManager.dispose();
// backgroundTexture.dispose();
// }

// private Pixmap flipPixmapVertically(Pixmap pixmap) {
// Pixmap flipped = new Pixmap(pixmap.getWidth(), pixmap.getHeight(),
// pixmap.getFormat());
// for (int y = 0; y < pixmap.getHeight(); y++) {
// for (int x = 0; x < pixmap.getWidth(); x++) {
// flipped.drawPixel(x, pixmap.getHeight() - 1 - y, pixmap.getPixel(x, y));
// }
// }
// pixmap.dispose();
// return flipped;
// }

// /**
// * Calculate the world coordinates of the bottom-left corner of the
// * IsometricMapRenderer's
// * bounds origin based on the top center corner's coordinates.
// *
// * @param topCenterX The x-coordinate of the top center corner in world
// * coordinates.
// * @param topCenterY The y-coordinate of the top center corner in world
// * coordinates.
// * @param mapWidthInTiles The width of the map in tiles.
// * @param mapHeightInTiles The height of the map in tiles.
// * @param tileWidth The width of a single tile in pixels.
// * @param tileHeight The height of a single tile in pixels.
// * @return A Vector2 containing the x and y coordinates of the bottom-left
// * corner.
// */
// public static Vector2 calculateBottomLeftCorner(float topCenterX, float
// topCenterY, int mapWidthInTiles,
// int mapHeightInTiles, float tileWidth, float tileHeight) {
// int[] mapSize = calculateIsometricDimensions(mapWidthInTiles,
// mapHeightInTiles, (int) tileWidth,
// (int) tileHeight);
// // Calculate the width and height of the map in pixels
// float mapWidthInPixels = mapSize[0];
// float mapHeightInPixels = mapSize[1];

// // Calculate the position of the bottom left of the map
// float bottomLeftX = topCenterX - (mapWidthInPixels / 2);
// float bottomLeftY;

// if (tileWidth == 128 && tileHeight == 64) {
// bottomLeftY = topCenterY - mapHeightInPixels - (tileHeight / 4);
// return new Vector2(bottomLeftX, bottomLeftY + mapHeightInPixels / 2);
// } else {
// bottomLeftY = topCenterY / 2 - mapHeightInPixels + (tileHeight / 2); //
// bigger
// }

// return new Vector2(bottomLeftX, bottomLeftY + mapHeightInPixels);// bigger
// }
// public static Vector2 calculateTopCenterCorner(float worldWidth, float
// worldHeight, float tileHeightInPixels) throws Exception {

// Vector2 pos = new Vector2();
// pos.x = worldWidth / 2;

// if (worldWidth == 12288 && worldHeight == 6144) {
// // System.out.println("backgroundWidth == 12288 && backgroundHeight == 6144 "
// + worldWidth + " x " + worldHeight);
// pos.y = (float) ((worldHeight * 2 / 3) + (2 * tileHeightInPixels));
// } else if(worldWidth == 8192 && worldHeight == 4096){
// // System.out.println("worldWidth == 8192 && worldHeight == 4096 " +
// worldWidth + " x " + worldHeight);
// pos.y = (float) ((worldHeight * 2 / 3) + (1 * tileHeightInPixels) + 6);
// }else{
// System.out.println("ELSE: worldWidth != 8092 && worldHeight != 4096 " +
// worldWidth + " x " + worldHeight);
// throw new Exception("Unsupported size parameters");
// }

// return pos;
// }

// public static int getMaxTextureSize() {
// IntBuffer buffer = BufferUtils.newIntBuffer(16);
// Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, buffer);
// return buffer.get(0);
// }

// public static int[] calculateIsometricDimensions(int mapWidthInTiles, int
// mapHeightInTiles,
// int tileWidthInPixels, int tileHeightInPixels) {
// int mapWidthInPixels = (mapWidthInTiles + mapHeightInTiles) *
// (tileWidthInPixels / 2);
// int mapHeightInPixels = (mapWidthInTiles + mapHeightInTiles) *
// (tileHeightInPixels / 2);
// return new int[] { mapWidthInPixels, mapHeightInPixels };
// }

// //finally works
// private Vector2 screenToMap(float screenX, float screenY) {
// Vector2 mapPos = carToIso(screenX-topCenter.x, topCenter.y - screenY);

// return new Vector2(mapPos.x, mapPos.y);
// }

// //finally works
// private Vector2 mapToScreen(float mapX, float mapY) {
// Vector2 screenPos = isoToCar(mapX, mapY);

// return new Vector2(topCenter.x + screenPos.x, topCenter.y-screenPos.y);
// }

// //finally works
// private Vector2 carToIso(float screenX, float screenY) {
// float mapX = screenX / tileWidthInPixels + screenY / tileHeightInPixels;
// float mapY = screenY / tileHeightInPixels - screenX / tileWidthInPixels;
// return new Vector2(mapX, mapY);
// }

// //finally works
// private Vector2 isoToCar(float mapX, float mapY) {
// float screenX = (mapX - mapY) * TILE_WIDTH_HALF;;
// float screenY = (mapX + mapY) * TILE_HEIGHT_HALF;

// return new Vector2(screenX, screenY);
// }
// }
