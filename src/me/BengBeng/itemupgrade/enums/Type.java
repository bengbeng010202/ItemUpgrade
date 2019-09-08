package me.BengBeng.itemupgrade.enums;

public enum Type {
	
	UPGRADE("UPGRADE"),
	LUCK("LUCK"),
	LEVEL("LEVEL"),
	FAIRY("FAIRY");
	
	private String typeName;
	
	Type(String typeName){
		this.typeName = typeName;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public static boolean isValid(String type) {
		boolean valid = false;
		type = type.toUpperCase();
		for(Type types : Type.values()) {
			if(types.getTypeName().equals(type)) {
				valid = true;
			}
		}
		return valid;
	}
	
}
