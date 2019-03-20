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
    }

    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }
}
