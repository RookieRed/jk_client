package http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Arrays;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Classe permettant la communication entre l'application et les serveurs.</br>
 * Toutes les méthodes sont accessible en static, il n'existe pas 
 * @author JK
 *
 */
public abstract class RequeteServeur {
	
	//Données de connexion serveur
	private static final String cible		= "/jk/api.php";
	private static final String serveur		= "localhost";
	private static final int portServeurImg = 9997;
	private static final int tailleBfr      = 2048;
	
	//Attribus gérant la connexion
	private static URL					url			= null;
	private static HttpURLConnection 	connection	= null;
	
	//Enumérations des deux niveaux de requête
	public static enum NIVEAU1 {
		JeanKevin,
		Amitie,
	}
	public static enum NIVEAU2 {
		accepter,
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
	
	
	
	/**
	 * Fonction permettant d'envoyer une requête HTTP formatée pour le serveur
	 * @param niv1 Correspond à la classe visée
	 * @param req correspond à la méthode de classe correspondante
	 * @param params les paramètres de la méthode dans un tableau JSON
	 * @throws JSONException
	 * @throws IOException
	 */
	public static void envoyerRequete(NIVEAU1 niv1, NIVEAU2 req, JSONArray params) 
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
			return new ReponseServeur(true);
		}
		
		//Si une exception occure on retourne une réponse par défaut
		return null;
	}
	
	
	
	public static ReponseServeur transfererImage(File img, JSONArray params)
			throws IllegalArgumentException{
		
		//Vérification du fichier
		String typeFic = new MimetypesFileTypeMap().getContentType(img);
		typeFic = typeFic.split("/")[0];
		if(!typeFic.equals("image")){
			throw new IllegalArgumentException("Le fichier n'est pas une image");
		}
		
		//Traitements de la communication
		try {
			
			//Envoie de la requête pour ajouter un avatar à JK
			final ReponseServeur r = new ReponseServeur(false);
			params.put(img.getName());
			Thread actuel = Thread.currentThread();
			Thread envoie;
			synchronized (actuel) {
				
				//Lacement du thread envoie
				envoie = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							//envoie de la requête
							envoyerRequete(NIVEAU1.JeanKevin, NIVEAU2.ajouterAvatar, params);
							//On notifie le thread parent que la requête a été envoyée
							synchronized (actuel) {
								actuel.notify();
							}
							//Lecture de la réponse serveur
							BufferedReader br = new BufferedReader(new InputStreamReader(
									connection.getInputStream(), "utf-8"));
							String ligne;
							while((ligne = br.readLine())!= null){
								System.out.println(ligne);
							}
							r.getCorps().put("reponseRequete", ligne);
						} catch (JSONException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
				envoie.start();
				actuel.wait();
			}
			//Traitement de l'image
			byte[] buffer = IOUtils.toByteArray(new FileInputStream(img));
			
			//S'il n'y a pas eu de problèmes on lance la connexion avec le nouveau socket
			System.out.println("Tentative de connexion");
			Socket s = new Socket(serveur, portServeurImg);
			
			//Si la connexion a été acceptée
			if(s.isConnected()){
				//Envoie de l'image
				s.setSendBufferSize(tailleBfr);
				OutputStream os = s.getOutputStream();
				os.write(buffer);
				
				//Lecture de la réponse
				InputStream ir = s.getInputStream();
				byte b[] = new byte[tailleBfr];
				int rel = ir.read(b);
				r.getCorps().put("transfert", new String(Arrays.copyOf(b, rel)));
			}
			s.close();
			envoie.interrupt();
			return r;
		}
		catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e) { return new ReponseServeur(true); }
		
		return null;
	}
	
}

