package demo;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"pollList"})
public class Moderator {

    private long id;
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String password;
    private String created_at;
    
    private ArrayList<Poll> pollList=null;
    
    static Hashtable<Long, Moderator> moderatorsTable= new Hashtable<Long,Moderator>();
    static ArrayList<Poll> globalList= new ArrayList<Poll>();
    
    public Moderator(){
    	
    }
    
    public Moderator(long id, String name, String email, String password, String createdAt) {
        this.id = id;
        this.name = name;
        this.email=email;
        this.password=password;
        this.created_at=createdAt;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
    
    @JsonIgnore
    public ArrayList<Poll> getPollList(){
    	
    	if(this.pollList!=null){
    			return pollList;
    	}
    	else{
    		pollList=new ArrayList<Poll>();
    		return pollList;
    	}
    	
    }
    
    public void setEmail(String email) {
        this.email=email;
    }
    
    public void setPassword(String password) {
        this.password=password;
    }
    
    public void setName(String name) {
        this.name=name;
    }
    
    public String getCreated_at() {
        return created_at;
    }
}
