package http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class RequeteServeur {
	
	private static final String CIBLE = "localhost/jk/api.php";
	private static URL url;
	private static HttpURLConnection connection = null;
	public static enum NIVEAU1 {
		Avatar,
		JeanKevin
	}
	public static enum NIVEAU2 {
		ajouter,
		supprimer,
		existe,
		selectionner,
		definirPhotoProfile,
		preinscrire,
	}
	
	
	private static void construireRequete(NIVEAU1 niv1, NIVEAU2 req, JSONArray params)
			throws JSONException, IOException{
		
		//Cr�ation de la req�te JSON qui sera envoy� dans POST['JSON']
		JSONObject requete = new JSONObject();
		requete.put("niv_1", niv1);
		requete.put("niv_2", req);
		if(params != null){
			requete.put("param", params);
		}
		else {
			requete.put("param", new JSONArray());
		}
		
		//Lancement de la connexion
		url = new URL("http://"+CIBLE);
		connection = (HttpURLConnection) url.openConnection();
		
		//Cr�ation de la requ�te post
		String post = "JSON="+requete.toString();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length",
				""+(CIBLE.length()+post.toString().length()));
		connection.setUseCaches(false);
		connection.setDoOutput(true);
		
		//Envoie du POST
		OutputStream os = connection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charset.forName("utf-8")));
		writer.write(post);
		writer.flush();
		
	}
	
	public static ReponseServeur envoyerRequete(NIVEAU1 niv1, NIVEAU2 req, JSONArray params){
		
		try {
			//Envoie de la requ�te
			construireRequete(niv1, req, params);
			
			//Lecture de la r�ponse serveur
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "utf-8"));
			ArrayList<String> repServ = new ArrayList<String>();
			String ligne;
			while((ligne = br.readLine())!= null){
				System.out.println(ligne);
				repServ.add(ligne);
			}
			
			//Cr�ation de l'objet de r�ponse
			ReponseServeur r = new ReponseServeur(repServ);
			return r;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e) {
			return new ReponseServeur();
		}
		
		//Si une exception occure on retourne une r�ponse par d�faut
		return null;
	}
	
	public static ReponseServeur transfererImage(ImageIcon img, JSONArray params){
		
		try {
			construireRequete(NIVEAU1.Avatar, NIVEAU2.definirPhotoProfile, params);
		}
		//Si la r�ponse du serveur n'est pas correcte on l'affiche et on retourne une Reponse erreur
		catch (JSONException e) {
			return new ReponseServeur();
		}
		//Autres exceptions
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
}

