package com.ashenrider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Rock extends Projectile {
	static Texture GUN_PROJECTILE = null;
	
	// frames per particle spawn
	private int particleTimer = 0;
	
	public Rock(Vector2 initPosition, Vector2 direction, int playerNumber) {
		super(initPosition, direction, playerNumber);
		size = new Vector2(16, 16);
		if (GUN_PROJECTILE == null) {
			GUN_PROJECTILE = new Texture("gun_projectile.png");
		}
		img = GUN_PROJECTILE;
		
		// you cannot hurt yourself within the first fraction of a second that a shot is fired
		SPEED = 1400.0f;
		speed = direction.nor().scl(SPEED);
		//GRAVITY = -300.0f;
		falls = false;
		
		BOUNCE_WALLS = false;
		BOUNCE_CEILING = false;
		groundBounces = 0;
		ELASTICITY = 0.7f;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		Particle p = new GunParticle(getCentre(), speed.cpy().scl(-1), 1.0f, 0.4f);
		scene.addEntity(p, Scene.PARTICLE_LAYER);
	}
}
