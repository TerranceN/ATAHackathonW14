package com.ashenrider.game.userinterface;

import com.ashenrider.game.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Intro {

    float scale = 1.0f;
    float animationTime = 0.0f;

    private static Animation staticAnim;
    private static SpriteBatch batch;
    private static int NUM_FRAMES;

    private float FRAME_DURATION = 0.025f;
    private Animation anim;
    
    private int LOOP_FRAME;
    private float LOOP_DELAY = 2.25f;
    
    float angle;

    public Intro() {       
        if (staticAnim == null) {
            TextureAtlas atlas = Assets.manager.get("pack/intro.atlas", TextureAtlas.class);
            TextureRegion[] frames = new TextureRegion[181];
            NUM_FRAMES = frames.length;
            LOOP_FRAME = 170;
            for (int i=0; i<NUM_FRAMES; i++) {
                frames[i] = atlas.findRegion(String.format("intro-%05d", i));
            }
            anim = new Animation(FRAME_DURATION, frames);
            staticAnim = anim;

        	batch = new SpriteBatch();
        } else {
            anim = staticAnim;
        }
        
        // 170-180 are loopable
    }

    public TextureRegion getSprite() {
        return anim.getKeyFrame(animationTime, false);
    }

    public void update(float dt) {
        animationTime += dt;
        // every 2s or so, play the last 10 frames of the animation again
        if (animationTime > FRAME_DURATION * NUM_FRAMES + LOOP_DELAY) {
        	animationTime = LOOP_FRAME * FRAME_DURATION;
        }
        
    }

    public void skip() {
    	animationTime = FRAME_DURATION * LOOP_FRAME;
    }
    
    public boolean isFinished() {
    	return animationTime >= FRAME_DURATION * LOOP_FRAME;
    }
    
    public void render(float screenWidth, float screenHeight) {
        TextureRegion frame = getSprite();
        batch.begin();
        batch.draw(frame, (screenWidth - frame.getRegionWidth())/2f, (screenHeight - frame.getRegionHeight())/2f);
        batch.end();
    }
}
