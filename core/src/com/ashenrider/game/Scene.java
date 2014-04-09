package com.ashenrider.game;

import java.util.ArrayList;

import com.ashenrider.game.Input.InputAxis;
import com.ashenrider.game.Input.InputButton;
import com.ashenrider.game.Input.KeyboardAxis;
import com.ashenrider.game.Input.KeyboardButton;
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
        addPlayer(new Vector2(100, 100),
        		  new KeyboardAxis(Keys.A, Keys.D),
        		  new KeyboardButton(Keys.W),
        		  new KeyboardButton(Keys.ENTER));
        
        addPlayer(new Vector2(400, 200),
	      		  new KeyboardAxis(Keys.LEFT, Keys.RIGHT),
	      		  new KeyboardButton(Keys.UP),
	      		  new KeyboardButton(Keys.CONTROL_RIGHT));
    }

	public void update(float dt) {
		for (Entity e : entities) {
			e.update(dt);
		}
	}
	
	public void addPlayer(Vector2 position,  InputAxis moveAxis, InputButton jump, InputButton shoot) {
		Player p = new Player(position, moveAxis, jump, shoot);
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
