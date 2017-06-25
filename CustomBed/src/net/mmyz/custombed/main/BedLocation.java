package net.mmyz.custombed.main;

public class BedLocation {
	
	double LX;
	double LY;
	double LZ;
	String worldName;
	
	public BedLocation(double x, double y, double z,String worldName) {
		this.LX = x;
		this.LY = y;
		this.LZ = z;
		this.worldName = worldName;
	}
	
}
