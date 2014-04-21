package com.ashenrider.game.AI;

import java.util.Random;

import com.ashenrider.game.Map;
import com.ashenrider.game.Scene;
import com.ashenrider.game.Buffs.Buff;
import com.ashenrider.game.Buffs.Buff.Status;
import com.ashenrider.game.Entities.Player;
import com.ashenrider.game.Entities.Player.Action;
import com.ashenrider.game.Input.PlayerInput;
import com.badlogic.gdx.math.Vector2;

public class AILogic extends PlayerInput {
    private Player player = null;
    private Scene scene = null;
    private Map map = null;
    
    private float dir = 1;
    Random rand;
    
    public AILogic() {
        super(AI,
            new AIAxis(),
            new AIAxis(),
            new AIAxis(),
            new AIAxis(),
            new AIButton(),
            new AIButton(),
            new AIButton(),
            new AIButton(),
            new AIButton(),
            new AIButton(),
            new AIButton());
        rand = new Random();
    }
    
    // new Scene
    public void setPlayer(Player player) {
        this.player = player;
        this.scene = player.scene;
        this.map = scene.map;
    }
    
    // destroy scene
    public void gameEnded() {
        this.player = null;
        this.scene = null;
        this.map = null;
    }
    
    @Override
    public void update() {
        super.update();
        // update buttons here based on that the computer player wants to do
        if (player != null) {
            if ((player.isOnGround() || player.isOnWall()) && player.getCooldown(Action.JUMP) == 0.0f) {
                AIButton btn = (AIButton) getButton(Action.JUMP);
                btn.press();
            }
            
            Vector2 pos = player.pos.cpy();
            Vector2 centre = player.getCentre();
            Vector2 size = player.size.cpy();
            // Turn when you hit a wall
            if (map.isInsideLevel(pos.x + size.x/2 + (size.x/2 + 20) * dir, pos.y + 2) || map.isInsideLevel(pos.x + size.x/2 + (size.x/2 + 20) * dir, pos.y + size.y - 2)) {
                dir = -dir;
            // Walk in a random direction after landing or after spawning
            } else if (player.hasStatus(Status.LAND_STUN) || (player.lives > 0 && !player.alive)) {
                dir = rand.nextBoolean() ? 1 : -1;
            }

            AIAxis axis = (AIAxis) getAxis(Action.MOVE_HORIZONTAL);
            axis.setValue(dir);            
        }
    }
}
