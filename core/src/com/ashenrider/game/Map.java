package com.ashenrider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Map {
    TiledMap tiledMap;
    OrthogonalTiledMapRenderer mapRenderer;

    TiledMapTileLayer levelLayer;
    TiledMapTileLayer spawnLayer;
    TiledMapTileLayer decorationFrontLayer;
    TiledMapTileLayer decorationBackLayer;

    float tileSize = 1.f;
    float unitScale = 1.f;

    public Map(String fileName) {
        tiledMap = new TmxMapLoader().load(fileName);
        spawnLayer = getLayerByName("spawn");
        levelLayer = getLayerByName("level");
        decorationFrontLayer = getLayerByName("decoration_front");
        decorationBackLayer = getLayerByName("decoration_back");

        spawnLayer.setVisible(false);

        float screenRatio = (float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        float tileRatio = (float)levelLayer.getWidth() / Gdx.graphics.getHeight();
        tileSize = levelLayer.getTileWidth();

        if (screenRatio >= tileRatio) {
            unitScale = Gdx.graphics.getWidth() / (levelLayer.getWidth() * tileSize);
        } else {
            unitScale = Gdx.graphics.getHeight() / (levelLayer.getHeight() * tileSize);
        }

        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
    }

    public Vector2 toWorldSpace(Vector2 tileCoord) {
        int x = Math.round(tileCoord.x);
        int y = Math.round(tileCoord.y);

        return toWorldSpace(x, y);
    }

    public Vector2 toWorldSpace(int x, int y) {
        return new Vector2(tileSize * (x + 0.5f) / unitScale, tileSize * y / unitScale);
    }

    public ArrayList<Vector2> getSpawnPoints() {
        ArrayList<Vector2> lst = new ArrayList<Vector2>();

        int layerWidth = spawnLayer.getWidth();
        int layerHeight = spawnLayer.getHeight();
        for (int x = 0; x < spawnLayer.getWidth(); x++) {
            for (int y = 0; y < spawnLayer.getHeight(); y++) {
                if (spawnLayer.getCell(x, y) != null) {
                    lst.add(toWorldSpace(x, y));
                }
            }
        }

        return lst;
    }

    public void hideAllLayers() {
        MapLayers layers = tiledMap.getLayers();

        for (int i = 0; i < layers.getCount(); i++) {
            ((TiledMapTileLayer)layers.get(i)).setVisible(false);
        }
    }

    public TiledMapTileLayer getLayerByName(String name) {
        MapLayers layers = tiledMap.getLayers();

        for (int i = 0; i < layers.getCount(); i++) {
            if (name.equals(layers.get(i).getName())) {
                return (TiledMapTileLayer)layers.get(i);
            }
        }

        String allLayerNames = null;

        for (int i = 0; i < layers.getCount(); i++) {
            if (allLayerNames == null) {
                allLayerNames = "";
            } else {
                allLayerNames += ", ";
            }

            allLayerNames += layers.get(i).getName();
        }

        System.out.println("No layer with name: " + name + ". Available names are: " + allLayerNames);
        System.exit(0);

        return null;
    }

    public void renderBackground(OrthographicCamera camera) {
        hideAllLayers();

        decorationBackLayer.setVisible(true);

        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void renderForeground(OrthographicCamera camera) {
        hideAllLayers();

        levelLayer.setVisible(true);
        decorationFrontLayer.setVisible(true);

        mapRenderer.setView(camera);
        mapRenderer.render();
    }
}
