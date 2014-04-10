package com.ashenrider.game;

import com.ashenrider.game.userinterface.MainMenuScreen;
import com.badlogic.gdx.*;

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
