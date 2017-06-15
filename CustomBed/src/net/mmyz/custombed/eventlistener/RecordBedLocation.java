package net.mmyz.custombed.eventlistener;

//import org.bukkit.Location;
//import org.bukkit.entity.Player;
//import org.bukkit.Location;
//import org.bukkit.entity.Player;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
//import org.bukkit.event.block.Action;
//import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public final class RecordBedLocation implements Listener{

	
	@EventHandler
	public void getBedLocation(PlayerInteractEvent e){
		if (new File("plugins/CustomBed/Temp/data.json").exists() 
				&& e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			try {
				JsonParser jp = new JsonParser();
				JsonObject jo1 = (JsonObject) jp.parse(new FileReader("plugins/CustomBed/Temp/data.json"));
				System.out.println(123456789);
				JsonArray ja = jo1.get("PlayerData").getAsJsonArray();

				String playName = ((JsonObject)ja.get(0)).get("PlayerName").getAsString();
				String markTool = ((JsonObject)ja.get(1)).get("MarkTool").getAsString();
				System.out.println(markTool);
				
//				if(e.getPlayer().getName().equals(playName)
//					&& e.getItem().getType().name().equalsIgnoreCase(markTool)){
//					//this.itemStack为null，不会执行这一句
//					System.out.println("进来了");
//				}
			} catch (JsonIOException e1) {
				e1.printStackTrace();
			} catch (JsonSyntaxException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
			
//			Player player = e.getPlayer();
//			Location l = e.getClickedBlock().getLocation();
//			System.out.println(player.getName()+"BedName:"+"   "+MyCommand.bedName+"   "+"X:"+l.getBlockX()+"   "+"Y:"+l.getBlockY()+"   "+"Z:"+l.getBlockZ());
		}
	}