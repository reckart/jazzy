package com.swabunga.spell.engine;

import java.util.ResourceBundle;

/**
 * @author aim4min
 *
 */
public abstract class Configuration {

    public static final String EDIT_DEL1 = "EDIT_DEL1";
    public static final String EDIT_DEL2 = "EDIT_DEL2";
    public static final String EDIT_SWAP = "EDIT_SWAP";
    public static final String EDIT_SUB = "EDIT_SUB";
    public static final String EDIT_SIMILAR = "EDIT_SIMILAR";
    public static final String EDIT_MIN = "EDIT_MIN";
    public static final String EDIT_MAX = "EDIT_MAX";
	
	public static final String SPELL_THRESHOLD = "SPELL_THRESHOLD";
	public static final String SPELL_IGNOREUPPERCASE = "SPELL_IGNOREUPPERCASE";
	public static final String SPELL_IGNOREMIXEDCASE = "SPELL_IGNOREMIXEDCASE";
	public static final String SPELL_IGNOREINTERNETADDRESSES = "SPELL_IGNOREINTERNETADDRESS";
	public static final String SPELL_IGNOREDIGITWORDS = "SPELL_IGNOREDIGITWORDS";
	public static final String SPELL_IGNOREMULTIPLEWORDS = "SPELL_IGNOREMULTIPLEWORDS";
	public static final String SPELL_IGNORESENTANCECAPITALIZATION = "SPELL_IGNORESENTANCECAPTILIZATION";
	
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
