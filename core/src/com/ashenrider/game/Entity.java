package com.ashenrider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	Vector2 pos;
	Vector2 speed;
	float GRAVITY = -200.0f;
	boolean falling = true;
	
	public Entity() {
		pos = new Vector2(100,100);
		speed = new Vector2(0,0);
	}
	
	public void update(float dt) {
		if (falling) {
			speed.add(0, GRAVITY * dt);
		}

		pos.add(speed.cpy().scl(dt));
		if (pos.y < 0) {
			pos.y = pos.y + Gdx.graphics.getHeight();
		}
	}
	
	public abstract void render();
}
