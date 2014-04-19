package com.ashenrider.game.Input;

import java.util.HashMap;

import com.ashenrider.game.Entities.Player.Action;
import com.badlogic.gdx.Gdx;

public class PlayerInput {

    private HashMap<Action, InputButton> buttonMap;
    private HashMap<Action, InputAxis> axisMap;
    
    public PlayerInput() {
        buttonMap = new HashMap<Action, InputButton>();
        axisMap = new HashMap<Action, InputAxis>();
    }
    
    public PlayerInput(InputAxis moveH, InputAxis moveV, InputAxis aimH, InputAxis aimV, InputButton jump, InputButton shoot, InputButton swing, InputButton dash, InputButton nullSphere) {
        buttonMap = new HashMap<Action, InputButton>();
        axisMap = new HashMap<Action, InputAxis>();
        setInputs(moveH, moveV, aimH, aimV, jump, shoot, swing, dash, nullSphere);
    }
    
    public void setInputs(InputAxis moveH, InputAxis moveV, InputAxis aimH, InputAxis aimV, InputButton jump, InputButton shoot, InputButton swing, InputButton dash, InputButton nullSphere) {
        axisMap.put(Action.MOVE_HORIZONTAL, moveH);
        axisMap.put(Action.MOVE_VERTICAL, moveV);
        axisMap.put(Action.AIM_HORIZONTAL, aimH);
        axisMap.put(Action.AIM_VERTICAL, aimV);

        buttonMap.put(Action.JUMP, jump);
        buttonMap.put(Action.SHOOT, shoot);
        buttonMap.put(Action.SWING, swing);
        buttonMap.put(Action.DASH, dash);
        buttonMap.put(Action.NULL_SPHERE, nullSphere);
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
            Gdx.app.log("INPUT", "Action (" + action + " ) is null");
        }
        return buttonMap.get(action);
    }
    public InputAxis getAxis(Action action) {
        if (axisMap.get(action) == null) {
            Gdx.app.log("INPUT", "Action (" + action + " ) is null");
        }
        return axisMap.get(action);
    }
}
