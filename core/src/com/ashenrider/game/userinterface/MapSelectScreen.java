package com.ashenrider.game.userinterface;

import java.util.ArrayList;
import java.util.List;

import com.ashenrider.game.GameScreen;
import com.ashenrider.game.HackathonApp;
import com.ashenrider.game.Map;
import com.ashenrider.game.Entities.Player;
import com.ashenrider.game.Entities.Player.Action;
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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
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

public class MapSelectScreen implements Screen {

    private HackathonApp app;
    private TextureAtlas atlas;
    
    private Stage stage;
    private Table menu;
    
    private Skin skin;

    private OrthographicCamera previewCam;
    private Vector2 previewCentre;
    private Vector2 PREVIEW_SIZE = new Vector2(200, 150);
    
    // 2d array of maps that can be navigated spacially
    private String[] mapNames = {"finalMap2", "finalMap", "test"};
    private ArrayList<ArrayList<Map>> maps;
    private float mapXIndex; // floor to get actual index
    private float mapYIndex; // floor to get actual index
    private int MAPS_PER_ROW = 1;
    Map currentMap;
    
    // axis value required to move up a single space
    private static final float MIN_AXIS_MOVE = 0.35f;
    // axis value required to start scrolling at NAV_SPEED/second
    private static final float MIN_AXIS_HOLD = 0.7f;
    private static final float NAV_SPEED = 3.0f; // buttons per second

    TextButton.TextButtonStyle style;
    TextButton.TextButtonStyle selectedStyle;
    
    ShapeRenderer shapeRenderer;

    public MapSelectScreen(final HackathonApp app) {
        this.app = app;

        atlas = Assets.manager.get("pack/gui.atlas", TextureAtlas.class);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        BitmapFont labelFont = new BitmapFont();
        Skin labelSkin = new Skin();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = HackathonApp.buttonFont;
        labelStyle.fontColor = Color.WHITE;
        labelSkin.add("default", labelStyle);
                
        menu = new Table();
        menu.setFillParent(true);
        menu.center().bottom();
        stage.addActor(menu);

        maps = new ArrayList<ArrayList<Map>>();
        for (int y = 0; y < (mapNames.length + MAPS_PER_ROW - 1) / MAPS_PER_ROW; y++) {
            ArrayList<Map> row = new ArrayList<Map>();
            for (int x = 0; x < MAPS_PER_ROW && y * MAPS_PER_ROW + x < mapNames.length; x++) {
                String mapName = mapNames[y * MAPS_PER_ROW + x];
                row.add(new Map("maps/" + mapName + "/" + mapName + ".tmx"));
            }
            // invert the order of the rows,
            maps.add(0, row);
        }
        mapXIndex = 0.5f;
        mapYIndex = maps.size() - 0.5f;
        
        previewCam = new OrthographicCamera();
        shapeRenderer = new ShapeRenderer();
    }

    public void startGame() {
        app.setScreen(new GameScreen(app, currentMap));
    }

    public void exitGame() {
        Gdx.app.exit();
    }

    private void update(float delta) {
        for (PlayerInput input : HackathonApp.playerInputs) {
            // Horizontal
            InputAxis hAxis = input.getAxis(Action.MOVE_HORIZONTAL);
            if (hAxis.getValue() > MIN_AXIS_MOVE && hAxis.getValue() - hAxis.getDelta() < MIN_AXIS_MOVE) {
                //Go right once
                mapXIndex = (float) Math.floor(mapXIndex + 1) + 0.5f;
            } else if (hAxis.getValue() > MIN_AXIS_HOLD) {
                //Scroll right
                mapXIndex += NAV_SPEED * delta;
            }
            
            if (hAxis.getValue() < -MIN_AXIS_MOVE && hAxis.getValue() - hAxis.getDelta() > -MIN_AXIS_MOVE) {
                //Go left once
                mapXIndex = (float) Math.floor(mapXIndex - 1) + 0.5f;
            } else if (hAxis.getValue() < -MIN_AXIS_HOLD) {
                //Scroll left
                mapXIndex -= NAV_SPEED * delta;
            }
            
            // Vertical
            InputAxis vAxis = input.getAxis(Action.MOVE_VERTICAL);
            if (vAxis.getValue() > MIN_AXIS_MOVE && vAxis.getValue() - vAxis.getDelta() < MIN_AXIS_MOVE) {
                //Go up once
                mapYIndex = (float) Math.floor(mapYIndex + 1) + 0.5f;
            } else if (vAxis.getValue() > MIN_AXIS_HOLD) {
                //Scroll up
                mapYIndex += NAV_SPEED * delta;
            }
            
            if (vAxis.getValue() < -MIN_AXIS_MOVE && vAxis.getValue() - vAxis.getDelta() > -MIN_AXIS_MOVE) {
                //Go down once
                mapYIndex = (float) Math.floor(mapYIndex - 1) + 0.5f;
            } else if (vAxis.getValue() < -MIN_AXIS_HOLD) {
                //Scroll down
                mapYIndex -= NAV_SPEED * delta;
            }

            // scoll wrap
            if (mapYIndex < 0) {
                mapYIndex += maps.size();
            } else {
                mapYIndex = mapYIndex % maps.size();
            }
            if (mapXIndex < 0) {
                mapXIndex += maps.get((int) mapYIndex).size();
            } else {
                mapXIndex = mapXIndex % maps.get((int) mapYIndex).size();
            }
            
            if (input.getButton(Action.MENU_OK).justPressed()) {
                // start with the selected map
                startGame();
            }
        }
        currentMap = maps.get((int) mapYIndex).get((int) mapXIndex);
    }
    
