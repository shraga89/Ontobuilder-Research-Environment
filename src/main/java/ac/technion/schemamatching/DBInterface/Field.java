package ac.technion.schemamatching.DBInterface;


public class Field {
    String name;
    Field.FieldType type;

    public Field(String fieldName, Field.FieldType ftype) {
        this.name = fieldName;
        this.type = ftype;
    }

    public String toString() {
        return "Field [name=" + this.name + ", type=" + this.type + "]";
    }

    public enum FieldType {
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BIGDECIMAL,
        STRING,
        DATE,
        TIME,
        FILE;

        FieldType() {
        }
    }
}
