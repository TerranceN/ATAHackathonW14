package com.ashenrider.game.Input;

import com.badlogic.gdx.controllers.Controller;

public class ControllerButton extends InputButton {
    private Controller controller;
    private int button;

    public ControllerButton(Controller controller, int button) {
        this.controller = controller;
        this.button = button;
    }

    @Override
    public boolean isDown() {
        return controller.getButton(button);
    }
}
