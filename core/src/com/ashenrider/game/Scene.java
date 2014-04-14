package com.ashenrider.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.ashenrider.game.Input.*;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Pixmap;

public class Scene {
    Random random = new Random();

	// map and entitity layers
	int NUM_LAYERS = 5;
	static int BACKGROUND_LAYER = 0;
	static int PLAYER_LAYER = 1;
	static int SHOT_LAYER = 2;
	static int FOREGROUND_LAYER = 3;
	static int PARTICLE_LAYER = 4;
	
    OrthographicCamera camera;
    OrthographicCamera mapCam;
    public ArrayList<Entity> newEntities;
    public ArrayList<Entity> entities;
    // list of lists of entities
    public ArrayList<ArrayList<Entity>> entityLayers;
    ArrayList<Vector2> spawnPoints;
    ArrayList<Vector2> powerUpPoints;
    
    public ArrayList<Player> players;
    
    public Map map;
    SpriteBatch batch;
    Texture background;
    private List<PlayerDeathListener> playerDeathListeners = new LinkedList<PlayerDeathListener>();

    ShapeRenderer shapeRenderer;

    float unitScale = 1f;
    private static float NULL_FADE_TIME = 0.8f;

    ShaderProgram nullSphereMaskingShader;
    ShaderProgram nullSphereFadeShader;
    ShaderProgram nullSphereFilterShader;

    FrameBuffer collisionMask;
    TextureRegion collisionMaskRegion;

    FrameBuffer tmpMapBuffer;
    TextureRegion tmpMapBufferRegion;

    FrameBuffer levelBuffer;
    TextureRegion levelBufferRegion;
    
    public Scene(String filename) {
    	batch = new SpriteBatch();
        map = new Map(filename);
        camera = new OrthographicCamera();
        mapCam = new OrthographicCamera();
        mapCam.setToOrtho(false, map.getWidth(), map.getHeight());

        collisionMask = new FrameBuffer(Format.RGB888, (int)map.getWidth(), (int)map.getHeight(), false);
        collisionMaskRegion = new TextureRegion(collisionMask.getColorBufferTexture());
        collisionMaskRegion.flip(false, true);

        // clear the mask at the beginning
        collisionMask.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        collisionMask.end();

        tmpMapBuffer = new FrameBuffer(Format.RGBA8888, (int)map.getWidth(), (int)map.getHeight(), false);
        tmpMapBufferRegion = new TextureRegion(tmpMapBuffer.getColorBufferTexture());
        tmpMapBufferRegion.flip(false, true);

        levelBuffer = new FrameBuffer(Format.RGBA8888, (int)map.getWidth(), (int)map.getHeight(), false);
        levelBufferRegion = new TextureRegion(levelBuffer.getColorBufferTexture());
        levelBufferRegion.flip(false, true);

        background = Assets.manager.get("background.png", Texture.class);

        onResize();
        newEntities = new ArrayList<Entity>();
        entities = new ArrayList<Entity>();
        entityLayers = new ArrayList<ArrayList<Entity>>();
        for (int layer =0; layer < NUM_LAYERS; layer++) {
        	entityLayers.add(new ArrayList<Entity>());
        }
        
        players = new ArrayList<Player>();
        boolean controllerDebug = false;

        for(Controller controller : Controllers.getControllers()) {
            addPlayer(new Vector2(400, 200),
                    ControllerHelper.getAxis(controller, ControllerHelper.LEFT_STICK_HORIZONTAL),
                    ControllerHelper.getAxis(controller, ControllerHelper.RIGHT_STICK_HORIZONTAL),
                    ControllerHelper.getAxis(controller, ControllerHelper.RIGHT_STICK_VERTICAL),
                    ControllerHelper.getButton(controller, ControllerHelper.A_BTN),
                    ControllerHelper.getButton(controller, ControllerHelper.RIGHT_TRIGGER),
                    ControllerHelper.getButton(controller, ControllerHelper.LEFT_TRIGGER),
                    ControllerHelper.getButton(controller, ControllerHelper.B_BTN));


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
        
        // keyboard/mouse player
		Player p = addPlayer(new Vector2(100, 100),
			  new KeyboardAxis(Keys.A, Keys.D),
			  // mouseAxis needs a reference to the player
		  		  null,
		  		  null,
		  		  new KeyboardButton(Keys.W),
		  		  new MouseButton(Buttons.LEFT),
		  		  new KeyboardButton(Keys.S),
		            new KeyboardButton(Keys.SHIFT_LEFT));
		p.axisMap.put(Player.Action.AIM_HORIZONTAL, new MouseAxis(p, camera, true));
		p.axisMap.put(Player.Action.AIM_VERTICAL, new MouseAxis(p, camera, false));

        // uncontrollable players
        for (int i = players.size(); i < 4; i++) {
			addPlayer(new Vector2(100, 100),
					  new KeyboardAxis(Keys.LEFT, Keys.RIGHT),
					  new KeyboardAxis(Keys.NUMPAD_4, Keys.NUMPAD_6),
					  new KeyboardAxis(Keys.NUMPAD_5, Keys.NUMPAD_8),
			  		  new KeyboardButton(Keys.UP),
			  		  new KeyboardButton(Keys.ENTER),
			  		  new KeyboardButton(Keys.CONTROL_RIGHT),
			          new KeyboardButton(Keys.SHIFT_RIGHT));
        }

        spawnPoints = map.getSpawnPoints();
        powerUpPoints = map.getPowerupPoints();

        addEntity(new InvulnerabilityPowerUp(powerUpPoints), PLAYER_LAYER);
        addEntity(new SpeedPowerUp(powerUpPoints), PLAYER_LAYER);

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            // stagger the initial player spawns ?
            respawnPlayer(player, 0.5f * i);
        }

        loadShaders();

        shapeRenderer = new ShapeRenderer();
    }

