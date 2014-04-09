package com.ashenrider.game;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity {

	SpriteBatch batch;
	Texture img;

	float JUMP = 200.0f;
	
	public enum Action {
		LEFT, RIGHT, JUMP, SHOOT
	}
	
	HashMap<Action, Integer> keyMap;
	
	public Player(Vector2 initPosition, int keyL, int keyR, int keyJump, int keyShoot) {
		super(initPosition);
		batch = new SpriteBatch();
		img = new Texture("player.png");
		
		keyMap = new HashMap<Action, Integer>();
		keyMap.put(Action.LEFT, keyL);
		keyMap.put(Action.RIGHT, keyR);
		keyMap.put(Action.JUMP, keyJump);
		keyMap.put(Action.SHOOT, keyShoot);
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		//if (Gdx.input.isKeyPressed(keyMap.get(Action.JUMP)) && onGround) {
		if (Gdx.input.isKeyPressed(keyMap.get(Action.JUMP))) {
			speed.y = JUMP;
		}
	}
	
	@Override
	public void render() {
		batch.begin();
		batch.draw(img, pos.x, pos.y);
		batch.end();
	}
}
