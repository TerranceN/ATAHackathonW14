package com.ashenrider.game.Input;

import com.badlogic.gdx.Gdx;

public class KeyboardButton extends InputButton{
	int key;
	public KeyboardButton(int keycode) {
		key = keycode;
	}

	@Override
	public boolean isDown() {
		return Gdx.input.isKeyPressed(key);
		
	}		
}
