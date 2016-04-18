package repo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.NivImg;
import http.RequeteServeur.Niveau1;
import http.RequeteServeur.Niveau2;

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
	
	
	/*----------------------
	-- METHODES DE CLASSE --
	-----------------------*/
	
	/**
	 * Retourne un objet JeanKevin � partir d'un objet JSON
	 * @param obj l'objet au format JSON
	 * @return le JeanK�vin correspondant ou bine null en cas de probl�me
	 */
	protected static JeanKevin parseJSON(JSONObject obj) {
		try {
			return new JeanKevin(obj.getString("nom"), obj.getString("prenom"),
					obj.getString("identifiant"), obj.getString("mail"));
		} catch (JSONException e) {e.printStackTrace();}
		return null;
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
			if(r != null && r.estOK()){
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
			System.out.println(r.estOK() +" ");
			if(r != null && r.estOK() && r.getCorps().getBoolean("connecte") && r.getCorps().getBoolean("actif")){
				JSONObject jk = r.getCorps().getJSONObject("jk");
				return  new JeanKevin(jk.getString("nom"), jk.getString("prenom"),
						jk.getString("identifiant"), null);
			} 
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
			if(r != null && r.estOK()){
				return (r.getCorps().getBoolean("inscriptionOK") && r.getCorps().getBoolean("mailOK"));
			} 
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
			if(r != null && r.estOK()){
				return parseJSON(r.getCorps().getJSONObject("jk"));
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
			if(r != null && r.estOK()){
				return r.getCorps().getBoolean("ajoutOK");
			}
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	
	
	/**
	 * Effectue une recherche parmi les JK enregistr�s dans la base de donn�es
	 * @param motCles La chaine de carract�re de recherche
	 * @return un HashSet contenant tous les r�sultats, ou null en cas de probl�me
	 */
	public static HashSet<JeanKevin> rechercher(String motsCles){
		try{
			//On parse les mots cl�s en les s�parant � chaque espace
			String[] mots = motsCles.split(" ");
			HashSet<JeanKevin> jks = new HashSet<JeanKevin>();
			//Pour chaque mot cl� r�cup�r� on fait une recherche
			for(String mot : mots){
				ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.rechercher,
						new JSONArray(new String[]{mot}));
				if(r != null && r.estOK()){
					//On r�cup�re un � un les r�sultats et on les ajoute dans la liste s'ils n'y sont pas
					JSONArray resultats = r.getCorps().getJSONArray("resultats");
					for(int i=0; i<resultats.length(); i++){
						JSONObject res = resultats.getJSONObject(i);
						jks.add(new JeanKevin(res.getString("nom"), res.getString("prenom"),
								res.getString("identifiant"), res.getString("mail")));
					}
				}
				else { break; }
			}
			return jks;
		} catch (JSONException e){e.printStackTrace();}
		
		return null;
	}
	
	/**
	 * Supprime le Jean-K�vin de la base de donn�es
	 * @return vrai si r�ussi, faux sinon
	 */
	public boolean supprimer(){
		try {
			JSONArray params = new JSONArray(new String[]{this.identifiant});
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.supprimer, params);
			if(r != null && r.estOK()){
				return r.getCorps().getBoolean("suprOK");
			}
		}
		catch (JSONException e){e.printStackTrace();}
		return false;
	}
	
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
			if(r!= null && !r.estOK()){
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
			if(r != null && r.estOK()){
				return r.getCorps().getBoolean("modifMail") && r.getCorps().getBoolean("mailOK");
			} 
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
			if(r!=null && r.estOK()){
				return r.getCorps().getBoolean("finCommuncation") && r.getCorps().getBoolean("ajoutBD");
			}
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
			if(r != null && r.estOK()){
				return r.getCorps().getBoolean("modifPP");
			} 
		} catch (JSONException e){ e.printStackTrace();}
		return false;
	}
	
	public File selectionnerAvatar(){
		
		return null;
	}
	
	/**
	 * Selectionne tous les avatars du Jean K�vin enregistr�s sur le serveur
	 * et les retourne dans une ArrayList
	 * @return un ArrayList contenant tous les avatars enregistr�s ou null en cas de probl�me
	 */
	public ArrayList<File> selectionnerTousAvatars(){
		try{
			//On r�cup�re l'ensemble des noms d'avatars 
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Image, Niveau2.selectionnerNoms,
					new JSONArray(new String[]{this.identifiant}));
			if(r != null && r.estOK()){
				JSONArray noms = r.getCorps().getJSONArray("chemins");
				ArrayList<File> ret = new ArrayList<File>();
				//On r�cup�re tous les avatars correspondants
				for (int i = 0; i < noms.length(); i++) {
					File img = new File(noms.getString(i));
					ReponseServeur rep = RequeteServeur.recevoirImage(img, new JSONArray(new String[]{
							this.identifiant, noms.getString(i)}));
					if(rep != null && rep.estOK()){
						ret.add(img);
					}
				}
				return ret;
			} 
		} catch (JSONException e){e.printStackTrace();}
		return null;
	}
	
	/**
	 * Inscrit Jean-K�vin � un lieu enregistr�
	 * @param lieu le lieu auquel JK souhaite s'inscrire
	 * @return vrai en cas de succ�s faux sinon
	 */
	public boolean ajouterLieuJK(Lieu lieu){
		
		try{
			JSONArray params = new JSONArray();
			params.put(lieu.getId());
			params.put(this.identifiant);
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Lieu, Niveau2.ajouterLieuJK, params);
			if(r != null && r.estOK()){
				return r.getCorps().getBoolean("ajoutLienOK");
			}
		}
		catch(JSONException e){e.printStackTrace();}
		
		return false;
	}
	
	
	/**
	 * Selectionne l'ensemble des lieux auxquels JK est inscrit
	 * @return une ArrayList de Lieux ou null en cas de probl�mes
	 */
	public ArrayList<Lieu> selectionnerLieuxJK(){
		
		try{
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Lieu, Niveau2.selectionnerLieuxJK, 
					new JSONArray(new String[]{this.identifiant}));
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
	 * D�sincrit Jean-K�vin d'un lieu auquel il �tait pr�c�demment inscrit
	 * @param lieu le lieu dont JK veut se d�sinscrire
	 * @return vrai en cas de succ�s faux sinon
	 */
	public boolean supprimerLieuJK(Lieu lieu){
		try{
			JSONArray params = new JSONArray();
			params.put(lieu.getId());
			params.put(this.identifiant);
			ReponseServeur r = RequeteServeur.executerRequete(Niveau1.Lieu, Niveau2.supprimerLieuJK, params);
			if(r != null && r.estOK()){
				return r.getCorps().getBoolean("suppressionOK");
			}
		}
		catch(JSONException e){e.printStackTrace();}
		return false;
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


		/***************************
		**    EQUALS & HASHCODE   **
		****************************/
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifiant == null) ? 0 : identifiant.hashCode());
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
		JeanKevin other = (JeanKevin) obj;
		if (identifiant == null) {
			if (other.identifiant != null)
				return false;
		} else if (!identifiant.equals(other.identifiant))
			return false;
		return true;
	}
	
	
}
