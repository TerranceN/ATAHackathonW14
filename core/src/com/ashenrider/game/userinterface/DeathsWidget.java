package com.ashenrider.game.userinterface;

import com.ashenrider.game.Player;
import com.ashenrider.game.PlayerDeathListener;
import com.ashenrider.game.Scene;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.LinkedList;
import java.util.ListIterator;

public class DeathsWidget implements PlayerDeathListener {

    private static final float DISPLAY_TIME = 3.0f;
    private static final int DISPLAY_COUNT = 5;

    private LinkedList<DeathEvent> events = new LinkedList<DeathEvent>();
    private Image[] aggressorImages = new Image[5];
    private Image[] attackImages = new Image[5];
    private Image[] victimImages = new Image[5];

    private Scene scene;
    private Stage stage;

    Texture attackIcon;

    public DeathsWidget(Scene scene) {
        this.scene = scene;
        scene.addPlayerDeathListener(this);

        attackIcon = new Texture("death_icon.png");

        stage = new Stage();
        Table ui = new Table();
        ui.setFillParent(true);
        stage.addActor(ui);

        ui.left().top();
        for(int i=0; i<DISPLAY_COUNT; i++) {
            Image image = new Image();
            image.setVisible(false);

            aggressorImages[i] = new Image();
            aggressorImages[i].setVisible(false);
            ui.add(aggressorImages[i]);
            attackImages[i] = new Image(new SpriteDrawable(new Sprite(attackIcon)));
            attackImages[i].setVisible(false);
            ui.add(attackImages[i]).padLeft(10);
            victimImages[i] = new Image();
            victimImages[i].setVisible(false);
            ui.add(victimImages[i]).padLeft(10);
            ui.row();
        }

    }

    public void render(float delta) {
        for(ListIterator<DeathEvent> iter = events.listIterator(); iter.hasNext(); ) {
            DeathEvent event = iter.next();
            event.time -= delta;
            if(event.time < 0) iter.remove();
        }

        for(int i=0; i<DISPLAY_COUNT; i++) {
            if(i > events.size()-1) {
                aggressorImages[i].setVisible(false);
                attackImages[i].setVisible(false);
                victimImages[i].setVisible(false);
            }
            else {
                aggressorImages[i].setVisible(true);
                if(aggressorImages[i].getDrawable() == null) {
                    aggressorImages[i].setDrawable(new SpriteDrawable(new Sprite(events.get(i).aggressor.getSprite())));
                } else {
                    ((SpriteDrawable)aggressorImages[i].getDrawable()).getSprite().setTexture(events.get(i).aggressor.getSprite().getTexture());
                    ((SpriteDrawable)aggressorImages[i].getDrawable()).getSprite().setRegion(events.get(i).aggressor.getSprite());
                }
                attackImages[i].setVisible(true);
                victimImages[i].setVisible(true);
                if(victimImages[i].getDrawable() == null) {
                    victimImages[i].setDrawable(new SpriteDrawable(new Sprite(events.get(i).victim.getSprite())));
                } else {
                    ((SpriteDrawable)victimImages[i].getDrawable()).getSprite().setTexture(events.get(i).victim.getSprite().getTexture());
                    ((SpriteDrawable)victimImages[i].getDrawable()).getSprite().setRegion(events.get(i).victim.getSprite());
                }
                ((SpriteDrawable)aggressorImages[i].getDrawable()).getSprite().setAlpha((events.get(i).time > 1.0f) ? 1.0f : events.get(i).time);
                ((SpriteDrawable)attackImages[i].getDrawable()).getSprite().setAlpha((events.get(i).time > 1.0f) ? 1.0f : events.get(i).time);
                ((SpriteDrawable)victimImages[i].getDrawable()).getSprite().setAlpha((events.get(i).time > 1.0f) ? 1.0f : events.get(i).time);
            }
        }

        stage.act(delta);
        stage.draw();

        Table.drawDebug(stage);
    }

    @Override
    public void onPlayerDeath(Player aggressor, Player victim) {
        DeathEvent newEvent = new DeathEvent(aggressor, victim);
        events.add(newEvent);
    }

    private class DeathEvent {
        public Player aggressor;
        public Player victim;
        public float time;

        public DeathEvent(Player aggressor, Player victim) {
            this.aggressor = aggressor;
            this.victim = victim;
            time = DISPLAY_TIME;
        }
    }
}
