package com.ashenrider.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.ashenrider.game.AI.AILogic;
import com.ashenrider.game.Entities.Player;
import com.ashenrider.game.Entities.Player.Action;
import com.ashenrider.game.Input.ControllerAxis;
import com.ashenrider.game.Input.ControllerAxisButton;
import com.ashenrider.game.Input.ControllerButton;
import com.ashenrider.game.Input.ControllerHelper;
import com.ashenrider.game.Input.InputButton;
import com.ashenrider.game.Input.KeyboardAxis;
import com.ashenrider.game.Input.KeyboardButton;
import com.ashenrider.game.Input.MouseAxis;
import com.ashenrider.game.Input.MouseButton;
import com.ashenrider.game.Input.PlayerInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;

public class ControlSchemeManager {
    // libgdx json deserializer converts lists to the libgdx Array type, so I use it here
    public static HashMap<String, Array<PlayerInput>> controlMap;
    public static Json json = null;
    public static final String CONFIG_FILENAME = "config.json";
    
    public static Json jsonParser() {
        Json jsonP = new Json();
        jsonP.addClassTag("mouse", MouseAxis.class);
        jsonP.addClassTag("mouseBtn", MouseButton.class);
        jsonP.addClassTag("key", KeyboardButton.class);
        jsonP.addClassTag("keyPair", KeyboardAxis.class);
        jsonP.addClassTag("controllerBtn", ControllerButton.class);
        jsonP.addClassTag("controllerAxis", ControllerAxis.class);
        jsonP.addClassTag("controllerAxisBtn", ControllerAxisButton.class);
        jsonP.addClassTag("controlScheme", PlayerInput.class);
        jsonP.setTypeName("type");
        return jsonP;
    }
    
    public static void saveControls() {
        if (json == null) {
            json = jsonParser();
        }
        String s = json.prettyPrint(controlMap);
        FileHandle file = Gdx.files.local(CONFIG_FILENAME);
        file.writeString(s, false);
        Gdx.app.log("ControlScheme", "Saved changes to control schemes");
    }
    
