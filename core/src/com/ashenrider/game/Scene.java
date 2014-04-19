package com.ashenrider.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.ashenrider.game.Entities.Entity;
import com.ashenrider.game.Entities.Fireball;
import com.ashenrider.game.Entities.InvulnerabilityPowerUp;
import com.ashenrider.game.Entities.Particle;
import com.ashenrider.game.Entities.Player;
import com.ashenrider.game.Entities.ShotPowerUp;
import com.ashenrider.game.Entities.SpeedPowerUp;
import com.ashenrider.game.Input.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
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
    public static Texture DEBUG_PARTICLE = null;
    Random random = new Random();

    // map and entitity layers
    int NUM_LAYERS = 5;
    public static int BACKGROUND_LAYER = 0;
    public static int PLAYER_LAYER = 1;
    public static int SHOT_LAYER = 2;
    public static int FOREGROUND_LAYER = 3;
    public static int PARTICLE_LAYER = 4;
    
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
    private static float HEAT_FADE_TIME = 1.0f;
    private static float FIRE_GLOW_FADE_TIME = 0.8f;

    ShaderProgram nullSphereMaskingShader;
    ShaderProgram nullSphereFadeShader;
    ShaderProgram nullSphereFilterShader;
    ShaderProgram heatEffectShader;

    FrameBuffer collisionMask;
    TextureRegion collisionMaskRegion;

    FrameBuffer tmpMapBuffer;
    TextureRegion tmpMapBufferRegion;

    FrameBuffer levelBuffer;
    TextureRegion levelBufferRegion;

    FrameBuffer heatEffectBuffer;
    TextureRegion heatEffectBufferRegion;

    Texture circleGradient;
    Texture noiseTexture;

    float gameTime = 0f;
    float backgroundHeat = 0.0f;
    
    public Scene(String filename) {
        batch = new SpriteBatch();
        map = new Map(filename);
        camera = new OrthographicCamera();
        mapCam = new OrthographicCamera();
        mapCam.setToOrtho(false, map.getWidth(), map.getHeight());

        circleGradient = Assets.manager.get("circle_gradient.png", Texture.class);
        noiseTexture = Assets.manager.get("noise.png");
        noiseTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        noiseTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        collisionMask = new FrameBuffer(Format.RGB888, (int)map.getWidth(), (int)map.getHeight(), false);
        collisionMaskRegion = new TextureRegion(collisionMask.getColorBufferTexture());
        collisionMaskRegion.flip(false, true);

        // clear the mask at the beginning
        collisionMask.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        collisionMask.end();

        heatEffectBuffer = new FrameBuffer(Format.RGB888, (int)map.getWidth(), (int)map.getHeight(), false);
        heatEffectBufferRegion = new TextureRegion(heatEffectBuffer.getColorBufferTexture());
        heatEffectBufferRegion.flip(false, true);

        // clear the buffer at the beginning
        heatEffectBuffer.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        heatEffectBuffer.end();

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
            Player player = addPlayer(new Vector2(400, 200));
            // need to update controllerHelper in order to support more buttons, or rethink the control scheme
            player.setInputs(ControllerHelper.getAxis(controller, ControllerHelper.LEFT_STICK_HORIZONTAL),
                    ControllerHelper.getAxis(controller, ControllerHelper.LEFT_STICK_VERTICAL),
                    ControllerHelper.getAxis(controller, ControllerHelper.RIGHT_STICK_HORIZONTAL),
                    ControllerHelper.getAxis(controller, ControllerHelper.RIGHT_STICK_VERTICAL),
                    ControllerHelper.getButton(controller, ControllerHelper.A_BTN),
                    ControllerHelper.getButton(controller, ControllerHelper.RIGHT_TRIGGER),
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
        Player player = addPlayer(new Vector2(100, 100));
        player.setInputs(new KeyboardAxis(Keys.A, Keys.D),
                        new KeyboardAxis(Keys.S, Keys.W),
                        new MouseAxis(player, camera, true),
                        new MouseAxis(player, camera, false),
                        new KeyboardButton(Keys.W),
                        new MouseButton(Buttons.LEFT),
                        new MouseButton(Buttons.RIGHT),
                        new KeyboardButton(Keys.E),
                        new KeyboardButton(Keys.SHIFT_LEFT));

        // uncontrollable players
        if (HackathonApp.FILLER_PLAYERS) {
            for (int i = players.size(); i < 4; i++) {
                player = addPlayer(new Vector2(100, 100));
                player.setInputs(new KeyboardAxis(Keys.LEFT, Keys.RIGHT),
                            new KeyboardAxis(Keys.DOWN, Keys.UP),
                            new KeyboardAxis(Keys.NUMPAD_4, Keys.NUMPAD_6),
                            new KeyboardAxis(Keys.NUMPAD_5, Keys.NUMPAD_8),
                            new KeyboardButton(Keys.UP),
                            new KeyboardButton(Keys.ENTER),
                            new KeyboardButton(Keys.ENTER),
                            new KeyboardButton(Keys.SHIFT_RIGHT),
                            new KeyboardButton(Keys.CONTROL_RIGHT));
            }
        }

        spawnPoints = map.getSpawnPoints();
        powerUpPoints = map.getPowerupPoints();

        addEntity(new InvulnerabilityPowerUp(powerUpPoints), PLAYER_LAYER);
        addEntity(new SpeedPowerUp(powerUpPoints), PLAYER_LAYER);
        addEntity(new ShotPowerUp(powerUpPoints), PLAYER_LAYER);

        for (int i = 0; i < players.size(); i++) {
            player = players.get(i);
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

        heatEffectShader = new ShaderProgram(
                Gdx.files.internal("shaders/heatEffect.vert").readString(),
                Gdx.files.internal("shaders/heatEffect.frag").readString());
        testShaderCompilation(heatEffectShader);
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
        // fade heat effect
        heatEffectBuffer.begin();
        batch.setShader(nullSphereFadeShader);
        nullSphereFadeShader.begin();
        nullSphereFadeShader.setUniformf("u_dt", dt);
        nullSphereFadeShader.setUniformf("u_fadeRates", 1.0f/HEAT_FADE_TIME, 1.0f/FIRE_GLOW_FADE_TIME, 1000000f);
        batch.begin();
        batch.setProjectionMatrix(mapCam.combined);
        batch.draw(heatEffectBufferRegion, 0, 0, map.getWidth(), map.getHeight());
        batch.end();
        nullSphereFadeShader.end();
        batch.setShader(null);
        heatEffectBuffer.end();

        gameTime += dt;
        // if you resize the window, it pauses all rendering
        // so you can get a delta time of around 3 seconds, which would result in collision bugs
        dt = Math.min(dt, 1/30f);
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
        nullSphereFadeShader.setUniformf("u_fadeRates", 1.0f/NULL_FADE_TIME, 0.0f, 0.0f);
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
            if (!e.isDestroyed()) {
                e.update(dt);
            }
        }

        collisionMask.begin();
        for (Entity e : entities) {
            if (!e.isDestroyed()) {
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
            if (e.isDestroyed()) {
                entities.remove(i);
            }
        }
        newEntities.clear();
    }
    
    public Player addPlayer(Vector2 position) {
        Player p = new Player(players.size(), position);
        players.add(p);
        addEntity(p, PLAYER_LAYER);
        return p;
    }

    public void addEntity(Entity e, int layer) {
        e.scene = this;
        e.layer = layer;
        newEntities.add(e);
    }

    public void renderSpheresToCollisionMask() {
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
    }

    public void renderSpheresToHeatMask() {
        int oldSrcFunc = batch.getBlendSrcFunc();
        int oldDstFunc = batch.getBlendDstFunc();

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
        heatEffectBuffer.begin();

        batch.begin();
        batch.setProjectionMatrix(mapCam.combined);
        for (Entity e : entities) {
            if (e instanceof Fireball) {
                float size = 140f * (float)Math.min(1.0f, e.life / 0.2f);
                batch.setColor(0, 1, 0, 0.25f);
                Vector2 drawPos = e.pos.cpy().add(e.size.cpy().scl(1f/2f)).sub(new Vector2(1, 1).scl(size/2f));
                batch.draw(circleGradient, drawPos.x, drawPos.y, size, size);
                batch.setColor(1, 0, 0, 0.25f);
                drawPos.sub(e.speed.cpy().nor().scl(0.5f * size));
                batch.draw(circleGradient, drawPos.x, drawPos.y, size, size);
            }
        }
        batch.setColor(1, 1, 1, 1);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        shapeRenderer.begin(ShapeType.Filled);
        float s = (float)Math.sin(gameTime) + 0.5f * (float)Math.sin(gameTime * 4) + 0.25f * (float)Math.sin(gameTime * 16);
        Color haze = new Color(0, 0, 1, 0.6f + 0.1f * s);
        Color noHaze = new Color(0, 0, 0, 0f);
        float edgeSize = 400 + 50 * s;
        shapeRenderer.rect(edgeSize, 0, map.getWidth() - edgeSize * 2, edgeSize, haze, haze, noHaze, noHaze);
        shapeRenderer.rect(0, 0, edgeSize, edgeSize, haze, haze, noHaze, haze);

        shapeRenderer.rect(0, edgeSize, edgeSize, map.getHeight() - edgeSize * 2, haze, noHaze, noHaze, haze);
        shapeRenderer.triangle(0, map.getHeight() - edgeSize, edgeSize, map.getHeight() - edgeSize, 0, map.getHeight(), haze, noHaze, haze);
        shapeRenderer.triangle(edgeSize, map.getHeight() - edgeSize, edgeSize, map.getHeight(), 0, map.getHeight(), noHaze, haze, haze);

        shapeRenderer.rect(edgeSize, map.getHeight() - edgeSize, map.getWidth() - edgeSize * 2, edgeSize, noHaze, noHaze, haze, haze);
        shapeRenderer.rect(map.getWidth() - edgeSize, map.getHeight() - edgeSize, edgeSize, edgeSize, noHaze, haze, haze, haze);

        shapeRenderer.rect(map.getWidth() - edgeSize, edgeSize, edgeSize, map.getHeight() - edgeSize * 2, noHaze, haze, haze, noHaze);
        shapeRenderer.triangle(map.getWidth() - edgeSize, 0, map.getWidth(), 0, map.getWidth() - edgeSize, edgeSize, haze, haze, noHaze);
        shapeRenderer.triangle(map.getWidth(), 0, map.getWidth(), edgeSize, map.getWidth() - edgeSize, edgeSize, haze, haze, noHaze);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        heatEffectBuffer.end();
        batch.setBlendFunction(oldSrcFunc, oldDstFunc);
    }

    public void render() {
        // render circles onto collisionMask
        renderSpheresToCollisionMask();

        renderSpheresToHeatMask();

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

        tmpMapBuffer.begin();
        batch.setShader(nullSphereFilterShader);
        nullSphereFilterShader.begin();
        Texture collisionTexture = collisionMaskRegion.getTexture();
        collisionTexture.bind(1);
        nullSphereMaskingShader.setUniformi("u_maskTexture", 1);
        Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
        batch.begin();
        batch.setProjectionMatrix(mapCam.combined);
        batch.draw(levelBufferRegion, 0, 0, map.getWidth(), map.getHeight());
        batch.end();
        nullSphereFilterShader.end();
        batch.setShader(null);
        tmpMapBuffer.end();

        batch.setShader(heatEffectShader);
        heatEffectShader.begin();
        Texture heatTexture = heatEffectBufferRegion.getTexture();
        heatTexture.bind(1);
        heatEffectShader.setUniformi("u_heatTexture", 1);
        noiseTexture.bind(2);
        heatEffectShader.setUniformi("u_noiseTexture", 2);
        heatEffectShader.setUniformf("u_gameTime", gameTime);
        heatEffectShader.setUniformf("u_noiseTexSize", (float)noiseTexture.getWidth(), (float)noiseTexture.getHeight());
        heatEffectShader.setUniformf("u_backgroundHeat", backgroundHeat);
        Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(tmpMapBufferRegion, 0, 0, map.getWidth(), map.getHeight());
        batch.end();
        heatEffectShader.end();
        batch.setShader(null);
    }

    void renderLayers(int start, int end) {
        levelBuffer.begin();
        batch.setProjectionMatrix(mapCam.combined);
        for (int layer = start; layer <= end; layer++) {
            map.renderLayer(layer, mapCam);
            batch.begin();
            for (Entity e : entityLayers.get(layer)) {
                if(!e.isDestroyed()) {
                    e.renderWithWrapAround(batch);
                    if (HackathonApp.DEBUG_HITBOXES) {
                        if (DEBUG_PARTICLE == null) {
                            DEBUG_PARTICLE = Assets.manager.get("particle.png", Texture.class);
                        } else if (DEBUG_PARTICLE != null) {
                            float sz = 10f;
                            batch.draw(DEBUG_PARTICLE, e.pos.x - sz / 2f, e.pos.y - sz / 2f, sz, sz);
                            batch.draw(DEBUG_PARTICLE, e.pos.x + e.size.x - sz / 2f, e.pos.y - sz / 2f, sz, sz);
                            batch.draw(DEBUG_PARTICLE, e.pos.x + e.size.x - sz / 2f, e.pos.y + e.size.y - sz / 2f, sz, sz);
                            batch.draw(DEBUG_PARTICLE, e.pos.x - sz / 2f, e.pos.y + e.size.y - sz / 2f, sz, sz);
                        }
                    }
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
