package com.ashenrider.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class GunParticle extends Particle {
	static Texture GUN_TRAIL = null;
	
	public GunParticle(Vector2 initPosition, Vector2 direction, float scale, float lifespan) {
		super(initPosition, direction, 0.0f, scale, lifespan, new Color(1,1,1,1));
		Random rand = new Random();
		float pSpeed = 120 + rand.nextFloat() * 500;
		float spread = 0.6f;
		speed = direction.nor().rotateRad((rand.nextFloat()-0.5f) * spread).scl(pSpeed);
		if (GUN_TRAIL == null) {
			GUN_TRAIL = new Texture("gun_trail.png");
		}
		img = GUN_TRAIL;
		size = new Vector2(img.getWidth() * scale, img.getHeight() * scale);
	}
}