    public static ArrayList<PlayerInput> loadControls() {
        if (json == null) {
            json = jsonParser();
        }
        FileHandle file = Gdx.files.local(CONFIG_FILENAME);
        if (file.exists()) {
            String s = file.readString();
            // http://stackoverflow.com/questions/509076/how-do-i-address-unchecked-cast-warnings
            controlMap = json.fromJson(HashMap.class, s);
            // the deserializer converts Actions to strings.
            for (Array<PlayerInput> inputs : controlMap.values()) {
                for (PlayerInput input : inputs) {
                    input.jsonFix();
                }
            }
        } else {
            controlMap = new HashMap<String, Array<PlayerInput>>();
        }
        
        ArrayList<PlayerInput> playerInputs = new ArrayList<PlayerInput>();
        playerInputs.add(getControlScheme(PlayerInput.KEYBOARD_AND_MOUSE, 0));

        boolean controllerDebug = false;
        for(Controller controller : Controllers.getControllers()) {
            // need to update controllerHelper in order to support more buttons, or rethink the control scheme
            playerInputs.add(getControlScheme(controller, 0));

            if (controllerDebug) {
                Gdx.app.log("NAME", controller.getName());
                controller.addListener(new ControllerListener() {
                    @Override
                    public void connected(Controller controller) {

                    }

                    @Override
                    public void disconnected(Controller controller) {

                    }

                    @Override
                    public boolean buttonDown(Controller controller, int i) {
                        Gdx.app.log("Controller", "BUTTON: " + i);
                        return false;
                    }

                    @Override
                    public boolean buttonUp(Controller controller, int i) {
                        return false;
                    }

                    @Override
                    public boolean axisMoved(Controller controller, int i, float v) {
                        Gdx.app.log("Controller", "AXIS: " + i + " || " + v);
                        return false;
                    }

                    @Override
                    public boolean povMoved(Controller controller, int i, PovDirection povDirection) {
                        return false;
                    }

                    @Override
                    public boolean xSliderMoved(Controller controller, int i, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean ySliderMoved(Controller controller, int i, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean accelerometerMoved(Controller controller, int i, Vector3 vector3) {
                        return false;
                    }
                });
            }
        }
        // uncontrollable players
        if (HackathonApp.FILLER_PLAYERS) {
            for (int i = playerInputs.size(); i < 4; i++) {
                // the keyboard - no mouse control scheme
                //playerInputs.add(getControlScheme(PlayerInput.KEYBOARD_ONLY, 0));
                playerInputs.add(new AILogic());
            }
        }
        return playerInputs;
    }

    
    // load control schemes or generate new default control schemes
    public static PlayerInput getControlScheme(String controllerName, int index) {
        if (controlMap.containsKey(controllerName)) {
            Array<PlayerInput> schemes = controlMap.get(controllerName);
            return schemes.get(index % schemes.size);
        } else {
            // default control schemes
            PlayerInput input;
            if (controllerName == PlayerInput.KEYBOARD_AND_MOUSE) {
                input = new PlayerInput(PlayerInput.KEYBOARD_AND_MOUSE,
                        new KeyboardAxis(Keys.A, Keys.D),
                        new KeyboardAxis(Keys.S, Keys.W),
                        null,
                        null,
                        new KeyboardButton(Keys.W),
                        new MouseButton(Buttons.LEFT),
                        new MouseButton(Buttons.RIGHT),
                        new KeyboardButton(Keys.E),
                        new KeyboardButton(Keys.SHIFT_LEFT),
                        new KeyboardButton(Keys.ENTER),
                        new KeyboardButton(Keys.ESCAPE));
            } else if (controllerName == PlayerInput.KEYBOARD_ONLY) {
                // not really meant to be used, but it allows for filler players to exist
                input = new PlayerInput(PlayerInput.KEYBOARD_ONLY,
                        new KeyboardAxis(Keys.LEFT, Keys.RIGHT),
                        new KeyboardAxis(Keys.DOWN, Keys.UP),
                        new KeyboardAxis(Keys.NUMPAD_4, Keys.NUMPAD_6),
                        new KeyboardAxis(Keys.NUMPAD_5, Keys.NUMPAD_8),
                        new KeyboardButton(Keys.UP),
                        new KeyboardButton(Keys.ENTER),
                        new KeyboardButton(Keys.ENTER),
                        new KeyboardButton(Keys.SHIFT_RIGHT),
                        new KeyboardButton(Keys.CONTROL_RIGHT),
                        new KeyboardButton(Keys.NUMPAD_9),
                        new KeyboardButton(Keys.NUMPAD_7));
            } else {
                return null;
            }
            Gdx.app.log("ControlScheme", "New control scheme for: " + controllerName);
            Array<PlayerInput> schemes = new Array<PlayerInput>();
            schemes.add(input);
            controlMap.put(controllerName, schemes);
            saveControls();
            return input;
        }
    }
    
    public static PlayerInput getControlScheme(Controller controller, int index) {
        PlayerInput input = getControlScheme(controller.getName(), index);
        if (input != null) {
            return input;
        } else {
            // controller configuration if not specified in config file
            input = new PlayerInput(controller.getName(),
                    ControllerHelper.getAxis(controller, ControllerHelper.LEFT_STICK_HORIZONTAL),
                    ControllerHelper.getAxis(controller, ControllerHelper.LEFT_STICK_VERTICAL),
                    ControllerHelper.getAxis(controller, ControllerHelper.RIGHT_STICK_HORIZONTAL),
                    ControllerHelper.getAxis(controller, ControllerHelper.RIGHT_STICK_VERTICAL),
                    ControllerHelper.getButton(controller, ControllerHelper.A_BTN),
                    ControllerHelper.getButton(controller, ControllerHelper.RIGHT_TRIGGER),
                    ControllerHelper.getButton(controller, ControllerHelper.RIGHT_TRIGGER),
                    ControllerHelper.getButton(controller, ControllerHelper.LEFT_TRIGGER),
                    ControllerHelper.getButton(controller, ControllerHelper.B_BTN),
                    ControllerHelper.getButton(controller, ControllerHelper.A_BTN),
                    ControllerHelper.getButton(controller, ControllerHelper.B_BTN));
            Gdx.app.log("ControlScheme", "New control scheme for: " + controller.getName());
            Array<PlayerInput> schemes = new Array<PlayerInput>();
            schemes.add(input);
            controlMap.put(controller.getName(), schemes);
            saveControls();
            return input;
        }
    }
}
