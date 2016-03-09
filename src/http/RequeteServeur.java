package http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class RequeteServeur {
	
	private static final String CIBLE = "localhost/jk/api.php";
	private static URL url;
	private static HttpURLConnection connection = null;
	public static enum NIVEAU1 {
		TEST,
		AVATAR
	}
	
	
	public static ReponseServeur envoyerRequete(NIVEAU1 niv1, String req, JSONArray params){
		try {
			
			//Création de la reqête JSON qui sera envoyé dans POST['JSON']
			JSONObject requete = new JSONObject();
			requete.put("niv_1", niv1);
			requete.put("niv_2", req);
			requete.put("param", params);
			
			//Lancement de la connexion
			url = new URL("http://"+CIBLE);
			connection = (HttpURLConnection) url.openConnection();
			
			//Création de la requête post
			String post = "JSON="+requete.toString();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length",
					""+(CIBLE.length()+post.toString().length()));
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			
			//Envoie du POST
			OutputStream os = connection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
			writer.write(post);
			writer.flush();
			
			//Lecture de la réponse serveur
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			ArrayList<String> repServ = new ArrayList<String>();
			String ligne;
			while((ligne = bf.readLine())!= null){
				repServ.add(ligne);
			}
			
			//Création de l'objet de réponse
			ReponseServeur r = new ReponseServeur(repServ);
			return r;
			
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Si une exception occure on retourne null
		return null;
	}
	
}

