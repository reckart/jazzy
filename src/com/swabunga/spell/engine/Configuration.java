package com.swabunga.spell.engine;


/**
 * The various settings used to control how a spell checker works are read from here.
 * Includes the COST_* constants that decide how to figure the cost of converting one word to 
 * another in the EditDistance class. 
 * <p/>
 * Also includes SPELL_* constants that control how misspellings are detected, for example, how to handle
 * mixed-case words, etc. 
 * 
 * @author aim4min
 * @see EditDistance 
 */
public abstract class Configuration {

	/** used by EditDistance: the cost of having to remove a character <br/>(integer greater than 0) */
    public static final String COST_REMOVE_CHAR = "EDIT_DEL1";
	
	/** used by EditDistance: the cost of having to insert a character <br/>(integer greater than 0)*/
    public static final String COST_INSERT_CHAR = "EDIT_DEL2";
	
	/**
	 * used by EditDistance: the cost of having to swap two adjoinging characters 
	 * for the swap value to ever be used, it should be smaller than the COST_REMOVE_CHAR or COST_INSERT_CHAR values
	 * <br/>(integer greater than 0)
	 */
    public static final String COST_SWAP_CHARS = "EDIT_SWAP";
	
	/**
	 * used by EditDistance: the cost of having to substitute one character for another  
	 * for the sub value to ever be used, it should be smaller than the COST_REMOVE_CHAR or COST_INSERT_CHAR values
	 * <br/>(integer greater than 0)
	 */
    public static final String COST_SUBST_CHARS = "EDIT_SUB";
    
//    public static final String EDIT_SIMILAR = "EDIT_SIMILAR"; //DMV: these does not seem to be used at all
//    public static final String EDIT_MIN = "EDIT_MIN";
//    public static final String EDIT_MAX = "EDIT_MAX";
	
	/** the maximum cost of suggested spelling. Any suggestions that cost more are thrown away
	 * <br/> integer greater than 1) 
	 */
	public static final String SPELL_THRESHOLD = "SPELL_THRESHOLD";
	
	/** words that are all upper case are not spell checked, example: "CIA" <br/>(boolean) */
	public static final String SPELL_IGNOREUPPERCASE = "SPELL_IGNOREUPPERCASE";
	/**  words that have mixed case are not spell checked, example: "SpellChecker"<br/>(boolean) */
	public static final String SPELL_IGNOREMIXEDCASE = "SPELL_IGNOREMIXEDCASE";
	/** words that look like an internet address are not spell checked, example: "http://www.google.com" <br/>(boolean)*/
	public static final String SPELL_IGNOREINTERNETADDRESSES = "SPELL_IGNOREINTERNETADDRESS";
	/** words that have digits in them are not spell checked, example: "mach5" <br/>(boolean) */
	public static final String SPELL_IGNOREDIGITWORDS = "SPELL_IGNOREDIGITWORDS";
	/** I don't know what this does. It doesn't seem to be used <br/>(boolean) */
	public static final String SPELL_IGNOREMULTIPLEWORDS = "SPELL_IGNOREMULTIPLEWORDS";
	/** the first word of a sentance is expected to start with an upper case letter <br/>(boolean) */
	public static final String SPELL_IGNORESENTENCECAPITALIZATION = "SPELL_IGNORESENTENCECAPTILIZATION";
	
	/**
	 * Gets one of the integer constants
	 * @param key one of the integer constants defined in this class
	 * @return int value of the setting
	 */
	public abstract int getInteger(String key);
	
	/**
	 * Gets one of the boolean constants
	 * @param key one of the boolean constants defined in this class
	 * @return boolean value of the setting
	 */
	public abstract boolean getBoolean(String key);
	
	/**
	 * Sets one of the integer constants
	 * @param key one of the integer constants defined in this class
	 * @param value new integer value of the constant 
	 */
	public abstract void setInteger(String key, int value);

	/**
	 * Sets one of the boolean constants
	 * @param key one of the boolean constants defined in this class
	 * @param value new boolean value of this setting
	 */
	public abstract void setBoolean(String key, boolean value);
	
	/**
	 * gets a new default Configuration
	 * @return Configuration
	 */
	public static final Configuration getConfiguration() {
        return getConfiguration(null);
    }
    
	/**
	 * Returns a new instance of a Configuration class
	 * @param className - the class to return, must be based on Configuration
	 * @return Configuration
	 */
	public static final Configuration getConfiguration(String className) {
		
		Configuration result;
		
		if (className != null && className.length() > 0) {
			try {
				result = (Configuration)Class.forName(className).newInstance();
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
