package com.ashenrider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Projectile extends Entity {
	SpriteBatch batch;
	Texture img;
	
	private int shotBy;
	private float friendlyFireTimer;
	
	public float SPEED = 400.0f;
	
	public Projectile(Vector2 initPosition, Vector2 direction, int playerNumber) {
		super(initPosition);
		if (direction.isZero()) {
			speed = new Vector2(0, SPEED);
		} else {
			speed = direction.scl(SPEED / direction.len());
		}
		shotBy = playerNumber;
		batch = new SpriteBatch();
		img = new Texture("projectile.png");
		
		// you cannot hurt yourself within the first fraction of a second that a shot is fired
		friendlyFireTimer = 0.5f;
		falls = false;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		friendlyFireTimer -= dt;
	}
	
	//collision logic:
	// for each player
	//     if player is not shotBy or friendlyFire < 0:
	//			check collision

	@Override
	public void render() {
		batch.begin();
		batch.draw(img, pos.x, pos.y);
		batch.end();
	}

}
