package com.mygdx.game.tiled;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Disposable;

public class MapHandler implements Disposable{
    private TiledMap tiledMap;
    private TextureAtlas textureAtlas;



    public MapHandler(TiledMap tiledMap){
        this.tiledMap=tiledMap;
    }

    public void saveTiledMap(TMXMapWriter mapWriter, String file) {
        saveTiledMap(mapWriter, Gdx.files.internal(file));
    }
    public void saveTiledMap(TMXMapWriter mapWriter, FileHandle outputFile) {
        // Set of used image names (ids)
        Set<String> usedImageNames = new HashSet<>();

        // Iterate through all layers to collect used image names
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
                for (int y = 0; y < tileLayer.getHeight(); y++) {
                    for (int x = 0; x < tileLayer.getWidth(); x++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        if (cell != null && cell.getTile() != null) {
                            TextureRegion region = cell.getTile().getTextureRegion();
                            if (region != null && region.getTexture() != null) {
                                String imageName = region.getTexture().toString();
                                usedImageNames.add(imageName);
                            }
                        }
                    }
                }
            }
        }

        // Create a new texture atlas with only used images
        TextureAtlas newAtlas = createTextureAtlasFromUsedImages(usedImageNames);

        // Replace textures in the tiledMap with newAtlas textures
        replaceTexturesWithNewAtlas(newAtlas);

        try {
            mapWriter.writeMap(tiledMap, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateTile(int x, int y, int newId) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("mapLayer"); // Assuming "mapLayer" is the name of your layer

        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell != null) {
            TextureRegion region = null;

            // Check if the new ID is in the texture atlas
            region = textureAtlas.findRegion(Integer.toString(newId));

            // If not found in the atlas, load directly from file
            if (region == null) {
                Texture texture = new Texture(Gdx.files.internal(newId + ".png")); // Assuming images are named as "3.png", "5.png", etc.
                region = new TextureRegion(texture);
            }

            // Create a new tile with the updated region
            StaticTiledMapTile newTile = new StaticTiledMapTile(region);

            // Update the cell with the new tile
            cell.setTile(newTile);
        }
    }


    private TextureAtlas createTextureAtlasFromUsedImages(Set<String> usedImageNames) {
        // Create a new texture atlas
        TextureAtlas newAtlas = new TextureAtlas();

        // Load all used images into the new texture atlas
        for (String imageName : usedImageNames) {
            // Assuming image names are directly used as IDs (e.g., "3.png", "5.png")
            Texture texture = new Texture(Gdx.files.internal(imageName));
            TextureRegion region = new TextureRegion(texture);
            newAtlas.addRegion(imageName, region);
        }

        return newAtlas;
    }

    private void replaceTexturesWithNewAtlas(TextureAtlas newAtlas) {
        // Iterate through all layers to replace textures with those from newAtlas
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
                for (int y = 0; y < tileLayer.getHeight(); y++) {
                    for (int x = 0; x < tileLayer.getWidth(); x++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        if (cell != null && cell.getTile() != null) {
                            TextureRegion region = cell.getTile().getTextureRegion();
                            if (region != null && region.getTexture() != null) {
                                String imageName = region.getTexture().toString();
                                TextureRegion newRegion = newAtlas.findRegion(imageName);
                                if (newRegion != null) {
                                    // Create a new tile with the updated region from newAtlas
                                    StaticTiledMapTile newTile = new StaticTiledMapTile(newRegion);
                                    // Update the cell with the new tile
                                    cell.setTile(newTile);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dispose the old texture atlas
        textureAtlas.dispose();

        // Set the new texture atlas
        textureAtlas = newAtlas;
    }




    private void processMapLayers() {
        // Iterate through all tile layers in the TMX map
        for (int i = 0; i < tiledMap.getLayers().getCount(); i++) {
            if (tiledMap.getLayers().get(i) instanceof TiledMapTileLayer) {
                TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(i);

                // Iterate through cells in the layer
                for (int y = 0; y < layer.getHeight(); y++) {
                    for (int x = 0; x < layer.getWidth(); x++) {
                        TiledMapTileLayer.Cell cell = layer.getCell(x, y);

                        if (cell != null) {
                            int tileID = cell.getTile().getId();

                            // Assuming GIDs in the TMX file are 1-based
                            TextureRegion region = textureAtlas.findRegion(Integer.toString(tileID));

                            // Create a new tile with updated texture region
                            StaticTiledMapTile newTile = new StaticTiledMapTile(region);

                            // Update the cell with the new tile
                            cell.setTile(newTile);
                        }
                    }
                }
            }
        }
    }








    @Override
    public void dispose() {
        // Dispose resources when the game is disposed
        tiledMap.dispose();
        textureAtlas.dispose();
    }
}
