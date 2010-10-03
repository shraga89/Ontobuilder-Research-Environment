package smb_service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class LearnWorkingSet implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4814622265457737194L;
	public LearnWorkingSet(ArrayList<SchemaPair> pairs)
	{
		schemaPairList = pairs;
		
	}
	public LearnWorkingSet() {
		super();
	}
	public HashMap<Long,Schema> schemata = new HashMap<Long,Schema>();
	public ArrayList<SchemaPair> schemaPairList = new ArrayList<SchemaPair>();
	public HashMap<Long, String> basicConfigurations = new HashMap<Long, String>(); 
}