package com.ashenrider.game.Input;

import com.badlogic.gdx.Gdx;

public class MouseButton extends InputButton{
    int button;
    public MouseButton(int keycode) {
        button = keycode;
    }

    @Override
    public boolean isDown() {
        return Gdx.input.isButtonPressed(button);
    }        
}
