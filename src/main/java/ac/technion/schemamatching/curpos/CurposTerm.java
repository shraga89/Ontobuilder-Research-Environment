package ac.technion.schemamatching.curpos;

import java.io.Serializable;

import org.json.simple.JSONObject;

import ac.technion.iem.ontobuilder.core.ontology.Term;

/*
 * @Author Eyal Heinemann
 * @Author Tom Blinder
 * A class to represent the terms in any curpos
 */
public class CurposTerm implements Serializable {
	/**
	 * @Generated
	 */
	private static final long serialVersionUID = 723831891876813304L;

	public CurposTerm(Term term){
		this.name = term.getName();
	}
	
	public CurposTerm(String name){
		this.name = name;
	}
	
	protected CurposTerm(){}
	
	private String name;

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CurposTerm other = (CurposTerm) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
	@Override
    public String toString() {
		return "NameCurposTerm [name=" + name + "]";
	}


	@SuppressWarnings("unchecked")
	public JSONObject toJSON(){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", getName());		
		return jsonObject;
	}

	public static CurposTerm fromJSON(JSONObject json){
		CurposTerm term = new CurposTerm();
		term.name = (String) json.get(NAME);
		return term;
	}
	
	private static final String NAME = "name";
}
