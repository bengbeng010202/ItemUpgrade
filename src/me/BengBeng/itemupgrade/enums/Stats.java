package me.BengBeng.itemupgrade.enums;

public enum Stats {
	
	ATTACK_AOE_DAMAGE("ATTACK_AOE_DAMAGE", 0),
	ATTACK_AOE_RADIUS("ATTACK_AOE_RADIUS", 1),
	BLOCK_AMOUNT("BLOCK_AMOUNT", 2),
	BLOCK_RATE("BLOCK_RATE", 3),
	CRITICAL_CHANCE("CRITICAL_CHANCE", 4),
	CRITICAL_DAMAGE("CRITICAL_DAMAGE", 5),
	DAMAGE("DAMAGE", 6),
	DEFENSE("DEFENSE", 7),
	DODGE_RATE("DODGE_RATE", 8),
	DURABILITY("DURABILITY", 9),
	HEALTH("HEALTH", 10),
	HIT_RATE("HIT_RATE", 11),
	LEVEL("LEVEL", 12),
	PENETRATION("PENETRATION", 13),
	PVE_DAMAGE("PVE_DAMAGE", 14),
	PVE_DEFENSE("PVE_DEFENSE", 15),
	PVP_DAMAGE("PVP_DAMAGE", 16),
	PVP_DEFENSE("PVP_DEFENSE", 17);
	
	private String statsName;
	private int id;
	
	Stats(String statsName, int id) {
		this.statsName = statsName;
		this.id = id;
	}
	
	public String getStatsName() {
		return statsName;
	}
	
	public int getId() {
		return id;
	}
	
}
