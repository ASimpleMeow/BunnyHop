/**
 * @file        WorldController
 * @author      Oleksandr Kononov 20071032
 * @assignment  BunnyHop
 * @brief       Controls the logic of the game
 *
 * @notes       
 */

package ie.wit.cgd.bunnyhop.game;

import com.badlogic.gdx.graphics.Pixmap;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import ie.wit.cgd.bunnyhop.util.CameraHelper;
import ie.wit.cgd.bunnyhop.game.objects.Rock;
import ie.wit.cgd.bunnyhop.util.Constants;
import ie.wit.cgd.bunnyhop.util.LevelGenerator;

import com.badlogic.gdx.math.Rectangle;

import ie.wit.cgd.bunnyhop.game.objects.Agent;
import ie.wit.cgd.bunnyhop.game.objects.BunnyHead;
import ie.wit.cgd.bunnyhop.game.objects.BunnyHead.JUMP_STATE;
import ie.wit.cgd.bunnyhop.game.objects.Feather;
import ie.wit.cgd.bunnyhop.game.objects.Goal;
import ie.wit.cgd.bunnyhop.game.objects.GoldCoin;
import ie.wit.cgd.bunnyhop.game.objects.Life;

public class WorldController extends InputAdapter{

	private static final String TAG = WorldController.class.getName();

    public CameraHelper 		cameraHelper;
    public Level    			level;
    public int      			lives;
    public int      			score;
    public LevelGenerator		levelGen;
    public Pixmap				currentLevel;
    

    
    private float   timeLeftGameOverDelay;

    // Rectangles for collision detection
    private Rectangle   r1  = new Rectangle();
    private Rectangle   r2  = new Rectangle();
    
    public WorldController() {
        init();
    }

    private void init() {
    	levelGen = new LevelGenerator();
    	currentLevel = levelGen.generateLevel(new Pixmap(128,32,Pixmap.Format.RGBA8888),
        		Constants.LEVEL_GEN_X,Constants.LEVEL_GEN_Y);
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        timeLeftGameOverDelay = 0;
        initLevel();
    }

    private void initLevel() {
        score = 0;
        level = new Level(currentLevel);
        cameraHelper.setTarget(level.bunnyHead);
    }

    public void update(float deltaTime) {
        handleDebugInput(deltaTime);
        if (isGameOver() ) {
            timeLeftGameOverDelay -= deltaTime;
            if (timeLeftGameOverDelay < 0) init();
        } else {
            handleInputGame(deltaTime);
        }
        
        if(isGameWon()){
        	currentLevel = levelGen.generateLevel(currentLevel,Constants.LEVEL_GEN_X, Constants.LEVEL_GEN_Y);
        	initLevel();
        }
        
        for(Agent agent : level.agents)
        	if(level.bunnyHead.position.dst2(agent.position)  <= 3.0f){
            	agent.setTarget(level.bunnyHead);
            }
        	
        level.update(deltaTime);
        testCollisions();
        cameraHelper.update(deltaTime);
        if (!isGameOver() && (isPlayerInWater())) {
            lives--;
            if (isGameOver())timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            else
                initLevel();
        }
    }
    
    private void handleDebugInput(float deltaTime) {
        if (Gdx.app.getType() != ApplicationType.Desktop) return;

        
     // Camera Controls (move)
        if (!cameraHelper.hasTarget(level.bunnyHead)) { 

            float camMoveSpeed = 5 * deltaTime;
            float camMoveSpeedAccelerationFactor = 5;
            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor;
            if (Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed, 0);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed, 0);
            if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
            if (Gdx.input.isKeyPressed(Keys.DOWN)) moveCamera(0, -camMoveSpeed);
            if (Gdx.input.isKeyPressed(Keys.BACKSPACE)) cameraHelper.setPosition(0, 0);
        } 
        
