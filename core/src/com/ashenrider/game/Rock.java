package com.ashenrider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Rock extends Projectile {
	
	// frames per particle spawn
	private int particleTimer = 0;
	
	public Rock(Vector2 initPosition, Vector2 direction, int playerNumber) {
		super(initPosition, direction, playerNumber);
		size = new Vector2(16, 16);
		img = new Texture("gun_projectile.png");
		
		// you cannot hurt yourself within the first fraction of a second that a shot is fired
		SPEED = 700.0f;
		speed = direction.nor().scl(SPEED);
		//GRAVITY = -300.0f;
		falls = true;
		
		BOUNCE_WALLS = true;
		BOUNCE_CEILING = true;
		groundBounces = 2;
		ELASTICITY = 0.7f;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		Particle p = new GunParticle(getCentre(), speed.cpy().scl(-1), 1.0f, 0.4f);
		scene.addEntity(p, Scene.PARTICLE_LAYER);
	}
}
