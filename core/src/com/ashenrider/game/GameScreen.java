package com.ashenrider.game;

import com.ashenrider.game.userinterface.ScoreWidget;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.awt.*;

public class GameScreen implements Screen {
    HackathonApp app;

    OrthographicCamera camera;
    Scene scene;
    ScoreWidget widget;

    public GameScreen(HackathonApp app, String map) {
        this.app = app;

        scene = new Scene(map);

        widget = new ScoreWidget(scene);
    }

    @Override
    public void render(float delta) {
        scene.update(delta);

        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        scene.render();
        widget.render(delta);
    }

    @Override
    public void resize(int width, int height) {
    	scene.onResize();
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
