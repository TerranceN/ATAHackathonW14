package com.ashenrider.game;

import java.util.HashMap;
import java.util.Random;

import com.ashenrider.game.Input.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Player extends Entity {
	float scale = 2.0f;
	float animationTime = 0.0f;

    int lives = 10;

	float JUMP = 400.0f;
	float ACCEL = 4000.0f;
	float MAX_SPEED = 400.0f; // while not dashing
	
	float DASH_SPEED = 800.0f;
	float DASH_TIME = 0.125f;
	float dashTime = 0.0f;

    boolean jumpPressedLastFrame = false;
	
	// current
	int airJumps = 0;
	int airDashes = 0;
	
	// maximum
	static int NUM_AIRJUMPS = 0;
	static int NUM_AIRDASHES = 1;
	
	// state logic (for animation and some logic)
	//onGround
	boolean facingRight = true;
	boolean dashing = false;
	boolean landed = false;
	// landDuration / animation
	// standing
	// falling
    boolean onWall = false;
    int wallDir = 0;

	float minLandedSpeed = -20.0f;
	float landedTime = 0.0f;
	
	public enum Action {
		MOVE, AIM_HORIZONTAL, AIM_VERTICAL, JUMP, SHOOT, DASH
	}
	
	HashMap<Action, InputButton> buttonMap;
	HashMap<Action, InputAxis> axisMap;
	
	HashMap<Action, Float> cooldown;
	HashMap<Action, Float> maxCooldown;
		
	public int number;
	
	private int NUM_FRAMES = 2;
	private float RUNNING_FRAME_DURATION = 0.12f;
	
	private Animation walkLeftAnimation;
    private Animation walkRightAnimation;
	private TextureRegion landLeft;
	private TextureRegion landRight;
    
	public Player(int playerNumber, Vector2 initPosition, InputAxis moveAxis, InputAxis aimH, InputAxis aimV, InputButton jump, InputButton shoot, InputButton dash) {
		super(initPosition);

		number = playerNumber;
		//img = new Texture("p" + (playerNumber % 3) + ".png");
		// 16x32 regions
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("pack/animations.atlas"));
		TextureRegion[] leftFrames = new TextureRegion[2];
		TextureRegion[] rightFrames = new TextureRegion[2];
		for (int i=0; i<NUM_FRAMES; i++) {
			rightFrames[i] = atlas.findRegion("p" + (playerNumber % 3) + "-" + i);
			leftFrames[i] = new TextureRegion(rightFrames[i]);
            leftFrames[i].flip(true, false);
		}
		landLeft = leftFrames[1];
		landRight = rightFrames[1];
		walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, leftFrames);
		walkRightAnimation = new Animation(RUNNING_FRAME_DURATION, rightFrames);

		size = new Vector2(landLeft.getRegionWidth() * scale, landLeft.getRegionHeight() * scale);
		//size = new Vector2(img.getWidth(), img.getHeight());
		
		buttonMap = new HashMap<Action, InputButton>();
		axisMap = new HashMap<Action, InputAxis>();
		
		axisMap.put(Action.MOVE, moveAxis);
		axisMap.put(Action.AIM_HORIZONTAL, aimH);
		axisMap.put(Action.AIM_VERTICAL, aimV);

		buttonMap.put(Action.JUMP, jump);
		buttonMap.put(Action.SHOOT, shoot);
		buttonMap.put(Action.DASH, dash);
		
		cooldown = new HashMap<Action, Float>();
		cooldown.put(Action.JUMP, 0.0f);
		cooldown.put(Action.SHOOT, 0.0f);
		cooldown.put(Action.DASH, 0.0f);
		maxCooldown = new HashMap<Action, Float>();
		maxCooldown.put(Action.JUMP, 0.0f);
		maxCooldown.put(Action.SHOOT, 0.3f);
		maxCooldown.put(Action.DASH, DASH_TIME + 0.5f);
	}

    public int getLives() {
        return lives;
    }
	public TextureRegion getSprite() {
		TextureRegion frame;
		if (landed) {
			frame = facingRight ? landLeft : landRight;
		} else {
			// pass a time to animation to get the right frame
			frame = facingRight ? walkRightAnimation.getKeyFrame(animationTime, true) : walkLeftAnimation.getKeyFrame(animationTime, true);
		}
		return frame;
	}

	@Override
	public void update(float dt) {
		// jump
		if (!jumpPressedLastFrame && buttonMap.get(Action.JUMP).isDown() && cooldown.get(Action.JUMP) == 0.0f) {
            if (onGround || airJumps > 0) {
                speed.y = JUMP;
                cooldown.put(Action.JUMP, maxCooldown.get(Action.JUMP));
                if (!onGround) {
                    airJumps--;
                }
            } else if (onWall) {
                boolean nextToWall = false;
                if (wallDir == 1) {
                    Vector2 tileCoords = scene.map.getTileCoords(pos.cpy());
                    nextToWall = scene.map.levelLayer.getCell((int)(tileCoords.x - 1.0f), (int)tileCoords.y) != null;
                } else if (wallDir == -1) {
                    Vector2 tileCoords = scene.map.getTileCoords(pos.cpy().add(size));
                    nextToWall = scene.map.levelLayer.getCell((int)(tileCoords.x), (int)tileCoords.y) != null;
                }
                if (nextToWall) {
                    speed.y = JUMP * 1.25f;
                    speed.x = wallDir * JUMP * 0.75f;
                    onWall = false;
                }
            }
		}
		// shoot
		if (buttonMap.get(Action.SHOOT).isDown() && cooldown.get(Action.SHOOT) == 0.0f) {
			Vector2 dir = new Vector2(axisMap.get(Action.AIM_HORIZONTAL).getValue(), axisMap.get(Action.AIM_VERTICAL).getValue());
			Projectile p = new Rock(pos.cpy(), dir, number);
			cooldown.put(Action.SHOOT, maxCooldown.get(Action.SHOOT));
			scene.addEntity(p, Scene.SHOT_LAYER);
		}
		// dash quickly in the currently facing direction
		// (or in the aimed direction)
		if (buttonMap.get(Action.DASH).isDown() && cooldown.get(Action.DASH) == 0.0f && airDashes > 0) {
			speed.scl(DASH_SPEED/Math.abs(speed.x), 0.0f);
			cooldown.put(Action.DASH, maxCooldown.get(Action.DASH));
			airDashes--;
			falls = false;
			dashing = true;
			dashTime = DASH_TIME;
		}
		// accelerate
        float move = axisMap.get(Action.MOVE).getValue();
        float xAcceleration = ACCEL * move;
        if (Math.abs(move) > 0.25) {
            if (!onGround) {
                xAcceleration *= 0.25f;
            }
            if (!landed) {
            	speed.x = speed.x + xAcceleration * dt;
            }
        }
		// update cooldowns
		for (Action action : cooldown.keySet()) {
			cooldown.put(action, Math.max(0.0f, cooldown.get(action) - dt));
		}
		// landed stun
		if (landed) {
			landedTime = Math.max(0.0f, landedTime - dt);
			if (landedTime == 0.0f) {
				landed = false;
			}
		}
		// update duration of persistent effects
		if (dashing) {
			dashTime = Math.max(0.0f, dashTime - dt);
			if (dashTime == 0) {
				dashing = false;
				falls = true;
			}
		} else {
			// max speed and friction
			if (Math.abs(speed.x) > MAX_SPEED) {
				speed.scl(MAX_SPEED / Math.abs(speed.x), 1.0f);
			}
			if (Math.abs(speed.y) > MAX_SPEED) {
				speed.scl(1.0f, MAX_SPEED / Math.abs(speed.y));
			}
		}
		super.update(dt);

        if (onGround && !dashing) {
            speed.x -= Math.min(1, dt * 10) * speed.x;
        }
        if (speed.x != 0) {
        	facingRight = speed.x > 0;
        }
        animationTime += dt;
        jumpPressedLastFrame = buttonMap.get(Action.JUMP).isDown();
	}

    public int sign(float x) {
        if (x < 0) {
            return -1;
        } else if (x > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public void collisionCheck(Map map) {
        Vector2 pen = map.getLeastPenetration(speed, pos, pos.cpy().add(size));

        if (pen.len() > 0) {
            if (pen.y != 0 && (pen.x == 0 || Math.abs(pen.x) > Math.abs(pen.y))) {
                pos.add(new Vector2(0, pen.y));
                speed.y = 0;
                if (pen.y > 0) {
                    onGround = true;
                    airJumps = NUM_AIRJUMPS;
                	airDashes = NUM_AIRDASHES;
                }
            } else {
                pos.add(new Vector2(pen.x, 0));
                speed.x = 0;
                onWall = true;
                wallDir = sign(pen.x);
            }
        }
    }
    
    @Override
    public void handleCollision(Map map) {
    	float velY = speed.y; // speed before collision
        onGround = false;
		collisionCheck(map);
		collisionCheck(map);

        if (onGround) {
            onWall = false;
        }
        // if falling quickly and hit the ground
        if (onGround && velY < minLandedSpeed) {
        	landed = true;
        	landedTime = 0.125f;
        	// spawn some smoke particles
        	Random rand = new Random();
        	for (int i = 0; i<5; i++) {
        		float pX = pos.x + rand.nextFloat() * size.x;
        		float pY = pos.y + rand.nextFloat() * 5;
        		float pSize = 0.3f + rand.nextFloat() * 0.5f;
        		float pDuration = 0.2f + rand.nextFloat() * 0.6f;
        		float pSpeed = 20 + rand.nextFloat() * 120;
        		float pAngle = rand.nextFloat() * (float) Math.PI;
        		Particle p = new Particle(new Vector2(pX,pY), new Vector2(pSpeed,0).setAngleRad(pAngle), pSize, pDuration);
        		scene.addEntity(p, Scene.PARTICLE_LAYER);
        	}
        }
    }
	
	@Override
	public void render(SpriteBatch batch) {
		TextureRegion frame = getSprite();
		batch.draw(frame, pos.x, pos.y, frame.getRegionWidth() * scale, frame.getRegionHeight() * scale);
	}

    public void onShot(Projectile projectile) {
        int playerId = projectile.getShotBy();
        scene.reportPlayerDeath(scene.players.get(playerId), this);
        lives--;
        scene.respawnPlayer(this, true);
    }
}
