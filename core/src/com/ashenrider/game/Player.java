package com.ashenrider.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.Set;

import com.ashenrider.game.Buffs.Buff;
import com.ashenrider.game.Buffs.Buff.Status;
import com.ashenrider.game.Buffs.DashBuff;
import com.ashenrider.game.Buffs.SpeedBuff;
import com.ashenrider.game.Buffs.StatusBuff;
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
    private static final float INVULNERABILITY_LENGTH = 2.25f;

	float scale = 2.0f;
	float animationTime = 0.0f;
	int frameCount = 0;

    int lives = 3;
    boolean alive = false;
    float spawnDelay = 0.0f;

	float JUMP = 550.0f;
	float AIR_JUMP = 400.0f;
	float ACCEL = 4000.0f;
	float MAX_MOVE_SPEED = 300.0f; // while not dashing
    float MAX_FALL_SPEED = 800.0f;
	
	float DASH_SPEED = 800.0f;
	float DASH_TIME = 0.13f;
	
	float MAX_NULL_TIME = 0.7f;
	float nullTime = 0.0f;
    boolean nullSphereEnabled = false;

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
	// landDuration / animation
	// standing
	// falling
    boolean onWall = false;
    int wallDir = 0;

	float minLandedSpeed = -400.0f;

    public float speedMult = 1.0f;
	
	public enum Action {
		MOVE, AIM_HORIZONTAL, AIM_VERTICAL, JUMP, SHOOT, DASH, NULL_SPHERE
	}
	
	HashMap<Action, InputButton> buttonMap;
	HashMap<Action, InputAxis> axisMap;
	
	HashMap<Action, Float> cooldown;
	HashMap<Action, Float> maxCooldown;
	HashMap<Buff.Status, Buff> statusBuffs;
	ArrayList<Buff> buffs;
		
	// playerNumber is an ID for 
	public int number;
	ArrayList<Boolean> texCollidedBeforeUpdate;
	
	public int NOT_A_PLAYER = -1;

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

	private Texture head;
	public Color playerColor;
    
	public Player(int playerNumber, Vector2 initPosition, InputAxis moveAxis, InputAxis aimH, InputAxis aimV, InputButton jump, InputButton shoot, InputButton dash, InputButton nullSphere) {
		super(initPosition);
		number = playerNumber;
		switch (number % 4) {
			case 0:
				playerColor = new Color(0x951A1AFF);
				break;
			case 1:
				playerColor = new Color(0x372D8EFF);
				break;
			case 2:
				playerColor = new Color(0xA6F400FF);
				break;
			case 3:
				playerColor = new Color(0xFFD200FF);
				break;
		}
		// 16x32 regions
		TextureAtlas atlas = Assets.manager.get("pack/animations.atlas", TextureAtlas.class);
		// death 1-10
		// (unused)
		// run 1-5
		TextureRegion[] leftFrames = new TextureRegion[5];
		TextureRegion[] rightFrames = new TextureRegion[5];
		for (int i=0; i<5; i++) {
			rightFrames[i] = atlas.findRegion("p" + (playerNumber % 4) + "/run-0" + (i+1));
			leftFrames[i] = new TextureRegion(rightFrames[i]);
            rightFrames[i].flip(true, false);
		}
		walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, leftFrames);
		walkRightAnimation = new Animation(RUNNING_FRAME_DURATION, rightFrames);
		// stand 1-5
		leftFrames = new TextureRegion[5];
		rightFrames = new TextureRegion[5];
		for (int i=0; i<5; i++) {
			rightFrames[i] = atlas.findRegion("p" + (playerNumber % 4) + "/stand-0" + (i+1));
			leftFrames[i] = new TextureRegion(rightFrames[i]);
			rightFrames[i].flip(true, false);
		}
		idleLeftAnimation = new Animation(IDLE_FRAME_DURATION, leftFrames);
		idleRightAnimation = new Animation(IDLE_FRAME_DURATION, rightFrames);
		// jump 1-3
		leftFrames = new TextureRegion[3];
		rightFrames = new TextureRegion[3];
		for (int i=0; i<3; i++) {
			rightFrames[i] = atlas.findRegion("p" + (playerNumber % 4) + "/jump-0" + (i+1));
			leftFrames[i] = new TextureRegion(rightFrames[i]);
			rightFrames[i].flip(true, false);
		}
		jumpLeftAnimation = new Animation(JUMP_FRAME_DURATION, leftFrames);
		jumpRightAnimation = new Animation(JUMP_FRAME_DURATION, rightFrames);
		// land 1-5
		leftFrames = new TextureRegion[4];
		rightFrames = new TextureRegion[4];
		for (int i=0; i<4; i++) {
			rightFrames[i] = atlas.findRegion("p" + (playerNumber % 4) + "/land-0" + (i+2));
			leftFrames[i] = new TextureRegion(rightFrames[i]);
            rightFrames[i].flip(true, false);
		}
		landLeftAnimation = new Animation(LAND_FRAME_DURATION, leftFrames);
		landRightAnimation = new Animation(LAND_FRAME_DURATION, rightFrames);
		// wallhug 1
		wallHugRight = atlas.findRegion("p" + (playerNumber % 4) + "/wallhug-01");
		wallHugLeft = new TextureRegion(wallHugRight);
		wallHugRight.flip(true, false);
		
		head = Assets.manager.get("head" + playerNumber % 4 + ".png", Texture.class);
		
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
		cooldown.put(Action.NULL_SPHERE, 0.0f);
		maxCooldown = new HashMap<Action, Float>();
		maxCooldown.put(Action.JUMP, 0.0f);
		maxCooldown.put(Action.SHOOT, 0.8f);
		maxCooldown.put(Action.DASH, DASH_TIME + 0.35f);
		maxCooldown.put(Action.NULL_SPHERE, 1.0f);
		buffs = new ArrayList<Buff>();
		statusBuffs = new HashMap<Buff.Status, Buff>();
	}

    public ArrayList<Vector2> getPoints() {
        ArrayList<Vector2> points = new ArrayList<Vector2>();

        //feet
        points.add(pos);
        points.add(pos.cpy().add(new Vector2(size.x, 0)));
        // top of player
        points.add(pos.cpy().add(size));
        points.add(pos.cpy().add(new Vector2(0, size.y)));

        return points;
    }

    public void recordTexCollision() {
        ArrayList<Vector2> points = getPoints();
        texCollidedBeforeUpdate = new ArrayList<Boolean>();

        for (Vector2 p : points) {
            float modX = p.x % scene.map.getWidth();
            float modY = p.y % scene.map.getHeight();

            if (modX < 0) {
                modX += scene.map.getWidth();
            }
            if (modY < 0) {
                modY += scene.map.getHeight();
            }

            int textureValue = scene.getCollisionMaskValueAtPoint(modX, modY);
            // 0 is normal collission, >0 is masked value
            texCollidedBeforeUpdate.add(textureValue != 0);
        }
    }

    public void texCollisionResolve() {
        ArrayList<Vector2> points = getPoints();

        for (int i = 0; i < points.size(); i++) {
        	// for each corner of the player that was previously inside a null mask
            if (texCollidedBeforeUpdate.get(i)) {
                float modX = points.get(i).x % scene.map.getWidth();
                float modY = points.get(i).y % scene.map.getHeight();

                if (modX < 0) {
                    modX += scene.map.getWidth();
                }
                if (modY < 0) {
                    modY += scene.map.getHeight();
                }

                int textureValue = scene.getCollisionMaskValueAtPoint(modX, modY);

                // 0 is normal collission, this point was previously in the null mask
                // if this point is in a wall, then the player is (at least partially) inside the wall.
                // to make this more forgiving, require that all of the points in a small plus or box shape are inside a wall
                // otherwise just return to normal collision logic and the player will "pop" out a bit
                if (textureValue == 0 && scene.map.isInsideLevel(modX, modY)) {
                    scene.addEntity(new Blood(getCentre(), new Vector2(1,0)), Scene.PARTICLE_LAYER);
                	killPlayer(NOT_A_PLAYER, DeathSources.WALL);
                	break;
                }
            }
        }
    }

    public int getLives() {
        return lives;
    }
    
    public Texture getHead() {
    	return head;
    }
    
	public TextureRegion getSprite() {
		TextureRegion frame;
		// pass a time to animation to get the right frame
		if (hasStatus(Buff.Status.LAND_STUN)) {
			frame = facingRight ? landRightAnimation.getKeyFrame(animationTime, false)
								: landLeftAnimation.getKeyFrame(animationTime, false);
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
		if (alive) {
			// jump
			if (!nullSphereEnabled && buttonMap.get(Action.NULL_SPHERE).isDown() && cooldown.get(Action.NULL_SPHERE) == 0.0f) {
				nullSphereEnabled = true;
				nullTime = MAX_NULL_TIME;
			} else if (nullSphereEnabled) {
				nullTime -= dt;
				if (nullTime <= 0 || !buttonMap.get(Action.NULL_SPHERE).isDown()) {
					nullTime = 0;
					nullSphereEnabled = false;
		            cooldown.put(Action.NULL_SPHERE, maxCooldown.get(Action.NULL_SPHERE));
				}
			}
			
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
	                airDashes = NUM_AIRDASHES;
	            }
			}
			// if was on wall, am I still on the wall?
			if (onWall) {
				if (frameCount % 3 == 0) {
		            Random rand = new Random();
		            float pX = pos.x + size.x * (0.5f - 0.5f * wallDir);
		            float pY = pos.y + (0.25f + 0.5f * rand.nextFloat()) * size.y;
		
		            float smokeThreshold = 150f;
		
		            if (speed.y < -smokeThreshold) {
		                AirSmoke smoke = new AirSmoke(new Vector2(pX,pY), 90, wallDir == 1);
		                scene.addEntity(smoke, Scene.PARTICLE_LAYER);
		            } else if (speed.y > smokeThreshold) {
		                AirSmoke smoke = new AirSmoke(new Vector2(pX,pY), 270, wallDir == -1);
		                scene.addEntity(smoke, Scene.PARTICLE_LAYER);
		            }
				}
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
				if (dir.isZero()) {
					// if dir is 0 the projectile would sit still in space
					dir.x = 1;
				}
				Projectile p = new Fireball(getCentre(), dir, number);
				cooldown.put(Action.SHOOT, maxCooldown.get(Action.SHOOT));
				scene.addEntity(p, Scene.SHOT_LAYER);
			}
			// dash quickly in the currently facing direction
			// (or in the aimed direction)
			if (buttonMap.get(Action.DASH).isDown() && cooldown.get(Action.DASH) == 0.0f && airDashes > 0) {
		        float xAxis = axisMap.get(Action.MOVE).getValue();
		        float dir = speed.x > 0 ? 1 : -1;
		        if (Math.abs(xAxis) > 0.25) {
		        	dir = Math.signum(xAxis);
		        }
		        if (onWall) {
		        	dir = wallDir;
		        }
		        float a = dir == 1 ? 180 : 00;
	            scene.addEntity(new AirSmoke(getCentre(), a + 45, false), Scene.PARTICLE_LAYER);
	            scene.addEntity(new AirSmoke(getCentre(), a -45, true), Scene.PARTICLE_LAYER);
	            scene.addEntity(new AirSmoke(getCentre(), a + 22.5f, false), Scene.PARTICLE_LAYER);
	            scene.addEntity(new AirSmoke(getCentre(), a -22.5f, true), Scene.PARTICLE_LAYER);
	
				speed = new Vector2(DASH_SPEED * dir, 0.0f);
				cooldown.put(Action.DASH, maxCooldown.get(Action.DASH));
				airDashes--;
				falls = false;
				addBuff(new DashBuff(this, DASH_TIME));
				animationTime = 0.0f;
			}
			// accelerate
	        float move = axisMap.get(Action.MOVE).getValue();
	        float xAcceleration = ACCEL * move * speedMult;
	        if (Math.abs(move) > 0.25) {
	            if (!onGround) {
	                xAcceleration *= 0.25f;
	            }
	            if (!hasStatus(Buff.Status.LAND_STUN)) {
	            	speed.x = speed.x + xAcceleration * dt;
	            }
	        }
			if (!hasStatus(Buff.Status.DASHING)){
				// max speed
				if (Math.abs(speed.x) > MAX_MOVE_SPEED * speedMult) {
					speed.scl(MAX_MOVE_SPEED * speedMult / Math.abs(speed.x), 1.0f);
				}
				if (Math.abs(speed.y) > MAX_FALL_SPEED * speedMult) {
					speed.scl(1.0f, MAX_FALL_SPEED * speedMult / Math.abs(speed.y));
				}
				if (onGround) {
					speed.x -= Math.min(1, dt * 10) * speed.x;
				}
	        }
	        updateBuffs(dt);
	        
	        // movement
			super.update(dt);
	
	        if (speed.x != 0) {
	        	facingRight = speed.x > 0;
	        }
		} else {
			if (lives > 0) {
				spawnDelay -= dt;
				if (spawnDelay <= 0) {
					alive = true;
			    	onInvulnerable(INVULNERABILITY_LENGTH);
			        scene.addEntity(new RespawnParticle(this), Scene.PARTICLE_LAYER);
				}
			}
		}
		// cooldown / status effects that can happen while alive or dead happen here
		frameCount++;
        animationTime += dt;
        jumpPressedLastFrame = buttonMap.get(Action.JUMP).isDown();

		// update cooldowns
		for (Action action : cooldown.keySet()) {
			cooldown.put(action, Math.max(0.0f, cooldown.get(action) - dt));
		}
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

    public void normalLevelCollision(Map map) {
    	float velY = speed.y; // speed before collision

        collisionCheck(map);
        collisionCheck(map);

        if (pos.x + size.x > map.getWidth()) {
            pos.x -= map.getWidth();
            collisionCheck(map);
            collisionCheck(map);
            pos.x += map.getWidth();
        }

        if (pos.y + size.y > map.getHeight()) {
            pos.y -= map.getHeight();
            collisionCheck(map);
            collisionCheck(map);
            pos.y += map.getHeight();
        }

        if (onGround) {
            onWall = false;
        }
        // if falling quickly and hit the ground
        if (onGround && velY < minLandedSpeed) {
        	float duration = LAND_FRAME_DURATION * 4;
            animationTime = 0.0f;
        	addBuff(new StatusBuff(this, duration, Buff.Status.LAND_STUN));
            scene.addEntity(new GroundSmoke(new Vector2(pos.x + size.x/2, pos.y), false), Scene.PARTICLE_LAYER);
            scene.addEntity(new GroundSmoke(new Vector2(pos.x + size.x/2, pos.y), true), Scene.PARTICLE_LAYER);
        }
    }

    @Override
    public void handleCollision(Map map) {
        onGround = false;
        if (!nullSphereEnabled) {
            //
            ArrayList<Vector2> points = getPoints();
            ArrayList<Boolean> texCollision = new ArrayList<Boolean>();
            ArrayList<Boolean> levelCollision = new ArrayList<Boolean>();

            int numNotMasked = 0;
            int numMaskedOrNotWall = 0;

            for (Vector2 p : points) {
                float modX = p.x % map.getWidth();
                float modY = p.y % map.getHeight();

                if (modX < 0) {
                    modX += map.getWidth();
                }
                if (modY < 0) {
                    modY += map.getHeight();
                }

                int textureValue = scene.getCollisionMaskValueAtPoint(modX, modY);
                boolean insideLevel = map.isInsideLevel(modX, modY);

                // 0 is normal collission, >0 is masked value
                if (textureValue == 0) {
                	numNotMasked += 1;
                }

                if (textureValue > 0 || !insideLevel) {
                	// this point is not in the null mask but it is also not in a wall.
                	numMaskedOrNotWall += 1;
                }

                texCollision.add(new Boolean(textureValue == 0));
                levelCollision.add(new Boolean(insideLevel));
            }
            if (numNotMasked == points.size()) {
                // Totally out of the mask, normal collision logic
                //System.out.println("normal collision");
                normalLevelCollision(map);
            } else if (numMaskedOrNotWall == points.size()) {
                // Totally in the mask or not a wall, no collision
                //System.out.println("no collision");
                // do nothing
            } else {
            	// do more complicated collision logic based on the pixel data
                //System.out.println("circle collision");
                int numPoints = 0;
                Vector2 average = new Vector2(0, 0);

                ArrayList<Vector2> collidingPoints = new ArrayList<Vector2>();

                for (int i = 0; i < points.size(); i++) {
                    if (texCollision.get(i) && levelCollision.get(i)) {
                        Vector2 relPos = points.get(i).cpy().sub(pos);
                        average.add(relPos);
                        collidingPoints.add(points.get(i).cpy());
                        numPoints++;
                    }
                }

                if (numPoints == 0) {
                    System.out.println("fancy no collision in circle logic");
                }
                average.scl(1f/numPoints);

                Vector2 centre = size.cpy().scl(0.5f);
                Vector2 diff = centre.cpy().sub(average);

                float oldVel = speed.y;

                if (diff.y > 0) {
                    onGround = true;
                    speed.y = 0;
                }

                if (Math.abs(diff.x) > 0) {
                    speed.x = 0;
                }


                diff.nor();
                float low = 0f;
                float high = 1f;
                boolean growing = true;
                // this seems to be about 5 iterations on average
                // Ideally this is precise enough to allow the player to accelerate enough in the next frame to be on the ground again
                // in a single frame from rest, a player will accelerate to 600 * 1/60 = 10, and then move 10 * 1/60 = 0.16
                // so 0.1 is being used as the margin for now.
                // Alternatively, onGround could be a duration and last a few frames to be more forgiving.
                while (high - low > 0.1f && high < 1000) {
                    boolean stillColliding = false;
                    float mid;
                    if (growing) {
                        mid = high;
                    } else {
                        mid = (high + low) / 2f;
                    }
                    for (Vector2 p : collidingPoints) {
                        Vector2 finishedPoint = p.cpy().add(diff.cpy().scl(mid));
                        if (scene.getCollisionMaskValueAtPoint(finishedPoint.x, finishedPoint.y) == 0 && map.isInsideLevel(finishedPoint.x, finishedPoint.y)) {
                            stillColliding = true;
                            break;
                        }
                    }
                    if (growing) {
                        if (stillColliding) {
                            low = high;
                            high *= 2f;
                        } else {
                            growing = false;
                        }
                    } else {
                        if (stillColliding) {
                            low = mid;
                        } else {
                            high = mid;
                        }
                    }
                }
                pos.add(diff.cpy().scl(high));
            }
        }
    }
	
	@Override
	public void render(SpriteBatch batch) {
		if (alive) {
			TextureRegion frame = getSprite();
	        if (hasStatus(Buff.Status.INVULNERABLE)) {
	            batch.setColor(1.0f, 1.0f, 1.0f - (animationTime % 0.3f) * 1.5f, 0.4f + (animationTime % 0.3f) * 2.0f);
	        }
	
			batch.draw(frame, pos.x + (animationOffset * scale), pos.y, frame.getRegionWidth() * scale, frame.getRegionHeight() * scale);
	        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}

    public boolean killPlayer(int killerID, int deathSource) {
        if(alive && (!hasStatus(Buff.Status.INVULNERABLE) || deathSource == DeathSources.WALL)) {
            lives--;
            alive = false;
            scene.addEntity(new PlayerBody(number, pos, speed.cpy(), 5.0f, facingRight), Scene.PLAYER_LAYER);
            Player murderer = (killerID == NOT_A_PLAYER) ? null : scene.players.get(killerID);
            scene.reportPlayerDeath(murderer, this, deathSource);
            if (lives > 0) {
                scene.respawnPlayer(this, 0.5f);
            } else {
                destroy();
            }
            return true;
        }
        return false;
    }

    public boolean onShot(Projectile projectile) {
    	Vector2 originalPos = getCentre();
        if (killPlayer(projectile.getShotBy(), projectile.getType())) {
            scene.addEntity(new Blood(originalPos, projectile.speed.cpy()), Scene.PARTICLE_LAYER);
            return true;
        }
        return false;
    }

    public void onInvulnerable(float time) {
    	addBuff(new StatusBuff(this, time, Status.INVULNERABLE));
    }

    public void onSpeedBoost(float mult, float time) {
    	addBuff(new SpeedBuff(this, time, mult));
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public boolean isAlive() {
    	return alive;
    }
    
    public boolean isDestroyed() {
        return destroyed;
    }
    
    private void updateBuffs(float dt) {
		for (int i = buffs.size() - 1; i >= 0; i--) {
			Buff b = buffs.get(i);
    		b.update(dt);
    		if (b.finished) {
    			buffs.remove(i);
    		}
    	}
		
		Set<Buff.Status> expired = new HashSet<Buff.Status>();
    	for (Buff.Status status : statusBuffs.keySet()) {
    		Buff b = statusBuffs.get(status);
    		b.update(dt);
    		if (b.finished) {
    			expired.add(status);
    		}
    	}
    	// avoid concurrent modification of statusBuffs	
    	for (Buff.Status status : expired) {
			statusBuffs.remove(status);
    	}
    }
    
    public void addBuff(Buff b) {
    	if (b.status == null) {
        	buffs.add(b);
			b.init();
    	} else if (getBuffDuration(b.status) < b.duration) {
        	statusBuffs.put(b.status, b);
			b.init();
		}
    }
    
    private boolean hasStatus(Buff.Status status) {
    	return statusBuffs.containsKey(status);
    }
    
    private float getBuffDuration(Buff.Status status) {
    	if (hasStatus(status)) {
    		return statusBuffs.get(status).duration;
    	} else {
    		return 0.0f;
    	}
    }
    
    public void clearBuffs() {
    	for (Buff b : buffs) {
    		b.duration = 0.0f;
    	}
    	for (Buff.Status status : statusBuffs.keySet()) {
    		Buff b = statusBuffs.get(status);
    		b.duration = 0.0f;
    	}
    	// they will call their "end" methods and be removed next update
    }
}
