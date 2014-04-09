package com.ashenrider.game;

import java.util.ArrayList;

import com.ashenrider.game.Input.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;

public class Scene {
    public ArrayList<Entity> newEntities;
    
    public ArrayList<Entity> entities;
    public ArrayList<Player> players;
    
    public Map map;
    
    public Scene(String filename) {
        map = new Map(filename);

        newEntities = new ArrayList<Entity>();
        entities = new ArrayList<Entity>();
        players = new ArrayList<Player>();
        addPlayer(new Vector2(100, 100),
        		  new KeyboardAxis(Keys.A, Keys.D),
        		  new KeyboardButton(Keys.W),
        		  new KeyboardButton(Keys.ENTER));

        for(Controller controller : Controllers.getControllers()) {
            addPlayer(new Vector2(400, 200),
                    new ControllerAxis(controller, Xbox.AXIS_LEFT_STICK_HORIZONTAL),
                    new ControllerButton(controller, Xbox.BTN_A),
                    new ControllerAxisButton(controller, Xbox.AXIS_RIGHT_TRIGGER));
        }

        ArrayList<Vector2> spawnPoints = map.getSpawnPoints();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            player.pos = spawnPoints.get(i % spawnPoints.size()).cpy().sub(new Vector2(player.img.getWidth() / 2.f, 0f));
        }
    }

	public void update(float dt) {
		for (Entity e : entities) {
			e.update(dt);
		}
		for (Entity e : newEntities) {
			entities.add(0, e);
		}
		newEntities.clear();
	}
	
	public void addPlayer(Vector2 position,  InputAxis moveAxis, InputButton jump, InputButton shoot) {
		Player p = new Player(players.size(), position, moveAxis, jump, shoot);
        players.add(p);
        addEntity(p);
	}

	public void addEntity(Entity e) {
		newEntities.add(e);
		e.scene = this;
	}
	
    public void render(OrthographicCamera camera) {
        map.renderBackground(camera);
		for (Entity e : entities) {
			e.render();
		}
        map.renderForeground(camera);
    }
}
