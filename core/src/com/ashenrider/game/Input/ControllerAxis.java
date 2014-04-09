package com.ashenrider.game.Input;

import com.badlogic.gdx.controllers.Controller;

public class ControllerAxis extends InputAxis {
    private Controller controller;
    private int axis;

    public ControllerAxis(Controller controller, int axis) {
        this.controller = controller;
        this.axis = axis;
    }

    @Override
    public float getValue() {
        return controller.getAxis(axis);
    }
}
