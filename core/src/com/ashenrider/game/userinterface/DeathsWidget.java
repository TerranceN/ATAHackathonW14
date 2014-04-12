package com.ashenrider.game.userinterface;

import com.ashenrider.game.DeathSources;
import com.ashenrider.game.Player;
import com.ashenrider.game.PlayerDeathListener;
import com.ashenrider.game.Scene;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class DeathsWidget implements PlayerDeathListener {

    private static final float DISPLAY_TIME = 2.0f;
    private static final int DISPLAY_COUNT = 5;
    private static final int ROW_PADDING = 5;
    
    private LinkedList<DeathEvent> events = new LinkedList<DeathEvent>();
    private Table[] rows = new Table[5];
    private Image[] aggressorImages = new Image[5];
    private Image[] attackImages = new Image[5];
    private Image[] victimImages = new Image[5];

    private Scene scene;
    private Stage stage;

    private TextureAtlas atlas;
    private HashMap<Integer, Texture> attackIcons;
    
    public DeathsWidget(Scene scene) {
        this.scene = scene;
        atlas = new TextureAtlas(Gdx.files.internal("pack/gui.atlas"));
        scene.addPlayerDeathListener(this);

        attackIcons = new HashMap<Integer, Texture>();
        attackIcons.put(DeathSources.FIREBALL, new Texture("fire.png"));
        attackIcons.put(DeathSources.PLASMA, new Texture("gun_projectile.png"));
        attackIcons.put(DeathSources.WALL, new Texture("wall_block.png"));

        stage = new Stage();
        Table ui = new Table();
        ui.setFillParent(true);
        stage.addActor(ui);

        ui.center().right();
        for(int i=0; i<DISPLAY_COUNT; i++) {
        	rows[i] = new Table();
        	NinePatch patch = atlas.createPatch("ashenrider_btn_pressed");
        	patch.setColor(new Color(1,1,1,0.5f));
        	NinePatchDrawable rowBkg = new NinePatchDrawable(patch);
        	rows[i].setVisible(false);
        	rows[i].setBackground(rowBkg);
            ui.add(rows[i]).width(200).height(80).pad(ROW_PADDING);

            Image image = new Image();
            image.setVisible(false);

            aggressorImages[i] = new Image();
            rows[i].add(aggressorImages[i]);
            attackImages[i] = new Image();
            rows[i].add(attackImages[i]).padLeft(10);
            victimImages[i] = new Image();
            rows[i].add(victimImages[i]).padLeft(10);
            
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
                rows[i].setVisible(false);
            }
            else {
                rows[i].setVisible(true);
                if (events.get(i).aggressor != null) {
	                if(aggressorImages[i].getDrawable() == null) {
	                    aggressorImages[i].setDrawable(new SpriteDrawable(new Sprite(events.get(i).aggressor.getHead())));
	                } else {
	                    ((SpriteDrawable)aggressorImages[i].getDrawable()).getSprite().setTexture(events.get(i).aggressor.getHead());
	                }
                }
                Texture attackImg = attackIcons.get(events.get(i).source);
                if(attackImages[i].getDrawable() == null) {
                	attackImages[i].setDrawable(new SpriteDrawable(new Sprite(attackImg)));
                } else {
                    ((SpriteDrawable)attackImages[i].getDrawable()).getSprite().setTexture(attackImg);
                }
                if(victimImages[i].getDrawable() == null) {
                    victimImages[i].setDrawable(new SpriteDrawable(new Sprite(events.get(i).victim.getHead())));
                } else {
                    ((SpriteDrawable)victimImages[i].getDrawable()).getSprite().setTexture(events.get(i).victim.getHead());
                }
                // fade out
                rows[i].setColor(1,1,1,(events.get(i).time > 1.0f) ? 1.0f : events.get(i).time);
            }
        }

        stage.act(delta);
        stage.draw();

        Table.drawDebug(stage);
    }

    @Override
    public void onPlayerDeath(Player aggressor, Player victim, int deathSource) {
        DeathEvent newEvent = new DeathEvent(aggressor, victim, deathSource);
        events.add(newEvent);
    }

    private class DeathEvent {
        public Player aggressor;
        public Player victim;
        public int source;
        public float time;

        public DeathEvent(Player aggressor, Player victim, int deathSource) {
            this.aggressor = aggressor;
            this.victim = victim;
            this.source = deathSource;
            time = DISPLAY_TIME;
        }
    }
}
