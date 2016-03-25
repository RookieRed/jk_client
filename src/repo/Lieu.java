package repo;

import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.Niveau1;
import http.RequeteServeur.Niveau2;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class Lieu {
	
	private int id;
	private String libelle;
	
	private Lieu(int id, String libelle) {
		this.id = id;
		this.libelle = libelle;
	}
	
	
	/**
	 * Ajoute un nouveau lieu dans la base de données
	 * @param libelle Le nom du lieu
	 * @return vrai si l'ajout s'est bien passé, faux sinon
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
	
	
	/**
	 * Supprime la carte de la base de données
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
	 * @param jk le Jean-Kévin dont on cherche les lieux
	 * @return une ArrayList de Lieux ou null en cas de problèmes
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
							lieux.getJSONObject(i).getString("libelle")));
				}
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		return null;
	}
	
	/**
	 * 
	 * @param libelle
	 * @return
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

	public int getId() {
		return this.id;
	}
	
	public String getLibelle(){
		return this.libelle;
	}
	
	

}
