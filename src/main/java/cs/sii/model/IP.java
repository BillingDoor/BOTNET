package cs.sii.model;

import java.util.Objects;



public class IP implements Cloneable{
    private String ip;

    public IP(String ip) {
        this.ip = ip;
    }
    
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
    @Override
    protected Object clone(){
        return new IP(this.toString());
    }
    
   @Override
    public String toString(){
    	return ip;
    }
    
    public String toJsonString() {
        return "{\"user_ip\":\""+ip+"\"}";
    }

    @Override 
    public boolean equals(Object o){
        return ip.equals(((IP)o).getIp());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.ip);
        return hash;
    }
}
