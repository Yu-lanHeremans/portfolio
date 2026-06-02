package utils;

public enum Gebouw {

	PAVILJOEN, HAREM, ARCADE, WONING, TUIN, TOREN;

	
	public static Gebouw willekeurig() {
        Gebouw[] gebouwen = values();
        return gebouwen[new java.util.Random().nextInt(gebouwen.length)];
    }
	
	public int geefIndex() {
		return this.ordinal(); 
	}
}
