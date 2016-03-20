package repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONArray;

import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.Niveau1;
import http.RequeteServeur.Niveau2;
import mysql.BdD;

public class Amitie {
	
	/**
	 * V�rifie l'existence d'un lien entre deux Jean K�vin, m�me s'il n'est pas effectif
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
	 * Selectionne tous les Jean Kevins amis de Jean Kevin en param�tre
	 * @param jk le Jean K�vin dont on veut r�cup�rer les amis
	 * @return la liste des amis de Jean K�vin
	 */
	public static ArrayList<JeanKevin> selectAmis(JeanKevin jk){
		try {
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.selectionnerAmis,
					new JSONArray(new String[]{jk.getIdentifiant()}));
			if(r.estOK()){
				ArrayList<JeanKevin> list = new ArrayList<JeanKevin>();
				JSONArray amis = r.getCorps().getJSONArray("amis");
				for (int i=0; i<amis.length(); i++) {
					list.add(JeanKevin.parseJSON(amis.getJSONObject(i)));
				}
				return list;
			}
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
	
	/**
	 * V�rifie si il existe une relation entre les 2 JK et si elle est effective
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 * @return vrai si la demande a �t� accept�e, faux sinon
	 */
	public static boolean estEffective(JeanKevin jk1, JeanKevin jk2){

		try {
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Amitie, Niveau2.estEffective,
					new JSONArray(new String[]{jk1.getIdentifiant(), jk2.getIdentifiant()}));
			if(r.estOK()){
				return r.getCorps().getBoolean("estEffective");
			}
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	
	/**
	 * Supprime une realtion d'amiti� entre 2 Jean Kevin
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 */
	public static boolean supprimer(JeanKevin jk1, JeanKevin jk2){
		
		try {
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Amitie, Niveau2.estEffective,
					new JSONArray(new String[]{jk1.getIdentifiant(), jk2.getIdentifiant()}));
			if(r.estOK()){
				return r.getCorps().getBoolean("suppressionOK");
			}
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	
	/**
	 * Permet d'accepter une demande en amiti� entre 2 Jean Kevins
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 * @return vrai si l'op�ration s'est bien d�roul�e, faux sinon
	 */
	public static boolean accetper(JeanKevin jk1, JeanKevin jk2){
		try {
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Amitie, Niveau2.accepter,
					new JSONArray(new String[]{jk1.getIdentifiant(), jk2.getIdentifiant()}));
			if(r.estOK()){
				return r.getCorps().getBoolean("acceptee");
			}
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
}
