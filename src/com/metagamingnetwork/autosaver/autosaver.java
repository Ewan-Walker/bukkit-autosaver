package com.metagamingnetwork.autosaver;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class autosaver extends JavaPlugin{
	long backups;
	long interval;
	int backID2 = 0;
	int taskID2 = 0;
	String logger;
	public void onLoad(){
		this.saveDefaultConfig();
	}
	@Override
	public void onEnable(){
		setup();
	}
	
	@Override
	public void onDisable(){
		if(taskID2 != 0)
			Bukkit.getServer().getScheduler().cancelTask(taskID2);
		taskID2 = 0;
		if(backID2 != 0)
			Bukkit.getServer().getScheduler().cancelTask(backID2);
		backID2 = 0;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
			if(cmd.getName().equalsIgnoreCase("autosave")){
				if(args.length > 1){
					sender.sendMessage("You are using too many arguements");
				}else if(args.length < 1){
					sender.sendMessage("You did not provide an argument");
				} else {
					if(args[0].equals("enable")){
						if(taskID2 != 0){
							sender.sendMessage("AutoSave is already enabled.");
						} else {
							taskID2 = turnMeOn();
							if(taskID2 != 0){
								sender.sendMessage("AutoSave enabled.");
								if(this.getConfig().getString("status").equalsIgnoreCase("disabled")){
									this.getConfig().set("status", "enabled");
									reConf();
								}
							}
						}
					} else if(args[0].equals("disable")){
						Bukkit.getServer().getScheduler().cancelTask(taskID2);
						taskID2 = 0;
						if(taskID2==0){
							sender.sendMessage("AutoSave disabled.");
							if(this.getConfig().getString("status").equalsIgnoreCase("enabled")){
								this.getConfig().set("status", "disabled");
								reConf();
							}
						}
					} else if(args[0].equals("save")){
						saveWorld();
						sender.sendMessage("World Saved.");
					} else if(args[0].equals("reload")){
						this.reloadConfig();
						setup();
					} else {
						sender.sendMessage("Invalid argument");
					}
				}
			}
		return true;
	}
	public void setup(){
		if(taskID2 != 0){
			Bukkit.getServer().getScheduler().cancelTask(taskID2);
			taskID2 = 0;
		}
		if(backID2 != 0){
			Bukkit.getServer().getScheduler().cancelTask(backID2);
			backID2 = 0;
		}
		backups = this.getConfig().getLong("backups");
		backups = (backups * 60) * 20;
		logger = this.getConfig().getString("log");
		interval = this.getConfig().getLong("interval");
		interval = (interval * 60) * 20;
		if(this.getConfig().getString("status").equalsIgnoreCase("enabled")){
			taskID2 = turnMeOn();
			getLogger().info("\u001B[32m is enabled. \u001B[0m");
		} else if(this.getConfig().getString("status").equalsIgnoreCase("disabled")){
			getLogger().info("\u001B[32m is disabled. \u001B[0m");
		}
		if(this.getConfig().getString("backup").equalsIgnoreCase("enabled")){
			backID2 = backUpProcess();
			getLogger().info("\u001B[32m backup process is enabled. \u001B[0m");
		} else {
			getLogger().info("\u001B[32m backup process is disabled. \u001B[0m");
		}
	}
	public int turnMeOn(){
			//Schedule task to be run on an interval
			int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
				@Override
				public void run() {	
					//Invoke world and player saving
					Bukkit.savePlayers();
					for (World world : Bukkit.getWorlds()){
						world.save();
					}
					if(logger.equalsIgnoreCase("enabled"))
						getLogger().info("\u001B[32m world and player data has been saved. \u001B[0m");
				}
			}, interval, interval);	
		return taskID;
	}
	public int backUpProcess(){
		int backID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				worldBacker wb = new worldBacker();
				if(logger.equalsIgnoreCase("enabled"))
					getLogger().info("\u001B[32m server has been backed up. \u001B[0m");
				wb = null;
			}
		}, backups, backups);
		return backID;
	}
	public void saveWorld() {
		//Invoke world and player saving
		Bukkit.savePlayers();
		for (World world : Bukkit.getWorlds()){
			world.save();
		}
		getLogger().info("\u001B[32m world and player data has been [force] saved. \u001B[0m");
	}
	//Called when there are changes to the configuration
	public void reConf(){
		this.saveConfig();
		this.reloadConfig();
	}
}
