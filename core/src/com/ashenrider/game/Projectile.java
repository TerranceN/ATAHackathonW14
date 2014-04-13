package com.ashenrider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Projectile extends Entity {
	static Texture BASE_PROJECTILE = null;
	Texture img;
	
	private int shotBy;
	private float friendlyFireTimer;
	
	public float SPEED = 750.0f;
	public float MAX_SPEED = 1000.0f;
	
	// frames per particle spawn (unused)
	private int particleTimer = 0;
	protected int type;
	
	// shoud lit bounce on wall collision or be destroyed?
	protected boolean BOUNCE_WALLS = false;
	protected boolean BOUNCE_CEILING = false;
	protected int groundBounces = 0;
	protected float ELASTICITY = 1.0f;
	
	public Projectile(Vector2 initPosition, Vector2 direction, int playerNumber) {
		super(initPosition);
		size = new Vector2(8, 8);
		speed = direction.nor().scl(SPEED);
		shotBy = playerNumber;

		if (BASE_PROJECTILE == null) {
			BASE_PROJECTILE = new Texture("projectile.png");
		}
		img = BASE_PROJECTILE;
		
		// you cannot hurt yourself within the first fraction of a second that a shot is fired
		friendlyFireTimer = 0.75f;
		falls = false;
	}

    public int getShotBy() {
        return shotBy;
    }

	@Override
	public void update(float dt) {
		super.update(dt);
		if (speed.len() > MAX_SPEED) {
			speed.scl(MAX_SPEED / speed.len());
		}

		friendlyFireTimer -= dt;
	}

    void checkTextureCollision(Map map) {
        ArrayList<Vector2> points = new ArrayList<Vector2>();

        points.add(pos);
        points.add(pos.cpy().add(new Vector2(size.x, 0)));
        points.add(pos.cpy().add(size));
        points.add(pos.cpy().add(new Vector2(0, size.y)));

        int numTexCollisionZero = 0;

        for (Vector2 p : points) {
            float modX = p.x % map.getWidth();
            float modY = p.y % map.getHeight();

            if (modX < 0) {
                modX += map.getWidth();
            }
            if (modY < 0) {
                modY += map.getHeight();
            }

            int textureValue = scene.getCollisionMaskValueAtPoint(modX, modY);

            if (textureValue == 0) {
                numTexCollisionZero += 1;
            }
        }
        if (numTexCollisionZero != 0){
            destroy();
        }
    }

    @Override
    public void handleCollision(Map map) {
    	boolean collided = false;
    	// check if the projectile is currently overlapping a wall based on its size and position
    	Vector2 pen = map.getLeastPenetration(speed, pos, pos.cpy().add(size));
        if (pen.len() > 0) {
            if (pen.y != 0 && (pen.x == 0 || Math.abs(pen.x) > Math.abs(pen.y))) {
            	// vertical collision
            	if ((pen.y < 0 && BOUNCE_CEILING) || groundBounces > 0) {
                    pos.add(new Vector2(0, pen.y));
                    speed.y = speed.y * -ELASTICITY;
                    if (pen.y > 0) {
                    	groundBounces--;
                    }
            	} else {
            		checkTextureCollision(map);
            	}
            } else {
            	// horizontal collision
            	if (BOUNCE_WALLS) {
                    pos.add(new Vector2(pen.x, 0));
                    speed.x = speed.x * - ELASTICITY;
            	} else {
            		checkTextureCollision(map);
            	}
            }
        }
        //
        for (Player p : scene.players) {
        	if (p.number != shotBy || friendlyFireTimer <= 0) {
        		Rectangle shotBox = getBounds();
        		Rectangle playerBox = p.getBounds();
        		if (shotBox.overlaps(playerBox)) {
                    if(p.onShot(this)) {
                        destroy();
                    }
        		}
        	}
        }
    }
    
	@Override
	public void render(SpriteBatch batch) {
		// center the image at the center of this object's hitbox
		batch.draw(img, pos.x + (size.x - img.getWidth())/2, pos.y + (size.y - img.getHeight())/2);
	}
	
	public int getType() {
		return type;
	}
}
