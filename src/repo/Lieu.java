package repo;

import java.awt.Image;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import mysql.BdD;

public class Lieu {
	
	private int id;
	private String libelle;
	
	public Lieu() {
		// TODO Auto-generated constructor stub
	}
	
	public void ajouter(){
		Statement s = BdD.getStatement();
		try {
			s.execute("INSERT INTO lieu(libelle) VALUES ('"+this.libelle+"');");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateSchema(Image i){
		
	}
	
	public void supprimer(){
		Statement s = BdD.getStatement();
		try {
			s.execute("DELETE FROM lieu WHERE id="+this.libelle+";");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Lieu> selectLieuxJK(JeanKevin jk){
		ArrayList<Lieu> list = new ArrayList<Lieu>();
		return list;
	}
	
	public String getLibelle() {
		return this.libelle;
	}

	public void updateLibelle(String libelle) {
		this.libelle = libelle;Statement s = BdD.getStatement();
		try {
			s.execute("UPDATE lieu SET libelle='"+this.libelle+"' WHERE id="+this.id+";");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public int getId() {
		return this.id;
	}
	
	

}
