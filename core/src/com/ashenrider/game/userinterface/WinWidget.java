package com.ashenrider.game.userinterface;

import com.ashenrider.game.HackathonApp;
import com.ashenrider.game.PlayerDeathListener;
import com.ashenrider.game.Scene;
import com.ashenrider.game.Assets;
import com.ashenrider.game.Entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import java.util.LinkedList;
import java.util.List;

public class WinWidget implements PlayerDeathListener {

    Image playerImage;
    Label winText;
    Table container;

    private Scene scene;
    private TextureAtlas atlas;
    private Stage stage;
    private Skin skin;
    private Player winner;

    public WinWidget(Scene scene) {
        this.scene = scene;
        scene.addPlayerDeathListener(this);

        skin = new Skin();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = HackathonApp.hudFont;
        labelStyle.fontColor = Color.BLACK;

        skin.add("default", labelStyle);

        stage = new Stage();
        Table ui = new Table();
        ui.setFillParent(true);
        stage.addActor(ui);

        atlas = Assets.manager.get("pack/gui.atlas", TextureAtlas.class);

        ui.center();
        container = new Table();
        container.setBackground(new NinePatchDrawable(atlas.createPatch("ashenrider_btn_pressed")));
        ui.add(container).width(200).height(80);

        container.center();
        playerImage = new Image();
        container.add(playerImage);

        winText = new Label("Wins!", skin);
        container.add(winText).padLeft(10);

        container.setVisible(false);
        playerImage.setVisible(false);
        winText.setVisible(false);
    }

    public void render(float delta) {
        if(winner != null) {
            playerImage.setVisible(true);
            winText.setVisible(true);
            container.setVisible(true);

            if(playerImage.getDrawable() == null) {
                playerImage.setDrawable(new SpriteDrawable(new Sprite(winner.getSprite())));
            } else {
                ((SpriteDrawable)playerImage.getDrawable()).getSprite().setTexture(winner.getSprite().getTexture());
                ((SpriteDrawable)playerImage.getDrawable()).getSprite().setRegion(winner.getSprite());
            }
        }

        stage.act(delta);
        stage.draw();

        Table.drawDebug(stage);
    }

    @Override
    public void onPlayerDeath(Player aggressor, Player victim, int deathSource) {
        if (this.winner != null) {
            // someone already won, they are still the winner even if they die now
            return;
        }
        Player livingPlayer=null;
        for(Player player : scene.players) {
            if(!player.isDestroyed() && player.getLives() > 0) {
                if(livingPlayer == null) {
                    livingPlayer = player;
                } else {
                    // two players are alive, no winner yet
                    return;
                }
            }
        }
        this.winner = livingPlayer;
    }
}
