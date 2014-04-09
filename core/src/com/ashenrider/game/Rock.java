package com.ashenrider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Rock extends Projectile {
	
	// frames per particle spawn
	private int particleTimer = 0;
	
	public Rock(Vector2 initPosition, Vector2 direction, int playerNumber) {
		super(initPosition, direction, playerNumber);
		size = new Vector2(16, 16);
		img = new Texture("projectile.png");
		
		// you cannot hurt yourself within the first fraction of a second that a shot is fired
		SPEED = 700.0f;
		//GRAVITY = -300.0f;
		falls = true;
		
		speed = direction.nor().scl(SPEED);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		Particle p = new Particle(getCentre(), speed.cpy().scl(-1), 0.3f, 0.3f);
		scene.addEntity(p, Scene.PARTICLE_LAYER);
	}
}
