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
				JsonParser temp = new JsonParser();
				JsonObject jo = (JsonObject) temp.parse(new FileReader("plugins/CustomBed/Temp/data.json"));
				System.out.println(123456789);
                JsonArray playerDataArray = jo.get("PlayerData").getAsJsonArray();

                String playName = ((JsonObject)playerDataArray.get(0)).get("PlayerName").getAsString();
                String markTool = ((JsonObject)playerDataArray.get(1)).get("MarkTool").getAsString();
				System.out.println(markTool);
				
				if(e.getPlayer().getName().equals(playName)
					&& e.getItem().getType().name().equalsIgnoreCase(markTool)){

					System.out.println("进来了");
                    
//                  Player player = e.getPlayer();
//                  Location l = e.getClickedBlock().getLocation();
//		        	System.out.println(player.getName()+"BedName:"+"   "+MyCommand.bedName+"   "+"X:"+l.getBlockX()+"   "+"Y:"+l.getBlockY()+"   "+"Z:"+l.getBlockZ());
                    
                     JsonObject temp = new JsonObject();

                     JsonArray locationDataArray = new JsonArray();

                     JsonObject locatioDataElement = new JsonObject();
                     locationDataElement.addProperty("LX",l.getBlockX());
                     locationDataElement.addProperty("LY",l.getBlockY());
                     locationDataElement.addProperty("LZ",l.getBlockZ());
                     
                     locationDataArray.add(locationDataElement);

                     temp.add("LocationData",locationDataArray);
                     
                     fos = new FileOutputStream("plugins/CustomBed/Temp/data.json");
                     OutputStreamWriter osw = new OutputStreamWriter(fos);
                     osw.write("\n"+temp.toString());
                     osw.close();
                     fos.close();
				}
			} catch (JsonIOException e1) {
				e1.printStackTrace();
			} catch (JsonSyntaxException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
			
		}
	}
