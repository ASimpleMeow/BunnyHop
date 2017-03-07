/**
 * @file        Constants
 * @author      Oleksandr Kononov 20071032
 * @assignment  BunnyHop
 * @brief       Constant values that are used in the game
 *
 * @notes       
 */

package ie.wit.cgd.bunnyhop.util;

public class Constants {

	public static final float VIEWPORT_WIDTH            = 5.0f;         // Visible game world is 5 meters wide
    public static final float VIEWPORT_HEIGHT           = 5.0f;         // Visible game world is 5 meters tall

    public static final float VIEWPORT_GUI_WIDTH        = 800.0f;       // GUI Width
    public static final float VIEWPORT_GUI_HEIGHT       = 480.0f;       // GUI Height

    // Location of description file for texture atlas
    public static final String TEXTURE_ATLAS_OBJECTS    = "images/bunnyhop.atlas";
    
    //Location of image file for generatedLevel
    public static final String LEVEL_GEN                = "levels/levelGen.png";  
    
    //Value for level generator
    public static final int LEVEL_GEN_X					= 7;
    public static final int LEVEL_GEN_Y					= 6;

    // Duration of feather power-up in seconds
    public static final float ITEM_FEATHER_POWERUP_DURATION = 4;
    
    // Delay after game over
    public static final float TIME_DELAY_GAME_OVER = 3;
    
    // Amount of time the player is invincible after taking damage
    public static final float INVINCIBLITY_TIME = 2;
    
    public static final int LIVES_START                 = 3;            // Amount of extra lives at level start
}