package com.ashenrider.game;

import com.ashenrider.game.Input.ControllerAxis;
import com.ashenrider.game.Input.ControllerAxisButton;
import com.ashenrider.game.Input.ControllerButton;
import com.ashenrider.game.Input.InputAxis;
import com.ashenrider.game.Input.InputButton;
import com.ashenrider.game.Input.KeyboardAxis;
import com.ashenrider.game.Input.KeyboardButton;
import com.ashenrider.game.Input.Xbox;
import com.ashenrider.game.userinterface.LoadingScreen;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class HackathonApp extends Game {

    Stack<Screen> backStack;
    private boolean isEscaping = false;

    public static BitmapFont hudFont;
    public static BitmapFont buttonFont;
    public static BitmapFont titleFont;
    
    
    public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<>";
    
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
