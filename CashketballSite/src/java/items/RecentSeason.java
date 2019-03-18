/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author bkschwar
 */
public class RecentSeason {

    /**
     * Creates a new instance of RecentSeason
     */
    private HashMap<String,Object> field_value;
    private HashMap<String,String> field_list;
    private ArrayList<String> col_names;
    private DBConnect dbConnect = new DBConnect();
    

    public HashMap<String, Object> getField_value() {
        return field_value;
    }

    public void setField_value(HashMap<String, Object> field_value) {
        this.field_value = field_value;
    }

    public Collection<Object> getValues() {
        return field_value.values();
    }
    
    public void setField(String field,Object o){
        field_value.put(field, o);
    }
    
    public Object getField(String field){
        return field_value.get(field);
    }
    
    public RecentSeason() throws SQLException {
        field_value = new HashMap<String,Object>();
        col_names = new ArrayList<String>();
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        PreparedStatement ps
                = con.prepareStatement(
                        "SELECT column_name,data_type FROM information_schema.columns WHERE table_name = '" + "PerGame" + "'");

        //get Player data from database
        ResultSet result = ps.executeQuery();
        
        field_list = new HashMap<String,String>();

        while (result.next()) {
            String col_name = result.getString("column_name");
            String data_type = result.getString("data_type");
            //System.out.println(table + " " + col_name + " " + data_type);
            col_names.add(col_name);
            field_list.put(col_name,data_type);
        }
    }

    public HashMap<String, String> getField_list() {
        return field_list;
    }

    public void setField_list(HashMap<String, String> field_list) {
        this.field_list = field_list;
    }

    public ArrayList<String> getCol_names() {
        return col_names;
    }

    public void setCol_names(ArrayList<String> col_names) {
        this.col_names = col_names;
    }

    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }
}
