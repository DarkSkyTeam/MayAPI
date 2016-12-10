package com.mayspeed.mayapi;

import org.bukkit.plugin.java.JavaPlugin;

public class MayAPIMain extends JavaPlugin {
	
	public static MayAPIMain Main;
	
	public void onEnable() {
		getLogger().info("MayAPI ря╪сть Power By May_Speed");
	}
	public static MayAPIMain getMain() {
		return Main;
	}
}
