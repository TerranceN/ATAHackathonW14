package com.ashenrider.game.Input;

public abstract class InputButton {
    private boolean wasPressed = false;
    private boolean isPressed = false;
    // return a boolean if a key is pressed
    public abstract boolean isDown();
    
    public void update() {
        wasPressed = isPressed;
        isPressed = isDown();
    }
    
    public boolean justPressed() {
        return (isPressed && !wasPressed);
    }
    
    public boolean justReleased() {
        return (!isPressed && wasPressed);
    }
}    
