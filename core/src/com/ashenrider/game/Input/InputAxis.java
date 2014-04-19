package com.ashenrider.game.Input;

public abstract class InputAxis {    
    // return a value between -1 and 1
    private float oldValue = 0.0f;
    private float newValue = 0.0f;
    public abstract float getValue();
    
    public void update() {
        oldValue = newValue;
        newValue = getValue();
    }
    
    public float getDelta() {
        return newValue - oldValue;
    }
}