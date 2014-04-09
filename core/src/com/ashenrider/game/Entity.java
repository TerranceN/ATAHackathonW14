package com.ashenrider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	Scene scene;
	
	Vector2 pos;
	Vector2 speed;
	float GRAVITY = -200.0f;
	boolean falls = true;
	boolean onGround = false;
	boolean collides = true;
	
	public Entity(Vector2 initPosition) {
		pos = initPosition;
		speed = new Vector2(0,0);
	}
	
	public void update(float dt) {
		if (falls && !onGround) {
			speed.add(0, GRAVITY * dt);
		}

		pos.add(speed.cpy().scl(dt));
		if (pos.y < 0) {
			pos.y = pos.y + Gdx.graphics.getHeight();
		} else if (pos.y > Gdx.graphics.getHeight()) {
			pos.y = pos.y - Gdx.graphics.getHeight();
		}
		if (pos.x < 0) {
			pos.x = pos.x + Gdx.graphics.getWidth();
		} else if (pos.x > Gdx.graphics.getWidth()) {
			pos.x = pos.x - Gdx.graphics.getWidth();
		}
	}
	
	public abstract void render();
}
