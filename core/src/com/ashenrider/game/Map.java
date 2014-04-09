package com.ashenrider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Map {
    TiledMap tiledMap;
    OrthogonalTiledMapRenderer mapRenderer;

    TiledMapTileLayer levelLayer;
    TiledMapTileLayer spawnLayer;
    TiledMapTileLayer decorationFrontLayer;
    TiledMapTileLayer decorationBackLayer;

    float TILE_SIZE = 32.0f;

    public Map(String fileName) {
        tiledMap = new TmxMapLoader().load(fileName);
        spawnLayer = getLayerByName("spawn");
        levelLayer = getLayerByName("level");
        decorationFrontLayer = getLayerByName("decoration_front");
        decorationBackLayer = getLayerByName("decoration_back");

        spawnLayer.setVisible(false);

        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, Gdx.graphics.getWidth() / (levelLayer.getWidth() * TILE_SIZE));
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
