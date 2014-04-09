package com.ashenrider.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Scene {
    ArrayList<Entity> entities;
    TiledMap map;
    OrthogonalTiledMapRenderer tiledRenderer;
    float TILE_SIZE = 32.0f;
    
    public Scene(String filename) {
        map = new TmxMapLoader().load(filename);
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
        tiledRenderer = new OrthogonalTiledMapRenderer(map, Gdx.graphics.getHeight() / (layer.getHeight() * TILE_SIZE));
        System.out.println(layer.getWidth() + ", " + layer.getHeight());

        entities = new ArrayList<Entity>();
        entities.add(new Player());
    }

	public void update(float dt) {
		for (Entity e : entities) {
			e.update(1/30.0f);
		}
	}
    
    
    public void render(OrthographicCamera camera) {
        tiledRenderer.setView(camera);
        tiledRenderer.render();
		for (Entity e : entities) {
			e.render();
		}
    }
}
