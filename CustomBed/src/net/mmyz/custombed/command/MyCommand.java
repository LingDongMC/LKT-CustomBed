package net.mmyz.custombed.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
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
						playerDataElement.addProperty("PlayerName",
								player.getName());
						playerDataElement.addProperty("MarkTool", markTool);

						playerDataArray.add(playerDataElement);

						temp.add("PlayerData", playerDataArray);

						File f = new File("plugins/CustomBed/Temp/");
						FileOutputStream fos;
						try {
							if (f.exists() == false) {
								f.mkdirs();
							}
							fos = new FileOutputStream(
									"plugins/CustomBed/Temp/data.json");
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
							try {
								JsonObject info = new JsonObject();

								JsonArray bedDataArray = new JsonArray();

								JsonObject bedDataElement = new JsonObject();
								
								bedDataElement.addProperty("BedName",this.bedName);

								JsonParser jp = new JsonParser();
								JsonObject temp = (JsonObject) jp.parse(new FileReader("plugins/CustomBed/Temp/data.json"));
								
								JsonArray playerDataArray = temp.get("PlayerData").getAsJsonArray();
								String playName = playerDataArray.get(0).getAsJsonObject().get("PlayerName").getAsString();
								
								bedDataElement.addProperty("PlayerName",playName);
								
								int LX = playerDataArray.get(1).getAsJsonObject().get("LX").getAsInt();
								int LY = playerDataArray.get(1).getAsJsonObject().get("LY").getAsInt();
								int LZ = playerDataArray.get(1).getAsJsonObject().get("LZ").getAsInt();
								
			                    bedDataElement.addProperty("LX",LX);
			                    bedDataElement.addProperty("LY",LY);
			                    bedDataElement.addProperty("LZ",LZ);
								
			                    bedDataArray.add(bedDataElement);
			                    
			                    info.add("BedInfo", bedDataArray);
			                    
			                    File f = new File("plugins/CustomBed/Data/");
			                    FileOutputStream fos;
			                    	if (f.exists() == false) {
			                    		f.mkdirs();
			                    	fos = new FileOutputStream("plugins/CustomBed/Data/bedinfo.json");
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
							}catch (IOException e) {
								e.printStackTrace();
							}
			                 sender.sendMessage("已设置床！");
			                 return true;
						}else {
							sender.sendMessage("设置床失败！");
							return true;
						}
					}else if (args.length == 1) {
						sender.sendMessage("请输入床的名字");
						return true;
					}
				}else {
					sender.sendMessage("请输入正确的设定指令");
					return true;
				}

				
				
				
				
				
				if (args[0].equalsIgnoreCase("deletebed")) {
					if (args.length == 2) {
						// 查找床
						// 加入监听器
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
		return true;
	}
}
