package com.ashenrider.game.Input;

import com.badlogic.gdx.controllers.Controller;

public class ControllerAxis extends InputAxis {
    private Controller controller;
    private int axis;
    private boolean inverted;

    public ControllerAxis() {
        
    }
    public ControllerAxis(Controller controller, int axis, boolean inverted) {
        this.controller = controller;
        this.axis = axis;
        this.inverted = inverted;
    }

    public ControllerAxis(Controller controller, int axis) {
        this(controller, axis, false);
    }

    @Override
    public float getValue() {
        if (inverted) {
            return -controller.getAxis(axis);
        } else {
            return controller.getAxis(axis);
        }
    }
}
