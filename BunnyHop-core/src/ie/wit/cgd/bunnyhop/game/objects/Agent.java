/**
 * @file        Agent
 * @author      Oleksandr Kononov 20071032
 * @assignment  BunnyHop
 * @brief       The AI assets for the game
 *
 * @notes       Was made and implemented by me to be added as an
 * 				extra feature to the game as part of the assignment
 */

package ie.wit.cgd.bunnyhop.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ie.wit.cgd.bunnyhop.game.Assets;

public class Agent extends AbstractGameObject{
	
	public static final String TAG = Agent.class.getName();
	
	private final float         CHANGE_DIRECTION_TIME     = 5f;
	private final float			FOLLOWING_SPEED			  = 1.5f;
	private final float 		FOLLOWING_TIME			  = 1.5f;
	private final float			JUMP_TIME				  = 0.75f;
	
	public enum VIEW_DIRECTION{
    	LEFT, RIGHT
    }
	
	public enum STATE{
		IDLE, FOLLOWING, JUMPING;
	}
	
	private TextureRegion		regAgent;
	public VIEW_DIRECTION		viewDirection;
	public STATE				state;
	public float				directionChangeTimer;
	public float				jumpingTimer;
	public float				followingTimer;
	
	public AbstractGameObject	target;
	public boolean				dead;
	
	
	public Agent(){
		init();
	}
	
	private void init(){
		dimension.set(1, 1);
		
		regAgent = Assets.instance.agent.agent;
		target = null;
		
		origin.set(dimension.x / 2, dimension.y / 2);         // Center image on game object

        bounds.set(0, 0, dimension.x, dimension.y);           // Bounding box for collision detection
        
        terminalVelocity.set(1.0f, 1.0f);                     // Set physics values
        friction.set(12.0f, 0.0f);
        acceleration.set(0.0f, -25.0f);
        
        viewDirection = VIEW_DIRECTION.RIGHT;				  //View Direction
        state = STATE.IDLE;									  //Agents State
        
        directionChangeTimer = CHANGE_DIRECTION_TIME;
        followingTimer = FOLLOWING_TIME;
        dead = false;
		jumpingTimer = 0;
	}
	
	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
		
		if(state == STATE.IDLE){
			directionChangeTimer -= deltaTime;
			if(directionChangeTimer <= 0){
				viewDirection = (viewDirection == VIEW_DIRECTION.LEFT)? VIEW_DIRECTION.RIGHT : VIEW_DIRECTION.LEFT;
				directionChangeTimer = CHANGE_DIRECTION_TIME;
			}
		}
		
		if(state == STATE.FOLLOWING){
			followingTimer -= deltaTime;
			
			if(followingTimer <= 0){
			setTarget(null);
			followingTimer = FOLLOWING_TIME;
			state = STATE.IDLE;
			}
		}
	}
	
	@Override
	public void render(SpriteBatch batch){
		if(dead)return;
		TextureRegion reg = null;
		
		// Draw image
        reg = regAgent;
        batch.draw(reg.getTexture(), position.x, position.y, origin.x,
                origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation,
                reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
                reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.RIGHT,
                false);
	}
	
	public void setTarget(AbstractGameObject target){
		this.target = target;
		setFollowing((this.target != null)? true : false);
	}
	
	public boolean hasTarget(){
		return target != null;
	}
	
	private void setFollowing(boolean follow){
		switch(state){
		case IDLE:
			if(follow){
				followingTimer = FOLLOWING_TIME;
				state = STATE.FOLLOWING;
			}
			break;
		case FOLLOWING:
			if(!follow) state = STATE.IDLE;
			break;
		case JUMPING:
			break;
		}
	}
	
	public boolean isFollowing(){
		return state == STATE.FOLLOWING;
	}
	
	public void setDirectionToLeft(boolean setLeft){
		if(setLeft){
			viewDirection = VIEW_DIRECTION.LEFT;
			directionChangeTimer = CHANGE_DIRECTION_TIME;
		}
		else{
			viewDirection = VIEW_DIRECTION.RIGHT;
			directionChangeTimer = CHANGE_DIRECTION_TIME;
		}
	}
	
	public void setJumping(boolean edgeReached){
		switch(state){
		case IDLE:
		case FOLLOWING:
			jumpingTimer = JUMP_TIME;
			if(edgeReached) state = STATE.JUMPING;
			break;
		case JUMPING:
			break;
		}
	}
	
	@Override
	public void updateMotionY(float deltaTime){
		super.updateMotionY(deltaTime);
		switch(state){
		case IDLE:
			break;
		case FOLLOWING:
			break;
		case JUMPING:
			jumpingTimer -= deltaTime;
            // Jump time left?
            if (jumpingTimer > 0) {
                // Still jumping
                velocity.y = terminalVelocity.y;
            }else{
            	if(hasTarget()) state = STATE.FOLLOWING;
            }
            break;
		}
	}
	
	@Override
	public void updateMotionX(float deltaTime){
		super.updateMotionX(deltaTime);
		switch(state){
		case IDLE:
			velocity.x = (viewDirection == VIEW_DIRECTION.LEFT)?
					-terminalVelocity.x : terminalVelocity.x;
			break;
		case JUMPING:
		case FOLLOWING:
			if(target.position.x > position.x){
				velocity.x = terminalVelocity.x * FOLLOWING_SPEED;
				viewDirection = VIEW_DIRECTION.RIGHT;
			}else{
				velocity.x = -terminalVelocity.x * FOLLOWING_SPEED;
				viewDirection = VIEW_DIRECTION.LEFT;
			}
			break;
		}
	}
	
	public int getScore(){
		return 150;
	}
}
