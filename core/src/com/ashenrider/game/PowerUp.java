package com.ashenrider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class PowerUp extends Entity {
    float scale = 2.5f;

    final float respawnTime;
    float respawnTimer;
    Vector2 initPosition;
    Texture img;

    public PowerUp(Vector2 initPosition, float respawnTime, String texture) {
        super(initPosition);
        this.initPosition = initPosition.cpy();
        this.respawnTime = respawnTime;
        falls = false;
        img = new Texture(texture);
        size = new Vector2(img.getWidth(), img.getHeight()).scl(scale);
    }

    public void onPickup(Player player) {
        respawnTimer = respawnTime;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        respawnTimer -= dt;
        this.pos.y = initPosition.y + (float)Math.sin(respawnTimer*(float)Math.PI)*size.y/3.0f;
    }

    @Override
    public void handleCollision(Map map) {
        for (Player p : scene.players) {
            if (respawnTimer <= 0 && !p.isDestroyed()) {
                Rectangle shotBox = getBounds();
                Rectangle playerBox = p.getBounds();
                if (shotBox.overlaps(playerBox)) {
                    this.onPickup(p);
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if(respawnTimer < 0.0f) {
            batch.draw(img, pos.x, pos.y, img.getWidth() * scale, img.getHeight() * scale);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        img.dispose();
    }
}
