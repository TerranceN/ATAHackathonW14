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
                // sometimes jump away fro mthe wall, and other times stay on it
                if (player.isOnWall() && rand.nextBoolean()) {
                    dir = -dir;
                }
            }
            
            Vector2 pos = player.pos.cpy();
            Vector2 centre = player.getCentre();
            Vector2 size = player.size.cpy();
            // Turn when you hit a wall and not in the air
            Vector2 p1 = new Vector2(pos.x + size.x/2 + (size.x/2 + 20) * dir, pos.y + 2);
            Vector2 p2 = new Vector2(pos.x + size.x/2 + (size.x/2 + 20) * dir, pos.y + size.y - 2);
            if (player.isOnGround() && (map.isInsideLevel(p1) || map.isInsideLevel(p2))) {
                dir = -dir;
            // Walk in a random direction after landing or after spawning
            } else if (player.hasStatus(Status.LAND_STUN) || (player.lives > 0 && !player.alive)) {
                dir = rand.nextBoolean() ? 1 : -1;
            }
            // look for floating platforms to dash to for wall jumps
            if (!player.isOnGround() && player.getNumAirDashes() > 0 && player.getCooldown(Action.JUMP) == 0.0f) {
                float dash_distance = player.getDashDistance();
                Vector2 p = centre.cpy().add(dash_distance * dir, 0);
                if (map.isInsideLevel(p)) {
                    AIButton btn = (AIButton) getButton(Action.DASH);
                    btn.press();
                }
            }

            AIAxis axis = (AIAxis) getAxis(Action.MOVE_HORIZONTAL);
            axis.setValue(dir);            
        }
    }
}