    @Override
    public void render(float delta) {
        //Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClearColor(57/255f, 133/255f, 142/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        // draw the current map large and center
        float screenRatio = width / height;
        float mapRatio = (float)currentMap.getWidth() / currentMap.getHeight();
        float scale = screenRatio <= mapRatio ? width / currentMap.getWidth() : height / currentMap.getHeight();
        previewCam.setToOrtho(false, Gdx.graphics.getWidth() / scale, Gdx.graphics.getHeight() / scale);
        if (screenRatio <= mapRatio) {
            previewCam.translate(0, -(height / scale - currentMap.getHeight())/2f);
        } else {
            previewCam.translate(-(width / scale - currentMap.getWidth())/2f, 0);
        }
        previewCam.update();
        currentMap.drawPreview(previewCam);
        
        // draw all maps (with current map highlighted)
        previewCentre = new Vector2(10 + PREVIEW_SIZE.x/2f, Gdx.graphics.getHeight()/2f);
        for (int y=0; y <maps.size(); y++) {
            ArrayList<Map> row = maps.get(y);
            for (int x=0; x<row.size(); x++) {
                
                Map map = row.get(x);
                // draw a preview for this map
                float prevRatio = PREVIEW_SIZE.x / PREVIEW_SIZE.y;
                mapRatio = (float)map.getWidth() / map.getHeight();
                float padding = 6;
                float border = 3;
                scale = prevRatio <= mapRatio ? PREVIEW_SIZE.x / map.getWidth() : PREVIEW_SIZE.y / map.getHeight();
                float x1 = previewCentre.x + (PREVIEW_SIZE.x + padding) * (x - row.size()/2f);
                float y1 = previewCentre.y + (PREVIEW_SIZE.y + padding) * (y - maps.size()/2f);
                // draw a box around the map preview
                shapeRenderer.begin(ShapeType.Filled);
                if (map == currentMap) {
                    shapeRenderer.setColor(1f, 1, 0, 1);
                } else {
                    shapeRenderer.setColor(0, 0, 0, 1);
                }
                shapeRenderer.rect(x1-border, y1-border, PREVIEW_SIZE.x+2*border, PREVIEW_SIZE.y+2*border);
                shapeRenderer.setColor(57/255f, 133/255f, 142/255f, 1);
                shapeRenderer.rect(x1, y1, PREVIEW_SIZE.x, PREVIEW_SIZE.y);
                shapeRenderer.end();
                // transform and scale map coordinates to center it in the box
                previewCam.setToOrtho(false, Gdx.graphics.getWidth() / scale, Gdx.graphics.getHeight() / scale);
                if (prevRatio <= mapRatio) {
                    previewCam.translate(-x1/scale, -(y1/scale + (PREVIEW_SIZE.y / scale - map.getHeight())/2f));
                } else {
                    previewCam.translate(-(x1/scale + (PREVIEW_SIZE.x / scale - map.getWidth())/2f), -y1/scale);
                }
                previewCam.update();
                map.drawPreview(previewCam);
            }
        }
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
