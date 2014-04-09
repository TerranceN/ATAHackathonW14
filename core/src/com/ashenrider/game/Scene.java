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
        		  new KeyboardButton(Keys.ENTER),
        		  new KeyboardButton(Keys.APOSTROPHE));

        for(Controller controller : Controllers.getControllers()) {
            addPlayer(new Vector2(400, 200),
                    new ControllerAxis(controller, Xbox.AXIS_LEFT_STICK_HORIZONTAL),
                    new ControllerButton(controller, Xbox.BTN_A),
                    new ControllerAxisButton(controller, Xbox.AXIS_RIGHT_TRIGGER),
                    new ControllerAxisButton(controller, Xbox.AXIS_LEFT_TRIGGER));


            controller.addListener(new ControllerListener() {
                @Override
                public void connected(Controller controller) {

                }

                @Override
                public void disconnected(Controller controller) {

                }

                @Override
                public boolean buttonDown(Controller controller, int i) {
                    Gdx.app.log("Controller", "BUTTON: " + i);
                    return false;
                }

                @Override
                public boolean buttonUp(Controller controller, int i) {
                    return false;
                }

                @Override
                public boolean axisMoved(Controller controller, int i, float v) {
                    Gdx.app.log("Controller", "AXIS: " + i + " || " + v);
                    return false;
                }

                @Override
                public boolean povMoved(Controller controller, int i, PovDirection povDirection) {
                    return false;
                }

                @Override
                public boolean xSliderMoved(Controller controller, int i, boolean b) {
                    return false;
                }

                @Override
                public boolean ySliderMoved(Controller controller, int i, boolean b) {
                    return false;
                }

                @Override
                public boolean accelerometerMoved(Controller controller, int i, Vector3 vector3) {
                    return false;
                }
            });
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
		for (Entity e : entities) {
			e.handleCollision(map);
		}
		for (Entity e : newEntities) {
			entities.add(0, e);
		}
		newEntities.clear();
	}
	
	public void addPlayer(Vector2 position,  InputAxis moveAxis, InputButton jump, InputButton shoot, InputButton dash) {
		Player p = new Player(players.size(), position, moveAxis, jump, shoot, dash);
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
