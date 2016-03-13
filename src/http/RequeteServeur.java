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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
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
		Avatar,
		Carte,
	}
	
	
	
	/**
	 * Permet d'envoyer une requ�te HTTP format�e pour le serveur
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
	 * Lit la r�ponse du serveur et l'enregistre dans une liste de string
	 * @return l'ArrayList contenant toutes les lignes renvoy�e par le serveur
	 * @throws IOException
	 */
	private static ArrayList<String> lireReponse() throws IOException {
		
		if(connection != null){
			BufferedReader br = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "utf-8"));
			ArrayList<String> repServ = new ArrayList<String>();
			String ligne;
			while((ligne = br.readLine())!= null){
				repServ.add(ligne);
			}
			return repServ;
		}
		return null;
	}
	
	
	
	
	/**
	 * Permet d'envoyer une requ�te au serveur qui l'ex�cutera et retournera une r�ponse
	 * @param niv1 correspond � la classe dont on veut appeler la m�thode (niveau 2)
	 * @param req correspond au nom de la m�thode � appeler
	 * @param params correspon aux param�tres de cette fonction
	 * @return la r�ponse du serveur ou <b>null</b> en cas de probl�mes
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
			
			//Cr�ation de l'objet de r�ponse
			ReponseServeur r = new ReponseServeur(lireReponse());
			return r;
			
		} catch (IOException e) {
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
	 * @param img L'handler vers le fichier image � transf�rer
	 * @param niv peut prendre la valeur "Carte" (ajoute une carte � un lieu) ou "Avatar" (ajoute un avatar � JK)
	 * @param params les param�tres correspondant � la fonction serveur appel�e
	 * @return la r�ponse du serveur ou bien null en cas d'erreur
	 * @throws IllegalArgumentException si le fichier en param�tre n'est pas une image
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
			requete.put("niv_2", "ajouter"+niv);
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
							ReponseServeur rep = new ReponseServeur(lireReponse());
							r.getCorps().put("reponse", rep.getCorps());
							r.setException(!rep.estOK());
						}
						catch (IOException e) { e.printStackTrace(); }
						catch (JSONException e){
							r.setException(true);
							try { r.getCorps().put("erreur", "La r�ponse n'est pas au format JSON"); }
							catch (JSONException e1) {}
						} finally{
							synchronized (principal) {
								principal.notify();
							}
						}
					}
				});
				envoie.start();
					
				//Traitement de l'image
				byte[] buffer = IOUtils.toByteArray(new FileInputStream(img));
				
				//S'il n'y a pas eu de probl�mes on lance la connexion avec le nouveau socket
				Socket s = new Socket(serveur, portServeurImg);
				
				//Si la connexion a �t� accept�e on envoie l'image
				if(s.isConnected()){
					//Envoie de l'image
					s.setSendBufferSize(tailleBfr);
					OutputStream os = s.getOutputStream();
					os.write(buffer);
				}
				else{
					s.close();
					r.setException(true);
					r.getCorps().put("erreur", "Connection refus�e");
					return r;
				}
				
				//On ferme le socket on attend la synchronisation avec le thread d'envoie et on retourne la r�ponse
				s.close();
				principal.wait();
				return r;
			}
		}
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		catch (JSONException e) { return new ReponseServeur(true); }
		
		return null;
	}
	
	/**
	 * Permet de recevoir une image stock�e sur le serveur tel un avatar de JK ou une carte d'un lieu
	 * @param niv peut prendre la valeur "Carte" (re�oit la carte d'un lieu) ou "Avatar" (re�oit l'vatar d'un JK)
	 * @param params les param�tres de la m�thode � appeler
	 * @param img l'handler qui donnera acc�s � l'image re�ue
	 * @return un objet R�ponseServeur ou null en cas d'erreur
	 */
	public static ReponseServeur recevoirImage(File img, NivImg niv, JSONArray params){

		try {
			
			//Envoie de la requete
			JSONObject req = new JSONObject();
			req.put("niv_1", "Images");
			req.put("niv_2", "selectionner"+niv);
			req.put("param", (params==null)?new JSONArray():params);
			envoyerRequete(req);
			
			//Lecture de la r�ponse
			ReponseServeur r = new ReponseServeur(lireReponse());
			
			//Lancement de la connexion
			Socket s = new Socket(serveur, portServeurImg);
			if(s.isConnected()){
				//On efface tout
				FileUtils.writeByteArrayToFile(img, null, false);
				
				//Reception de l'image
				InputStream is = s.getInputStream();
				byte[] b = new byte[tailleBfr];
				int longueur = 0;
				while((longueur = is.read(b)) != -1){
					FileUtils.writeByteArrayToFile(img, Arrays.copyOf(b, longueur), true);
				}
			}
			s.close();
			return r;
			
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}

