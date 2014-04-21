package com.ashenrider.game.AI;

import com.ashenrider.game.Input.InputButton;

public class AIButton extends InputButton{
    
    private boolean pressed = false;
    public AIButton() {
    }
    
    @Override
    public void update() {
        super.update();
        pressed = false;
    }
    
    public void press() {
        pressed = true;
    }

    @Override
    public boolean isDown() {
        return pressed;
    }        
}
