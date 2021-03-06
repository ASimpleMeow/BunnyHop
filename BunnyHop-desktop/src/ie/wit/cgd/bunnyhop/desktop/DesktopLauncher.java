/**
 * @file        DesktopLauncher
 * @author      Oleksandr Kononov 20071032
 * @assignment  BunnyHop
 * @brief       Launches the game with desktop settings
 *
 * @notes       
 */

package ie.wit.cgd.bunnyhop.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ie.wit.cgd.bunnyhop.BunnyHopMain;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class DesktopLauncher {
    private static boolean  rebuildAtlas        = false;
    private static boolean  drawDebugOutline    = false;

    public static void main(String[] args) {
        if (rebuildAtlas) {
            Settings settings = new Settings();
            settings.maxWidth = 1024;
            settings.maxHeight = 1024;
            settings.debug = drawDebugOutline;
            TexturePacker.process(settings, "assets-raw/images", "../android/assets/images",
                    "bunnyhop.atlas");
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "BunnyHop";
        config.width = 800;
        config.height = 480;
        new LwjglApplication(new BunnyHopMain(), config);
    }
}
