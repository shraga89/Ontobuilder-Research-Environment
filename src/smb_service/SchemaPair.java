/**
 * 
 */
package smb_service;

import java.io.Serializable;
import java.util.HashMap;


/**
 * @author tomer_s
 *
 */
public class SchemaPair  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3898949694296264118L;
	/**
	 * 
	 */
	public SchemaPair() {
		
	}
	
	@Override
	public String toString() {
		return "SchemaPair [candidate=" + candidateSchema
				+ ", target=" + targetSchema + "]";
	}

	public SchemaPair(Schema candidateSchema, Schema targetSchema) {
		super();
		this.candidateSchema = candidateSchema;
		this.targetSchema = targetSchema;
		exactMatch  = new SimilarityMatrix(candidateSchema.schemaID,targetSchema.schemaID,new HashMap<Long,Integer>(),new HashMap<Long,Integer>(),new double[candidateSchema.terms.size()][targetSchema.terms.size()]);
	}

	Schema candidateSchema;
	Schema targetSchema;
	SimilarityMatrix exactMatch;
	HashMap<Long,SimilarityMatrix> correspondenceSet = new HashMap<Long,SimilarityMatrix>();
	int taskClass; //Type of matching task class this schema pair belongs to
}
