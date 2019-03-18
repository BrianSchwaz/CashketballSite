/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;
/**
 *
 * @author bkschwar
 */
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.util.Date;
import java.util.TimeZone;

@Named(value = "Player")
@SessionScoped
@ManagedBean
public class Player implements Serializable {

    private DBConnect dbConnect;
    private Integer pid;
    private String name;
    private Integer height;
    private Integer weight;
    private String recent_season;
    private String recent_team;

    public Player() throws SQLException {
        this.dbConnect = DBConnect.getInstance();
    }
    
    public Player(Integer pid, String name, Integer height, Integer weight) throws SQLException {
        this.dbConnect = DBConnect.getInstance();
        this.pid = pid;
        this.name = name;
        this.height = height;
        this.weight = weight;
    }

    public String getRecent_team() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select team_id from 'Game' where pid = " + this.getPid().toString() + " and date_game = " + this.getLastGame());

        //get Player data from database
        ResultSet result = ps.executeQuery();

        String team = result.getString("team_id");
        result.close();
        con.close();
        return team;
    }

    public void setRecent_team(String recent_team) {
        this.recent_team = recent_team;
    }

    public DBConnect getDbConnect() {
        return dbConnect;
    }
    
    public String getLastGame() throws SQLException{
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select MAX(date_game) as date_game from 'Game' where pid = " + this.getPid().toString());

        //get Player data from database
        ResultSet result = ps.executeQuery();

        String game = result.getString("date_game");

        result.close();
        con.close();
        return game;
    }
    
    public String getMaxSeason() throws SQLException{
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select MAX(season) as season from 'Game' where pid = " + this.getPid().toString());

        //get Player data from database
        ResultSet result = ps.executeQuery();

        String game = result.getString("season");

        result.close();
        con.close();
        return game;
    }

    public String getRecent_season() throws SQLException {
         Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select season from 'PerGame' where pid = " + this.getPid().toString() + " and season = " + this.getMaxSeason() + " and team_id = " + this.getRecent_team());

        //get Player data from database
        ResultSet result = ps.executeQuery();

        String season = result.getString("season");

        result.close();
        con.close();
        return season;
    }

    public Integer getPid() throws SQLException {
        if (pid == null) {
            Connection con = dbConnect.getConnection();

            if (con == null) {
                throw new SQLException("Can't get database connection");
            }

            PreparedStatement ps
                    = con.prepareStatement(
                            "select max(pid)+1 from 'Player'");
            ResultSet result = ps.executeQuery();
            if (!result.next()) {
                return null;
            }
            pid = result.getInt(1);
            result.close();
            con.close();
        }
        return pid;
    }

    public void setPid(Integer PlayerID) {
        this.pid = PlayerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String createPlayer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("Insert into 'Player' values(?,?,?,?)");
        preparedStatement.setInt(1, pid);
        preparedStatement.setString(2, name);
        preparedStatement.setInt(3, weight);
        preparedStatement.setInt(4, height);
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        //Util.invalidateUserSession();
        return "main";
    }

    public String deletePlayer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();
        statement.executeUpdate("Delete from 'Player' where pid = " + pid);
        statement.close();
        con.commit();
        con.close();
        Util.invalidateUserSession();
        return "main";
    }

    public String showPlayer() {
        return "showPlayer";
    }

    public Player getPlayer() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from 'Player' where pid = " + pid);

        //get Player data from database
        ResultSet result = ps.executeQuery();

        result.next();

        name = result.getString("name");
        weight = result.getInt("weight");
        return this;
    }

    public List<Player> getPlayerList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select pid, name, height, weight from 'Player' order by pid");

        //get Player data from database
        ResultSet result = ps.executeQuery();

        List<Player> list = new ArrayList<Player>();

        while (result.next()) {
            Player cust = new Player();

            cust.setPid(result.getInt("pid"));
            cust.setName(result.getString("name"));
            cust.setWeight(result.getInt("weight"));
            cust.setHeight(result.getInt("height"));

            //store all data into a List
            list.add(cust);
        }
        result.close();
        con.close();
        return list;
    }

    public void PlayerIDExists(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {

        if (!existsPid((Integer) value)) {
            FacesMessage errorMessage = new FacesMessage("ID does not exist");
            throw new ValidatorException(errorMessage);
        }
    }

    public void validatePlayerID(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        int id = (Integer) value;
        if (id < 0) {
            FacesMessage errorMessage = new FacesMessage("ID must be positive");
            throw new ValidatorException(errorMessage);
        }
        if (existsPid((Integer) value)) {
            FacesMessage errorMessage = new FacesMessage("ID already exists");
            throw new ValidatorException(errorMessage);
        }
    }

    private boolean existsPid(int id) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement("select * from Player where pid = " + id);

        ResultSet result = ps.executeQuery();
        if (result.next()) {
            result.close();
            con.close();
            return true;
        }
        result.close();
        con.close();
        return false;
    }
}