package com.ashenrider.game;

import java.util.HashMap;

import com.ashenrider.game.Input.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity {

	SpriteBatch batch;
	Texture img;

	float JUMP = 400.0f;
	float ACCEL = 800.0f;
	float MAX_SPEED = 400.0f; // while not dashing
	
	float DASH_SPEED = 800.0f;
	float DASH_TIME = 0.125f;
	boolean dashing = false;
	float dashTime = 0.0f;
	
	int width = 32;
	int height = 32;
	
	// current
	int airJumps = 0;
	int airDashes = 0;
	
	// maximum
	static int NUM_AIRJUMPS = 2;
	static int NUM_AIRDASHES = 1;
	
	public enum Action {
		MOVE, AIM_HORIZONTAL, AIM_VERTICAL, JUMP, SHOOT, DASH
	}
	
	HashMap<Action, InputButton> buttonMap;
	HashMap<Action, InputAxis> axisMap;
	
	HashMap<Action, Float> cooldown;
	HashMap<Action, Float> maxCooldown;
	
	public int number;
	
	public Player(int playerNumber, Vector2 initPosition, InputAxis moveAxis, InputButton jump, InputButton shoot, InputButton dash) {
		super(initPosition);

		number = playerNumber;
		batch = new SpriteBatch();
		img = new Texture("p" + (playerNumber % 3) + ".png");
		
		buttonMap = new HashMap<Action, InputButton>();
		axisMap = new HashMap<Action, InputAxis>();
		
		axisMap.put(Action.MOVE, moveAxis);
		buttonMap.put(Action.JUMP, jump);
		buttonMap.put(Action.SHOOT, shoot);
		buttonMap.put(Action.DASH, dash);
		
		cooldown = new HashMap<Action, Float>();
		cooldown.put(Action.JUMP, 0.0f);
		cooldown.put(Action.SHOOT, 0.0f);
		cooldown.put(Action.DASH, 0.0f);
		maxCooldown = new HashMap<Action, Float>();
		maxCooldown.put(Action.JUMP, 0.5f);
		maxCooldown.put(Action.SHOOT, 0.3f);
		maxCooldown.put(Action.DASH, DASH_TIME + 0.5f);
	}
	
	@Override
	public void update(float dt) {
		// jump
		if (buttonMap.get(Action.JUMP).isDown() && cooldown.get(Action.JUMP) == 0.0f && (onGround || airJumps > 0)) {
			speed.y = JUMP;
			cooldown.put(Action.JUMP, maxCooldown.get(Action.JUMP));
			if (!onGround) {
				airJumps--;
			}
		}
		// shoot
		if (buttonMap.get(Action.SHOOT).isDown() && cooldown.get(Action.SHOOT) == 0.0f) {
			Projectile p = new Projectile(pos.cpy(), speed.cpy(), number);
			cooldown.put(Action.SHOOT, maxCooldown.get(Action.SHOOT));
			scene.addEntity(p);
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
		speed.x = speed.x + ACCEL * axisMap.get(Action.MOVE).getValue() * dt;
		// update cooldowns
		for (Action action : cooldown.keySet()) {
			cooldown.put(action, Math.max(0.0f, cooldown.get(action) - dt));
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
	}

    @Override
    public void handleCollision(Map map) {
        Vector2 pen = map.getLeastPenetration(speed, pos, pos.cpy().add(new Vector2(img.getWidth(), img.getHeight())));
        onGround = false;
        
        if (pen.len() > 0) {
            if (Math.abs(pen.x) > Math.abs(pen.y)) {
                pos.add(new Vector2(0, pen.y));
                speed.y = 0;
            } else {
                pos.add(new Vector2(pen.x, 0));
                speed.x = 0;
            }
            
            // moved upwards from a collision
            if (pen.y > 0) {
            	onGround = true;
            	airJumps = NUM_AIRJUMPS;
            	airDashes = NUM_AIRDASHES;
            }

            pen = map.getLeastPenetration(speed, pos, pos.cpy().add(new Vector2(img.getWidth(), img.getHeight())));
            pos.add(pen);

            if (pen.x != 0) {
                speed.x = 0;
            }

            if (pen.y != 0) {
                speed.y = 0;
            }
        }
    }
	
	@Override
	public void render() {
		batch.begin();
		batch.draw(img, pos.x, pos.y);
		batch.end();
	}
}
