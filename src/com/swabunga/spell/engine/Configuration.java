package com.swabunga.spell.engine;


/**
 * @author aim4min
 *
 */
public abstract class Configuration {

	/**
	 * used by EditDistance: the cost of having to remove a character 
	 */
    public static final String COST_REMOVE_CHAR = "EDIT_DEL1";
	
	/**
	 * used by EditDistance: the cost of having to insert a character
	 */
    public static final String COST_INSERT_CHAR = "EDIT_DEL2";
	
	/**
	 * used by EditDistance: the cost of having to swap two adjoinging characters 
	 * for the swap value to ever be used, it should be smaller than the insert or delete values
	 */
    public static final String COST_SWAP_CHARS = "EDIT_SWAP";
	
	/**
	 * used by EditDistance: the cost of having to substitute one character for another  
	 * for the sub value to ever be used, it should be smaller than the insert or delete values
	 */
    public static final String COST_SUBST_CHARS = "EDIT_SUB";
    
//    public static final String EDIT_SIMILAR = "EDIT_SIMILAR"; //DMV: these does not seem to be used at all
//    public static final String EDIT_MIN = "EDIT_MIN";
//    public static final String EDIT_MAX = "EDIT_MAX";
	
	public static final String SPELL_THRESHOLD = "SPELL_THRESHOLD";
	public static final String SPELL_IGNOREUPPERCASE = "SPELL_IGNOREUPPERCASE";
	public static final String SPELL_IGNOREMIXEDCASE = "SPELL_IGNOREMIXEDCASE";
	public static final String SPELL_IGNOREINTERNETADDRESSES = "SPELL_IGNOREINTERNETADDRESS";
	public static final String SPELL_IGNOREDIGITWORDS = "SPELL_IGNOREDIGITWORDS";
	public static final String SPELL_IGNOREMULTIPLEWORDS = "SPELL_IGNOREMULTIPLEWORDS";
	public static final String SPELL_IGNORESENTENCECAPITALIZATION = "SPELL_IGNORESENTENCECAPTILIZATION";
	
	public abstract int getInteger(String key);
	public abstract boolean getBoolean(String key);
	public abstract void setInteger(String key, int value);
	public abstract void setBoolean(String key, boolean value);
	
	public static final Configuration getConfiguration() {
        return getConfiguration(null);
    }
    
	public static final Configuration getConfiguration(String config) {
		Configuration result;
		if (config != null && config.length() > 0) {
			try {
				result = (Configuration)Class.forName(config).newInstance();
			} catch (InstantiationException e) {
				result = new PropertyConfiguration();
			} catch (IllegalAccessException e) {
				result = new PropertyConfiguration();
			} catch (ClassNotFoundException e) {
				result = new PropertyConfiguration();
			}
		} else {
			result = new PropertyConfiguration();	
		}
		return result;
	}
}
