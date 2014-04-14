package com.ashenrider.game.userinterface;

import com.ashenrider.game.HackathonApp;
import com.ashenrider.game.Scene;
import com.ashenrider.game.Entities.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import java.util.LinkedList;
import java.util.List;

public class LivesWidget {

    private List<Player> playersList;
    private List<Label> playerLivesList = new LinkedList<Label>();
    private List<Image> playerImageList = new LinkedList<Image>();
    private Scene scene;
    private Stage stage;
    private Skin skin;

    public LivesWidget(Scene scene) {
        this.scene = scene;
        playersList = scene.players;

        BitmapFont labelFont = new BitmapFont();
        skin = new Skin();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = HackathonApp.hudFont;
        labelStyle.fontColor = Color.WHITE;

        skin.add("default", labelStyle);

        stage = new Stage();
        Table ui = new Table();
        ui.setFillParent(true);
        stage.addActor(ui);

        ui.left().bottom();
        for(Player player : playersList) {
            Sprite playerSprite = new Sprite(player.getSprite());
            playerSprite.setScale(0.7f);
            playerSprite.setAlpha(0.7f);

            Image playerImage = new Image(new SpriteDrawable(playerSprite));
            playerImageList.add(playerImage);
            // the player images all have a lot of transparency around them
            ui.add(playerImage).padLeft(-10).padRight(-10);

            Label playerLives = new Label("x" + player.getLives(), skin);
            playerLivesList.add(playerLives);
            ui.add(playerLives).padLeft(0);
        }
    }

    public void render(float delta) {
        for(int i=0; i<playersList.size(); i++) {
            Player player = playersList.get(i);
            ((SpriteDrawable)playerImageList.get(i).getDrawable()).getSprite().setRegion(player.getSprite());
            playerLivesList.get(i).setText("x" + player.getLives());
        }

        stage.act(delta);
        stage.draw();

        Table.drawDebug(stage);
    }
}
