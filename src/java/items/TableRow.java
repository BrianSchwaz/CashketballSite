/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author bkschwar
 */
public class TableRow {

    /**
     * Creates a new instance of TableRow
     */
    private HashMap<String,Object> field_value;

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
    
    public TableRow() throws SQLException {
        field_value = new HashMap<String,Object>();
    }
}
