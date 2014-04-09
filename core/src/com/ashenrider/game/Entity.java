package com.ashenrider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	Scene scene;
	int layer;
	
	Vector2 pos;
	Vector2 speed;
	Vector2 size;
	
	float GRAVITY = -600.0f;
	boolean falls = true;
	boolean onGround = false;
	boolean collides = true;
	
	boolean destroyed = false;
	
	public Entity(Vector2 initPosition) {
		pos = initPosition;
		speed = new Vector2(0,0);
	}
	
	public void update(float dt) {
		//if (falls && !onGround) {
		if (falls) {
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

    public boolean handleCollision(Map map) {
        return false;
    }
	
    public Rectangle getBounds() {
    	return new Rectangle(pos.x ,pos.y, size.x, size.y);
    }
    
    public Vector2 getCentre() {
    	return new Vector2(pos.x + size.x/2, pos.y + size.y/2);
    }
	public abstract void render();
	
	public void destroy() {
		// remove it from the current layer's list of entities
		scene.entityLayers.get(layer).remove(this);
		// and mark it to be removed from the list of all entities later
		destroyed = true;
	}
}