    public void respawnPlayer(Player player, float delay) {
    	// pick the spawn point for which the closest player is the furthest away.
    	// alternatively, pick any spawnpoint that has no players within a minimum distance
    	float spawnD = 0.0f;
    	Vector2 furthestSpawn = spawnPoints.get(0);
    	for (Vector2 spawn : spawnPoints) {
    		float minD = Float.MAX_VALUE;
    		for (Player otherPlayer : players) {
    			// Dead players have already been moved to a spawn location, don't go near them if they are respawning soon
    			// Don't spawn near your own corpse either
    			if (player.lives > 0) {
    				minD = Math.min(otherPlayer.getCentre().dst(spawn), minD);
    			}
    		}
    		if (minD > spawnD) {
    			spawnD = minD;
    			furthestSpawn = spawn;
    		}
    	}
        player.pos = furthestSpawn.cpy().sub(new Vector2(player.size.x / 2.f, 0f));
        // clear any leftover state information
    	player.speed = new Vector2(0,0);
    	player.onWall = false;
    	player.nullSphereEnabled = false;
    	player.clearBuffs();
    	
    	player.spawnDelay = delay;
    }

    public void testShaderCompilation(ShaderProgram program) {
        if (!program.isCompiled()) {
            System.out.println(program.getLog());
            System.exit(0);
        }
    }

    public void loadShaders() {
        nullSphereMaskingShader = new ShaderProgram(
                Gdx.files.internal("shaders/nullSphereMasking.vert").readString(),
                Gdx.files.internal("shaders/nullSphereMasking.frag").readString());
        testShaderCompilation(nullSphereMaskingShader);

        nullSphereFadeShader = new ShaderProgram(
                Gdx.files.internal("shaders/nullSphereFade.vert").readString(),
                Gdx.files.internal("shaders/nullSphereFade.frag").readString());
        testShaderCompilation(nullSphereFadeShader);

        nullSphereFilterShader = new ShaderProgram(
                Gdx.files.internal("shaders/nullSphereFilter.vert").readString(),
                Gdx.files.internal("shaders/nullSphereFilter.frag").readString());
        testShaderCompilation(nullSphereFilterShader);
    }

