package com.ashenrider.game;

import java.util.HashMap;

import com.ashenrider.game.Input.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity {

	SpriteBatch batch;
	Texture img;

	float JUMP = 200.0f;
	float ACCEL = 400.0f;
	
	int width = 32;
	int height = 32;
	
	public enum Action {
		MOVE, AIM_HORIZONTAL, AIM_VERTICAL, JUMP, SHOOT
	}
	
	HashMap<Action, InputButton> buttonMap;
	HashMap<Action, InputAxis> axisMap;
	
	public Player(int playerNumber, Vector2 initPosition, InputAxis moveAxis, InputButton jump, InputButton shoot) {
		super(initPosition);
		batch = new SpriteBatch();
		img = new Texture("p" + (playerNumber % 3) + ".png");
		
		buttonMap = new HashMap<Action, InputButton>();
		axisMap = new HashMap<Action, InputAxis>();
		
		axisMap.put(Action.MOVE, moveAxis);
		buttonMap.put(Action.JUMP, jump);
		buttonMap.put(Action.SHOOT, shoot);
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		//if (Gdx.input.isKeyPressed(keyMap.get(Action.JUMP)) && onGround) {
		if (buttonMap.get(Action.JUMP).isDown()) {
			speed.y = JUMP;
		}
		speed.x = speed.x + ACCEL * axisMap.get(Action.MOVE).getValue() * dt;
	}
	
	@Override
	public void render() {
		batch.begin();
		batch.draw(img, pos.x, pos.y);
		batch.end();
	}
}
