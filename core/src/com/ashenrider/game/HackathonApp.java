package com.ashenrider.game;

import com.ashenrider.game.userinterface.MainMenuScreen;
import com.badlogic.gdx.*;

import java.util.Stack;


public class HackathonApp extends Game {

    Stack<Screen> backStack;

	@Override
	public void create () {
        backStack = new Stack<Screen>();

        setScreen(new MainMenuScreen(this));
    }

	@Override
	public void render () {
        super.render();
	}

    @Override
    public void setScreen(Screen screen) {
        backStack.push(getScreen());
        super.setScreen(screen);
    }

    public void popBackstack() {
        setScreen(this.backStack.pop());
    }
}
