package com.ashenrider.game;

import java.util.HashMap;
import java.util.Random;

import com.ashenrider.game.Input.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Player extends Entity {
    private static final float INVULNERABILITY_LENGTH = 2.0f;

	float scale = 2.0f;
	float animationTime = 0.0f;

    int lives = 10;

	float JUMP = 550.0f;
	float AIR_JUMP = 400.0f;
	float ACCEL = 4000.0f;
	float MAX_MOVE_SPEED = 300.0f; // while not dashing
    float MAX_FALL_SPEED = 800.0f;
	
	float DASH_SPEED = 800.0f;
	float DASH_TIME = 0.125f;
	float dashTime = 0.0f;

    boolean jumpPressedLastFrame = false;

    boolean nullSphereEnabled = false;
	
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

	float minLandedSpeed = -400.0f;
	float landedTime = 0.0f;

    float invulnerableTime;
	
	public enum Action {
		MOVE, AIM_HORIZONTAL, AIM_VERTICAL, JUMP, SHOOT, DASH, NULL_SPHERE
	}
	
	HashMap<Action, InputButton> buttonMap;
	HashMap<Action, InputAxis> axisMap;
	
	HashMap<Action, Float> cooldown;
	HashMap<Action, Float> maxCooldown;
		
	public int number;
	
	// every image is 70 wide and the middle 20 are the hitbox
	int animationOffset = -27;
	private float RUNNING_FRAME_DURATION = 0.06f;
	private float JUMP_FRAME_DURATION = 0.12f;
	private float LAND_FRAME_DURATION = 0.08f;
	private float IDLE_FRAME_DURATION = 0.16f;

	private Animation walkLeftAnimation;
    private Animation walkRightAnimation;
	private Animation jumpLeftAnimation;
    private Animation jumpRightAnimation;
	private Animation idleLeftAnimation;
    private Animation idleRightAnimation;
	private Animation landLeftAnimation;
	private Animation landRightAnimation;
	private TextureRegion wallHugLeft;
	private TextureRegion wallHugRight;
    
	public Player(int playerNumber, Vector2 initPosition, InputAxis moveAxis, InputAxis aimH, InputAxis aimV, InputButton jump, InputButton shoot, InputButton dash, InputButton nullSphere) {
		super(initPosition);
		number = playerNumber;
		//img = new Texture("p" + (playerNumber % 3) + ".png");
		// 16x32 regions
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("pack/animations.atlas"));
		// death 1-10
		// (unused)
		// run 1-5
		TextureRegion[] leftFrames = new TextureRegion[5];
		TextureRegion[] rightFrames = new TextureRegion[5];
		for (int i=0; i<5; i++) {
			rightFrames[i] = atlas.findRegion("p" + (playerNumber % 3) + "/run-0" + (i+1));
			leftFrames[i] = new TextureRegion(rightFrames[i]);
            rightFrames[i].flip(true, false);
		}
		walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, leftFrames);
		walkRightAnimation = new Animation(RUNNING_FRAME_DURATION, rightFrames);
		// stand 1-5
		leftFrames = new TextureRegion[5];
		rightFrames = new TextureRegion[5];
		for (int i=0; i<5; i++) {
			rightFrames[i] = atlas.findRegion("p" + (playerNumber % 3) + "/stand-0" + (i+1));
			leftFrames[i] = new TextureRegion(rightFrames[i]);
			rightFrames[i].flip(true, false);
		}
		idleLeftAnimation = new Animation(IDLE_FRAME_DURATION, leftFrames);
		idleRightAnimation = new Animation(IDLE_FRAME_DURATION, rightFrames);
		// jump 1-3
		leftFrames = new TextureRegion[3];
		rightFrames = new TextureRegion[3];
		for (int i=0; i<3; i++) {
			rightFrames[i] = atlas.findRegion("p" + (playerNumber % 3) + "/jump-0" + (i+1));
			leftFrames[i] = new TextureRegion(rightFrames[i]);
			rightFrames[i].flip(true, false);
		}
		jumpLeftAnimation = new Animation(JUMP_FRAME_DURATION, leftFrames);
		jumpRightAnimation = new Animation(JUMP_FRAME_DURATION, rightFrames);
		// land 1-5
		leftFrames = new TextureRegion[4];
		rightFrames = new TextureRegion[4];
		for (int i=0; i<4; i++) {
			rightFrames[i] = atlas.findRegion("p" + (playerNumber % 3) + "/land-0" + (i+2));
			leftFrames[i] = new TextureRegion(rightFrames[i]);
            rightFrames[i].flip(true, false);
		}
		landLeftAnimation = new Animation(LAND_FRAME_DURATION, leftFrames);
		landRightAnimation = new Animation(LAND_FRAME_DURATION, rightFrames);
		// wallhug 1
		wallHugRight = atlas.findRegion("p0/wallhug-01");
		wallHugLeft = new TextureRegion(wallHugRight);
		wallHugRight.flip(true, false);
		
		// approximate size of the player
		size = new Vector2(16.0f, 48.0f).scl(scale);
		
		buttonMap = new HashMap<Action, InputButton>();
		axisMap = new HashMap<Action, InputAxis>();
		
		axisMap.put(Action.MOVE, moveAxis);
		axisMap.put(Action.AIM_HORIZONTAL, aimH);
		axisMap.put(Action.AIM_VERTICAL, aimV);

		buttonMap.put(Action.JUMP, jump);
		buttonMap.put(Action.SHOOT, shoot);
		buttonMap.put(Action.DASH, dash);
		buttonMap.put(Action.NULL_SPHERE, nullSphere);
		
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
		// pass a time to animation to get the right frame
		if (landed) {
			frame = facingRight ? landRightAnimation.getKeyFrame(animationTime, true)
								: landLeftAnimation.getKeyFrame(animationTime, true);
		} else if (onWall) {
			frame = facingRight ? wallHugRight : wallHugLeft;
		} else if (!onGround) {
			frame = facingRight ? jumpRightAnimation.getKeyFrame(animationTime, true)
								: jumpLeftAnimation.getKeyFrame(animationTime, true);
		} else if (Math.abs(speed.x) > 20.0f) { 
			frame = facingRight ? walkRightAnimation.getKeyFrame(animationTime, true)
								: walkLeftAnimation.getKeyFrame(animationTime, true);
		} else {
			frame = facingRight ? idleRightAnimation.getKeyFrame(animationTime, true)
								: idleLeftAnimation.getKeyFrame(animationTime, true);
		}
		return frame;
	}

	@Override
	public void update(float dt) {
		// jump
		nullSphereEnabled = buttonMap.get(Action.NULL_SPHERE).isDown();
		if (!jumpPressedLastFrame && buttonMap.get(Action.JUMP).isDown() && cooldown.get(Action.JUMP) == 0.0f) {
            if (onGround || airJumps > 0) {
                speed.y = JUMP;
                cooldown.put(Action.JUMP, maxCooldown.get(Action.JUMP));
                if (!onGround) {
                    airJumps--;
                }
            } else if (onWall) {
                speed = new Vector2(wallDir * 0.75f, 1.25f).scl(AIR_JUMP);
                cooldown.put(Action.JUMP, maxCooldown.get(Action.JUMP));
                onWall = false;
            }
		}
		// if was on wall, am I still on the wall?
		if (onWall) {
            Random rand = new Random();
            float pX = pos.x + size.x * (0.5f - 0.5f * wallDir);
            float pY = pos.y + (0.25f + 0.5f * rand.nextFloat()) * size.y;
            float pSize = 0.2f + rand.nextFloat() * 0.4f;
            float pDuration = 0.4f + rand.nextFloat();
            float pSpeed = (40 + rand.nextFloat() * 200);
            Particle p = new Particle(new Vector2(pX,pY), new Vector2(wallDir ,0), pSpeed, pSize, pDuration, new Color(1.0f,1.0f, 1.0f, 1.0f));
            scene.addEntity(p, Scene.PARTICLE_LAYER);
            
            boolean nextToWall = false;
            if (wallDir == 1) {
                Vector2 tileCoords = scene.map.getTileCoords(pos.cpy().add(new Vector2(0, size.y/4)));
                nextToWall = scene.map.levelLayer.getCell((int)(tileCoords.x - 1.0f), (int)tileCoords.y) != null;
            } else if (wallDir == -1) {
                Vector2 tileCoords = scene.map.getTileCoords(pos.cpy().add(new Vector2(size.x, size.y/4)));
                nextToWall = scene.map.levelLayer.getCell((int)(tileCoords.x), (int)tileCoords.y) != null;
            }
            onWall = nextToWall;
		}
		// shoot
		if (buttonMap.get(Action.SHOOT).isDown() && cooldown.get(Action.SHOOT) == 0.0f) {
			Vector2 dir = new Vector2(axisMap.get(Action.AIM_HORIZONTAL).getValue(), axisMap.get(Action.AIM_VERTICAL).getValue());
			Projectile p = new Rock(getCentre(), dir, number);
			cooldown.put(Action.SHOOT, maxCooldown.get(Action.SHOOT));
			scene.addEntity(p, Scene.SHOT_LAYER);
		}
		// dash quickly in the currently facing direction
		// (or in the aimed direction)
		if (buttonMap.get(Action.DASH).isDown() && cooldown.get(Action.DASH) == 0.0f && airDashes > 0) {
			speed = new Vector2(DASH_SPEED * Math.signum(speed.x), 0.0f);
			cooldown.put(Action.DASH, maxCooldown.get(Action.DASH));
			airDashes--;
			falls = false;
			dashing = true;
			animationTime = 0.0f;
			dashTime = DASH_TIME;

            Random rand = new Random();
            for (int i = 0; i<8; i++) {
                float pX = pos.x + size.x * 0.5f + (rand.nextFloat() * size.x * 0.5f * ((speed.x < 0) ? -1 : 1) );
                float pY = pos.y + rand.nextFloat() * size.y;
                float pSize = 0.2f + rand.nextFloat() * 0.4f;
                float pDuration = 0.4f + rand.nextFloat();
                float pSpeed = 40 + rand.nextFloat() * 200;
                Particle p = new Particle(new Vector2(pX,pY), new Vector2( (speed.x > 0) ? -1 : 1 ,0), pSpeed, pSize, pDuration, new Color(1.0f,1.0f, 1.0f, 1.0f));
                scene.addEntity(p, Scene.PARTICLE_LAYER);
            }
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
			// max speed
			if (Math.abs(speed.x) > MAX_MOVE_SPEED) {
				speed.scl(MAX_MOVE_SPEED / Math.abs(speed.x), 1.0f);
			}
			if (Math.abs(speed.y) > MAX_FALL_SPEED) {
				speed.scl(1.0f, MAX_FALL_SPEED / Math.abs(speed.y));
			}
		}
        if(invulnerableTime > 0.0f) {
            invulnerableTime = (invulnerableTime - dt > 0) ? invulnerableTime - dt : 0.0f;
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
        Vector2 pen = map.getLeastPenetration(speed.cpy(), pos.cpy(), pos.cpy().add(size));

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
                facingRight = wallDir > 0;
            }
        }
    }
    
    @Override
    public void handleCollision(Map map) {
    	float velY = speed.y; // speed before collision
        onGround = false;
        if (!nullSphereEnabled) {
            collisionCheck(map);
            collisionCheck(map);

            if (onGround) {
                onWall = false;
            }
            // if falling quickly and hit the ground
            if (onGround && velY < minLandedSpeed) {
                landed = true;
                animationTime = 0.0f;
                landedTime = LAND_FRAME_DURATION * 4;
                // spawn some smoke particles
                Random rand = new Random();
                for (int i = 0; i<5; i++) {
                    float pX = pos.x + rand.nextFloat() * size.x;
                    float pY = pos.y + rand.nextFloat() * 5;
                    float pSize = 0.2f + rand.nextFloat() * 0.2f;
                    float pDuration = 0.3f + rand.nextFloat() * 0.9f;
                    float pSpeed = 20 + rand.nextFloat() * 100;
                    float pAngle = rand.nextFloat() * (float) Math.PI;
                    Particle p = new Particle(new Vector2(pX,pY), new Vector2(1,0).setAngleRad(pAngle), pSpeed, pSize, pDuration, new Color(1.0f,1.0f, 1.0f, 1.0f));
                    scene.addEntity(p, Scene.PARTICLE_LAYER);
                }
            }
        }
    }
	
	@Override
	public void render(SpriteBatch batch) {
		TextureRegion frame = getSprite();
        if(invulnerableTime > 0.0f) {
            batch.setColor(1.0f, 1.0f, 1.0f - (invulnerableTime%0.3f) * 1.5f, 0.4f + (invulnerableTime%0.3f)*2.0f);
        }

		batch.draw(frame, pos.x + (animationOffset * scale), pos.y, frame.getRegionWidth() * scale, frame.getRegionHeight() * scale);

        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
	}

    public boolean onShot(Projectile projectile) {
        if(invulnerableTime <= 0.0f) {
            int playerId = projectile.getShotBy();
            scene.reportPlayerDeath(scene.players.get(playerId), this);
            lives--;
            scene.addEntity(new PlayerBody(number, pos, speed.cpy(), 5.0f), Scene.PLAYER_LAYER);
            scene.respawnPlayer(this, true);
            onInvulnerable(INVULNERABILITY_LENGTH);
            return true;
        }
        return false;
    }

    public void onInvulnerable(float time) {
        this.invulnerableTime = time;
    }
}
