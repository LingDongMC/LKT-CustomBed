package net.mmyz.custombed.command;

import net.mmyz.custombed.eventlistener.RecordBedLocation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public  class MyCommand implements CommandExecutor{
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(cmd.getName().equalsIgnoreCase("custombed")){
    		if (sender instanceof Player) {
    			if(args.length ==0){
    				sender.sendMessage("/custombed setbed <床名字> - 设置你自己的床(注意准星方向，准星方向是脚的朝向)");
    				sender.sendMessage("/custombed deletebed <床名字> - 删除你自己的床");	
    				return true;
    			}
    			RecordBedLocation rbl = new RecordBedLocation();
    			
    			if (args[0].equalsIgnoreCase("setmarktool")){
    				rbl.setMarkTool(((Player) sender).getPlayer(),sender);
				}
    			
    			if (args[0].equalsIgnoreCase("setbed")) {
    				if (args.length == 2) {
    					if(rbl.isMarkTool()== true){
    						rbl.sendBedName(args[1]);
    						if (rbl.isSuccessful()) {
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
    	}
        return true;
    }




}
