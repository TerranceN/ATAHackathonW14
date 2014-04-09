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
    float tileSizeInScreenSpace = 1.f;

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

        tileSizeInScreenSpace = tileSize * unitScale;

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
    
    public void renderLayer(int layer, OrthographicCamera camera) {
        hideAllLayers();
        if (layer == Scene.BACKGROUND_LAYER) {
            decorationBackLayer.setVisible(true);
        } else if (layer == Scene.FOREGROUND_LAYER) {
            levelLayer.setVisible(true);
            decorationFrontLayer.setVisible(true);
        } else {
        	return;
        }
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public Vector2 getLeastPenetration(Vector2 vel, Vector2 lower, Vector2 upper) {
        int lowerX = (int)Math.floor(lower.x / tileSizeInScreenSpace);
        int lowerY = (int)Math.floor(lower.y / tileSizeInScreenSpace);
        int upperX = (int)Math.ceil(upper.x / tileSizeInScreenSpace);
        int upperY = (int)Math.ceil(upper.y / tileSizeInScreenSpace);

        float penX = 0;
        float penY = 0;

        int startX = 0;
        int xDir = 0;

        if (vel.x < 0) {
            startX = lowerX;
            xDir = 1;
        } else {
            startX = upperX - 1;
            xDir = -1;
        }

        for (int y = lowerY; y < upperY; y++) {
            boolean foundCollision = false;
            float newPenX = 0;
            for (int x = startX; foundCollision || (x < upperX && x >= lowerX); x += xDir) {
                if (levelLayer.getCell(x, y) != null) {
                    foundCollision = true;
                    if (vel.x < 0) {
                        newPenX = (x + 1) * tileSizeInScreenSpace - lower.x;
                    } else {
                        newPenX = (x) * tileSizeInScreenSpace - upper.x;
                    }
                } else {
                    if (foundCollision) {
                        break;
                    }
                }
            }

            if (penX == 0 || (newPenX != 0 && Math.abs(newPenX) < Math.abs(penX))) {
                penX = newPenX;
            }
        }

        int startY = 0;
        int yDir = 0;

        if (vel.y < 0) {
            startY = lowerY;
            yDir = 1;
        } else {
            startY = upperY - 1;
            yDir = -1;
        }

        for (int x = lowerX; x < upperX; x++) {
            boolean foundCollision = false;
            float newPenY = 0;
            for (int y = startY; foundCollision || (y < upperY && y >= lowerY); y += yDir) {
                if (levelLayer.getCell(x, y) != null) {
                    foundCollision = true;
                    if (vel.y < 0) {
                        newPenY = (y + 1) * tileSizeInScreenSpace - lower.y;
                    } else {
                        newPenY = (y) * tileSizeInScreenSpace - upper.y;
                    }
                } else {
                    if (foundCollision) {
                        break;
                    }
                }
            }

            if (penY == 0 || (newPenY != 0 && Math.abs(newPenY) < Math.abs(penY))) {
                penY = newPenY;
            }
        }

        return new Vector2(penX, penY);
    }
}
