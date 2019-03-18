/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;

import java.io.Serializable;
import static java.rmi.server.LogStream.log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author bkschwar
 */
@ManagedBean
@Named(value = "playerListBean")
@SessionScoped
public class PlayerListBean implements Serializable{
    
    @ManagedProperty(value="#{login}")
    private ELContext elContext = FacesContext.getCurrentInstance().getELContext();
    private Login login = (Login) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "login");
    private HashMap<Integer,RecentSeason> players = new HashMap<>();
    private HashMap<Integer,RecentSeason> roster = new HashMap<>();
    private HashMap<Integer,RecentSeason> opRoster = new HashMap<>();
    private HashMap<String,String> showingFields = new HashMap<>();
    private RecentSeason selected = null;
    private Integer offset = 0;
    private Integer rows = 10;
    private DBConnect dbConnect = DBConnect.getInstance();
    private Connection con;
    private String input = "";
    private String[] imp = {"name","team_id","mp_per_g","fg_per_g","fg_pct","fg3_per_g","fg3_pct","efg_pct","ft_per_g","ft_pct","orb_per_g","trb_per_g","ast_per_g","stl_per_g","blk_per_g","tov_per_g","pts_per_g"};
    private ArrayList<String> important = new ArrayList<String>(Arrays.asList(imp));
    private ArrayList<String> selectedImp = new ArrayList<String>(Arrays.asList(imp));
    private String team = "Roster";
    private String currentSeason = "2018-19";
    
    
    public Login getLogin(){
        return login;
    }
    
    public void setLogin(Login login) {
        this.login = login;
    }
    
    public HashMap<String, String> getShowingFields() {
        return showingFields;
    }
    
    public RecentSeason getSelected() {
        return selected;
    }

    public void setSelected(RecentSeason selected) {
        this.selected = selected;
    }

    public void setShowingFields(HashMap<String, String> showingFields) {
        this.showingFields = showingFields;
    }
    
    public ArrayList<String> getSelectedImp() {
        selectedImp = new ArrayList<String>();
        for(int i = 0; i < important.size(); i++){
            String field = important.get(i);
            if(showingFields.containsKey(field)&&Boolean.parseBoolean(showingFields.get(field)))
            {
                selectedImp.add(field);
            }
        }
        return selectedImp;
    }

    public void setSelectedImp(ArrayList<String> selectedImp) {
        this.selectedImp = selectedImp;
    }
    
    public ArrayList<String> getImportant() {
        return important;
    }

    public void setImportant(ArrayList<String> important) {
        this.important = important;
    }
    
    public void setTeam(String team) throws SQLException {
        this.team = team;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
    
    public String getTeam() {
        return team;
    }

    public List<RecentSeason> getRoster() {
        List<RecentSeason> r = new ArrayList<RecentSeason>(roster.values());
        return r;
    }
   
    public void setRoster(HashMap<Integer, RecentSeason> roster) {
        this.roster = roster;
    }

    public List<RecentSeason> getOpRoster() {
        List<RecentSeason> r = new ArrayList<RecentSeason>(opRoster.values());
        return r;
    }

    public void setOpRoster(HashMap<Integer, RecentSeason> opRoster) {
        this.opRoster = opRoster;
    }

    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    /**
     * Creates a new instance of PlayerListBean
     */
    
    public void fillMap(HashMap<Integer,RecentSeason> table,ResultSet result) throws SQLException
    {
        while (result.next()) {
            RecentSeason recent = new RecentSeason();
            for(String col_name: recent.getCol_names())
            {
                if(recent.getField_list().get(col_name).equals("text"))
                {
                    recent.setField(col_name, (Object)result.getString(col_name));
                }
                else if(recent.getField_list().get(col_name).equals("integer"))
                {
                    recent.setField(col_name, (Object)result.getInt(col_name));
                }
                else if(recent.getField_list().get(col_name).equals("real"))
                {
                    recent.setField(col_name, (Object)result.getFloat(col_name));
                }
            }
            //store all data into a List
            table.put(result.getInt("pid"),recent);
        }
    }
    
    @PostConstruct
    public void init() {
    }
    
    public String getDisplayable(String s){
        return s.replace("_per_g", "").toUpperCase();
    }
    
    public void updatePlayers() throws SQLException{
        System.out.println(team);
        players = new HashMap<>();
        PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM"
                                + "(SELECT * FROM \"PerGame\" WHERE season = '" + currentSeason +"' AND name LIKE '%" + input + "%')pg "
                                        + "LEFT JOIN "
                                + "(SELECT * FROM \"" + team + "\" WHERE login = '" + curLogin() + "')roster "
                                + "ON pg.pid = roster.pid "
                                + "WHERE roster.pid IS NULL "
                                + "ORDER BY pg.name "
                                + "LIMIT " + rows + " OFFSET " + offset);
        //get Player data from database
        ResultSet result = ps.executeQuery();
        
        fillMap(players,result);
                
        result.close();
    }
    
    public void updateRoster() throws SQLException{
        if(team.equals("Roster"))
        {
            roster = new HashMap<>();
        }
        else
        {
            opRoster = new HashMap<>();
        }
        PreparedStatement ps = con.prepareStatement(
                "SELECT \"pg\".* FROM"
                        + "(SELECT * FROM \"PerGame\" WHERE season = '" + currentSeason + "')pg INNER JOIN "
                        + "(SELECT * FROM \""+ team +"\" WHERE login = '" + curLogin() + "')roster "
                        + "ON pg.pid = roster.pid");
        
        ResultSet result = ps.executeQuery();
        
        fillMap(team.equals("Roster")?roster:opRoster,result);
        
        result.close();
    }
    
    public String curLogin(){
        return getLogin().getLogin();
    }
    
    public PlayerListBean() throws SQLException {
        System.out.println("Cur Login: " + curLogin());

        input = "";
        String[] startingFields = {"name","team_id","mp_per_g","trb_per_g","ast_per_g","stl_per_g","blk_per_g","tov_per_g","pts_per_g"};
        for(int i=0;i<startingFields.length;i++){
            showingFields.put(startingFields[i],"true");
        }
        for(int i=0;i<important.size();i++){
            if(!showingFields.containsKey(important.get(i)))
            {
                showingFields.put(important.get(i),"false");
            }
        }
        
        con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        this.team = "Opposing_Roster";
        updateRoster();
        this.team = "Roster";
        updateRoster();
        updatePlayers();
    }
    
    public void search() throws SQLException
    {
        
        if(input.equals("")){
            return;
        }
        players = new HashMap<Integer,RecentSeason>();
        PreparedStatement ps
                = con.prepareStatement(
                        "SELECT * FROM"
                                + "(SELECT * FROM \"PerGame\" WHERE season = '" + currentSeason +"' AND name LIKE '%" + input + "%')pg "
                                        + "LEFT JOIN "
                                + "(SELECT * FROM \"" + team + "\" WHERE login = '" + curLogin() + "')roster "
                                + "ON pg.pid = roster.pid "
                                + "WHERE roster.pid IS NULL "
                                + "ORDER BY pg.name "
                                + "LIMIT " + rows + " OFFSET " + offset);

        //get Player data from database
        ResultSet result = ps.executeQuery();
        fillMap(players,result);
        result.close();
    }
    
    public List<RecentSeason> getPlayers()
    {
        List<RecentSeason> r = new ArrayList<RecentSeason>(players.values());
        return r;
    }
    
    public String logout(){
        
        System.out.print("logout");
        System.out.println("Logging out: " + curLogin());
        setLogin(null);
        System.out.println("New Login: " + curLogin());
        return "back";
    }
    
    public void change(){
        System.out.print("change");
    }
    
    public void remove(String teamTable) throws SQLException
    {
        setTeam(teamTable);
        System.out.println(teamTable + " " + selected.getField("pid"));
        PreparedStatement ps
            = con.prepareStatement("DELETE FROM \"" + teamTable + "\" WHERE login = '" + curLogin() + "' AND pid = " + selected.getField("pid") + ";");
        ps.executeUpdate();
        ps.close();
        //(team.equals("Roster")?roster:opRoster).remove(pid);
        updateRoster();
        updatePlayers();
    }
    
    public void next10()
    {
        
    }
    
    public void add() throws SQLException
    {
        Integer pid;
        if(selected == null){return;}
        pid = (Integer)selected.getField("pid");
        System.out.println(selected.getField("name"));
        if(!(team.equals("Roster")?roster:opRoster).containsKey(pid))
        {
            PreparedStatement ps
                    = con.prepareStatement(
                        "INSERT INTO \"" + team + "\" VALUES(" + "'" + curLogin() + "'" + "," + pid + ")");
            //get Player data from database
            ps.executeUpdate();
            //con.commit();
            ps.close();
        }
        updateRoster();
        updatePlayers();
    }
    
    public void onRowSelect(SelectEvent event) {
        System.out.println(((RecentSeason) selected).getField("name"));
    }
 
    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Car Unselected", (String)(((RecentSeason) event.getObject()).getField("pid")));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}