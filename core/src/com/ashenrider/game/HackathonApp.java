package com.ashenrider.game;

import com.ashenrider.game.Entities.Player;
import com.ashenrider.game.Input.ControllerAxis;
import com.ashenrider.game.Input.ControllerAxisButton;
import com.ashenrider.game.Input.ControllerButton;
import com.ashenrider.game.Input.ControllerHelper;
import com.ashenrider.game.Input.InputAxis;
import com.ashenrider.game.Input.InputButton;
import com.ashenrider.game.Input.KeyboardAxis;
import com.ashenrider.game.Input.KeyboardButton;
import com.ashenrider.game.Input.MouseButton;
import com.ashenrider.game.Input.PlayerInput;
import com.ashenrider.game.Input.Xbox;
import com.ashenrider.game.userinterface.LoadingScreen;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class HackathonApp extends Game {
    public static final boolean DEBUG_HITBOXES = false;
    public static final boolean DEBUG_FPS = false;
    public static final boolean FILLER_PLAYERS = true;
    public static final String MAP = "finalMap2";
    
    Stack<Screen> backStack;
    private boolean isEscaping = false;

    public static BitmapFont hudFont;
    public static BitmapFont buttonFont;
    public static BitmapFont titleFont;
    
    public static ArrayList<PlayerInput> playerInputs;
    public static int mousePlayer = -1;
    
    public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<>";
    FPSLogger logger;
    
    @Override
    public void create () {
        packTextures();

        backStack = new Stack<Screen>();
        // generate fonts
        FileHandle centuryGothic = Gdx.files.internal("fonts/Gothic.TTF");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(centuryGothic);
        titleFont = generator.generateFont(48);
        hudFont = generator.generateFont(18);
        buttonFont = generator.generateFont(26);
        generator.dispose();

        // initialize playerInput objects
        // these should be persistent across matches and can be reused for menu interactions
        playerInputs = new ArrayList<PlayerInput>();
        // Keyboard + mouse guy
        mousePlayer = 0;
        playerInputs.add(new PlayerInput(new KeyboardAxis(Keys.A, Keys.D),
                                        new KeyboardAxis(Keys.S, Keys.W),
                                        null,
                                        null,
                                        new KeyboardButton(Keys.W),
                                        new MouseButton(Buttons.LEFT),
                                        new MouseButton(Buttons.RIGHT),
                                        new KeyboardButton(Keys.E),
                                        new KeyboardButton(Keys.SHIFT_LEFT)));
        // controller players

        boolean controllerDebug = false;

        for(Controller controller : Controllers.getControllers()) {
            // need to update controllerHelper in order to support more buttons, or rethink the control scheme
            playerInputs.add(new PlayerInput(ControllerHelper.getAxis(controller, ControllerHelper.LEFT_STICK_HORIZONTAL),
                    ControllerHelper.getAxis(controller, ControllerHelper.LEFT_STICK_VERTICAL),
                    ControllerHelper.getAxis(controller, ControllerHelper.RIGHT_STICK_HORIZONTAL),
                    ControllerHelper.getAxis(controller, ControllerHelper.RIGHT_STICK_VERTICAL),
                    ControllerHelper.getButton(controller, ControllerHelper.A_BTN),
                    ControllerHelper.getButton(controller, ControllerHelper.RIGHT_TRIGGER),
                    ControllerHelper.getButton(controller, ControllerHelper.RIGHT_TRIGGER),
                    ControllerHelper.getButton(controller, ControllerHelper.LEFT_TRIGGER),
                    ControllerHelper.getButton(controller, ControllerHelper.B_BTN)));

            if (controllerDebug) {
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
                playerInputs.add(new PlayerInput(new KeyboardAxis(Keys.LEFT, Keys.RIGHT),
                        new KeyboardAxis(Keys.DOWN, Keys.UP),
                        new KeyboardAxis(Keys.NUMPAD_4, Keys.NUMPAD_6),
                        new KeyboardAxis(Keys.NUMPAD_5, Keys.NUMPAD_8),
                        new KeyboardButton(Keys.UP),
                        new KeyboardButton(Keys.ENTER),
                        new KeyboardButton(Keys.ENTER),
                        new KeyboardButton(Keys.SHIFT_RIGHT),
                        new KeyboardButton(Keys.CONTROL_RIGHT)));
            }
        }
        
        logger = new FPSLogger();
        setScreen(new LoadingScreen(this));
    }

    public void packTextures() {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth=512;
        settings.maxHeight=512;

        // In order to update animations you must delete the .atlas file associated with it

        String[] atlases = {"gui", "animations", "fx"};

        for (String atlasName : atlases) {
            if (!Gdx.files.internal("pack/" + atlasName + ".atlas").exists()) {
                TexturePacker.process(settings, atlasName, "pack", atlasName);
            }
        }

        if (!Gdx.files.internal("pack/intro.atlas").exists()) {
            // intro is 1200 x 800
            settings.maxWidth=2048;
            settings.maxHeight=1024;
            TexturePacker.process(settings, "intro_frames", "pack", "intro");
        }
    }

    @Override
    public void render () {
        if (DEBUG_FPS) {
            logger.log();
        }
        // update playerinput objects
        for (PlayerInput input : playerInputs) {
            input.update();
        }
        
        // draws the current screen
        super.render();

        //Go back when esc is hit.
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            isEscaping = true;
        } else if(isEscaping == true) {
            isEscaping = false;
            popBackstack();
        }
    }

    @Override
    public void setScreen(Screen screen) {
        backStack.push(getScreen());
        super.setScreen(screen);
    }

    public void popBackstack() {
        getScreen().dispose();
        Screen next = this.backStack.pop();
        if (next == null) {
            Gdx.app.exit();
        } else {
            super.setScreen(next);
        }
    }
}
