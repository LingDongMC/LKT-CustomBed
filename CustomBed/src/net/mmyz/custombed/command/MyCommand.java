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
    				sender.sendMessage("/custombed setbed <������> - �������Լ��Ĵ�(ע��׼�Ƿ���׼�Ƿ����ǽŵĳ���)");
    				sender.sendMessage("/custombed deletebed <������> - ɾ�����Լ��Ĵ�");	
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
    							sender.sendMessage("�����ô���");
    							return true;    														
							}else{
								sender.sendMessage("���ô�ʧ�ܣ�");
								return true;
							}
    					}else{
    						
    					}
					}else if(args.length == 1){
						sender.sendMessage("�����봲������");
						return true;
					}else{
						sender.sendMessage("��������ȷ���趨ָ��");
						return true;
					}
				}
    			if (args[0].equalsIgnoreCase("deletebed")) {
    				if (args.length == 2) {
    					//���Ҵ�
    					//���������
    					sender.sendMessage("��ɾ������");
    					return true;
					}else if(args.length == 1){
						sender.sendMessage("������ɾ����������");
						return true;
					}else{
						sender.sendMessage("��������ȷ��ɾ��ָ��");
						return true;
					}
				}
    		}else{
    			sender.sendMessage("������Ϸ��ʹ�ø�����");
    			return true;    			
    		}
    	}
        return true;
    }




}
