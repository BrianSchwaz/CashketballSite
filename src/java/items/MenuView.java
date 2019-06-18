/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;

import java.io.Serializable;
import java.sql.SQLException;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author bkschwar
 */
@ManagedBean
@SessionScoped
public class MenuView implements Serializable{

    private MenuModel graphMenu;
    private MenuModel chartMenu;
 
    @PostConstruct
    public void init() {
        System.out.println("initialiazed");  
        graphMenu = new DefaultMenuModel();
        chartMenu = new DefaultMenuModel();
 
        //first submenu
        DefaultSubMenu graphSubmenu = new DefaultSubMenu("Stats");
        DefaultMenuItem item;
        
        item = new DefaultMenuItem("Points");
        item.setCommand("#{menuView.setPoints}");
        item.setUpdate("@(form)");
        item.setAjax(true);
        graphSubmenu.addElement(item);
        
        item = new DefaultMenuItem("Rebounds");
        item.setCommand("#{menuView.setRebounds}");
        item.setUpdate("@(form)");
        item.setAjax(true);
        graphSubmenu.addElement(item);
        
        item = new DefaultMenuItem("Assists");
        item.setCommand("#{menuView.setAssists}");
        item.setUpdate("@(form)");
        item.setAjax(true);
        graphSubmenu.addElement(item);
        
        item = new DefaultMenuItem("Steals");
        item.setCommand("#{menuView.setSteals}");
        item.setUpdate("@(form)");
        item.setAjax(true);
        graphSubmenu.addElement(item);
        
        item = new DefaultMenuItem("Blocks");
        item.setCommand("#{menuView.setBlocks}");
        item.setUpdate("@(form)");
        item.setAjax(true);
        graphSubmenu.addElement(item);
        
        item = new DefaultMenuItem("Turnovers");
        item.setCommand("#{menuView.setTurnovers}");
        item.setUpdate("@(form)");
        item.setAjax(true);
        graphSubmenu.addElement(item);
        
        DefaultSubMenu chartSubmenu = new DefaultSubMenu("Stats");
        
        item = new DefaultMenuItem("Field Goals");
        item.setCommand("#{menuView.setFG}");
        item.setUpdate("@(form)");
        item.setAjax(true);
        chartSubmenu.addElement(item);
        
        item = new DefaultMenuItem("3 Pointers");
        item.setCommand("#{menuView.set3FG}");
        item.setUpdate("@(form)");
        item.setAjax(true);
        chartSubmenu.addElement(item);
        
        item = new DefaultMenuItem("Free Throws");
        item.setCommand("#{menuView.setFT}");
        item.setUpdate("@(form)");
        item.setAjax(true);
        chartSubmenu.addElement(item);

 
        graphMenu.addElement(graphSubmenu);
        chartMenu.addElement(chartSubmenu);
        
    }
    
    public static void setFG() throws SQLException{
        ChartView.chartField = "fg";
        ChartView.chartAttempts = "fga";
        ChartView.chartTitle = "Field Goals";
        if(PlayerListBean.currentPid == null) return;
        ChartView.generateCharts(PlayerListBean.currentPid);
    }
    
    public static void set3FG() throws SQLException{
        ChartView.chartField = "fg3";
        ChartView.chartAttempts = "fg3a";
        ChartView.chartTitle = "3 Pointers";
        if(PlayerListBean.currentPid == null) return;
        ChartView.generateCharts(PlayerListBean.currentPid);
    }
    
    public static void setFT() throws SQLException{
        ChartView.chartField = "ft";
        ChartView.chartAttempts = "fta";
        ChartView.chartTitle = "Free Throws";
        if(PlayerListBean.currentPid == null) return;
        ChartView.generateCharts(PlayerListBean.currentPid);
    }
    
    public static void setPoints() throws SQLException{
        ChartView.graphField = "pts";
        ChartView.graphTitle = "Points";
        if(PlayerListBean.currentPid == null) return;
        ChartView.generateCharts(PlayerListBean.currentPid);
    }
    
    public static void setRebounds() throws SQLException{
        ChartView.graphField = "trb";
        ChartView.graphTitle = "Rebounds";
        if(PlayerListBean.currentPid == null) return;
        ChartView.generateCharts(PlayerListBean.currentPid);
    }
    
    public static void setAssists() throws SQLException{
        ChartView.graphField = "ast";
        ChartView.graphTitle = "Assists";
        if(PlayerListBean.currentPid == null) return;
        ChartView.generateCharts(PlayerListBean.currentPid);
    }
    
    public static void setSteals() throws SQLException{
        ChartView.graphField = "stl";
        ChartView.graphTitle = "Steals";
        if(PlayerListBean.currentPid == null) return;
        ChartView.generateCharts(PlayerListBean.currentPid);
    }
    
    public void setBlocks() throws SQLException{
        ChartView.graphField = "blk";
        ChartView.graphTitle = "Blocks";
        if(PlayerListBean.currentPid == null) return;
        ChartView.generateCharts(PlayerListBean.currentPid);
    }
    
    public void setTurnovers() throws SQLException{
        ChartView.graphField = "tov";
        ChartView.graphTitle = "Turnovers";
        if(PlayerListBean.currentPid == null) return;
        ChartView.generateCharts(PlayerListBean.currentPid);
    }
 
    public MenuModel getGraphMenu() {
        return graphMenu;
    }
    
    public MenuModel getChartMenu() {
        return chartMenu;
    }

    
}
