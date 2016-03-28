package repo;

import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.NivImg;
import http.RequeteServeur.Niveau1;
import http.RequeteServeur.Niveau2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author RookieRed
 *
 */
public class Lieu {

		/*-------------------------
		-- ATTRIBUS DE LA CLASSE --
		--------------------------*/
	
	private int id;
	private String libelle;
	private String ville;
	
	
		/*-----------------
		-- CONSTRUCTEURS --
		------------------*/
	
	private Lieu(int id, String libelle, String ville) {
		this.id = id;
		this.libelle = libelle;
		this.ville = ville;
	}
	

		/*----------------------
		-- METHODES DE CLASSE --
		-----------------------*/
	
	/**
	 * Ajoute un nouveau lieu dans la base de donn�es
	 * @param libelle Le nom du lieu
	 * @return vrai si l'ajout s'est bien pass�, faux sinon
	 */
	public static boolean ajouter(String libelle){
		
		try {
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Lieu, Niveau2.ajouter,
					new JSONArray(new String[]{libelle}));
			if(r.estOK())
				return r.getCorps().getBoolean("ajoutOK");
		} catch (JSONException e) {e.printStackTrace();}
		return false;
	}
	

	public static Lieu selection(int id) {

		try {
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Lieu, Niveau2.selectionner,
					new JSONArray(new int[]{id}));
			if(r.estOK()){
				JSONObject lieu = r.getCorps().getJSONObject("lieu");
				return new Lieu(id, lieu.getString("libelle"), lieu.getString("ville"));
			}
		} catch (JSONException e) {e.printStackTrace();}
		return null;
	}
	
	/**
	 * Supprime la carte de la base de donn�es
	 * @return
	 */
	public boolean supprimer(){
		
		ReponseServeur r;
		try {
			r = RequeteServeur.executerRequete(Niveau1.Lieu, Niveau2.supprimer,
					new JSONArray(new int[]{this.id}));
			if(r.estOK())
				return r.getCorps().getBoolean("supprOK");
		} 
		catch (JSONException e) {e.printStackTrace();}
		return false;
	}
	
	/**
	 * Selectionne l'ensemble des lieux auxquels JK est inscrit
	 * @param jk le Jean-K�vin dont on cherche les lieux
	 * @return une ArrayList de Lieux ou null en cas de probl�mes
	 */
	public ArrayList<Lieu> selectionnerLieuxJK(JeanKevin jk){
		
		try{
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Lieu, Niveau2.selectionnerLieuxJK, 
					new JSONArray(new String[]{jk.getIdentifiant()}));
			if(r.estOK()){
				JSONArray lieux = r.getCorps().getJSONArray("lieux");
				ArrayList<Lieu> list = new ArrayList<Lieu>();
				for (int i=0; i < lieux.length(); i++) {
					list.add(new Lieu(lieux.getJSONObject(i).getInt("id"),
							lieux.getJSONObject(i).getString("libelle"),
							lieux.getJSONObject(i).getString("ville")));
				}
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		return null;
	}
	
	/**
	 * Modifie le nom associ� � un lieu dans la base de donn�es
	 * @param libelle le nouveua libell� du lieu
	 * @return vrai si l'op�ration est un succ�s, faux sinon
	 */
	public boolean modifierLibelle(String libelle) {
		
		try{
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Lieu, Niveau2.modifier, 
					new JSONArray(new String[]{""+this.id, libelle}));
			if(r.estOK() && r.getCorps().getBoolean("modifOK")){
				this.libelle = libelle;
				return true;
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		return false;
	}
	
	//TODO
	public boolean ajouterLieuJK(JeanKevin jk){
		
//		try{
//			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Lieu, Niveau2.rechercher,
//					new JSONArray(new String[]{}));
//		}
//		catch(JSONException e){e.printStackTrace();}
		
		return false;
	}
	
	/**
	 * Effectue une recherche dans les lieux enregistr� dans la base de donn�es
	 * @param motCles
	 * @return
	 */
	public static HashSet<Lieu> rechercher(String motCles){

		try{
			HashSet<Lieu> ret = new HashSet<Lieu>();
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Lieu, Niveau2.rechercher,
					new JSONArray(new String[]{motCles}));
			if(!r.estOK()){
				return null;
			}
			JSONArray lieux = r.getCorps().getJSONArray("resultats");
			for(int i=0; i<lieux.length(); i++){
				JSONObject lieu = lieux.getJSONObject(i);
				ret.add(new Lieu(lieu.getInt("id"), lieu.getString("libelle"),
						lieu.getString("ville")));
			}
			return ret;
		}
		catch(JSONException e){e.printStackTrace();}
		
		return null;
	}
	
	/**
	 * Enregistre une nouvelle carte � un lieu choisi
	 * @param img
	 * @return
	 */
	public boolean ajouterCarte(File img){
		
		try {
			ReponseServeur r = RequeteServeur.transfererImage(img, NivImg.Carte, 
					new JSONArray(new String[]{""+this.id, img.getName()}));
			if(r == null || !r.estOK()) return false;
			return r.getCorps().getBoolean("finTransfert");
		}
		catch (JSONException e) {e.printStackTrace();}
		return false;
	}
	
	
		/*----------------
		 *    GETTERS    *
		 *****************/
	
	public int getId() {
		return this.id;
	}
	public String getLibelle(){
		return this.libelle;
	}
	public String getVille(){
		return this.ville;
	}
	@Override
	public String toString() {
		return "Lieu N� " +this.id +" - " + this.libelle + " � " + this.ville;
	}


		/***************************
		**    EQUALS & HASHCODE   **
		****************************/
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lieu other = (Lieu) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
}
