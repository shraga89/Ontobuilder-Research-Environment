package ac.technion.schemamatching.curpos;

import ac.technion.iem.ontobuilder.core.ontology.Term;

/*
 * @Author Eyal Heinemann
 * @Author Tom Blinder
 * NameCurposTerm - a Curpos term that uses the Term name only.
 */
public class NameCurposTerm implements CurposTerm {
	
	/**
	 * @Generated
	 */
	private static final long serialVersionUID = 723831891876813304L;

	public NameCurposTerm(Term term){
		this.name = term.getName();
	}
	
	public NameCurposTerm(String name){
		this.name = name;
	}
	
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
		NameCurposTerm other = (NameCurposTerm) obj;
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

}
