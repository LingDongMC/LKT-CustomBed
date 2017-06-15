package net.mmyz.custombed.eventlistener;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
				JsonObject temp = (JsonObject) jp.parse(new FileReader("plugins/CustomBed/Temp/data.json"));
				
                JsonArray playerDataArray = temp.get("PlayerData").getAsJsonArray();

                String playName = playerDataArray.get(0).getAsJsonObject().get("PlayerName").getAsString();
                String markTool = playerDataArray.get(0).getAsJsonObject().get("MarkTool").getAsString();
				
				if(e.getPlayer().getName().equals(playName)
					&& e.getItem().getType().name().equalsIgnoreCase(markTool)){
                    
					Location l = e.getClickedBlock().getLocation();

                    JsonObject locationDataElement = new JsonObject();
                    locationDataElement.addProperty("LX",l.getBlockX());
                    locationDataElement.addProperty("LY",l.getBlockY());
                    locationDataElement.addProperty("LZ",l.getBlockZ());
                     
                    playerDataArray.add(locationDataElement);

                    temp.add("PlayerData",playerDataArray);
                     
                     FileOutputStream fos = new FileOutputStream("plugins/CustomBed/Temp/data.json");
                     OutputStreamWriter osw = new OutputStreamWriter(fos);
                     osw.write(temp.toString());
                     osw.close();
                     fos.close();
				}
			} catch (JsonIOException e1) {
				e1.printStackTrace();
			} catch (JsonSyntaxException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
			
		}
	}
