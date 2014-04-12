package com.ashenrider.game;

import com.ashenrider.game.Input.ControllerAxis;
import com.ashenrider.game.Input.ControllerAxisButton;
import com.ashenrider.game.Input.ControllerButton;
import com.ashenrider.game.Input.InputAxis;
import com.ashenrider.game.Input.InputButton;
import com.ashenrider.game.Input.KeyboardAxis;
import com.ashenrider.game.Input.KeyboardButton;
import com.ashenrider.game.Input.Xbox;
import com.ashenrider.game.userinterface.MainMenuScreen;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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
        backStack = new Stack<Screen>();
        // generate fonts
        FileHandle centuryGothic = Gdx.files.internal("fonts/Gothic.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(centuryGothic);
        titleFont = generator.generateFont(48);
        hudFont = generator.generateFont(18);
        buttonFont = generator.generateFont(26);
        generator.dispose();

        setScreen(new MainMenuScreen(this));
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
        super.setScreen(this.backStack.pop());
    }
}
