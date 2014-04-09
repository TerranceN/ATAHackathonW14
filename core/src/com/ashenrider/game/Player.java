package com.ashenrider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player extends Entity {

	SpriteBatch batch;
	Texture img;
	
	public Player() {
		super();
		batch = new SpriteBatch();
		img = new Texture("player.png");
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
	}
	
	@Override
	public void render() {
		batch.begin();
		batch.draw(img, pos.x, pos.y);
		batch.end();
	}
}
