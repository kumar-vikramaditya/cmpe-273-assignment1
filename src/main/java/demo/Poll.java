package demo;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class Poll {
	
	private String id;
	@NotNull
    private String question;
	@NotNull
    private String started_at;
	@NotNull
    private String expired_at;
	@NotNull
    private String[] choice;
    private int[] results;
    
    public Poll(){
    	
    }
    
    public Poll(String id, String question, String started_at, String expired_at, String[] choice){
    	this.id=id;
    	this.question=question;
    	this.started_at=started_at;
    	this.expired_at=expired_at;
    	this.choice=choice;
    	//results[0]=0;
    	//results[1]=0;
    }
    
    public String getId(){
    	return id;
    }
    
    public String getQuestion(){
    	return question;
    }
    
    public String getStarted_at(){
    	return started_at;
    }
    
    public String getExpired_at(){
    	return expired_at;
    }
    
    public String[] getChoice(){
    	return choice;
    }
    
    public int[] getResults(){
    	return results;
    }
    
    public void setResults(int[] tempArr){
    	this.results=tempArr;
    }

}
