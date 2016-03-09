package repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import mysql.BdD;

public class Amitie {
	
	/**
	 * Vérifie l'existence d'un lien entre deux Jean Kévin, même s'il n'est pas effectif
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 * @return
	 */
	public static boolean existe(String log1, String log2){
		Statement s = BdD.getStatement();
		try {
			ResultSet r = s.executeQuery("SELECT * FROM r_lier WHERE (identifiant1='"
					+log1+"' AND identifiant2='"+log2+"') OR (identifiant2='"
					+log1+"' AND identifiant1='"+log2+"');");
			if(r.next())
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Selectionne tous les Jean Kevins amis de Jean Kevin en paramètre
	 * @param jk le Jean Kévin central
	 * @return la liste des amis de Jean Kévin
	 */
	public static ArrayList<JeanKevin> selectAmis(JeanKevin jk){
		ArrayList<JeanKevin> list = new ArrayList<JeanKevin>();
		Statement s = BdD.getStatement();
		try {
			ResultSet r = s.executeQuery("SELECT jk.nom, jk.prenom, jk.identifiant, jk.mail"
			+" FROM jean_kevin jk, r_lier a WHERE (a.identifiant1='"+jk.getIdentifiant()
			+"' OR a.identifiant2='"+jk.getIdentifiant()+"') AND (a.identifiant1=jk.identifiant"
			+" OR a.identifiant2=jk.identifiant) AND jk.identifiant<>'"+jk.getIdentifiant()+"'"
			+" AND effectif=1;");
			while(r.next()){
				JeanKevin jks = new JeanKevin(
						r.getString(1),
						r.getString(2),
						r.getString(3));
				list.add(jks);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * Vérifie si il existe une relation entre els 2 JK et si elle est effective
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 * @return vrai si la demande a été acceptée, faux sinon
	 */
	public static boolean estEffective(String jk1, String jk2){
		Statement s = BdD.getStatement();
		try {
			ResultSet r = s.executeQuery("SELECT * FROM r_lier WHERE (identifiant1='"
					+jk1+"' AND identifiant2='"+jk1+"') OR (identifiant2='"
					+jk2+"' AND identifiant1='"+jk2+"')"
					+"AND effectif=1;");
			if(r.next())
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Supprime une realtion d'amitié entre 2 Jean Kevin
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 */
	public static void supprimer(String jk1, String jk2){
		Statement s = BdD.getStatement();
		try {
			s.execute("DELETE FROM r_lier WHERE (identifiant1='"
					+jk1+"' AND identifiant2='"+jk2+"') OR (identifiant2='"
					+jk1+"' AND identifiant1='"+jk2+"');");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Permet d'accepter une demande en amitié entre 2 Jean Kevins
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 */
	public static void accetper(String jk1, String jk2){
		Statement s = BdD.getStatement();
		try {
			s.execute("UPDATE r_lier SET `effectif`=1 WHERE (identifiant1='"
					+jk1+"' AND identifiant2='"+jk2+"') OR (identifiant2='"
					+jk1+"' AND identifiant1='"+jk2+"');");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
