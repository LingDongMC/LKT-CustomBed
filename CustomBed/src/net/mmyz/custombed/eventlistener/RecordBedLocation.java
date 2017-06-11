package net.mmyz.custombed.eventlistener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class RecordBedLocation implements Listener{
	private ItemStack itemStack;
	private Player player;
	private String bedName;
	private boolean succ;
	
	public void setMarkTool(Player p,CommandSender sender){
		this.player = p;
		this.itemStack = p.getItemInHand();
		sender.sendMessage("�����ñ�ǹ���Ϊ"+itemStack.toString());
	}
	public boolean isMarkTool(){
		if(this.itemStack != null){
			return true;
		}else{
			return false;
		}
	}
	public void sendBedName(String bedName){
		this.bedName = bedName;
	}
    
	@EventHandler
	public void getBedLocation(PlayerInteractEvent e){
		if(this.player.getItemInHand().equals(this.itemStack) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			Location l = e.getClickedBlock().getLocation();
			File f = new File("data/playerbed.txt");
			try {
				FileOutputStream fos = new FileOutputStream(f);
				OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
				osw.write(this.player.getName()+"BedName:"+"   "+this.bedName+"   "+"X:"+l.getBlockX()+"   "+"Y:"+l.getBlockY()+"   "+"Z:"+l.getBlockZ());
				f.createNewFile();
				osw.close();
				fos.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				this.succ=false;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				this.succ=false;
			} catch (IOException e1) {
				e1.printStackTrace();
				this.succ=false;
			}

		}
	}
	public boolean isSuccessful(){
		return succ;
	}
}
