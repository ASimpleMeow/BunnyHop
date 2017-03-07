/**
 * @file        Life
 * @author      Oleksandr Kononov 20071032
 * @assignment  BunnyHop
 * @brief       Gives the player an extra life for collecting this asset
 *
 * @notes       
 */

package ie.wit.cgd.bunnyhop.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ie.wit.cgd.bunnyhop.game.Assets;

public class Life extends AbstractGameObject{
	
	private TextureRegion	regLife;
	public boolean			collected;
	
	public Life(){
		init();
	}
	
	private void init(){
		dimension.set(0.5f,0.5f);
		regLife = Assets.instance.bunny.head;
		//Set bounding box for collision detection
		bounds.set(0,0,dimension.x,dimension.y);
		collected = false;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		if (collected) return;
		
		TextureRegion reg = null;
		reg = regLife;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x,
                scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
                false, false);
	}
}
