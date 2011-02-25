package finditnow.apk;

public class Building {
    private int bid;
    private String name;
    private int[] fid;
    private String[] floor_names;
    
    //no arg constructor using Gson
    public Building()
    {
    	
    }
    public Building(int bid, String name, int[] fid, String[] fn) {
		this.bid = bid;
		this.name = name;
		this.fid = fid;
		this.floor_names = fn;
    }
    
    public int getBID() {
    	return bid;
    }
    
    public String getName() {
    	return name;
    }
    
    public String[] getFloorName(){
    	return floor_names;
    }
    public int[] getFid(){
    	return fid;
    }
    
    public String toString(){
    	String result = "Name: "+ name + ", bid: "+bid
    		+", fid: "+fid.toString()+", Floor Names: " + floor_names.toString();
    	return result;
    }
}