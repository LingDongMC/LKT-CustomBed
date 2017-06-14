package net.mmyz.custombed.eventlistener;

//import org.bukkit.Location;
//import org.bukkit.entity.Player;
//import org.bukkit.Location;
//import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
//import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class RecordBedLocation implements Listener{
	
	private ItemStack itemStack;
	
	@EventHandler
	public void getBedLocation(PlayerInteractEvent e){
		//看看this.itemStack是否为null
		System.out.println(new Object().equals(this.itemStack));
		
		if(e.getItem().equals(this.itemStack) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			
			//this.itemStack为null，不会执行这一句
			System.out.println("进来了");
//			Player player = e.getPlayer();
//			Location l = e.getClickedBlock().getLocation();
//			System.out.println(player.getName()+"BedName:"+"   "+MyCommand.bedName+"   "+"X:"+l.getBlockX()+"   "+"Y:"+l.getBlockY()+"   "+"Z:"+l.getBlockZ());
		}
	}
	
	public void setPlayerItem(ItemStack is){
		//得到传进来的is对象
		this.itemStack = is;
		System.out.println(this.itemStack.getType().name());
	}
	
}
