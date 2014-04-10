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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class HackathonApp extends Game {

    Stack<Screen> backStack;
    private boolean isEscaping = false;
    
    @Override
	public void create () {
        backStack = new Stack<Screen>();

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
