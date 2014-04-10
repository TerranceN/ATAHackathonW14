package com.ashenrider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Blood extends Entity {

    private float lifeTime = 5.0f;

    float scale = 2.0f;
    float animationTime = 0.0f;

    private float FRAME_DURATION = 0.15f;
    private Animation anim;
    
    float angle;

    public Blood(Vector2 initPosition, Vector2 direction) {
        super(initPosition);
        angle = direction.angle();

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("pack/fx.atlas"));
        TextureRegion[] frames = new TextureRegion[9];
        for (int i=0; i<9; i++) {
            frames[i] = atlas.findRegion("blood/blood-" + (i+1));
            //if (direction.x < 0) {
            //	frames[i].flip(true,  false);
            //}
        }
        anim = new Animation(FRAME_DURATION, frames);

        size = new Vector2(16.0f, 16.0f).scl(scale);
        pos = initPosition.cpy().sub(size.cpy().scl(0.5f));
        
        lifeTime = FRAME_DURATION * 10;
        falls = false;
    }

    public TextureRegion getSprite() {
        return anim.getKeyFrame(animationTime, false);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        animationTime += dt;
        lifeTime -= dt;
        if(lifeTime < 0) {
            destroy();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = getSprite();
        float w = frame.getRegionWidth();
        float h = frame.getRegionHeight();
        float oX = 21;
        float oY = 28;
        batch.draw(frame, pos.x + size.x/2f -oX, pos.y + size.y/2f -oY, oX, oY, w, h, scale, scale, angle);
    }
}
