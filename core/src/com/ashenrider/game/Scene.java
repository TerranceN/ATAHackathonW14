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
    Map map;
    
    public Scene(String filename) {
        map = new Map(filename);

        entities = new ArrayList<Entity>();
        entities.add(new Player());
    }

	public void update(float dt) {
		for (Entity e : entities) {
			e.update(1/30.0f);
		}
	}
    
    public void render(OrthographicCamera camera) {
        map.renderBackground(camera);
		for (Entity e : entities) {
			e.render();
		}
        map.renderForeground(camera);
    }
}
