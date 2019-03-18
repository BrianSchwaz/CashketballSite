/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author brian
 */
@Named(value = "newlogin")
@SessionScoped
@ManagedBean
public class NewLogin implements Serializable {
    
    private String login;
    private String password;
    private UIInput loginUI;

    public UIInput getLoginUI() {
        return loginUI;
    }

    public void setLoginUI(UIInput loginUI) {
        this.loginUI = loginUI;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    private HashMap<String,String> getLogins() throws SQLException {
        
        DBConnect dbConnect = new DBConnect();

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "Select * from \"Login\"");

        //get Player data from database
        ResultSet result = ps.executeQuery();

        HashMap<String,String> log_pwd = new HashMap<String,String>();

        while (result.next()) {

            log_pwd.put(result.getString("login"),result.getString("password"));
        }
        result.close();
        con.close();
        return log_pwd;
    }
    
    public void validate(FacesContext context, UIComponent component, Object value) throws SQLException,ValidatorException
    {
        login = loginUI.getLocalValue().toString();
        password = value.toString();
        System.out.println(password);
        System.out.println(login);
        HashMap<String,String> log_pwd = getLogins();
        
        if ((log_pwd.containsKey(login))) {
            FacesMessage errorMessage = new FacesMessage("Login already exists");
            throw new ValidatorException(errorMessage);
        }
        else
        {
            DBConnect dbConnect = new DBConnect();

            Connection con = dbConnect.getConnection();

            if (con == null) {
                throw new SQLException("Can't get database connection");
            }

            PreparedStatement ps
                    = con.prepareStatement(
                        "Insert Into \"Login\" Values('" + login + "','" + password + "')");

            ps.executeUpdate();
            //con.commit();
            ps.close();
            
            con.close();
        }
    }
    
    public String back() {
        return "back";
    }

}