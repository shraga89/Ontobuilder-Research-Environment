/**
 * 
 */
package smb_service;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Defines a schema for use in SMB_service
 * @author tomer_s
 */
public class Schema implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1469097441122163887L;
	long schemaID;
	String schemaName;
	HashMap<String,Long> features;
	HashMap<Long,String> terms;

	public Schema()
	{
		
	}
	
	public Schema(long schemaID, String schemaName,
			HashMap<String, Long> features, HashMap<Long, String> terms) {
		super();
		this.schemaID = schemaID;
		this.schemaName = schemaName;
		this.features = features;
		this.terms = terms;
	}

	@Override
	public String toString() {
		return "Schema [ID=" + schemaID + ", Name=" + schemaName
				+ "]";
	}
	
	
}
