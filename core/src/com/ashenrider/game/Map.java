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

    TiledMapTileLayer backgroundLayer;
    public TiledMapTileLayer levelLayer;
    TiledMapTileLayer powerUpLayer;
    TiledMapTileLayer spawnLayer;
    TiledMapTileLayer decorationFrontLayer;
    TiledMapTileLayer decorationBackLayer;

    float tileSize = 1.f;
    float mapScale = 1.2f;

    float width = 0f;
    float height = 0f;

    public Map(String fileName) {
        tiledMap = new TmxMapLoader().load(fileName);
        spawnLayer = getLayerByName("spawn");
        powerUpLayer = getLayerByName("powerup");
        levelLayer = getLayerByName("level");
        backgroundLayer = getLayerByName("background");
        decorationFrontLayer = getLayerByName("decoration_front");
        decorationBackLayer = getLayerByName("decoration_back");

        spawnLayer.setVisible(false);

        tileSize = levelLayer.getTileWidth() * mapScale;

        width = tileSize * levelLayer.getWidth();
        height = tileSize * levelLayer.getHeight();

        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, mapScale);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector2 toWorldSpace(Vector2 tileCoord) {
        return toWorldSpace(tileCoord.x, tileCoord.y);
    }

    public Vector2 toWorldSpace(float x, float y) {
        return new Vector2(tileSize * x, tileSize * y);
    }

    public ArrayList<Vector2> getSpawnPoints() {
        ArrayList<Vector2> lst = new ArrayList<Vector2>();

        int layerWidth = spawnLayer.getWidth();
        int layerHeight = spawnLayer.getHeight();
        for (int x = 0; x < spawnLayer.getWidth(); x++) {
            for (int y = 0; y < spawnLayer.getHeight(); y++) {
                if (spawnLayer.getCell(x, y) != null) {
                    lst.add(toWorldSpace(x + 0.5f, y));
                }
            }
        }

        return lst;
    }

    public ArrayList<Vector2> getPowerupPoints() {
        ArrayList<Vector2> powerUps = new ArrayList<Vector2>();

        int layerWidth = powerUpLayer.getWidth();
        int layerHeight = powerUpLayer.getHeight();
        for (int x=0; x < powerUpLayer.getWidth(); x++) {
            for(int y=0; y < powerUpLayer.getHeight(); y++) {
                if(powerUpLayer.getCell(x, y) != null) {
                    powerUps.add(toWorldSpace(x, y));
                }
            }
        }

        return powerUps;
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
            backgroundLayer.setVisible(true);
            decorationBackLayer.setVisible(true);
        } else if (layer == Scene.FOREGROUND_LAYER) {
            levelLayer.setVisible(true);
            decorationFrontLayer.setVisible(true);
        } else {
        	return;
        }
        if (camera != null) {
            mapRenderer.setView(camera);
        }
        mapRenderer.render();
    }

    public Vector2 getTileCoords(Vector2 worldCoords) {
        return new Vector2((float)Math.floor(worldCoords.x / tileSize), (float)Math.floor(worldCoords.y / tileSize));
    }

    public boolean isInsideLevel(float x, float y) {
        int tileX = (int)Math.floor(x / tileSize);
        int tileY = (int)Math.floor(y / tileSize);

        return levelLayer.getCell(tileX, tileY) != null;
    }

    public Vector2 getLeastPenetration(Vector2 vel, Vector2 lower, Vector2 upper) {
    	// check this many tiles further than the size of the character to determine the "depth" of the wall.
    	// this may require fewer cells to be checked
    	int maxDepth = 2;
    	
        int lowerX = (int)Math.floor(lower.x / tileSize);
        int lowerY = (int)Math.floor(lower.y / tileSize);
        int upperX = (int)Math.ceil(upper.x / tileSize);
        int upperY = (int)Math.ceil(upper.y / tileSize);

        float penX = 0;
        float penY = 0;

        int startX = 0;
        int xDir = 0;

        vel = new Vector2(1, 1);
        for (int i = 0; i < 2; i++) {
            if (i % 2 == 1) {
                vel = vel.scl(-1);
            }
            if (vel.x < 0) {
                startX = lowerX;
                xDir = 1;
            } else {
                startX = upperX - 1;
                xDir = -1;
            }

            // check rows for collision offset
            for (int y = lowerY; y < upperY; y++) {
                boolean foundCollision = false;
                float newPenX = 0;
                for (int x = startX; (foundCollision && (x < upperX + maxDepth && x >= lowerX - maxDepth)) || (x < upperX && x >= lowerX); x += xDir) {
                    if (levelLayer.getCell(x, y) != null) {
                        foundCollision = true;
                        if (vel.x < 0) {
                            newPenX = (x + 1) * tileSize - lower.x + 0.001f;
                        } else {
                            newPenX = (x) * tileSize - upper.x - 0.001f;
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

            // check columns for collision offset
            for (int x = lowerX; x < upperX; x++) {
                boolean foundCollision = false;
                float newPenY = 0;
                for (int y = startY; (foundCollision && (y < upperY + maxDepth && y >= lowerY - maxDepth)) || (y < upperY && y >= lowerY); y += yDir) {
                    if (levelLayer.getCell(x, y) != null) {
                        foundCollision = true;
                        if (vel.y < 0) {
                            newPenY = (y + 1) * tileSize - lower.y;
                        } else {
                            newPenY = (y) * tileSize - upper.y;
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

        }

        return new Vector2(penX, penY);
    }
}
