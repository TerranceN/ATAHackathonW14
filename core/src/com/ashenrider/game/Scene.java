package com.ashenrider.game;

import java.util.ArrayList;

import com.ashenrider.game.Input.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;

public class Scene {
	// map and entitity layers
	int NUM_LAYERS = 5;
	static int BACKGROUND_LAYER = 0;
	static int PLAYER_LAYER = 1;
	static int PARTICLE_LAYER = 2;
	static int SHOT_LAYER = 3;
	static int FOREGROUND_LAYER = 4;
	
    OrthographicCamera camera;
    public ArrayList<Entity> newEntities;
    public ArrayList<Entity> entities;
    // list of lists of entities
    public ArrayList<ArrayList<Entity>> entityLayers;
    
    public ArrayList<Player> players;
    
    public Map map;
    SpriteBatch batch;
    
    public Scene(String filename) {
    	batch = new SpriteBatch();
        map = new Map(filename);

        newEntities = new ArrayList<Entity>();
        entities = new ArrayList<Entity>();
        entityLayers = new ArrayList<ArrayList<Entity>>();
        for (int layer =0; layer < NUM_LAYERS; layer++) {
        	entityLayers.add(new ArrayList<Entity>());
        }
        
        players = new ArrayList<Player>();
        addPlayer(new Vector2(100, 100),
	      		  new KeyboardAxis(Keys.A, Keys.D),
	    		  new KeyboardAxis(Keys.LEFT, Keys.RIGHT),
	    		  new KeyboardAxis(Keys.DOWN, Keys.UP),
        		  new KeyboardButton(Keys.W),
        		  new KeyboardButton(Keys.ENTER),
        		  new KeyboardButton(Keys.APOSTROPHE));

        for(Controller controller : Controllers.getControllers()) {
            addPlayer(new Vector2(400, 200),
                    new ControllerAxis(controller, Xbox.AXIS_LEFT_STICK_HORIZONTAL),
                    new ControllerAxis(controller, Xbox.AXIS_RIGHT_STICK_HORIZONTAL),
                    new ControllerAxis(controller, Xbox.AXIS_RIGHT_STICK_VERTICAL),
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

        onResize();
    }

    public void onResize() {
        float screenRatio = (float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        float tileRatio = (float)map.levelLayer.getWidth() / map.levelLayer.getHeight();
        float unitScale = 1f;

        if (screenRatio <= tileRatio) {
            unitScale = Gdx.graphics.getWidth() / (map.levelLayer.getWidth() * map.tileSize);
        } else {
            unitScale = Gdx.graphics.getHeight() / (map.levelLayer.getHeight() * map.tileSize);
        }

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / unitScale, Gdx.graphics.getHeight() / unitScale);
        camera.update();
    }

	public void update(float dt) {
		for (Entity e : entities) {
			if (!e.destroyed) {
				e.update(dt);
			}
		}
		for (Entity e : entities) {
			if (!e.destroyed) {
				e.handleCollision(map);
			}
		}
		for (Entity e : newEntities) {
			entities.add(e);
			entityLayers.get(e.layer).add(e);
		}
		// remove destroyed entities
		for (int i = entities.size() - 1; i >= 0; i--) {
			Entity e = entities.get(i);
			if (e.destroyed) {
				entities.remove(i);
			}
		}
		newEntities.clear();
	}
	
	public void addPlayer(Vector2 position,  InputAxis moveAxis, InputAxis aimH, InputAxis aimV, InputButton jump, InputButton shoot, InputButton dash) {
		Player p = new Player(players.size(), position, moveAxis, aimH, aimV, jump, shoot, dash);
        players.add(p);
        addEntity(p, PLAYER_LAYER);
	}

	public void addEntity(Entity e, int layer) {
		e.scene = this;
		e.layer = layer;
		newEntities.add(e);
	}
	
    public void render() {
        batch.setProjectionMatrix(camera.combined);
		for (int layer =0; layer < NUM_LAYERS; layer++) {
	        map.renderLayer(layer, camera);
			batch.begin();
			for (Entity e : entityLayers.get(layer)) {
				e.render(batch);
			}
			batch.end();
		}
    }
}
