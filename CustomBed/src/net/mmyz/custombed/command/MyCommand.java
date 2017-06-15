package net.mmyz.custombed.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public  class MyCommand implements CommandExecutor{
	
	public  ItemStack is;
	private boolean isMarkTool = false;
	public  String bedName;

		
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {	
    	if(cmd.getName().equalsIgnoreCase("custombed")){
    		if (sender instanceof Player) {
    			if(args.length ==0){
    				sender.sendMessage("/custombed setbed <床名字> - 设置你自己的床(注意准星方向，准星方向是脚的朝向)");
    				sender.sendMessage("/custombed deletebed <床名字> - 删除你自己的床");	
    				sender.sendMessage("/custombed setmarktool - 设置一个标志工具(手里的物品)来你自己床的位置");	
    				return true;
    			}
    			Player player = ((Player) sender).getPlayer();
    			
    			if (args[0].equalsIgnoreCase("setmarktool")){
    				if (player.getItemInHand().getType().equals(Material.AIR)) {
    					sender.sendMessage("标志工具不能为空气！");
    					return true;
					}else{
						String markTool;
						is = player.getItemInHand();
						markTool = is.getType().name().toLowerCase();
						isMarkTool = true;
						sender.sendMessage("已设置标志工具为:"+markTool);
						
						JsonObject temp = new JsonObject();
						
						JsonArray playerDataArray = new JsonArray();
						
						JsonObject playerDataElement = new JsonObject();
						playerDataElement.addProperty("PlayerName",player.getName());
						playerDataElement.addProperty("MarkTool", markTool);
						
						playerDataArray.add(playerDataElement);
						
						temp.add("PlayerData",playerDataArray);
						System.out.println(temp.toString());
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
    					if(isMarkTool== true){
    						bedName = args[1];
    							sender.sendMessage("已设置床！");
    							return true;    														
							}else{
								sender.sendMessage("设置床失败！");
								return true;
							}
    					}else{
    						
    					}
					}else if(args.length == 1){
						sender.sendMessage("请输入床的名字");
						return true;
					}else{
						sender.sendMessage("请输入正确的设定指令");
						return true;
					}
				}
    			if (args[0].equalsIgnoreCase("deletebed")) {
    				if (args.length == 2) {
    					//查找床
     					//加入监听器
    					sender.sendMessage("已删除床！");
    					return true;
					}else if(args.length == 1){
						sender.sendMessage("请输入删除床的名字");
						return true;
					}else{
						sender.sendMessage("请输入正确的删除指令");
						return true;
					}
				}
    		}else{
    			sender.sendMessage("请在游戏中使用该命令");
    			return true;    			
    		}
        return true;
    }

}
