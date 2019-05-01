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
public class Tuple implements Comparable{
    private int pid;
    private String name;
    private Double distance;
    
    public Tuple (int pid, String name, Double distance){
        this.pid = pid;
        this.name = name;
        this.distance = distance;
    }

    @Override
    public int compareTo(Object t) {
        if(t instanceof Tuple){
            if(this.distance < ((Tuple)t).getDistance()){
                return -1;
            }
            else if(this.distance == ((Tuple)t).getDistance()){
                return 0;
            }
        }
        return 1;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
