package com.ashenrider.game.userinterface;

import com.ashenrider.game.GameScreen;
import com.ashenrider.game.HackathonApp;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MainMenuScreen implements Screen {

    private HackathonApp app;
    private TextureAtlas atlas;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(final HackathonApp app) {
        this.app = app;

        BitmapFont buttonFont = new BitmapFont();
        buttonFont.setScale(2.0f);

        atlas = new TextureAtlas(Gdx.files.internal("pack/gui.atlas"));

        skin = new Skin();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new NinePatchDrawable(atlas.createPatch("ashenrider_btn"));
        style.down = new NinePatchDrawable(atlas.createPatch("ashenrider_btn_pressed"));
        style.disabled = new NinePatchDrawable(atlas.createPatch("ashenrider_btn_disabled"));
        style.over = new NinePatchDrawable(atlas.createPatch("ashenrider_btn_highlight"));
        style.font = buttonFont;
        style.fontColor = new Color(1, 1, 1, 1);

        skin.add("default", style);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        //Container menuContainer = new Container();
        //menuContainer.setScale();

        Table menu = new Table();
        menu.setFillParent(true);
        stage.addActor(menu);

        TextButton startButton = new TextButton("Start", skin);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(new GameScreen(app, "test.tmx"));
            }
        });

        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        menu.add(startButton).width(300).height(50).padBottom(10);
        menu.row();
        menu.add(exitButton).width(300).height(50).padBottom(10);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
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
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {
        hide();
    }

    @Override
    public void resume() {
        show();
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        stage.dispose();
    }
}