        // Camera Controls (zoom)
        float camZoomSpeed = 1 * deltaTime;
        float camZoomSpeedAccelerationFactor = 5;
        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *= camZoomSpeedAccelerationFactor;
        if (Gdx.input.isKeyPressed(Keys.COMMA)) cameraHelper.addZoom(camZoomSpeed);
        if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(-camZoomSpeed);
        if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
    }

    private void moveCamera(float x, float y) {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }
    
    @Override
    public boolean keyUp(int keycode) {

        if (keycode == Keys.G) {                            // Reset game world with new level
            init();
            Gdx.app.debug(TAG, "Game world resetted");
        } else if (keycode == Keys.R) {                 //Restart current level
            initLevel();
            Gdx.app.debug(TAG, "New level generated");
        }
        else if (keycode == Keys.F) {                 //Gives a feather powerup
        	level.bunnyHead.setFeatherPowerup(true);
            Gdx.app.debug(TAG, "Feather powerup granted");
        }else if (keycode == Keys.ENTER) {                 // Toggle camera follow
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.bunnyHead);
            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
        }
        return false;
    }
    
    private void handleInputGame(float deltaTime) {
        if (cameraHelper.hasTarget(level.bunnyHead)) {
            // Player Movement
            if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
            } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
            } else {
                // Execute auto-forward movement on non-desktop platform
                if (Gdx.app.getType() != ApplicationType.Desktop) {
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
                }
            }
            
            // Bunny Jump
            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) {
                level.bunnyHead.setJumping(true);
            } else {
                level.bunnyHead.setJumping(false);
            }
        }
    }
    
    private void onCollisionBunnyHeadWithRock(Rock rock) {
        BunnyHead bunnyHead = level.bunnyHead;
        float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y + rock.bounds.height));
        if (heightDifference > 0.25f) {
            boolean hitLeftEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);
            if (hitLeftEdge) {
                bunnyHead.position.x = rock.position.x + rock.bounds.width;
            } else {
                bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
            }
            return;
        }
        
        switch (bunnyHead.jumpState) {
        case GROUNDED:
            break;
        case FALLING:
        case JUMP_FALLING:
            bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
            bunnyHead.jumpState = JUMP_STATE.GROUNDED;
            break;
        case JUMP_RISING:
            bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
            break;
        }
    }
    
    private void onCollisionAgentWithRock(Agent agent,Rock rock) {
        //Hitting the edge of the other platform
        float heightDifference = Math.abs(agent.position.y - (rock.position.y + rock.bounds.height));
        if (heightDifference > 0.25f) {
        	boolean hitLeftEdge = agent.position.x > (rock.position.x + rock.bounds.width / 2.0f);
            if (hitLeftEdge) {
            	agent.setDirectionToLeft(false);
            } else {
            	agent.setDirectionToLeft(true);
            }
        }
        if(agent.position.x < rock.position.x){
    		if(agent.isFollowing()){
    			agent.setJumping(true);
    		}else {
    			agent.setDirectionToLeft(false);
    		}
    	}else if ((agent.position.x + agent.bounds.width) > (rock.position.x + rock.bounds.width)){
    		if(agent.isFollowing()) {
    			agent.setJumping(true);
    		}else{
    			agent.setDirectionToLeft(true);
    		}
    	}

        switch (agent.state) {
        case FOLLOWING:
        case IDLE:
            agent.position.y = rock.position.y + agent.bounds.height + agent.origin.y;
            break;
        case JUMPING:
            agent.position.y = rock.position.y + agent.bounds.height + agent.origin.y;
            break;
		default:
			break;
        }
    }
    
    private void onCollisionBunnyWithAgent(Agent agent){
    	if((level.bunnyHead.position.y>=agent.position.y+(agent.bounds.height*0.9)) && level.bunnyHead.velocity.y < 0){
    		agent.dead = true;
    		score += agent.getScore();
    	}else{
    		if(level.bunnyHead.canTakeDamage){
    			level.bunnyHead.canTakeDamage = false;
    			if(!isGameOver()){
    				lives--;
        			if(isGameOver())timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
    			}
        		Gdx.app.debug(TAG, "Bunny Took Damage");
    		}
    	}
    }

    private void onCollisionBunnyWithGoldCoin(GoldCoin goldcoin) {
        goldcoin.collected = true;
        score += goldcoin.getScore();
        Gdx.app.log(TAG, "Gold coin collected");
    }

    private void onCollisionBunnyWithFeather(Feather feather) {
        feather.collected = true;
        score += feather.getScore();
        level.bunnyHead.setFeatherPowerup(true);
        Gdx.app.log(TAG, "Feather collected");
    }
    
    private void onCollisionBunnyWithLife(Life life){
    	life.collected = true;
        lives += (lives < 4)? 1 : 0; //Have a max of 4 lives
        Gdx.app.log(TAG, "Life collected");
    }
    
    private void onCollisionBunnyWithGoal(Goal goal){
    	goal.collected = true;
        Gdx.app.log(TAG, "Goal Reached!");
    }
    
    private void testCollisions() {
        r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y, level.bunnyHead.bounds.width,
                level.bunnyHead.bounds.height);

        // Test collision: Bunny Head <-> Rocks
        for (Rock rock : level.rocks) {
            r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyHeadWithRock(rock);
            // IMPORTANT: must do all collisions for valid
            // edge testing on rocks.
        }
        
        // Test collision: Bunny Head <-> Agent
        for(Agent agent : level.agents){
        	if(!agent.dead){
            	r2.set(agent.position.x, agent.position.y, agent.bounds.width, agent.bounds.height);
                if (r1.overlaps(r2)) onCollisionBunnyWithAgent(agent);
            }
        }

        // Test collision: Bunny Head <-> Gold Coins
        for (GoldCoin goldCoin : level.goldCoins) {
            if (goldCoin.collected) continue;
            r2.set(goldCoin.position.x, goldCoin.position.y, goldCoin.bounds.width, goldCoin.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyWithGoldCoin(goldCoin);
            break;
        }

        // Test collision: Bunny Head <-> Feathers
        for (Feather feather : level.feathers) {
            if (feather.collected) continue;
            r2.set(feather.position.x, feather.position.y, feather.bounds.width, feather.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyWithFeather(feather);
            break;
        }
        
     // Test collision: Bunny Head <-> Lives
        for (Life life : level.lives) {
            if (life.collected) continue;
            r2.set(life.position.x, life.position.y, life.bounds.width, life.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyWithLife(life);
            break;
        }
        
        // Test collision: Bunny Head <-> Goal
        if (!level.goal.collected){
        	r2.set(level.goal.position.x, level.goal.position.y, level.goal.bounds.width,
        			level.goal.bounds.height);
            if (r1.overlaps(r2)) onCollisionBunnyWithGoal(level.goal);
        }
        
        //Test collisions: Agent <-> Rock
        for(Agent agent : level.agents){
        	r1.set(agent.position.x, agent.position.y, agent.bounds.width,
                    agent.bounds.height);
            
            //Test collision: Agent <-> Rocks
            for (Rock rock : level.rocks) {
                r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
                if (!r1.overlaps(r2)) continue;
                onCollisionAgentWithRock(agent,rock);
                
            }
        }
    }
    
    public boolean isGameOver() {
        return lives <= 0;
    }
    
    public boolean isGameWon(){
    	return level.goal.collected;
    }

    public boolean isPlayerInWater() {
        return level.bunnyHead.position.y < -5;
    }
}