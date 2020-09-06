package ac.technion.schemamatching.DBInterface;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DBInterface {
    private DBInterface.DB myDB = new DBInterface.DB();
    private Connection myConn;
    private int exp_no;

    public int getExp_no() {
        return this.exp_no;
    }

    public DBInterface() {
        this.myConn = this.myDB.dbConnect(1, "localhost:3306", "schemamatching", "temp", "");
    }

    public DBInterface(int dbmstype, String host, String dbName, String username, String pwd) {
        this.myConn = this.myDB.dbConnect(dbmstype, host, dbName, username, pwd);
    }

    public void disconnect() {
        try {
            this.myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean runUpdateQuery(String sql) {
        try {
            Statement st = this.myConn.createStatement();
            st.execute(sql);
            return true;
        } catch (SQLException e) {
            System.err.print(e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<String[]> runSelectQuery(String sql, int numFields) {
        ArrayList<String[]> res = new ArrayList<String[]>();

        try {
            Statement st = this.myConn.createStatement();
            st.execute(sql);
            ResultSet rs = st.getResultSet();

            while(rs.next()) {
                String[] ln = new String[numFields];

                for(int i = 0; i < numFields; ++i) {
                    ln[i] = rs.getString(i + 1);
                }

                res.add(ln);
            }
        } catch (SQLException e) {
            System.err.print(e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(0);
        }

        return res;
    }

    public void insertSingleRow(HashMap<Field, Object> values, String tableName) {
        StringBuffer fields = new StringBuffer();
        StringBuffer valueString = new StringBuffer();
        HashMap<String, Integer> fieldIndex = new HashMap<>();
        int lastIndex = 0;
        Iterator<Field> iterator = values.keySet().iterator();

        while(iterator.hasNext()) {
            Field f = (Field)iterator.next();
            if (fields.length() != 0) {
                fields.append(",");
                valueString.append(",");
            }

            fields.append(f.name);
            valueString.append("?");
            ++lastIndex;
            fieldIndex.put(f.name, lastIndex);
        }

        String sql = "INSERT INTO " + tableName + "(" + fields.toString() + ") VALUES (" + valueString + ");";

        try {
            PreparedStatement pstmt = this.myConn.prepareStatement(sql);
            iterator = values.keySet().iterator();

            while(iterator.hasNext()) {
                Field f = (Field)iterator.next();
                System.out.println(f.type.ordinal());
                System.out.println(f.type);
                switch(f.type.ordinal()+1) {
                    case 1:
                        pstmt.setBoolean((Integer)fieldIndex.get(f.name), (Boolean)values.get(f));
                        break;
                    case 2:
                        pstmt.setByte((Integer)fieldIndex.get(f.name), (Byte)values.get(f));
                        break;
                    case 3:
                        pstmt.setShort((Integer)fieldIndex.get(f.name), (Short)values.get(f));
                        break;
                    case 4:
                        pstmt.setInt((Integer)fieldIndex.get(f.name), (Integer)values.get(f));
                        break;
                    case 5:
                        pstmt.setLong((Integer)fieldIndex.get(f.name), (Long)values.get(f));
                        break;
                    case 6:
                        pstmt.setFloat((Integer)fieldIndex.get(f.name), (Float)values.get(f));
                        break;
                    case 7:
                        pstmt.setDouble((Integer)fieldIndex.get(f.name), (Double)values.get(f));
                        break;
                    case 8:
                        pstmt.setBigDecimal((Integer)fieldIndex.get(f.name), (BigDecimal)values.get(f));
                        break;
                    case 9:
                        pstmt.setString((Integer)fieldIndex.get(f.name), (String)values.get(f));
                        break;
                    case 10:
                        pstmt.setDate((Integer)fieldIndex.get(f.name), (Date)values.get(f));
                        break;
                    case 11:
                        pstmt.setTime((Integer)fieldIndex.get(f.name), (Time)values.get(f));
                        break;
                    case 12:
                        File file = (File)values.get(f);
                        FileInputStream is = new FileInputStream(file);
                        pstmt.setBinaryStream((Integer)fieldIndex.get(f.name), is, (int)file.length());
                }
            }

            pstmt.executeUpdate();
        } catch (SQLException e1) {
            System.err.print(e1.getLocalizedMessage());
            e1.printStackTrace();
            System.exit(0);
        } catch (FileNotFoundException e2) {
            System.err.print(e2.getLocalizedMessage());
            e2.printStackTrace();
            System.exit(0);
        }

    }

    public boolean runDeleteQuery(String sql) {
        try {
            Statement st = this.myConn.createStatement();
            return st.execute(sql);
        } catch (SQLException e) {
            System.err.print(e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(0);
            return false;
        }
    }

    public static MatchInformation createMIfromArrayList(Ontology candidate, Ontology target, ArrayList<String[]> matchList) {
        MatchInformation mi = new MatchInformation(candidate, target);
        ArrayList<Match> matches = new ArrayList<>();
        Iterator<String[]> it = matchList.iterator();

        while(it.hasNext()) {
            String[] match = (String[])it.next();
            Term c = candidate.getTermByID(Long.parseLong(match[2]));
            Term t = target.getTermByID(Long.parseLong(match[4]));

            assert c != null && t != null;

            matches.add(new Match(t, c, Double.parseDouble(match[5])));
        }

        mi.setMatches(matches);
        return mi;
    }

    public static void main(String[] args) {
        new DBInterface();
    }

    class DB {
        public DB() {

        }

        public Connection dbConnect(int dbmstype, String host, String dbName, String db_userid, String db_password) {
            try {
                String db_connect_string;
                switch(dbmstype) {
                    case 1:
                        Class.forName("com.mysql.jdbc.Driver");
                        db_connect_string = "jdbc:mysql://" + host + "/" + dbName;
                        break;
                    case 2:
                        Class.forName("net.sourceforge.jtds.jdbc.Driver");
                        db_connect_string = "jdbc:jtds:sqlserver://" + host + "/" + dbName;
                        break;
                    default:
                        IllegalArgumentException e = new IllegalArgumentException("unsupported DBMS type");
                        throw e;
                }

                Connection conn = DriverManager.getConnection(db_connect_string, db_userid, db_password);
                System.out.println("connected");
                return conn;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public void main(String[] args) {
            DB db = new DB();
            myConn = db.dbConnect(1, "localhost:3306/", "schemaMatching", "temp", (String)null);
        }
    }


}
