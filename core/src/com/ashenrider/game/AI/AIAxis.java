package com.ashenrider.game.AI;

import com.ashenrider.game.Input.InputAxis;

public class AIAxis extends InputAxis {
    
    private float val;
    public AIAxis() {
    }
    
    public void setValue(float newVal) {
        val = newVal;
    }
    
    @Override
    public float getValue() {
        return val;
    }
}