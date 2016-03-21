package repo;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;

import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.NivImg;
import http.RequeteServeur.Niveau1;
import http.RequeteServeur.Niveau2;
import mysql.BdD;

public class JeanKevin {
	
	
			/*-------------------------
			-- ATTRIBUS DE LA CLASSE --
			--------------------------*/

	private String nom;
	private String prenom;
	private String identifiant;
	private String mail;
	
			/*-----------------
			-- CONSTRUCTEURS --
			------------------*/
	
	private JeanKevin(String nom, String prenom, String identifiant, String mail) {
		this.nom = nom;
		this.prenom = prenom;
		this.identifiant = identifiant;
		this.mail = mail;
	}
	

	/**
	 * Fonction v�rifiant l'existence d'un Jean Kevin dans la base de donn�es
	 * @param identifiant du jean_kevin � rechercher
	 * @return vrai si trouv�, faux sinon ou en cas de probl�mes
	 */
	public static boolean existe(String identifiant){
		try {
			JSONArray params = new JSONArray(new String[]{identifiant});
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.existe, params);
			if(r.estOK()){
				return r.getCorps().getBoolean("existe");
			}
		} catch (JSONException e) {e.printStackTrace();}
		return false;
	}


	/**
	 * Fonction permettant la connection d'un Jean Kevin � l'application
	 * @param mdp le mot de passe crypt�
	 * @return un objet JK si la connexion a r�ussi, null sinon
	 */
	public static JeanKevin connexion(String identifiant, String mdp){
		try {
			JSONArray params = new JSONArray(new String[]{identifiant, mdp});
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.connecter, params);
			if(r.estOK() && r.getCorps().getBoolean("connecte") && r.getCorps().getBoolean("actif")){
				return selectionner(identifiant);
			} else {System.out.println(r);}
		} catch (JSONException e) {e.printStackTrace();}
		return null;
	}


	/**
	 * Ajoute un le Jean Kevin dans la base de donn�es et envoie un mail de confiramtion d'inscritpion
	 * � l'adresse renseign�e
	 * @param psw le mot de passe qu'il a choisi
	 * @return vrai en cas de r�ussite, faux en cas de probl�me, ou bien si l'utilisateur existe d�j�
	 */
	public boolean preinscrire(String nom, String prenom, String identifiant, String psw, String mail){
		try {
			JSONArray params = new JSONArray(new String[]{nom, prenom, identifiant, psw, mail});
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.preinscrire, params);
			if(r.estOK()){
				return (r.getCorps().getBoolean("inscriptionOK") && r.getCorps().getBoolean("mailOK"));
			} else {System.out.println(r);}
		} catch (JSONException e) {e.printStackTrace();}
		return false;
	}

	
	/**
	 * Fonction permettant la mise � jour des donn�es du Jean Kevin de la BdD vers l'objet
	 * @param identifiant le identifiant correspondant du jean_kevin
	 * @return Un objet contenant les infos de JK ou null en cas de probl�me
	 */
	public static JeanKevin selectionner(String identifiant){
		try {
			JSONArray params = new JSONArray(new String[]{identifiant});
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.selectionner, params);
			if(r.estOK()){
				return new JeanKevin(r.getCorps().getString("nom"), r.getCorps().getString("prenom"),
						r.getCorps().getString("identifiant"), r.getCorps().getString("mail"));
			} else {
				System.out.println(r);
			}
		} catch (JSONException e) {e.printStackTrace();}
		return null;
	}

	
	/**
	 * Envoie une demande en amie au jean_kevin2 pass� en param�tre
	 * @param jean_kevin2 le Jean-K�vin qu'on invite en ami
	 */
	public boolean demanderEnAmi(JeanKevin jean_kevin2){
		try {
			JSONArray params = new JSONArray(new String[]{this.identifiant, jean_kevin2.getIdentifiant()});
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Amitie, Niveau2.ajouter, params);
			if(r.estOK()){
				return r.getCorps().getBoolean("ajoutOK");
			} else {
				System.out.println(r);
			}
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	
//	public static LinkedList<JeanKevin> rechercher(){
//		LinkedList l = new LinkedList<JeanKevin>();
//		return l;
//	}
	
	/**
	 * Supprime le Jean-K�vin de la base de donn�es
	 * @return vrai si r�ussi, faux sinon
	 */
	public boolean supprimer(){
		try {
			JSONArray params = new JSONArray(new String[]{this.identifiant});
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.supprimer, params);
			if(r.estOK()){
				return r.getCorps().getBoolean("suprOK");
			} else {
				System.out.println(r);
			}
		}
		catch (JSONException e){e.printStackTrace();}
		return false;
	}

	
