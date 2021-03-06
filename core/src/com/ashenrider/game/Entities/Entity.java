package com.ashenrider.game.Entities;

import com.ashenrider.game.Map;
import com.ashenrider.game.Scene;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Entity {
    public Scene scene;
    public int layer;
    
    public Vector2 pos;
    public Vector2 speed;
    public Vector2 size;

    public float life = 0f;
    
    float GRAVITY = -600.0f;
    public boolean falls = true;
    boolean onGround = false;
    boolean collides = true;
    
    private boolean destroyed = false;
    
    public Entity(Vector2 initPosition) {
        pos = initPosition;
        speed = new Vector2(0,0);
        size = new Vector2(0,0);
    }
    
    public void update(float dt) {
        life += dt;

        //if (falls && !onGround) {
        if (falls) {
            speed.add(0, GRAVITY * dt);
        }

        pos.add(speed.cpy().scl(dt));
        if (pos.y < 0) {
            pos.y = pos.y + scene.map.getHeight();
        } else if (pos.y > scene.map.getHeight()) {
            pos.y = pos.y - scene.map.getHeight();
        }
        if (pos.x < 0) {
            pos.x = pos.x + scene.map.getWidth();
        } else if (pos.x > scene.map.getWidth()) {
            pos.x = pos.x - scene.map.getWidth();
        }
    }

    public void handleCollision(Map map) {
    }
    
    public Rectangle getBounds() {
        return new Rectangle(pos.x ,pos.y, size.x, size.y);
    }
    
    public Vector2 getCentre() {
        return new Vector2(pos.x + size.x/2, pos.y + size.y/2);
    }

    public void renderWithWrapAround(SpriteBatch batch) {
        if (pos.x + size.x > scene.map.getWidth()) {
            pos.x -= scene.map.getWidth();
            render(batch);
            pos.x += scene.map.getWidth();
        }

        if (pos.y + size.y > scene.map.getHeight()) {
            pos.y -= scene.map.getHeight();
            render(batch);
            pos.y += scene.map.getHeight();
        }

        render(batch);
    }

    public abstract void render(SpriteBatch batch);
    
    public void destroy() {
        // remove it from the current layer's list of entities
        scene.entityLayers.get(layer).remove(this);
        // and mark it to be removed from the list of all entities later
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