    public void onResize() {
        float screenRatio = (float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        float tileRatio = (float)map.levelLayer.getWidth() / map.levelLayer.getHeight();

        if (screenRatio <= tileRatio) {
            unitScale = Gdx.graphics.getWidth() / map.getWidth();
        } else {
            unitScale = Gdx.graphics.getHeight() / map.getHeight();
        }

        camera.setToOrtho(false, Gdx.graphics.getWidth() / unitScale, Gdx.graphics.getHeight() / unitScale);

        if (screenRatio <= tileRatio) {
            camera.translate(0f, -(Gdx.graphics.getHeight() / unitScale - map.getHeight()) / 2f);
        } else {
            camera.translate(-(Gdx.graphics.getWidth() / unitScale - map.getWidth()) / 2f, 0f);
        }

        camera.update();
    }

    public int getCollisionMaskValueAtPoint(float x, float y) {
        Pixmap collisionMaskPixmap = ScreenUtils.getFrameBufferPixmap((int)x, (int)y, 1, 1);
        return (collisionMaskPixmap.getPixel(0, 0) >> 24) & 0x0FF;
    }

	public void update(float dt) {
        collisionMask.begin();
        // check which players were colliding/not colliding in the previous frame
        for (Player p : players) {
        	if (p.alive) {
        		p.recordTexCollision();
        	}
        }
        // fade collision mask
        batch.setShader(nullSphereFadeShader);
        nullSphereFadeShader.begin();
        batch.begin();
        nullSphereFadeShader.setUniformf("u_dt", dt);
        nullSphereFadeShader.setUniformf("u_fadeRate", 1.0f/NULL_FADE_TIME);
        batch.setProjectionMatrix(mapCam.combined);
        batch.draw(collisionMaskRegion, 0, 0, map.getWidth(), map.getHeight());
        batch.end();
        nullSphereFadeShader.end();
        batch.setShader(null);
        // see which players are colliding now compared to before the mask fade
        for (Player p : players) {
        	if (p.alive) {
        		p.texCollisionResolve();
        	}
        }
        collisionMask.end();

		for (Entity e : entities) {
			if (!e.destroyed) {
				e.update(dt);
			}
		}

        collisionMask.begin();
		for (Entity e : entities) {
			if (!e.destroyed) {
				e.handleCollision(map);
			}
		}
        collisionMask.end();

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
	
	public Player addPlayer(Vector2 position,  InputAxis moveAxis, InputAxis aimH, InputAxis aimV, InputButton jump, InputButton shoot, InputButton dash, InputButton nullSphere) {
		Player p = new Player(players.size(), position, moveAxis, aimH, aimV, jump, shoot, dash, nullSphere);
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
        // render circles onto collisionMask
        collisionMask.begin();
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(mapCam.combined);
        shapeRenderer.setColor(1, 0, 0, 1);
        float radius = 100;
        for (Player p : players) {
            if (p.nullSphereEnabled && !p.isDestroyed()) {
                shapeRenderer.circle(p.pos.x + p.size.x / 2f, p.pos.y + p.size.y / 2f, radius, 20);

                if (p.pos.x + p.size.x + radius > map.getWidth()) {
                    shapeRenderer.circle(p.pos.x + p.size.x / 2f - map.getWidth(), p.pos.y + p.size.y / 2f, radius, 20);
                } else if (p.pos.x - radius < 0) {
                    shapeRenderer.circle(p.pos.x + p.size.x / 2f + map.getWidth(), p.pos.y + p.size.y / 2f, radius, 20);
                }

                if (p.pos.y + p.size.y + radius > map.getHeight()) {
                    shapeRenderer.circle(p.pos.x + p.size.x / 2f, p.pos.y + p.size.y / 2f - map.getHeight(), radius, 20);
                } else if (p.pos.y - radius < 0) {
                    shapeRenderer.circle(p.pos.x + p.size.x / 2f, p.pos.y + p.size.y / 2f + map.getHeight(), radius, 20);
                }
            }
        }
        shapeRenderer.end();
        collisionMask.end();

        levelBuffer.begin();
        batch.setProjectionMatrix(mapCam.combined);
        batch.begin();
        batch.draw(background, 0, 0, map.getWidth(), map.getHeight());
        batch.end();
        levelBuffer.end();

        renderLayers(0, BACKGROUND_LAYER - 1);
        maskAndDrawTmpFrameBufferForLayer(BACKGROUND_LAYER);
        renderLayers(BACKGROUND_LAYER + 1, FOREGROUND_LAYER - 1);
        maskAndDrawTmpFrameBufferForLayer(FOREGROUND_LAYER);
        renderLayers(FOREGROUND_LAYER + 1, NUM_LAYERS - 1);

        batch.setShader(nullSphereFilterShader);
        nullSphereFilterShader.begin();
        Texture collisionTexture = collisionMaskRegion.getTexture();
        collisionTexture.bind(1);
        nullSphereMaskingShader.setUniformi("u_maskTexture", 1);
        Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(levelBufferRegion, 0, 0, map.getWidth(), map.getHeight());
        batch.end();
        nullSphereFilterShader.end();
        batch.setShader(null);
    }

    void renderLayers(int start, int end) {
        levelBuffer.begin();
        batch.setProjectionMatrix(mapCam.combined);
		for (int layer = start; layer <= end; layer++) {
	        map.renderLayer(layer, mapCam);
			batch.begin();
			for (Entity e : entityLayers.get(layer)) {
                if(!e.destroyed) {
                    e.renderWithWrapAround(batch);
                    //if (Particle.BASE_PARTICLE == null) {
                    //	Particle.BASE_PARTICLE = Assets.manager.get("particle.png", Texture.class);
                    //} else if (Particle.BASE_PARTICLE != null) {
                    //    batch.draw(Particle.BASE_PARTICLE, e.pos.x - Particle.BASE_PARTICLE.getWidth() / 2f, e.pos.y - Particle.BASE_PARTICLE.getHeight() / 2f, 10, 10);
                    //    batch.draw(Particle.BASE_PARTICLE, e.pos.x + e.size.x - Particle.BASE_PARTICLE.getWidth() / 2f, e.pos.y - Particle.BASE_PARTICLE.getHeight() / 2f, 10, 10);
                    //    batch.draw(Particle.BASE_PARTICLE, e.pos.x + e.size.x - Particle.BASE_PARTICLE.getWidth() / 2f, e.pos.y + e.size.y - Particle.BASE_PARTICLE.getHeight() / 2f, 10, 10);
                    //    batch.draw(Particle.BASE_PARTICLE, e.pos.x - Particle.BASE_PARTICLE.getWidth() / 2f, e.pos.y + e.size.y - Particle.BASE_PARTICLE.getHeight() / 2f, 10, 10);
                    //}
                }
			}
			batch.end();
		}
        levelBuffer.end();
    }

    void maskAndDrawTmpFrameBufferForLayer(int layer) {
        tmpMapBuffer.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        map.renderLayer(layer, mapCam);
        tmpMapBuffer.end();

        maskTmpFrameBuffer();

        levelBuffer.begin();
        batch.begin();
        batch.setProjectionMatrix(mapCam.combined);
        batch.draw(tmpMapBufferRegion, 0, 0, map.getWidth(), map.getHeight());
        batch.end();
        levelBuffer.end();
    }

    void maskTmpFrameBuffer() {
        int oldSrcFunc = batch.getBlendSrcFunc();
        int oldDstFunc = batch.getBlendDstFunc();

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ZERO);

        batch.setShader(nullSphereMaskingShader);
        nullSphereMaskingShader.begin();
        Texture collisionTexture = collisionMaskRegion.getTexture();
        collisionTexture.bind(1);
        nullSphereMaskingShader.setUniformi("u_maskTexture", 1);
        Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
        tmpMapBuffer.begin();
        batch.begin();
        batch.setProjectionMatrix(mapCam.combined);
        batch.draw(tmpMapBufferRegion, 0, 0, map.getWidth(), map.getHeight());
        batch.end();
        tmpMapBuffer.end();
        nullSphereMaskingShader.end();
        batch.setShader(null);

        batch.setBlendFunction(oldSrcFunc, oldDstFunc);
    }

    public void addPlayerDeathListener(PlayerDeathListener playerDeathListener) {
        this.playerDeathListeners.add(playerDeathListener);
    }

    public void reportPlayerDeath(Player aggressor, Player victim, int deathSource) {
        for(PlayerDeathListener listener : playerDeathListeners) {
            listener.onPlayerDeath(aggressor, victim, deathSource);
        }
    }
}
