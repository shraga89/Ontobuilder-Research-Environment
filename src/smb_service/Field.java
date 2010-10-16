package smb_service;

public class Field
{
	public Field(String fieldName, FieldType ftype)
	{
		name = fieldName;
		type = ftype;
	}
	public String name;
	public FieldType type;
	
	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + "]";
	}

	public enum FieldType {
		 BOOLEAN,BYTE,SHORT,INT,LONG,FLOAT,DOUBLE,BIGDECIMAL,STRING,DATE,TIME,FILE
	        }
}


