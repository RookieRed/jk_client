package mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;



public class BdD {
	
	//NOTES : BASE DE DONNEES JK SUR LOCALHOST (wamp port = 3306)
    private String serveur = "sql101.hebergratuit.net";
    private int    port    = 3306;
    private String user;
    private String psw;
    private Connection cnct;
    private static BdD instance = null;
    
    private BdD(){
    	String url  = "jdbc:mysql://"+this.serveur+":"+this.port+"/jean_kevin";
    	this.user   = "heber_17436339";
    	this.psw    = "q2lG3Nk6a4";
    	//this.user = "u227306295_user";
    	//this.psw  = "azerty";
    	try {
            //Chargement du driver MySQL
            Class.forName( "com.mysql.jdbc.Driver" );
            //Connection � la base de donn�es
            this.cnct = DriverManager.getConnection(url, this.user, this.psw);
        }
        catch ( Exception e ) {
        	e.printStackTrace();
        }
    }
    
    
    
    private static BdD getInstance() {
    	if(BdD.instance == null){
    		BdD.instance = new BdD();
    	}
        return BdD.instance;
	}
    

    public Connection getConnection() {
    	return getInstance().cnct;
    }
    
    
    public static Statement getStatement(){
    	Statement s = null;
    	try {
			s = getInstance().cnct.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return s;
    }	

}

