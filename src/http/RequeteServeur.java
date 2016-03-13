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
 * Toutes les m�thodes sont accessible en static, il n'existe pas d'instance de la classe
 * @author JK
 *
 */
public abstract class RequeteServeur {
	
	//Donn�es de connexion serveur
	private static final String cible		= "/jk/api.php";
	private static final String serveur		= "localhost";
	private static final int portServeurImg = 9997;
	private static final int tailleBfr      = 2048;
	
	//Attribus g�rant la connexion
	private static URL					url			= null;
	private static HttpURLConnection 	connection	= null;
	
	//Enum�rations des deux niveaux de requ�te
	public static enum Niveau1 {
		JeanKevin,
		Amitie,
	}
	public static enum Niveau2 {
		accepter,
		ajouter,
		definirPhotoProfile,
		estEffective,
		existe,
		preinscrire,
		selectionner,
		selectionnerAmis,
		supprimer,
	}
	public static enum NivImg {
		ajouterAvatar,
		ajouterCarte
	}
	
	
	
	/**
	 * Fonction permettant d'envoyer une requ�te HTTP format�e pour le serveur
	 * @param requete la requete sous format objet JSON � envoyer au serveur
	 * @throws JSONException
	 * @throws IOException
	 */
	private static void envoyerRequete(JSONObject requete) 
			throws JSONException, IOException{
		
		//Lancement de la connexion
		url = new URL("http://"+serveur+cible);
		connection = (HttpURLConnection) url.openConnection();
		
		//Cr�ation de la requ�te post
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
	
	
	
	
	/**
	 * Permet d'envoyer une requ�te au serveur qui l'ex�cutera et retournera une r�ponse
	 * @param niv1 correspond � la classe serveur d'o� sera appel� le niveau 2
	 * @param req correspond au no de la m�thode � appeler
	 * @param params correspon aux param�tres de cette fonction
	 * @return la r�ponse du serveur ou null en cas de probl�mes
	 */
	public static ReponseServeur executerRequete(Niveau1 niv1, Niveau2 niv2, JSONArray params){
		
		try {

			//Cr�ation de la req�te JSON qui sera envoy� dans POST['JSON']
			JSONObject requete = new JSONObject();
			requete.put("niv_1", niv1);
			requete.put("niv_2", niv2);
			if(params != null){
				requete.put("param", params);
			}
			else {
				requete.put("param", new JSONArray());
			}
			
			//Envoie de la requete
			envoyerRequete(requete); 
			
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
			return new ReponseServeur(true);
		}
		
		//Si une exception occure on retourne une r�ponse par d�faut
		return null;
	}
	
	
	
	
	/**
	 * Permet de transf�rer une image au serveur. Cette image peut �tre un nouvel avatar
	 * de Jean-K�vin ou bien une nouvelle carte pour un lieu
	 * @param img L'handler vers le ficier image � transf�rer
	 * @param niv le niveau 2 de la requ�te
	 * @param params les param�tres correspondant � la fonction serveur appel�e
	 * @return la r�ponse du serveur ou bien null en cas d'erreur
	 * @throws IllegalArgumentException si le fichier en param�tre n'est aps une image
	 */
	public static ReponseServeur transfererImage(File img, NivImg niv, JSONArray params)
			throws IllegalArgumentException{
		
		//V�rification du fichier
		String typeFic = new MimetypesFileTypeMap().getContentType(img);
		typeFic = typeFic.split("/")[0];
		if(!typeFic.equals("image")){
			throw new IllegalArgumentException("Le fichier n'est pas une image");
		}
		
		//Traitements de la communication
		try {

			//Cr�ation de la req�te JSON qui sera envoy� dans POST['JSON']
			JSONObject requete = new JSONObject();
			requete.put("niv_1", "Images");
			requete.put("niv_2", niv);
			if(params != null){
				requete.put("param", params);
			}
			else {
				requete.put("param", new JSONArray());
			}
			
			//Envoie de la requ�te pour ajouter un avatar � JK
			final ReponseServeur r = new ReponseServeur(false);
			params.put(img.getName());
			Thread principal = Thread.currentThread();
			Thread envoie;
			
			//envoie de la requ�te
			envoyerRequete(requete);
			
			synchronized (principal) {
				//Lacement du thread pour lire la r�ponse
				envoie = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							//Lecture de la r�ponse serveur
							BufferedReader br = new BufferedReader(
									new InputStreamReader(connection.getInputStream(), "utf-8"));
							String ligne;
							while ((ligne = br.readLine()) != null) {
								System.out.println("- " + ligne);
							}
							r.getCorps().put("reponseRequete", ligne);
							synchronized (principal) {
								principal.notify();
							}
						} catch (JSONException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			
				envoie.start();
					
				//Traitement de l'image
				byte[] buffer = IOUtils.toByteArray(new FileInputStream(img));
				
				//S'il n'y a pas eu de probl�mes on lance la connexion avec le nouveau socket
				System.out.println("Tentative de connexion");
				Socket s = new Socket(serveur, portServeurImg);
				
				//Si la connexion a �t� accept�e
				if(s.isConnected()){
					//Envoie de l'image
					System.out.println("Envoie de l'image");
					s.setSendBufferSize(tailleBfr);
					OutputStream os = s.getOutputStream();
					os.write(buffer);
					
	//				//Lecture de la r�ponse
	//				System.out.println("Image envoy�, lecture de la r�ponse");
	//				InputStream is = s.getInputStream();
	//				byte b[] = new byte[tailleBfr];
	//				int rel = is.read(b);
	//				System.out.println("Reception : "+new String(Arrays.copyOf(b, rel)));
	//				r.getCorps().put("transfert", ( (new String(Arrays.copyOf(b, rel))).equals("1"))?true:false);
				}
				System.out.println("Fermeture socket");
				s.close();
				principal.wait();
				System.out.println("Arret du thread");
				envoie.interrupt();
				return r;
			}
		}
		catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e) { return new ReponseServeur(true); }
		
		return null;
	}
	
}

