package com.ashenrider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Projectile extends Entity {
	SpriteBatch batch;
	Texture img;
	
	private int shotBy;
	private float friendlyFireTimer;
	
	public float SPEED = 750.0f;
	
	// frames per particle spawn
	private int particleTimer = 0;
	
	public Projectile(Vector2 initPosition, Vector2 direction, int playerNumber) {
		super(initPosition);
		size = new Vector2(8, 8);
		if (direction.isZero()) {
			speed = new Vector2(0, SPEED);
		} else {
			speed = direction.scl(SPEED / direction.len());
		}
		shotBy = playerNumber;
		batch = new SpriteBatch();
		img = new Texture("projectile.png");
		
		// you cannot hurt yourself within the first fraction of a second that a shot is fired
		friendlyFireTimer = 0.75f;
		falls = false;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		friendlyFireTimer -= dt;
		
		Particle p = new Particle(getCentre(), speed.cpy().scl(-1), 0.3f, 0.3f);
		scene.addEntity(p, Scene.PARTICLE_LAYER);
	}

    @Override
    public boolean handleCollision(Map map) {
    	boolean collided = false;
    	// check if the projectile is currently overlapping a wall based on its size and position
    	Vector2 pen = map.getLeastPenetration(speed, pos, pos.cpy().add(size));
        if (pen.len() > 0) {
        	collided = true;
        }
        //
        for (Player p : scene.players) {
        	if (p.number != shotBy || friendlyFireTimer <= 0) {
        		Rectangle shotBox = getBounds();
        		Rectangle playerBox = p.getBounds();
        		if (shotBox.overlaps(playerBox)) {
        			collided = true;
        		}
        	}
        }
        
    	// TODO
    	// and check the line of the projectiles path to see if it moved through any thin walls since the last frame
        if (collided) {
        	// bouncing projectiles could be cool too
        	// spawn particles here, possibly based on the normal of collision or the velocity
        	destroy();
        }
        return collided;
    }
    
	@Override
	public void render(OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// center the image at the center of this object's hitbox
		batch.draw(img, pos.x + (size.x - img.getWidth())/2, pos.y + (size.y - img.getHeight())/2);
		batch.end();
	}
	
}
