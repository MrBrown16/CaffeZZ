package com.mygdx.game.tiled;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.Base64Coder;
import java.io.*;
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlWriter;

public class TMXMapWriter {

    private static final int LAST_BYTE = 0x000000FF;

    private HashMap<TiledMapTileSet, Integer> firstGidPerTileset;

    public static class Settings {
        public boolean compressLayerData = true;
    }
    public Settings settings = new Settings();

    public void writeMap(TiledMap map, FileHandle outputFile) throws IOException {
        OutputStream os = outputFile.write(false); // Write to file without append mode

        Writer writer = new OutputStreamWriter(os, "UTF-8");
        XmlWriter xmlWriter = new XmlWriter(writer);

        xmlWriter.element("map")
                .attribute("version", "1.2")
                .attribute("orientation", map.getProperties().get("orientation")) // Assuming isometric for libGDX TiledMap
                .attribute("renderorder", "right-down")
                .attribute("width", map.getProperties().get("width", Integer.class))
                .attribute("height", map.getProperties().get("height", Integer.class))
                .attribute("tilewidth", map.getProperties().get("tilewidth", Integer.class))
                .attribute("tileheight", map.getProperties().get("tileheight", Integer.class))
                .attribute("infinite", 0);

        writeProperties(map.getProperties(), xmlWriter);

        firstGidPerTileset = new HashMap<>();
        int firstgid = 1;
        for (TiledMapTileSet tileset : map.getTileSets()) {
            setFirstGidForTileset(tileset, firstgid);
            writeTilesetReference(tileset, xmlWriter, outputFile);
            firstgid += tileset.size();
        }

        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                writeMapLayer((TiledMapTileLayer) layer, xmlWriter, outputFile);
            } else {
                // Handle other layer types if needed
            }
        }
        firstGidPerTileset = null;

        xmlWriter.pop(); // </map>

        writer.close();
    }

    private void writeProperties(MapProperties props, XmlWriter w) throws IOException {
        if (props != null) {
            Iterator<String> keysIterator = props.getKeys();
            if (keysIterator.hasNext()) {
                w.element("properties");
                while (keysIterator.hasNext()) {
                    String key = keysIterator.next();
                    Object value = props.get(key);

                    w.element("property")
                            .attribute("name", key)
                            .attribute("value", value.toString())
                            .pop(); // </property>
                }
                w.pop(); // </properties>
            }
        }
    }

    private void writeTilesetReference(TiledMapTileSet set, XmlWriter w, FileHandle outputFile) throws IOException {
        w.element("tileset")
                .attribute("firstgid", getFirstGidForTileset(set))
                .attribute("name", set.getName())
                .attribute("source", set.getName() + ".tsx") // Placeholder: Modify this to reference the actual TSX file or embedded tileset data
                .pop(); // </tileset>
    }

    private void writeMapLayer(TiledMapTileLayer l, XmlWriter w, FileHandle outputFile) throws IOException {
        w.element("layer");

        writeLayerAttributes(l, w);
        writeProperties(l.getProperties(), w);

        w.element("data");
        if (settings.compressLayerData) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStream out;

            w.attribute("encoding", "base64");

            DeflaterOutputStream dos = new DeflaterOutputStream(baos);
            out = dos;
            w.attribute("compression", "zlib");

            for (int y = 0; y < l.getHeight(); y++) {
                for (int x = 0; x < l.getWidth(); x++) {
                    TiledMapTileLayer.Cell cell = l.getCell(x, y);
                    int gid = 0;

                    if (cell != null) {
                        TiledMapTile tile = cell.getTile();
                        if (tile != null) {
                            gid = getGid(tile);
                        }
                    }

                    out.write(gid & LAST_BYTE);
                    out.write(gid >> Byte.SIZE & LAST_BYTE);
                    out.write(gid >> Byte.SIZE * 2 & LAST_BYTE);
                    out.write(gid >> Byte.SIZE * 3 & LAST_BYTE);
                }
            }

            dos.finish();

            byte[] dec = baos.toByteArray();
            w.text(Base64Coder.encodeString(new String(dec, "ISO-8859-1")));
        } else {
            for (int y = 0; y < l.getHeight(); y++) {
                for (int x = 0; x < l.getWidth(); x++) {
                    TiledMapTileLayer.Cell cell = l.getCell(x, y);
                    int gid = 0;

                    if (cell != null) {
                        TiledMapTile tile = cell.getTile();
                        if (tile != null) {
                            gid = getGid(tile);
                        }
                    }

                    w.element("tile").attribute("gid", gid).pop(); // </tile>
                }
            }
        }
        w.pop() // </data>
        .pop(); // </layer>
    }

    private void writeLayerAttributes(MapLayer l, XmlWriter w) throws IOException {
        w.attribute("name", l.getName());

        Boolean isVisible = l.isVisible();
        if (!isVisible) {
            w.attribute("visible", "0");
        }
        Float opacity = l.getOpacity();
        if (opacity < 1.0f) {
            w.attribute("opacity", opacity.toString());
        }

        w.attribute("id", (Integer) l.getProperties().get("id"));
    }
    private int getGid(TiledMapTile tile) {
        // Find the correct tileset for the tile
        for (TiledMapTileSet tileset : firstGidPerTileset.keySet()) {
            TiledMapTile foundTile = tileset.getTile(tile.getId());
            if (foundTile != null) {
                return foundTile.getId() + getFirstGidForTileset(tileset);
            }
        }
        return tile.getId(); // Fallback to tile's own id if no matching tileset found
    }

    private void setFirstGidForTileset(TiledMapTileSet tileset, int firstGid) {
        firstGidPerTileset.put(tileset, firstGid);
    }

    private int getFirstGidForTileset(TiledMapTileSet tileset) {
        return firstGidPerTileset.getOrDefault(tileset, 1);
    }

}
