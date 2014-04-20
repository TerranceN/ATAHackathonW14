package com.ashenrider.game;

import com.ashenrider.game.userinterface.DeathsWidget;
import com.ashenrider.game.userinterface.LivesWidget;
import com.ashenrider.game.userinterface.WinWidget;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameScreen implements Screen {
    HackathonApp app;

    OrthographicCamera camera;
    Scene scene;
    LivesWidget livesWidget;
    DeathsWidget deathsWidget;
    WinWidget winWidget;

    public GameScreen(HackathonApp app, Map map) {
        this.app = app;

        scene = new Scene(map);

        livesWidget = new LivesWidget(scene);
        deathsWidget = new DeathsWidget(scene);
        winWidget = new WinWidget(scene);
    }

    @Override
    public void render(float delta) {
        scene.update(delta);

        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        scene.render();
        livesWidget.render(delta);
        deathsWidget.render(delta);
        winWidget.render(delta);
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
