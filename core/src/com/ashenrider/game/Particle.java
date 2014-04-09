package com.ashenrider.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Particle extends Entity {
	Texture img;
	Color color;
	
	public float SPEED = 0.0f; // 150.0f
	
	float scale;
	float maxScale;
	
	private float duration;
	private float maxDuration;
	
	public Particle(Vector2 initPosition, Vector2 direction, float scale, float lifespan) {
		super(initPosition);
		if (direction.isZero()) {
			speed = new Vector2(0, SPEED);
		} else {
			speed = direction.scl(SPEED / direction.len());
		}
		// the particle image is a white circle, so tint it to a variable color
		color = new Color(0.0f, 0.0f, 0.5f, 1.0f);

		img = new Texture("particle.png");
		size = new Vector2(img.getWidth() * scale, img.getHeight() * scale);
		
		maxScale = scale;
		falls = false;
		
		maxDuration = lifespan;
		duration = lifespan;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		color.a = Math.min(1.0f, duration*2/maxDuration);
		duration -= dt;
		if (duration <= 0) {
			destroy();
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		scale = maxScale * Math.min(1.0f, duration/maxDuration);
		float width = img.getWidth()*scale;
		float height = img.getHeight()*scale;
		batch.setColor(color);
		batch.draw(img, pos.x - width/2, pos.y - height/2, width, height);
		batch.setColor(Color.WHITE);
	}
	
}