//	public void donnerPosition(int x, int y, int lieu){
//		try {
//			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, niv2, params)
//		} catch (SQLException e) { e.printStackTrace(); }
//	}
	
	
	/**
	 * Permet de modifier les inforamtions personnelles de Jean-K�vin
	 * @param nom le nouveau nom de JK
	 * @param prenom le nouveau pr�nom de JK
	 * @return vrai si les modifications ont �t� op�r�es, faux sinon
	 */
	public boolean modifierInformations(String nom, String prenom){
		try{
			JSONArray params = new JSONArray(new String[]{this.identifiant, nom, prenom});
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.modifier, params);
			if(!r.estOK()){
				System.out.println(r);
				return false;
			}
			return r.estOK() && r.getCorps().getBoolean("modif");
		} catch(JSONException e){e.printStackTrace();}
		return false;
	}
	
	
	/**
	 * Permet de modifier l'adresse mail d'un JK dont le compte n'est pas encore actif.
	 * Il recevra alors un nouveau mail sur la nouvelle adresse pour la confirmer
	 * @param mail la nouvelle adresse mail � enregistrer
	 * @return vrai si l'op�ration a r�ussie, faux sinon
	 */
	public boolean modifierMail(String mail){
		try{
			JSONArray params = new JSONArray(new String[]{this.identifiant, mail});
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.modifierMail, params);
			if(r.estOK()){
				return r.getCorps().getBoolean("modifMail") && r.getCorps().getBoolean("mailOK");
			} else {System.out.println(r);}
		} catch(JSONException e) {e.printStackTrace();}
		return false;
	}
	
	
	/**
	 * Permet d'ajouter un avatar � JK et de l'enregistrer comme photo de profile
	 * @param img le fichier image � enregistrer sur le serveur
	 * @return vrai si le transfert s'est bien ex�cut�, faux sinon
	 */
	public boolean ajouterPhotoProfil(File img){
		try{
			JSONArray params = new JSONArray(new String[]{this.identifiant});
			ReponseServeur r = RequeteServeur.transfererImage(img, NivImg.Avatar, params);
			if(r.estOK()){
				return r.getCorps().getBoolean("finCommuncation") && r.getCorps().getBoolean("ajoutBD");
			} else {System.out.println(r);}
		} catch (JSONException e){ e.printStackTrace();}
		return false;
	}
	
	/**
	 * Permet de d�finir une photo d�j� existante comme photo de profil de JK
	 * @param nomImage le nom de l'image
	 * @return vrai en cas de succ�s, faux sinon
	 */
	public boolean definirPhotoProfil(String nomImage){
		try{
			JSONArray params = new JSONArray(new String[]{this.identifiant, nomImage});
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.definirPhotoProfile, params);
			if(r.estOK()){
				return r.getCorps().getBoolean("modifPP");
			} else {System.out.println(r);}
		} catch (JSONException e){ e.printStackTrace();}
		return false;
	}
	
	@SuppressWarnings("unused")
	public ArrayList<File> selectionnerTousAvatars(){
		try{
			//On r�cup�re l'ensemble des noms d'avatars 
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Image, Niveau2.selectionnerNoms,
					new JSONArray(new String[]{this.identifiant}));
			if(r.estOK()){
				JSONArray noms = r.getCorps().getJSONArray("chemins");
				ArrayList<File> ret = new ArrayList<>();
				//On r�cup�re tous les avatars correspondants
				for (int i = 0; i < noms.length(); i++) {
					ReponseServeur rep = null;
					File img = RequeteServeur.recevoirImage(NivImg.Avatar, noms.getString(i),
							new JSONArray(new String[]{this.identifiant}), rep);
					if(rep != null && rep.estOK()){
						ret.add(img);
					}
				}
				return ret;
			} 
		} catch (JSONException e){e.printStackTrace();}
		return null;
	}
	

		/********************
		**     GETTERS     **
		*********************/
	

	public String getNom() {
		return this.nom;
	}
	public String getPrenom() {
		return this.prenom;
	}
	public String getIdentifiant() {
		return identifiant;
	}
	public String getMail(){
		return this.mail;
	}
	@Override
	public String toString() {
		return prenom  +" "+ nom + " login : " + identifiant;
	}
	
}
