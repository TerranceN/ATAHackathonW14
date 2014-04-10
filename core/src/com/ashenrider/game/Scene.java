package com.ashenrider.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.ashenrider.game.Input.*;
import com.ashenrider.game.userinterface.DeathsWidget;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
    ArrayList<Vector2> spawnPoints;
    
    public ArrayList<Player> players;
    
    public Map map;
    SpriteBatch batch;
    Texture background;
    private List<PlayerDeathListener> playerDeathListeners = new LinkedList<PlayerDeathListener>();

    public Scene(String filename) {
    	batch = new SpriteBatch();
        map = new Map(filename);
        camera = new OrthographicCamera();

        background = new Texture("background.png");

        onResize();
        newEntities = new ArrayList<Entity>();
        entities = new ArrayList<Entity>();
        entityLayers = new ArrayList<ArrayList<Entity>>();
        for (int layer =0; layer < NUM_LAYERS; layer++) {
        	entityLayers.add(new ArrayList<Entity>());
        }
        
        players = new ArrayList<Player>();
        Player p = addPlayer(new Vector2(100, 100),
        		  new KeyboardAxis(Keys.A, Keys.D),
        		  // mouseAxis needs a reference to the player
        		  null,
        		  null,
        		  new KeyboardButton(Keys.W),
        		  new MouseButton(Buttons.LEFT),
        		  new KeyboardButton(Keys.S));
        p.axisMap.put(Player.Action.AIM_HORIZONTAL, new MouseAxis(p, camera, true));
        p.axisMap.put(Player.Action.AIM_VERTICAL, new MouseAxis(p, camera, false));

        boolean controllerDebug = false;

        for(Controller controller : Controllers.getControllers()) {
            addPlayer(new Vector2(400, 200),
                    new ControllerAxis(controller, Xbox.AXIS_LEFT_STICK_HORIZONTAL),
                    new ControllerAxis(controller, Xbox.AXIS_RIGHT_STICK_HORIZONTAL),
                    new ControllerAxis(controller, Xbox.AXIS_RIGHT_STICK_VERTICAL, true),
                    new ControllerButton(controller, Xbox.BTN_A),
                    new ControllerAxisButton(controller, Xbox.AXIS_RIGHT_TRIGGER),
                    new ControllerAxisButton(controller, Xbox.AXIS_LEFT_TRIGGER));


            if (controllerDebug) {
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
        }

        spawnPoints = map.getSpawnPoints();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            respawnPlayer(player, false);
        }
    }

    public void respawnPlayer(Player player, boolean body) {
        //TODO: Random spawn points?
        player.pos = spawnPoints.get(player.number % spawnPoints.size()).cpy().sub(new Vector2(player.size.x / 2.f, 0f));

        //TODO Spawn a body.
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
	
	public Player addPlayer(Vector2 position,  InputAxis moveAxis, InputAxis aimH, InputAxis aimV, InputButton jump, InputButton shoot, InputButton dash) {
		Player p = new Player(players.size(), position, moveAxis, aimH, aimV, jump, shoot, dash);
        players.add(p);
        addEntity(p, PLAYER_LAYER);
        return p;
	}

	public void addEntity(Entity e, int layer) {
		e.scene = this;
		e.layer = layer;
		newEntities.add(e);
	}
	
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, map.getWidth(), map.getHeight());
        batch.end();
		for (int layer =0; layer < NUM_LAYERS; layer++) {
	        map.renderLayer(layer, camera);
			batch.begin();
			for (Entity e : entityLayers.get(layer)) {
				e.renderWithWrapAround(batch);
			}
			batch.end();
		}
    }

    public void addPlayerDeathListener(PlayerDeathListener playerDeathListener) {
        this.playerDeathListeners.add(playerDeathListener);
    }

    public void reportPlayerDeath(Player aggressor, Player victim) {
        for(PlayerDeathListener listener : playerDeathListeners) {
            listener.onPlayerDeath(aggressor, victim);
        }
    }
}
