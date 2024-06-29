package com.mygdx.game.tiled;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;

public class IsometricTiledMapRenderer extends BatchTiledMapRenderer {

    private float TILE_WIDTH_HALF;
	private float TILE_HEIGHT_HALF;

	public IsometricTiledMapRenderer(TiledMap map,String baseTileLayerName) {
		super(map);
		init(baseTileLayerName);
	}

	public IsometricTiledMapRenderer(TiledMap map, Batch batch, String baseTileLayerName) {
		super(map, batch);
		init(baseTileLayerName);
	}

	public IsometricTiledMapRenderer(TiledMap map, float unitScale, String baseTileLayerName) {
		super(map, unitScale);
		init(baseTileLayerName);
	}

	public IsometricTiledMapRenderer(TiledMap map, float unitScale, Batch batch, String baseTileLayerName) {
		super(map, unitScale, batch);
		init(baseTileLayerName);
	}

	private void init(String baseTileLayerName) {
		// Initialize half tile dimensions
		TILE_WIDTH_HALF = ((TiledMapTileLayer)map.getLayers().get(baseTileLayerName)).getTileWidth() * unitScale / 2.0f;
		TILE_HEIGHT_HALF = ((TiledMapTileLayer)map.getLayers().get(baseTileLayerName)).getTileHeight()  * unitScale / 2.0f;
	}

	@Override
    public void renderTileLayer(TiledMapTileLayer layer) {
        final Color batchColor = batch.getColor();
        final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());

        final float layerOffsetX = layer.getRenderOffsetX() * unitScale - viewBounds.x * (layer.getParallaxX() - 1);
        // offset in tiled is y down, so we flip it
        final float layerOffsetY = -layer.getRenderOffsetY() * unitScale - viewBounds.y * (layer.getParallaxY() - 1);


        int minRow = 0;
        int maxRow = layer.getWidth()-1;
        int minCol = 0;
        int maxCol = layer.getHeight()-1;


        // Render in diagonal order
        for (int sum = minRow + minCol; sum <= maxRow + maxCol; sum++) {
            for (int row = minRow; row <= maxRow; row++) {
                int col = sum - row;
                if (col < minCol || col > maxCol) continue;
                
                float x = (col * TILE_WIDTH_HALF) + (row * TILE_WIDTH_HALF);
                float y = (row * TILE_HEIGHT_HALF) - (col * TILE_HEIGHT_HALF);
                
                final TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                
                if (cell == null) continue;
                final TiledMapTile tile = cell.getTile();
                
                if (tile != null) {
                    final boolean flipX = cell.getFlipHorizontally();
                    final boolean flipY = cell.getFlipVertically();
                    final int rotations = cell.getRotation();

                    TextureRegion region = tile.getTextureRegion();

                    float x1 = x + tile.getOffsetX() * unitScale + layerOffsetX;
                    float y1 = y + tile.getOffsetY() * unitScale + layerOffsetY;
                    float x2 = x1 + region.getRegionWidth() * unitScale;
                    float y2 = y1 + region.getRegionHeight() * unitScale;

                    float u1 = region.getU();
                    float v1 = region.getV2();
                    float u2 = region.getU2();
                    float v2 = region.getV();

                    vertices[X1] = x1;
                    vertices[Y1] = y1;
                    vertices[C1] = color;
                    vertices[U1] = u1;
                    vertices[V1] = v1;

                    vertices[X2] = x1;
                    vertices[Y2] = y2;
                    vertices[C2] = color;
                    vertices[U2] = u1;
                    vertices[V2] = v2;

                    vertices[X3] = x2;
                    vertices[Y3] = y2;
                    vertices[C3] = color;
                    vertices[U3] = u2;
                    vertices[V3] = v2;

                    vertices[X4] = x2;
                    vertices[Y4] = y1;
                    vertices[C4] = color;
                    vertices[U4] = u2;
                    vertices[V4] = v1;

                    if (flipX) {
                        float temp = vertices[U1];
                        vertices[U1] = vertices[U3];
                        vertices[U3] = temp;
                        temp = vertices[U2];
                        vertices[U2] = vertices[U4];
                        vertices[U4] = temp;
                    }
                    if (flipY) {
                        float temp = vertices[V1];
                        vertices[V1] = vertices[V3];
                        vertices[V3] = temp;
                        temp = vertices[V2];
                        vertices[V2] = vertices[V4];
                        vertices[V4] = temp;
                    }
                    if (rotations != 0) {
                        switch (rotations) {
                        case Cell.ROTATE_90: {
                            float tempV = vertices[V1];
                            vertices[V1] = vertices[V2];
                            vertices[V2] = vertices[V3];
                            vertices[V3] = vertices[V4];
                            vertices[V4] = tempV;

                            float tempU = vertices[U1];
                            vertices[U1] = vertices[U2];
                            vertices[U2] = vertices[U3];
                            vertices[U3] = vertices[U4];
                            vertices[U4] = tempU;
                            break;
                        }
                        case Cell.ROTATE_180: {
                            float tempU = vertices[U1];
                            vertices[U1] = vertices[U3];
                            vertices[U3] = tempU;
                            tempU = vertices[U2];
                            vertices[U2] = vertices[U4];
                            vertices[U4] = tempU;
                            float tempV = vertices[V1];
                            vertices[V1] = vertices[V3];
                            vertices[V3] = tempV;
                            tempV = vertices[V2];
                            vertices[V2] = vertices[V4];
                            vertices[V4] = tempV;
                            break;
                        }
                        case Cell.ROTATE_270: {
                            float tempV = vertices[V1];
                            vertices[V1] = vertices[V4];
                            vertices[V4] = vertices[V3];
                            vertices[V3] = vertices[V2];
                            vertices[V2] = tempV;

                            float tempU = vertices[U1];
                            vertices[U1] = vertices[U4];
                            vertices[U4] = vertices[U3];
                            vertices[U3] = vertices[U2];
                            vertices[U2] = tempU;
                            break;
                        }
                        }
                    }
                    batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
                }
            }
        }
    }

}

