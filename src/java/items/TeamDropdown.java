
package items;

import items.DBConnect;
import items.Queries;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class TeamDropdown implements Serializable {
     
    private Map<String,String> teams;
    private static DBConnect dbConnect;
    private static Connection con;
    private String opposingTeam = "SELECT OPPONENT";
     
    @PostConstruct
    public void init() {
        teams = new HashMap<String,String>();
        opposingTeam = "SELECT OPPONENT";
        System.out.print("trying");
        try {
            dbConnect = DBConnect.getInstance();
            con = dbConnect.getConnection();
            
            PreparedStatement ps = con.prepareStatement(Queries.getTeams());
            
            ResultSet result = ps.executeQuery();
            
            while (result.next()) {
                String team = result.getString("team_id");
                System.out.print(team);
                teams.put(team,team);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(TeamDropdown.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getOpposingTeam() {
        return opposingTeam;
    }

    public void setOpposingTeam(String opposingTeam) {
        this.opposingTeam = opposingTeam;
    }
 
 
    public Map<String,String> getTeams() {
        return teams;
    }
    
    public void changeOpposing(){
        
    }
}
