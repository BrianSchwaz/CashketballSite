/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;

import java.util.ArrayList;

/**
 *
 * @author bkschwar
 */
public class Queries {
    
    public static String constructPlayerQuery(String pgsFields,String currentSeason,int rows,int offset, String searchInput,String team, boolean isSearch, String login){
        System.out.println("Query login: " + login);
        return 
        "SELECT curPlayers.*\n" + 
        "FROM\n" +
        "       ((SELECT tot.team_id as team_id" + pgsFields + "\n" + 
        "       FROM\n" +
        "               (SELECT string_agg(team_id,'/') as team_id, pid\n" + 
        "               FROM \"PerGame\"\n" + 
        "               WHERE season = '" + currentSeason +"' AND NOT team_id = 'TOT' \n" +
        "               GROUP BY pid)tot\n" +
        "       JOIN\n" +
        "               (SELECT * FROM Tradetable WHERE season ='" + currentSeason + "')pgs\n" +
        "       ON pgs.pid = tot.pid)\n" +
        "       UNION\n" +
        "       (SELECT pgs.team_id as team_id" + pgsFields + "\n" +
        "       FROM\n" +
        "               (SELECT * FROM \"PerGame\" WHERE season = '"+ currentSeason +"')pgs\n" +
        "       LEFT JOIN\n" +
        "               (SELECT *\n" +
        "               FROM (SELECT COUNT(pid) as count,pid FROM \"PerGame\" WHERE season = '" + currentSeason +"' GROUP BY pid)t1\n" +
        "               WHERE count > 1)tot\n" +
        "       ON pgs.pid = tot.pid\n" +
        "       WHERE tot.pid IS NULL))curPlayers\n" +
        (isSearch?"LEFT":"INNER") + " JOIN\n" +
        "       (SELECT * FROM \""+ team +"\" WHERE login = '" + login + "')onRoster\n" +
        "ON curPLayers.pid = onRoster.pid \n" +
        (isSearch?"WHERE onRoster.pid IS NULL AND LOWER(curPlayers.name) LIKE '" + searchInput.toLowerCase() + "%'\n":"") +
        "ORDER BY name asc\n" +
        (isSearch?"LIMIT " + rows + " OFFSET " + offset:"");
                
    }
    
    public static String pgsString(ArrayList<String> important){
        String pgs = "";
        for(int i=0;i< important.size();i++)
        {
            if(!important.get(i).equals("team_id")){
                pgs += ",pgs."+important.get(i);
            }
        }
        return pgs;
    }
}
