package com.swabunga.util;

import java.util.*;

public class VectorUtility{
	public static Vector addAll(Vector dest, Vector src){
		return addAll(dest, src, true);
	}

	public static Vector addAll(Vector dest, Vector src, boolean allow_duplicates){
		for(Enumeration e = src.elements(); e.hasMoreElements();){
			Object o = e.nextElement();
			if (!allow_duplicates && !dest.contains(o))         
				dest.addElement(o);
		}
		return dest;
	}
}
