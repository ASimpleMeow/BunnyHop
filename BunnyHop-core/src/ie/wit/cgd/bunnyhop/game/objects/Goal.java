/**
 * @file        Goal
 * @author      Oleksandr Kononov 20071032
 * @assignment  BunnyHop
 * @brief       The goal allows player to progress to the next level
 *
 * @notes       
 */

package ie.wit.cgd.bunnyhop.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ie.wit.cgd.bunnyhop.game.Assets;

public class Goal extends AbstractGameObject{
	
	private TextureRegion	regGoal;
	public boolean			collected;
	
	public Goal(){
		init();
	}
	
	private void init(){
		dimension.set(1f,1f);
		regGoal = Assets.instance.goal.goal;
		//Set bounding box for collision detection
		bounds.set(0,0,dimension.x,dimension.y);
		collected = false;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		if (collected) return;
		
		TextureRegion reg = null;
		reg = regGoal;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x,
                scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
                false, false);
	}
	
}
