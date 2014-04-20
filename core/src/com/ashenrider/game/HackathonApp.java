package com.ashenrider.game;

import com.ashenrider.game.Entities.Player;
import com.ashenrider.game.Entities.Player.Action;

import com.ashenrider.game.Input.PlayerInput;
import com.ashenrider.game.userinterface.LoadingScreen;
import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class HackathonApp extends Game {
    public static final boolean DEBUG_HITBOXES = false;
    public static final boolean DEBUG_FPS = false;
    public static final boolean FILLER_PLAYERS = true;
    
    Stack<Screen> backStack;
    private boolean isEscaping = false;

    public static BitmapFont hudFont;
    public static BitmapFont buttonFont;
    public static BitmapFont titleFont;
    
    // for every type of controller, have a list of control schemes
    // Allow users to pick/change an existing scheme for their controller, or allow them to create a new one.
    public static ArrayList<PlayerInput> playerInputs;
    
    public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<>";
    FPSLogger logger;
    Json json;

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
        playerInputs = ControlSchemeManager.loadControls();
        
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
            // for now, only the keyboard MENU_BACK pops the backStack
            if (input.name.equals(PlayerInput.KEYBOARD_AND_MOUSE) && input.getButton(Action.MENU_BACK).justReleased()) {
                popBackStack();
            }

        }
        
        // draws the current screen
        super.render();
    }

    @Override
    public void setScreen(Screen screen) {
        backStack.push(getScreen());
        super.setScreen(screen);
    }

    public void popBackStack() {
        getScreen().dispose();
        Screen next = this.backStack.pop();
        if (next == null) {
            Gdx.app.exit();
        } else {
            super.setScreen(next);
        }
    }
    
}
