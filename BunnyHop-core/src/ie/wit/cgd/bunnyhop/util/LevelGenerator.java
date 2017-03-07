/**
 * @file        LevelGenerator
 * @author      Oleksandr Kononov 20071032
 * @assignment  BunnyHop
 * @brief       Generates a (controlled) random level
 *
 * @notes       
 */

package ie.wit.cgd.bunnyhop.util;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import ie.wit.cgd.bunnyhop.game.Level;

public class LevelGenerator {
	
	public LevelGenerator(){}
	
	@SuppressWarnings("static-access")
	public Pixmap generateLevel(Pixmap level,int maxXScale, int maxYScale){
		Pixmap.setBlending(Pixmap.Blending.None);
		level.setColor(Level.BLOCK_TYPE.EMPTY.getColor());
		level.fill();
		
		int pixelY = 0;
		int pixelX = ThreadLocalRandom.current().nextInt(1, maxXScale);
		int lastPixelX = 0;
		int lastPixelY = level.getHeight()-2;
		
		for(;pixelX < level.getWidth();){
			pixelX += ThreadLocalRandom.current().nextInt(1, maxXScale);
			pixelY = level.getHeight()- ThreadLocalRandom.current().nextInt(1, maxYScale);
			
			level.setColor(Level.BLOCK_TYPE.ITEM_GOLD_COIN.getColor());
			level.drawLine(lastPixelX,lastPixelY-1 ,pixelX, pixelY-1); //Draw coins
			
			if(ThreadLocalRandom.current().nextInt(0, 5) != 0){ //1 in 5 chance to make a pit
				level.setColor(Level.BLOCK_TYPE.ROCK.getColor());
				level.drawLine(lastPixelX,lastPixelY ,pixelX, pixelY); //Draw floor
				
				//Half the time, spawn agent + health
				if(ThreadLocalRandom.current().nextInt(0, 2) != 0){
					//Spawn on flat ground which is 3 meters long
					if((slope(lastPixelX,pixelX,lastPixelY,pixelY) == 0) && (pixelX-lastPixelX)>3){
						level.setColor(Level.BLOCK_TYPE.AGENT.getColor());
						level.drawPixel(lastPixelX, lastPixelY-1);
						
						level.setColor(Level.BLOCK_TYPE.ITEM_LIFE.getColor());
						level.drawPixel(lastPixelX-1,lastPixelY + (int)(slope(lastPixelX,pixelX,lastPixelY,pixelY))- 1);
					}
				}
				
			}else{
				level.setColor(Level.BLOCK_TYPE.ITEM_FEATHER.getColor());
				if(level.getPixel(lastPixelX, lastPixelY) == Level.BLOCK_TYPE.ROCK.getColor()){
					// Spawn feather to help get over gaps
					if(distance(lastPixelX,pixelX,lastPixelY,pixelX) > 2 || slope(lastPixelX,pixelX,lastPixelY,pixelX) <= 0){
						level.drawPixel(lastPixelX, lastPixelY-1);
					}
				}
				else if(distance(lastPixelX,pixelX,lastPixelY,pixelX) > 5){ //If the distance is too great, spawn another feather + midpoint
					level.drawPixel((pixelX+lastPixelX)/2, (pixelY+lastPixelY)/2);
					level.drawPixel(pixelX, pixelY);
				}
			}
			
			lastPixelX = pixelX;
			lastPixelY = pixelY;
		}
		
		//Player Spawn Point
		level.setColor(Level.BLOCK_TYPE.PLAYER_SPAWNPOINT.getColor());
		level.drawPixel(maxXScale, level.getHeight()-maxYScale-3); //Giving high ground
		
		//Goal Point
		level.setColor(Level.BLOCK_TYPE.GOAL.getColor());
		level.drawPixel(level.getWidth()-1, lastPixelY-2);
		
		//Generate a png of the level for debug
		if(Gdx.app.getLogLevel() == Gdx.app.LOG_DEBUG)
			PixmapIO.writePNG(new FileHandle(new File("../android/assets/levels/levelGen.png")), level);
		
		return level;
	}
	
	private float distance(int x1, int x2, int y1, int y2){
		return (float)Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
	}
	
	private int slope(int x1, int x2, int y1, int y2){
		return (y2 - y1)/(x2 - x1);
	}
}
