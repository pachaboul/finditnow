package finditnow.apk;

public class Building {
    private int bid;
    private String name;
    
    public Building(int bid, String name) {
		this.bid = bid;
		this.name = name;
    }
    
    public int getBID() {
    	return bid;
    }
    
    public String getName() {
    	return name;
    }
}