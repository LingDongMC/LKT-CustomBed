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
		if (new File("plugins/CustomBed/Temp/data.json").exists() && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			try {
				JsonParser jp = new JsonParser();
				JsonObject temp = (JsonObject) jp.parse(new FileReader("plugins/CustomBed/Temp/data.json"));
				
                JsonArray playerDataArray = temp.get(e.getPlayer().getName()).getAsJsonArray();

                String markTool = playerDataArray.get(0).getAsJsonObject().get("MarkTool").getAsString();
				
				if(markTool.equalsIgnoreCase(e.getItem().getType().name())){
					if (playerDataArray.size() == 1) {
						Location l = e.getClickedBlock().getLocation();
						
						JsonObject locationDataElement = new JsonObject();
						locationDataElement.addProperty("LX",l.getBlockX());
						locationDataElement.addProperty("LY",l.getBlockY());
						locationDataElement.addProperty("LZ",l.getBlockZ());
						
						playerDataArray.add(locationDataElement);
						
						temp.add(e.getPlayer().getName(),playerDataArray);
						
						e.getPlayer().sendMessage("床位置为：X="+l.getBlockX()+";Y="+l.getBlockY()+";Z="+l.getBlockZ());
						
						FileOutputStream fos = new FileOutputStream("plugins/CustomBed/Temp/data.json");
						OutputStreamWriter osw = new OutputStreamWriter(fos);
						osw.write(temp.toString());
						osw.close();
						fos.close();
					}else if(playerDataArray.size() == 2){
						JsonObject newTemp = new JsonObject();
						
						JsonArray newPlayerDataArray = new JsonArray();

						JsonObject newPlayerDataElement = new JsonObject();

						newPlayerDataElement.addProperty("MarkTool", markTool);
						
						Location l = e.getClickedBlock().getLocation();
						
						JsonObject newLocationDataElement = new JsonObject();

						newLocationDataElement.addProperty("LX",l.getBlockX());
						newLocationDataElement.addProperty("LY",l.getBlockY());
						newLocationDataElement.addProperty("LZ",l.getBlockZ());
						
						newPlayerDataArray.add(newPlayerDataElement);
						newPlayerDataArray.add(newLocationDataElement);
						
						newTemp.add(e.getPlayer().getName(),newPlayerDataArray);
						
						e.getPlayer().sendMessage("床位置为：X="+l.getBlockX()+";Y="+l.getBlockY()+";Z="+l.getBlockZ());
						
						System.out.println(newTemp.toString());
						
						FileOutputStream fos = new FileOutputStream("plugins/CustomBed/Temp/data.json");
						OutputStreamWriter osw = new OutputStreamWriter(fos);
						osw.write(newTemp.toString());
						osw.close();
						fos.close();
					}
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
