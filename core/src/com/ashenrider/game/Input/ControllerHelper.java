package com.ashenrider.game.Input;

import com.badlogic.gdx.controllers.Controller;

public class ControllerHelper {

    //TODO: Make this not stupid.

    //Buttons
    public static final int A_BTN = 0;
    public static final int B_BTN = 1;
    public static final int RIGHT_TRIGGER = 2;
    public static final int LEFT_TRIGGER = 3;

    //Axis
    public static final int LEFT_STICK_HORIZONTAL = 0;
    public static final int LEFT_STICK_VERTICAL = 1;
    public static final int RIGHT_STICK_HORIZONTAL = 2;
    public static final int RIGHT_STICK_VERTICAL = 3;

    public static InputButton getButton(Controller controller, int uid) {
        if(controller.getName().toLowerCase().contains("microsoft")) {
            switch(uid) {
                case A_BTN:
                    return new ControllerButton(controller, Xbox.BTN_A);
                case B_BTN:
                    return new ControllerButton(controller, Xbox.BTN_B);
                case RIGHT_TRIGGER:
                    return new ControllerAxisButton(controller, Xbox.AXIS_RIGHT_TRIGGER);
                case LEFT_TRIGGER:
                    return new ControllerAxisButton(controller, Xbox.AXIS_LEFT_TRIGGER);
            }
        } else if (controller.getName().toLowerCase().contains("playstation")) {
            switch(uid) {
                case A_BTN:
                    return new ControllerButton(controller, Sony.BTN_A);
                case B_BTN:
                    return new ControllerButton(controller, Sony.BTN_B);
                case RIGHT_TRIGGER:
                    return new ControllerButton(controller, Sony.BTN_RIGHT_TRIGGER);
                case LEFT_TRIGGER:
                    return new ControllerButton(controller, Sony.BTN_LEFT_TRIGGER);
            }
        }
        return null;
    }

    public static InputAxis getAxis(Controller controller, int uid) {
        if(controller.getName().toLowerCase().contains("microsoft")) {
            switch(uid) {
                case LEFT_STICK_HORIZONTAL:
                    return new ControllerAxis(controller, Xbox.AXIS_LEFT_STICK_HORIZONTAL);
                case LEFT_STICK_VERTICAL:
                    return new ControllerAxis(controller, Xbox.AXIS_LEFT_STICK_VERTICAL, true);
                case RIGHT_STICK_HORIZONTAL:
                    return new ControllerAxis(controller, Xbox.AXIS_RIGHT_STICK_HORIZONTAL);
                case RIGHT_STICK_VERTICAL:
                    return new ControllerAxis(controller, Xbox.AXIS_RIGHT_STICK_VERTICAL, true);
            }
        } else if (controller.getName().toLowerCase().contains("playstation")) {
            switch(uid) {
                case LEFT_STICK_HORIZONTAL:
                    return new ControllerAxis(controller, Sony.AXIS_LEFT_STICK_HORIZONTAL);
                case LEFT_STICK_VERTICAL:
                    return new ControllerAxis(controller, Sony.AXIS_LEFT_STICK_VERTICAL, true);
                case RIGHT_STICK_HORIZONTAL:
                    return new ControllerAxis(controller, Sony.AXIS_RIGHT_STICK_HORIZONTAL);
                case RIGHT_STICK_VERTICAL:
                    return new ControllerAxis(controller, Sony.AXIS_RIGHT_STICK_VERTICAL, true);
            }
        }
        return null;
    }
}
