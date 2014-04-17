package com.ashenrider.game.Entities;

import com.ashenrider.game.Assets;
import com.ashenrider.game.Map;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Random;

public abstract class PowerUp extends Entity {
    float scale = 2.5f;

    Random rand = new Random();
    final float respawnTime;
    float respawnTimer;
    Vector2 initPosition;
    List<Vector2> spawns;
    Texture img;
    Color colorTint;

    public PowerUp(List<Vector2> spawns, float respawnTime, String texture, Color col) {
        super(new Vector2(0, 0));
        this.spawns = spawns;
        this.respawnTime = respawnTime;
        falls = false;
        img = Assets.manager.get(texture);
        size = new Vector2(img.getWidth(), img.getHeight()).scl(scale);
        colorTint = col;

        respawn();
    }

    protected void respawn() {
        this.initPosition = spawns.get(rand.nextInt(spawns.size())).cpy();
        pos = initPosition.cpy();
    }

    public void onPickup(Player player) {
        respawnTimer = respawnTime;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if(respawnTimer > 0.0f && respawnTimer - dt <= 0.0f) {
            respawn();
        }
        respawnTimer -= dt;
        this.pos.y = initPosition.y + (float)Math.sin(respawnTimer*(float)Math.PI)*size.y/3.0f;
    }

    @Override
    public void handleCollision(Map map) {
        for (Player p : scene.players) {
            if (respawnTimer <= 0 && p.isAlive()) {
                Rectangle box = getBounds();
                Rectangle playerBox = p.getBounds();
                if (box.overlaps(playerBox)) {
                    this.onPickup(p);
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if(respawnTimer < 0.0f) {
            batch.setColor(colorTint);
            batch.draw(img, pos.x, pos.y, img.getWidth() * scale, img.getHeight() * scale);
            batch.setColor(Color.WHITE);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        img.dispose();
    }
}
