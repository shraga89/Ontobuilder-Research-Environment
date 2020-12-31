/**
 * 
 */
package ac.technion.schemamatching.matchers;

/**
 * This enum type is used to describe first line matchers
 * Classification is based upon the work of Algergawy et al 2009
 * @author Tomer Sagi
 *
 */
public enum MatcherType {
	SYNTACTIC, //Uses string comparison techniques
	SEMANTIC, //Uses semantic information such as synonyms, thesauri etc.
	DATATYPE, //Compares element datatypes and domain constraints
	CONSTRAINT, /*Utilizes information from element constraints.  The cardinality (occurrence) constraint is considered
	the most significant. The minOccurs and maxOccurs in the XML schema define the minimum
	and maximum occurrence of an element that may appear in XML documents.*/
	ANNOTATION, /*XML Schemas and other Information representations can have elements that  contain  additional  information  and  are  dedicated  to  holding  human  readable
				documentation and machine readable information */
	STRUCTURAL_PARENTCHILD, //Uses hierarchical information such as parent child relationships or term-subterm
	STRUCTURAL_SIBLING, /*Compares siblings of the same hierarchy where some ordering information is supplied. 
						In Ontobuilder this matcher is called precedence*/ 
	STRUCTURAL_FD, //Uses functional dependency information
	INSTANCE, //Uses instance information
	CORPUS, //Uses a corpus of words to calculate tf / idf and / or to find if a similar pair was matched
	HUMAN; //Uses human expert sourcing
}
