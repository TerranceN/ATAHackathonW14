package com.ashenrider.game.userinterface;

import com.ashenrider.game.GameScreen;
import com.ashenrider.game.HackathonApp;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MainMenuScreen implements Screen {

    private HackathonApp app;
    private Stage stage;

    public MainMenuScreen(final HackathonApp app) {
        this.app = app;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        //Container menuContainer = new Container();
        //menuContainer.setScale();

        Table menu = new Table();
        menu.setFillParent(true);
        stage.addActor(menu);

        Texture upRegion = new Texture("p0.png");
        Texture downRegion = new Texture("p1.png");
        BitmapFont buttonFont = new BitmapFont();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new SpriteDrawable(new Sprite(upRegion));
        style.down = new SpriteDrawable(new Sprite(downRegion));
        style.font = buttonFont;

        TextButton startButton = new TextButton("Start", style);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(new GameScreen(app, "test.tmx"));
            }
        });

        menu.add(startButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        Table.drawDebug(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }
}
