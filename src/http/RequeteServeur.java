package http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class RequeteServeur {
	
	private static final String cible		= "/jk/api.php";
	private static final String serveur		= "localhost";
	private static final int portServeurImg = 10000;
	
	private static URL url;
	private static HttpURLConnection connection = null;
	public static enum NIVEAU1 {
		JeanKevin,
		Amitie,
	}
	public static enum NIVEAU2 {
		acceper,
		ajouter,
		ajouterAvatar,
		definirPhotoProfile,
		estEffective,
		existe,
		preinscrire,
		selectionner,
		selectionnerAmis,
		supprimer,
	}
	
	
	private static void envoyerRequete(NIVEAU1 niv1, NIVEAU2 req, JSONArray params) 
			throws JSONException, IOException{

		//Création de la reqête JSON qui sera envoyé dans POST['JSON']
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
		url = new URL("http://"+serveur+cible);
		connection = (HttpURLConnection) url.openConnection();
		
		//Création de la requête post
		String post = "JSON="+requete.toString();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length",
				""+((serveur+cible).length()+post.toString().length()));
		connection.setUseCaches(false);
		connection.setDoOutput(true);
		
		//Envoie du POST
		OutputStream os = connection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charset.forName("utf-8")));
		writer.write(post);
		writer.flush();
	}
	
	public static ReponseServeur executerRequete(NIVEAU1 niv1, NIVEAU2 req, JSONArray params){
		
		try {
			
			//Envoie de la requete
			envoyerRequete(niv1, req, params); 
			
			//Lecture de la réponse serveur
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "utf-8"));
			ArrayList<String> repServ = new ArrayList<String>();
			String ligne;
			while((ligne = br.readLine())!= null){
				System.out.println(ligne);
				repServ.add(ligne);
			}
			
			//Création de l'objet de réponse
			ReponseServeur r = new ReponseServeur(repServ);
			return r;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e) {
			return new ReponseServeur();
		}
		
		//Si une exception occure on retourne une réponse par défaut
		return null;
	}
	
	public static ReponseServeur transfererImage(ImageIcon img, JSONArray params){
		
		try {
			//On lance une requete pour récupérer les infos du nouveau socket
			envoyerRequete(NIVEAU1.JeanKevin, NIVEAU2.ajouterAvatar, params);
			
			//S'il n'y a pas eu de problèmes on lance la connexion avec le nouveau socket
				System.out.println("Tentative de connexion");
				Socket s = new Socket(serveur, portServeurImg);
				System.out.println("Connexion réussie : "+s.isConnected());
				s.close();
			
		}
		catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
}

