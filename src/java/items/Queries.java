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
        (isSearch?"WHERE onRoster.pid IS NULL AND LOWER(curPlayers.name) LIKE '%" + searchInput.toLowerCase() + "%'\n":"") +
        "ORDER BY name asc\n" +
        (isSearch?"LIMIT " + rows + " OFFSET " + offset:"");
                
    }
    
    public static String gamesQuery(Integer pid,String startDate,Integer limit){
        return "SELECT *\n" +
        "FROM \"Game\"\n" +
        "WHERE date_game > '" + startDate + "' AND pid = " + pid +"\n" +
        "ORDER BY date_game desc\n" +
        "LIMIT " + limit;
    }
    
    public static String gamesVal(Integer pid,String startDate,String agg,String field,Integer limit){
        return "SELECT " + agg + "(" + field + ") AS " + field + "\n" +
        "FROM (" + gamesQuery(pid,startDate,limit) + ")lastGames\n" +
        "GROUP BY lastGames.pid\n";
    }
    
    public static String getCols(String table){
        return "SELECT column_name,data_type FROM information_schema.columns WHERE table_name = '" + table + "'";
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
    
    public static String allPlayers(String pgsFields,String currentSeason){
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
        "       WHERE tot.pid IS NULL))curPlayers\n";
    }
    
    public static String getAvgFields(){
        String[] regFields = {"pid","name"};
        String[] avgFields = {"fg","fga","fg_pct","fg3","fg3a","fg3_pct","ft","fta","ft_pct","orb","drb","trb","ast","stl","blk","tov","pf","pts","plus_minus","game_result"};
        String fields = "";
        for(int i = 0; i < regFields.length; i++){
            fields += "top." + regFields[i] + ", ";
        }
        for(int i = 0; i < avgFields.length;i++){
            fields += "ROUND(CAST(AVG(top." + avgFields[i] + ") as numeric),2) as " + avgFields[i] + ", ";
        }
        return fields.substring(0, fields.length() - 2);
    }
    
    public static String pastGamesAvg(Integer pid, String opponent,Integer numGames){
        return 
        "SELECT " + 
        getAvgFields() + "\n" +
        "FROM\n" +
        "(SELECT *\n" +
        "  FROM public.\"Game\"\n" +
        "  WHERE pid = " + pid + " AND \n" +
        "  opp_id = '" + opponent + "'\n" +
        "  ORDER BY date_game desc\n" +
        "  LIMIT " + numGames + ")top\n" +
        "GROUP BY top.pid, top.name";
        
    }
}
