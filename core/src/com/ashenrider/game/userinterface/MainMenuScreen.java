package com.ashenrider.game.userinterface;

import java.util.ArrayList;
import java.util.List;

import com.ashenrider.game.GameScreen;
import com.ashenrider.game.HackathonApp;
import com.ashenrider.game.Input.*;
import com.ashenrider.game.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MainMenuScreen implements Screen {

    private HackathonApp app;
    private TextureAtlas atlas;
    
    private Stage stage;
    private Table menu;
    private Label skipLabel;
    Intro intro;
    
    private Skin skin;
    private ArrayList<Button> buttons;
    
    private List<InputAxis> hAxis;
    private List<InputAxis> vAxis;
    private List<InputButton> select;
    private List<InputButton> back;

    private float btnIndex; // floor to get actual button index
    private float navSpeed = 4.0f; // buttons per second

    TextButton.TextButtonStyle style;
    TextButton.TextButtonStyle selectedStyle;
    public MainMenuScreen(final HackathonApp app) {
        intro = new Intro();
        this.app = app;

        atlas = Assets.manager.get("pack/gui.atlas", TextureAtlas.class);

        skin = new Skin();

        style = new TextButton.TextButtonStyle();
        style.up = new NinePatchDrawable(atlas.createPatch("ashenrider_btn"));
        style.down = new NinePatchDrawable(atlas.createPatch("ashenrider_btn_pressed"));
        style.disabled = new NinePatchDrawable(atlas.createPatch("ashenrider_btn_disabled"));
        style.over = new NinePatchDrawable(atlas.createPatch("ashenrider_btn_highlight"));
        style.font = HackathonApp.buttonFont;
        style.fontColor = Color.BLACK;

        selectedStyle = new TextButton.TextButtonStyle();
        selectedStyle.up = new NinePatchDrawable(atlas.createPatch("ashenrider_btn_highlight"));
        selectedStyle.down = new NinePatchDrawable(atlas.createPatch("ashenrider_btn_pressed"));
        selectedStyle.disabled = new NinePatchDrawable(atlas.createPatch("ashenrider_btn_highlight"));
        selectedStyle.over = new NinePatchDrawable(atlas.createPatch("ashenrider_btn_highlight"));
        selectedStyle.font = HackathonApp.buttonFont;
        selectedStyle.fontColor = Color.BLACK;

        skin.add("default", style);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        //Container menuContainer = new Container();
        //menuContainer.setScale();


        BitmapFont labelFont = new BitmapFont();
        Skin labelSkin = new Skin();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = HackathonApp.buttonFont;
        labelStyle.fontColor = Color.WHITE;
        labelSkin.add("default", labelStyle);
        skipLabel = new Label("Press A or Enter to skip", labelSkin);
                
        menu = new Table();
        menu.setFillParent(true);
        menu.center().bottom();
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

        btnIndex = 0.0f;
        buttons = new ArrayList<Button>();
        buttons.add(startButton);
        menu.add(startButton).width(300).height(50).padBottom(10);
        menu.row();
        menu.add(exitButton).width(300).height(50).padBottom(10);
        menu.row();
        buttons.add(exitButton);

        menu.add(skipLabel);

        hAxis = new ArrayList<InputAxis>();
        vAxis = new ArrayList<InputAxis>();
        select = new ArrayList<InputButton>();
        back = new ArrayList<InputButton>();
        
        hAxis.add(new KeyboardAxis(Keys.A, Keys.D));
        hAxis.add(new KeyboardAxis(Keys.LEFT, Keys.RIGHT));
        vAxis.add(new KeyboardAxis(Keys.S, Keys.W));
        vAxis.add(new KeyboardAxis(Keys.DOWN, Keys.UP));

        select.add(new KeyboardButton(Keys.ENTER));
        back.add(new KeyboardButton(Keys.ESCAPE));
        
        for(Controller controller : Controllers.getControllers()) {
            hAxis.add(ControllerHelper.getAxis(controller, ControllerHelper.LEFT_STICK_HORIZONTAL));
            vAxis.add(ControllerHelper.getAxis(controller, ControllerHelper.LEFT_STICK_VERTICAL));
            select.add(ControllerHelper.getButton(controller, ControllerHelper.A_BTN));
            back.add(ControllerHelper.getButton(controller, ControllerHelper.B_BTN));
        }

        for(Controller cont : Controllers.getControllers()) {
            Gdx.app.log("NAME", cont.getName());
        }
    }

    public void startGame() {
        String mapName = HackathonApp.MAP;
        app.setScreen(new GameScreen(app, "maps/" + mapName + "/" + mapName + ".tmx"));
    }

    public void exitGame() {
        Gdx.app.exit();
    }

    private void update(float delta) {
        //
        for (InputAxis iAxis : hAxis) {
            if (iAxis.getValue() > 0.5f) {
                //Gdx.app.log("input", "Go right");
            } else if (iAxis.getValue() < -0.5f) {
                //Gdx.app.log("input", "Go left");
            }
        }
        for (InputAxis iAxis : vAxis) {
            if (Math.abs(iAxis.getValue()) > 0.2f) {
                btnIndex += iAxis.getValue() * navSpeed * delta;
                if (btnIndex < 0) {
                    btnIndex += buttons.size();
                } else {
                    btnIndex = btnIndex % buttons.size();
                }
                //Gdx.app.log("Button index", "btn index: " + btnIndex);
            }
            if (iAxis.getValue() > 0.5f) {
                //Gdx.app.log("input", "Go up");
            } else if (iAxis.getValue() < -0.5f) {
                //Gdx.app.log("input", "Go down");
            }
        }
        for (InputButton iButton : select) {
            if (iButton.isDown()) {
                //Gdx.app.log("input", "Ok");
                if (!intro.isFinished()) {
                    intro.skip();
                } else if (Math.floor(btnIndex) == 0) {
                    startGame();
                } else {
                    exitGame();
                }
            }
        }
        for (InputButton iButton : back) {
            if (iButton.isDown()) {
                //Gdx.app.log("input", "Back");
               //exitGame();
                if (!intro.isFinished()) {
                    intro.skip();
                }
            }
        }
        for (int i=0; i<buttons.size(); i++) {
            Button btn = buttons.get(i);
            if (i == Math.floor(btnIndex)) {
                btn.setStyle(selectedStyle);
            } else {
                btn.setStyle(style);
            }
        }
        // update intro at 60 fps with no jumping if there's lag
        intro.update(0.017f);
        for (Button b : buttons) {
            b.setVisible(intro.isFinished());
        }
        skipLabel.setVisible(!intro.isFinished());
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        intro.render(stage.getWidth(), stage.getHeight());
        stage.act(delta);
        update(delta);
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
