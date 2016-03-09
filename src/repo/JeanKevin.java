package repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

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

	public JeanKevin(){
		this.nom = "";
		this.prenom = "";
		this.identifiant = "";
		this.mail = "";
	}
	
	public JeanKevin(String nom, String prenom, String identifiant, String mail) {
		this.nom = nom;
		this.prenom = prenom;
		this.identifiant = identifiant;
		this.mail = mail;
	}

	public JeanKevin(String nom, String prenom, String identifiant) {
		this.nom = nom;
		this.prenom = prenom;
		this.identifiant = identifiant;
		this.mail = "";
	}

	public JeanKevin(String identifiant){
		this();
		if (this.existe()) {
			this.select(identifiant);
		} else {
			this.identifiant = identifiant;
		}
	}


	/**
	 * Fonction vérifiant l'existence d'un Jean Kevin dans la base de données
	 * @param identifiant du jean_kevin à rechercher
	 * @return vrai si trouvé, faux sinon
	 */
	public static boolean existe(String identifiant){
		Statement s = BdD.getStatement();
		try {
			ResultSet r = s.executeQuery("SELECT * FROM jean_kevin WHERE identifiant = "+identifiant+" ;");
			if (r.next())
				return true;
			r.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Vérifie l'existance du Jean Kevin dans la BdD
	 * @return vrai si existe, faux sinon
	 */
	public boolean existe(){
		Statement s = BdD.getStatement();
		try {
			ResultSet r = s.executeQuery("SELECT * FROM jean_kevin WHERE identifiant = '"+this.identifiant+"' ;");
			if (r.next()){
				return true;
			}
			r.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Fonction permettant la connection d'un Jean Kevin à l'application
	 * @param mdp le mot de passe crypté
	 * @return vrai si le identifiant/mot de passe marche, faux sinon
	 */
	public boolean connexion(String mdp){
		Statement s = BdD.getStatement();
		if(!this.isEmpty()){
			try {
				ResultSet r = s.executeQuery("SELECT * FROM jean_kevin WHERE identifiant = '"+this.identifiant+"' AND psw = '"+mdp+"';");
				r.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	/**
	 * Ajoute un le Jean Kevin dans la base de données s'il n'existe pas déjà
	 * @param psw le mot de passe qu'il a choisi
	 * @return vrai en cas de réussite, faux en cas de problème, ou bine si l'utilisateur existe déjà
	 */
	public boolean ajouter(String psw){
		if (this.existe()){
			return false;
		}
		Statement s = BdD.getStatement();
		try {
			int res = s.executeUpdate("INSERT INTO jean_kevin(nom, prenom, identifiant, mot_de_passe, mail)" +
					" VALUES ('" + this.nom + "', '" + this.prenom + "', '" + this.identifiant + "', '"
					+ psw + "', '"+this.mail+"');");
			if(res == 1){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	 * Fonction permettant la mise à jour des données du Jean Kevin de la BdD vers l'objet
	 * @param identifiant le identifiant correspondant du jean_kevin
	 */
	public static JeanKevin select(String identifiant){
		Statement s = BdD.getStatement();
		JeanKevin jk = new JeanKevin();
		try {
			ResultSet r = s.executeQuery("SELECT * FROM jean_kevin WHERE identifiant = '" + identifiant + "';");
			if(r.next()){
				jk.mail = r.getString("mail");
				jk.nom = r.getString("nom");
				jk.prenom = r.getString("prenom");
				jk.identifiant = identifiant;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jk;
	}

	/**
	 * Envoie une demande en amie au jean_kevin2 passé en paramètre
	 * @param jean_kevin2 le login du 2e Jean Kevin
	 */
	public void demanderEnAmi(String jean_kevin2){
		Statement s = BdD.getStatement();
		try {
			//boolean rep = s.execute
			s.execute("INSERT INTO r_lier(identifiant1, identifiant2, effectif)" +
					" VALUES('" + this.identifiant + "', '" + jean_kevin2 + "', 0 );");
			/*if (!rep){
				throw new RuntimeException();
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static LinkedList<JeanKevin> selectAll(){
		LinkedList l = new LinkedList<JeanKevin>();
		return l;
	}
	
	/**
	 * Supprime le Jean-Kévin de la base de données
	 * @return vrai si réussi, faux sinon
	 */
	public boolean supprimer(){
		Statement s = BdD.getStatement();
		try {
			boolean rep = s.execute("DELETE FROM jean_kevin WHERE identifiant='"+this.identifiant+"';");
			return rep;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Supprime le Jean-Kévin dont le identifiant est en paramètre de la base de données 
	 * @param identifiant du jean_kevin à supprimer
	 * @return
	 */
	public static boolean supprimer(String identifiant){
		Statement s = BdD.getStatement();
		try {
			boolean rep = s.execute("DELETE FROM jean_kevin WHERE identifiant='"+identifiant+"';");
			return rep;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public void donnerPosition(int x, int y, int lieu){
		Statement s = BdD.getStatement();
		boolean rep;
		try {
			rep = s.execute("INSERT INTO position (x, y, identifiant_jk, id_lieu, jour)"
					+ "VALUES ("+x+", "+y+", '"+this.identifiant+"', "+x+", "+x+")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public String toString() {
		return "JeanKevin [nom=" + nom + ", prenom=" + prenom + ", identifiant=" + identifiant + "]";
	}

		/**********************
		** GETTERS & SETTERS **
		** *******************/
	

	private boolean isEmpty(){
		return this.nom == "" && this.prenom == "" && this.identifiant == "";
	}
	public String getNom() {
		return this.nom;
	}
	public String getPrenom() {
		return this.prenom;
	}
	public String getidentifiant() {
		return identifiant;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}
	public String getIdentifiant() {
		return identifiant;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * Récupère l'adresse mail enregistrée dans la BdD pour le jean_kevin objet
	 * @return l'adresse mail ou une chaine vide si problème
	 */
	public String getMail(){
		String mail = "";
		Statement s = BdD.getStatement();
		try {
			ResultSet r = s.executeQuery("SELECT mail FROM jean_kevin WHERE identifiant = '" + this.identifiant + "';");
			if(r.next()){
				mail = r.getString("mail");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mail;
	}
	
}
