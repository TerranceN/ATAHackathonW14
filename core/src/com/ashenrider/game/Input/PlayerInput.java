package com.ashenrider.game.Input;

import java.util.HashMap;

import com.ashenrider.game.Entities.Player.Action;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public class PlayerInput {
    public static final String KEYBOARD_ONLY = "Keyboard";
    public static final String KEYBOARD_AND_MOUSE = "KeyboardMouse";
    
    private HashMap<Action, InputButton> buttonMap;
    private HashMap<Action, InputAxis> axisMap;
    public String name;
    
    public PlayerInput() {
        name = "";
        buttonMap = new HashMap<Action, InputButton>();
        axisMap = new HashMap<Action, InputAxis>();
    }
    
    public PlayerInput(String controllerName) {
        name = controllerName;
        buttonMap = new HashMap<Action, InputButton>();
        axisMap = new HashMap<Action, InputAxis>();
    }
    
    public PlayerInput(String controllerName, InputAxis moveH, InputAxis moveV, InputAxis aimH, InputAxis aimV, InputButton jump, InputButton shoot, InputButton swing, InputButton dash, InputButton nullSphere, InputButton menuOk, InputButton menuBack) {
        name = controllerName;
        buttonMap = new HashMap<Action, InputButton>();
        axisMap = new HashMap<Action, InputAxis>();
        setInputs(moveH, moveV, aimH, aimV, jump, shoot, swing, dash, nullSphere, menuOk, menuBack);
    }
    
    public void setInputs(InputAxis moveH, InputAxis moveV, InputAxis aimH, InputAxis aimV, InputButton jump, InputButton shoot, InputButton swing, InputButton dash, InputButton nullSphere, InputButton menuOk, InputButton menuBack) {
        axisMap.put(Action.MOVE_HORIZONTAL, moveH);
        axisMap.put(Action.MOVE_VERTICAL, moveV);
        axisMap.put(Action.AIM_HORIZONTAL, aimH);
        axisMap.put(Action.AIM_VERTICAL, aimV);

        buttonMap.put(Action.JUMP, jump);
        buttonMap.put(Action.SHOOT, shoot);
        buttonMap.put(Action.SWING, swing);
        buttonMap.put(Action.DASH, dash);
        buttonMap.put(Action.NULL_SPHERE, nullSphere);

        buttonMap.put(Action.MENU_OK, menuOk);
        buttonMap.put(Action.MENU_BACK, menuBack);
    }

    public void update() {
        // update input objects (to keep justPressed info accurate)
        // this should be called once per frame (by HackathonApp)
        for (InputButton btn : buttonMap.values()) {
            if (btn != null) {
                btn.update();
            }
        }
        for (InputAxis axis : axisMap.values()) {
            if (axis != null) {
                axis.update();
            }
        }
    }
    
    public void setInput(Action action, InputAxis axis) {
        axisMap.put(action, axis);
    }

    public void setInput(Action action, InputButton button) {
        buttonMap.put(action, button);
    }

    public InputButton getButton(Action action) {
        if (buttonMap.get(action) == null) {
            Gdx.app.log("INPUT", "Action (" + action + ") is null");
        }
        return buttonMap.get(action);
    }
    public InputAxis getAxis(Action action) {
        if (axisMap.get(action) == null) {
            Gdx.app.log("INPUT", "Action (" + action + ") is null");
        }
        return axisMap.get(action);
    }
    
    // the json parser converts the Actions to strings, since it doesn't try custom type deserialization for keys of maps
    // fix the result
    public void jsonFix() {
        HashMap<Action, InputButton> oldBtnMap = buttonMap;
        HashMap<Action, InputAxis> oldAxisMap = axisMap;
        buttonMap = new HashMap<Action, InputButton>();
        axisMap = new HashMap<Action, InputAxis>();
        for (Object o : oldBtnMap.keySet()) {
            if (o instanceof Action) {
                buttonMap.put((Action) o, oldBtnMap.get(o));
            } else { //String
                buttonMap.put(Action.valueOf(Action.class, String.valueOf(o)), oldBtnMap.get(o));
            }
        }
        for (Object o : oldAxisMap.keySet()) {
            if (o instanceof Action) {
                axisMap.put((Action) o, oldAxisMap.get(o));
            } else { //String
                axisMap.put(Action.valueOf(Action.class, String.valueOf(o)), oldAxisMap.get(o));
            }
        }
    }
}
