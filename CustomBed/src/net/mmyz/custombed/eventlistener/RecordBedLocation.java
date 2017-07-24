package net.mmyz.custombed.eventlistener;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
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
						locationDataElement.addProperty("World",l.getWorld().getName());
						locationDataElement.addProperty("LX",l.getBlockX());
						locationDataElement.addProperty("LY",l.getBlockY());
						locationDataElement.addProperty("LZ",l.getBlockZ());
						
						playerDataArray.add(locationDataElement);
						
						temp.add(e.getPlayer().getName(),playerDataArray);
						
						e.getPlayer().sendMessage("床位置为：世界="+l.getWorld().getName()+";X="+l.getBlockX()+";Y="+l.getBlockY()+";Z="+l.getBlockZ());
						
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
						
						newLocationDataElement.addProperty("World",l.getWorld().getName());
						newLocationDataElement.addProperty("LX",l.getBlockX());
						newLocationDataElement.addProperty("LY",l.getBlockY());
						newLocationDataElement.addProperty("LZ",l.getBlockZ());
						
						newPlayerDataArray.add(newPlayerDataElement);
						newPlayerDataArray.add(newLocationDataElement);
						
						newTemp.add(e.getPlayer().getName(),newPlayerDataArray);
						
						e.getPlayer().sendMessage("床位置为：世界="+l.getWorld().getName()+";X="+l.getBlockX()+";Y="+l.getBlockY()+";Z="+l.getBlockZ());
						
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
		
		/*理想：右键使玩家进入睡眠状态
		 *现实：没有任何反应
		 *为什么要创建一个改变方块的数据包，然后又创建一个变回来的数据包呢？
		 *因为在spigot一个插件dalao提到，要让玩家进入睡眠状态，要让客户端知道有一个床（方块）
		 *所以就先把点击的方块变成床，玩家睡眠后再变回来
		 */
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			try {
				ProtocolManager manager = ProtocolLibrary.getProtocolManager();
				Player player = e.getPlayer();
				Material bedBlock = e.getClickedBlock().getType();
				//bp：右键方块位置
				BlockPosition bp = new BlockPosition(e.getClickedBlock().getLocation().getBlockX(),
													 e.getClickedBlock().getLocation().getBlockY(),
													 e.getClickedBlock().getLocation().getBlockZ());
			
				//创建改变方块的数据包
				PacketContainer blockChangePacket = manager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
				//传入bp数据到数据包中
				blockChangePacket.getBlockPositionModifier().write(0, bp);
				//传入一个更改方块的信息到数据包中
				blockChangePacket.getBlockData().write(0, WrappedBlockData.createData(Material.BED_BLOCK));
				//发送数据包给客户端
				manager.sendServerPacket(player,blockChangePacket);

				//创建一个让玩家睡眠的数据包
				PacketContainer bedPacket = manager.createPacket(PacketType.Play.Server.BED);
				//传入玩家信息
				bedPacket.getEntityModifier(player.getWorld()).write(0, player);
				//传入方块位置
				bedPacket.getBlockPositionModifier().write(0,bp);
				
				//将此数据包广播给全服玩家
				for(Player observer : manager.getEntityTrackers(player)){
						//发送数据包
						manager.sendServerPacket(observer, bedPacket);
					
					}
				
				//创建一个改变方块的数据包（把原先方块变回来）
				PacketContainer blockChangeBackPacket = manager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
				blockChangeBackPacket.getBlockPositionModifier().write(0, bp);
				blockChangeBackPacket.getBlockData().write(0, WrappedBlockData.createData(bedBlock));
				manager.sendServerPacket(player,blockChangeBackPacket);
				
				
			}catch(InvocationTargetException exception){
					throw new RuntimeException("Cannot send packet.", exception);
					}
			
			
		}
	}
}