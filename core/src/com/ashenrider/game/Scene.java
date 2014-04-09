package com.ashenrider.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input.Keys;

public class Scene {
    ArrayList<Entity> entities;
    ArrayList<Player> players;
    
    Map map;
    
    public Scene(String filename) {
        map = new Map(filename);

        entities = new ArrayList<Entity>();
        players = new ArrayList<Player>();
        addPlayer(new Vector2(100, 100), Keys.A, Keys.D, Keys.W, Keys.ENTER);
        addPlayer(new Vector2(400, 200), Keys.LEFT, Keys.RIGHT, Keys.UP, Keys.CONTROL_RIGHT);
    }

	public void update(float dt) {
		for (Entity e : entities) {
			e.update(dt);
		}
	}
	
	public void addPlayer(Vector2 position, int keyL, int keyR, int keyJump, int keyShoot) {
		Player p = new Player(position, keyL, keyR, keyJump, keyShoot);
        entities.add(p);
        players.add(p);
	}
    
    public void render(OrthographicCamera camera) {
        map.renderBackground(camera);
		for (Entity e : entities) {
			e.render();
		}
        map.renderForeground(camera);
    }
}
