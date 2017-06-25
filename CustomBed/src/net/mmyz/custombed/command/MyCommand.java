package net.mmyz.custombed.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import net.mmyz.custombed.main.BedLocation;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class MyCommand implements CommandExecutor {

	public ItemStack is;
	private boolean isMarkTool = false;
	public String bedName;
	public boolean isexists;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		if (cmd.getName().equalsIgnoreCase("custombed")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					sender.sendMessage("/custombed setbed <床名字> - 设置你自己的床");
					sender.sendMessage("/custombed deletebed <床名字> - 删除你自己的床");
					sender.sendMessage("/custombed setmarktool - 设置一个标志工具(手里的物品)来你自己床的位置");
					return true;
				}
				Player player = ((Player) sender).getPlayer();

				if (args[0].equalsIgnoreCase("setmarktool")) {
					if (player.getItemInHand().getType().equals(Material.AIR)) {
						sender.sendMessage("标志工具不能为空气！");
						return true;
					} else {
						String markTool;
						is = player.getItemInHand();
						markTool = is.getType().name().toLowerCase();
						isMarkTool = true;
						sender.sendMessage("已设置标志工具为:" + markTool);

						JsonObject temp = new JsonObject();

						JsonArray playerDataArray = new JsonArray();

						JsonObject playerDataElement = new JsonObject();

						playerDataElement.addProperty("MarkTool", markTool);

						playerDataArray.add(playerDataElement);

						temp.add(player.getName(), playerDataArray);

						File f = new File("plugins/CustomBed/Temp/");
						FileOutputStream fos;
						try {
							if (f.exists() == false) {
								f.mkdirs();
							}
							fos = new FileOutputStream("plugins/CustomBed/Temp/data.json");
							OutputStreamWriter osw = new OutputStreamWriter(fos);
							osw.write(temp.toString());
							osw.close();
							fos.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return true;
					}
				}

				
				
				
				if (args[0].equalsIgnoreCase("setbed")) {
					if (args.length == 2) {
						if (this.isMarkTool == true) {
							this.bedName = args[1];
							// 添加一个/data/bedinfo.json来储存床名称、玩家名、床位置
							this.isexists = false;
							try {
								JsonParser jp1 = new JsonParser();
								JsonObject temp = (JsonObject) jp1.parse(new FileReader("plugins/CustomBed/Temp/data.json"));

								String playName = ((Player) sender).getPlayer().getName();
								JsonArray playerDataArray = temp.get(playName).getAsJsonArray();
								
								if (playerDataArray.size() >= 1) {
									JsonObject locationDataElement = (JsonObject) playerDataArray.get(1);
									if(new File("plugins/CustomBed/Data/bedinfo.json").exists()){
										JsonParser jp2 = new JsonParser();
										JsonObject info = (JsonObject) jp2.parse(new FileReader("plugins/CustomBed/Data/bedinfo.json"));
										
										JsonArray bedDataArray = info.get(playName).getAsJsonArray();
										
										String[] bedNameFromDataArray = new String[bedDataArray.size()];
										BedLocation locationFromPlayerDataArray = new BedLocation(
																	playerDataArray.get(1).getAsJsonObject().get("LX").getAsDouble(), 
																	playerDataArray.get(1).getAsJsonObject().get("LY").getAsDouble(), 
																	playerDataArray.get(1).getAsJsonObject().get("LZ").getAsDouble(),
																	playerDataArray.get(1).getAsJsonObject().get("World").getAsString());
										
										
										
										for (int i = 0; i < bedDataArray.size(); i++) {
											bedNameFromDataArray[i] = bedDataArray.get(i).getAsJsonObject().get("BedName").getAsString();
										}
										
										for (int j = 0; j < bedDataArray.size(); j++){
											System.out.println(j);
											System.out.println(bedNameFromDataArray[j]);
											if (this.bedName.equals(bedNameFromDataArray[j]) | 
													locationFromPlayerDataArray.equals(new BedLocation(
													bedDataArray.get(j).getAsJsonObject().get("LX").getAsDouble(), 
													bedDataArray.get(j).getAsJsonObject().get("LY").getAsDouble(), 
													bedDataArray.get(j).getAsJsonObject().get("LZ").getAsDouble(),
													bedDataArray.get(j).getAsJsonObject().get("World").getAsString()))) {
													this.isexists = false;
													sender.sendMessage("该位置已经有名字为"+bedNameFromDataArray[j]+"的床");
													System.out.println("jia");
													break;
											}else{
												this.isexists = true;
												System.out.println("zhen");
												}
										}
										System.out.println(isexists);
										if(isexists == true){
											locationDataElement.addProperty("BedName",this.bedName);
											
											bedDataArray.add(locationDataElement);
											
											info.add(playName, bedDataArray);
											
											FileOutputStream fos = new FileOutputStream("plugins/CustomBed/Data/bedinfo.json");
											OutputStreamWriter osw = new OutputStreamWriter(fos);
											osw.write(info.toString());
											osw.close();
											fos.close();
											sender.sendMessage("已设置床"+this.bedName+"!");
											}
										return true;
									}
									
									
									 else{
										JsonObject info = new JsonObject();
										JsonArray bedDataArray = new JsonArray();
										
										locationDataElement.addProperty("BedName",this.bedName);
										
										bedDataArray.add(locationDataElement);
										
										info.add(playName, bedDataArray);
										
										File f = new File("plugins/CustomBed/Data/");
										if (f.exists() == false) {
											f.mkdirs();
										}
										FileOutputStream fos = new FileOutputStream("plugins/CustomBed/Data/bedinfo.json");
										OutputStreamWriter osw = new OutputStreamWriter(fos);
										osw.write(info.toString());
										osw.close();
										fos.close();
										sender.sendMessage("已设置床"+this.bedName+"!");
										return true;
									  }
									}else {
										sender.sendMessage("设置床失败！请右键选择你要设置床的方块");
										return true;
								}
								
							} catch (JsonIOException e1) {
								e1.printStackTrace();
							} catch (JsonSyntaxException e1) {
								e1.printStackTrace();
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							}catch (IOException e) {
								e.printStackTrace();
							}
						}else{
							sender.sendMessage("设置床失败！请设定你的标记工具(手里的物品)");
							return true;
						}
					}else if (args.length == 1) {
						sender.sendMessage("请输入床的名字");
						return true;
					}else if(args.length > 2){
						sender.sendMessage("请输入正确的设定指令");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("deletebed")) {
					if (args.length == 2) {
						// 查找床
						sender.sendMessage("已删除床！");
						return true;
					} else if (args.length == 1) {
						sender.sendMessage("请输入删除床的名字");
						return true;
					} else {
						sender.sendMessage("请输入正确的删除指令");
						return true;
					}
				}
			} else {
				sender.sendMessage("请在游戏中使用该命令");
				return true;
			}
		}
//		if(args[0].equalsIgnoreCase("test")){
//			Player player = ((Player) sender).getPlayer();
//			Set<Player> sleeping = Collections.newSetFromMap(new WeakHashMap<Player, Boolean>());
//			
//			sleeping.add(player);
//			
//			PacketContainer bedPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BED, false);
//			Location l = player.getLocation();
//			BlockPosition bp = new BlockPosition(l.getBlockX(),l.getBlockY(),l.getBlockZ());
//			
//			bedPacket.getEntityModifier(player.getWorld()).write(0, player);
//			bedPacket.getIntegers().write(1, bp.getX()).write(2, bp.getY() + 1).write(3, bp.getZ());
//			
//			for(Player observer : ProtocolLibrary.getProtocolManager().getEntityTrackers(player)){
//				try{
//					ProtocolLibrary.getProtocolManager().sendServerPacket(observer, bedPacket);
//					}
//				
//				catch(InvocationTargetException e){
//					throw new RuntimeException("Cannot send packet.", e);
//				}
//			}
//			
//            System.out.println(123456);
//		}
		return true;
	}
}
