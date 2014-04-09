package com.ashenrider.game.userinterface;

import com.ashenrider.game.GameScreen;
import com.ashenrider.game.HackathonApp;
import com.ashenrider.game.Input.Xbox;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MainMenuScreen implements Screen {

    private ControllerListener listener;
    private HackathonApp app;
    private TextureAtlas atlas;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(final HackathonApp app) {
        this.app = app;

        BitmapFont buttonFont = new BitmapFont();

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
                startGame();
            }
        });

        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exitGame();
            }
        });

        menu.add(startButton).width(300).height(50).padBottom(10);
        menu.row();
        menu.add(exitButton).width(300).height(50).padBottom(10);

        listener = new ControllerListener() {
            @Override
            public void connected(Controller controller) {

            }

            @Override
            public void disconnected(Controller controller) {

            }

            @Override
            public boolean buttonDown(Controller controller, int i) {
                if(i == Xbox.BTN_A || i == Xbox.BTN_START) {
                    startGame();
                } else if (i == Xbox.BTN_B || i == Xbox.BTN_BACK) {
                    exitGame();
                }
                return false;
            }

            @Override
            public boolean buttonUp(Controller controller, int i) {
                return false;
            }

            @Override
            public boolean axisMoved(Controller controller, int i, float v) {
                return false;
            }

            @Override
            public boolean povMoved(Controller controller, int i, PovDirection povDirection) {
                return false;
            }

            @Override
            public boolean xSliderMoved(Controller controller, int i, boolean b) {
                return false;
            }

            @Override
            public boolean ySliderMoved(Controller controller, int i, boolean b) {
                return false;
            }

            @Override
            public boolean accelerometerMoved(Controller controller, int i, Vector3 vector3) {
                return false;
            }
        };
    }

    public void startGame() {
        app.setScreen(new GameScreen(app, "test.tmx"));
    }

    public void exitGame() {
        Gdx.app.exit();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
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
        Controllers.addListener(listener);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        Controllers.removeListener(listener);
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
