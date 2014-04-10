package com.ashenrider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class PlayerBody extends Entity {

    private float lifeTime = 5.0f;

    float scale = 2.0f;
    float animationTime = 0.0f;

    public int number;

    int animationOffset = -27;
    private float DEATH_FRAME_DURATION = 0.12f;
    private Animation deathAnimation;

    public PlayerBody(int playerNumber, Vector2 initPosition, Vector2 initVelocity, float lifeTime) {
        super(initPosition);
        speed.x = initVelocity.x;
        speed.y = initVelocity.y;
        this.number = playerNumber;

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("pack/animations.atlas"));
        TextureRegion[] deathFrames = new TextureRegion[10];
        for (int i=0; i<10; i++) {
            deathFrames[i] = atlas.findRegion(String.format("p%d/death-%02d", (playerNumber % 3), i+1));
        }
        deathAnimation = new Animation(DEATH_FRAME_DURATION, deathFrames);

        size = new Vector2(16.0f, 48.0f).scl(scale);
    }

    public TextureRegion getSprite() {
        return deathAnimation.getKeyFrame(animationTime, false);
    }

    public void collisionCheck(Map map) {
        Vector2 pen = map.getLeastPenetration(speed, pos, pos.cpy().add(size));

        if (pen.len() > 0) {
            if (pen.y != 0 && (pen.x == 0 || Math.abs(pen.x) > Math.abs(pen.y))) {
                pos.add(new Vector2(0, pen.y));
                speed.y = 0;
                if (pen.y > 0) {
                    onGround = true;
                }
            } else {
                pos.add(new Vector2(pen.x, 0));
                speed.x = 0;
            }
        }
    }

    @Override
    public void handleCollision(Map map) {
        onGround = false;
        collisionCheck(map);
        collisionCheck(map);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        animationTime += dt;
        if(animationTime > DEATH_FRAME_DURATION * 9.5f) animationTime = DEATH_FRAME_DURATION * 9.5f;
        lifeTime -= dt;
        if(lifeTime < 0) {
            destroy();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = getSprite();

        if(lifeTime < 1.0f) {
            batch.setColor(1.0f, 1.0f, 1.0f, lifeTime);
        }
        batch.draw(frame, pos.x + (animationOffset * scale), pos.y, frame.getRegionWidth() * scale, frame.getRegionHeight() * scale);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
