package com.ashenrider.game.Input;

import com.badlogic.gdx.Gdx;
public class KeyboardAxis extends InputAxis {
    int keyLess;
    int keyMore;
    public KeyboardAxis(int key1, int key2) {
        keyLess = key1;
        keyMore = key2;
    }

    @Override
    public float getValue() {
        float val = 0.0f;
        if (Gdx.input.isKeyPressed(keyLess)) val -= 1.0f;
        if (Gdx.input.isKeyPressed(keyMore)) val += 1.0f;
        return val;
    }
}