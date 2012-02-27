package tk.tyzoid.plugins.colors;

import java.util.HashMap;

import org.bukkit.plugin.PluginDescriptionFile;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import tk.tyzoid.plugins.lib.Perms;
import tk.tyzoid.plugins.lib.settings;
import tk.tyzoid.plugins.listeners.colorsPListener;


/**
 * Message plugin for Bukkit
 *
 * @author tyzoid
 */
public class colors extends JavaPlugin {
	public String pluginname = "Colors";

    public settings colorSettings = new settings();
    private final colorsPListener playerListener = new colorsPListener(this);
    private final HashMap<Player, Boolean> colorify = new HashMap<Player, Boolean>();
    public Perms permissionHandler;

    public void onDisable() {
        System.out.println("[" + pluginname +"] " + pluginname + " is closing...");
        playerListener.savePSNames();
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvents(playerListener, this);
        
        //pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Highest, this);
        //pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Highest, this);
        //pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        //pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[" + pluginname + "] Starting " + pluginname + " v" + pdfFile.getVersion() + "...");
        
        setupPermissions();
        
        colorSettings.readSettings();
        playerListener.plugin_init();
    }
    
    private void setupPermissions(){
    	permissionHandler = new Perms(this);
    }
    
    public boolean hasPermission(Player p, String node, boolean defaultValue){
    	return permissionHandler.hasPermission(p, node, defaultValue);
    }
    
    public boolean isChatColoring(final Player player) {
        if (colorify.containsKey(player)) {
            return colorify.get(player);
        } else {
            return false;
        }
    }

    public void toggleChatColoring(final Player player) {
    	boolean value = !isChatColoring(player);
    	if(value){
    		player.sendMessage("§1C§2o§3l§4o§5r§6s §fare now enabled");
    	} else {
    		player.sendMessage("§1C§2o§3l§4o§5r§6s §fare now disabled");
    	}
        colorify.put(player, value);
    }
    
    public String colorsChat(String message){
    	char[] charMessage = message.toCharArray();
    	String finalMessage ="";
    	int color = 1;
    	for(int i = 0; i < charMessage.length; i++){
    		finalMessage += "§" + Integer.toHexString(color);
    		finalMessage += charMessage[i];
    		color++;
    		if(color >= 16){
    			color = 1;
    		}
    	}
    	
    	return finalMessage;
    }
    
    public String convertToColor(String withoutColor, boolean rainbowAllowed){
    	int count = withoutColor.length();
    	char[] colorless = withoutColor.toCharArray();
    	//char[] colored = withoutColor.toCharArray();
    	String withColor = "";
    	for(int i = 0; i < count; i++){
    		if(isColorChar(colorless[i]) && (i+1) < count){
    			if(isColorNumber(colorless[i+1])){
    				withColor += "§";
    			} else if(Character.toLowerCase(colorless[i+1]) == 'r' && rainbowAllowed){
    				boolean found = false;
    				int indexOfColorChar = i+2;
    				String rainbowString = new String(colorless);
    				
    				while(indexOfColorChar < count && !found){
    					found = isColorChar(colorless[indexOfColorChar]) && (isColorNumber(colorless[indexOfColorChar+1]) || Character.toLowerCase(colorless[indexOfColorChar+1]) == 'r');
    					indexOfColorChar++;
    				}
    				
    				if(found){
    					indexOfColorChar--;
    				}
    				
    				rainbowString = colorsChat(rainbowString.substring(i+2, indexOfColorChar));
    				withColor += rainbowString;
    				
    				i = indexOfColorChar - 1;
    				
    			} else {
    				withColor += colorless[i];
    			}
    		} else {
    			withColor += colorless[i];
    		}
    	}
    	return withColor;
    }
    
    public boolean isColorChar(char c){
    	String[] chars = colorSettings.getProperty("color-chars").split(",");
    	boolean charUsed = false;
		for(int i = 0; (i < chars.length && !charUsed); i++){
			if(c == chars[i].toCharArray()[0]){
				charUsed = true;
			}
		}
    	return charUsed;
    }
    
    public boolean isColorNumber(char c){
    	c = Character.toLowerCase(c);
    	return ((c == '0') || (c == '1') || (c == '2') || (c == '3') || (c == '4') || (c == '5') || (c == '6') || (c == '7') || (c == '8') || (c == '9') || (c == 'a') || (c == 'b') || (c == 'c') || (c == 'd') || (c == 'e') || (c == 'f'));
    }
}