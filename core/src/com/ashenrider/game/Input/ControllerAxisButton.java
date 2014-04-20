package com.ashenrider.game.Input;

import com.badlogic.gdx.controllers.Controller;

public class ControllerAxisButton extends InputButton {
    private Controller controller;
    private int axis;
    private boolean countsPressure;
    private float threshold;

    public ControllerAxisButton() {
        
    }
    
    public ControllerAxisButton(Controller controller, int axis) {
        this(controller, axis, 0.5f, true);
    }

    public ControllerAxisButton(Controller controller, int axis, boolean moreIsPressed) {
        this(controller, axis, 0.5f, moreIsPressed);
    }

    public ControllerAxisButton(Controller controller, int axis, float threshold) {
        this(controller, axis, threshold, true);
    }

    public ControllerAxisButton(Controller controller, int axis, float threshold, boolean moreIsPressed) {
        this.controller = controller;
        this.axis = axis;
        this.threshold = threshold;
        this.countsPressure = moreIsPressed;
    }

    @Override
    public boolean isDown() {
       return ( (controller.getAxis(axis) > threshold && countsPressure) || (controller.getAxis(axis) < threshold && !countsPressure) );
    }
}
