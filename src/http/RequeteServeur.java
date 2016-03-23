package http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import javax.activation.MimetypesFileTypeMap;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Classe permettant la communication entre l'application et les serveurs.</br>
 * Toutes les méthodes sont accessible en static, il n'existe pas d'instance de la classe
 * @author JK
 *
 */
public abstract class RequeteServeur {
	
	//Données de connexion serveur
	private static final String cible		= "/api.php";
	private static final String serveur		= "jean-kevin.com";
	private static final int portServeurImg = 9997;
	private static final int tailleBfr      = 2048;
	
	//Attribus gérant la connexion
	private static URL					url			= null;
	private static HttpURLConnection 	connection	= null;
	
	//Enumérations des deux niveaux de requête
	public static enum Niveau1 {
		JeanKevin,
		Amitie,
		Image
	}
	public static enum Niveau2 {
		accepter,
		ajouter,
		connecter,
		definirPhotoProfile,
		estEffective,
		existe,
		modifier,
		modifierMail,
		modifierMotDePasse,
		preinscrire,
		rechercher,
		selectionner,
		selectionnerAmis,
		selectionnerNoms,
		supprimer,
	}
	public static enum NivImg {
		Avatar,
		Carte,
		Image
	}
	
	
	/**
	 * Instancie la connexion au serveur
	 */
	public static void lancerConnexion(){
		if(url == null){
			try {
				url = new URL("http://"+serveur+cible);
				connection = (HttpURLConnection) url.openConnection();
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	/**
	 * Ferme la connexion au serveur.
	 * A n'utiliser qu'à la fin de la session pour éviter de réouvrir une session (gain de temps)
	 */
	public static void fermerConnexion(){
		if (connection != null) connection.disconnect();
		url = null;
	}
	
	/**
	 * Permet d'envoyer une requête HTTP formatée pour le serveur
	 * @param requete la requete sous format objet JSON à envoyer au serveur
	 * @throws JSONException
	 * @throws IOException
	 */
	private static void envoyerRequete(JSONObject requete) 
			throws JSONException, IOException{
		
		//Lancement de la connexion
		fermerConnexion();
		if(url == null) lancerConnexion();
		
		//Création de la requête post
		String post = "JSON="+requete.toString();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length",
				""+((serveur+cible).length()+post.toString().length()));
		connection.setUseCaches(true);
		connection.setDoOutput(true);
		
		//Envoie du POST
		OutputStream os = connection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charset.forName("utf-8")));
		writer.write(post);
		writer.flush();
	}
	
	/**
	 * Lit la réponse du serveur et l'enregistre dans une liste de string
	 * @return l'ArrayList contenant toutes les lignes renvoyée par le serveur
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
				System.out.println(ligne);
			}
			return repServ;
		}
		return null;
	}
	
	
	
	/**
	 * Permet d'envoyer une requête au serveur qui l'exécutera et retournera une réponse
	 * @param niv1 correspond à la classe dont on veut appeler la méthode (niveau 2)
	 * @param req correspond au nom de la méthode à appeler
	 * @param params correspon aux paramètres de cette fonction
	 * @return la réponse du serveur ou <b>null</b> en cas de problèmes
	 */
	public static ReponseServeur executerRequete(Niveau1 niv1, Niveau2 niv2, JSONArray params){
		
		try {
			//On vérifie qu'on ne selectionne pas une image
			if(niv1 == Niveau1.Image && niv2 == Niveau2.selectionner){
				throw new IllegalArgumentException("Pour sélectionner une image utilisez la méthode recevoirImage");
			}
			
			//Création de la reqête JSON qui sera envoyé dans POST['JSON']
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
			
			//Création de l'objet de réponse
			ReponseServeur r = new ReponseServeur(lireReponse());
			return r;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			return new ReponseServeur(true);
		}
		
		//Si une exception occure on retourne une réponse par défaut
		return null;
	}
	
	
	
	
	/**
	 * Permet de transférer une image au serveur. Cette image peut être un nouvel avatar
	 * de Jean-Kévin ou bien une nouvelle carte pour un lieu
	 * @param img L'handler vers le fichier image à transférer
	 * @param niv peut prendre la valeur "Carte" (ajoute une carte à un lieu) ou "Avatar" (ajoute un avatar à JK)
	 * @param params les paramètres correspondant à la fonction serveur appelée
	 * @return la réponse du serveur ou bien null en cas d'erreur
	 * @throws IllegalArgumentException si le fichier en paramètre n'est pas une image
	 */
	public static ReponseServeur transfererImage(File img, NivImg niv, JSONArray params)
			throws IllegalArgumentException{
		
		//Vérification du fichier
		String typeFic = new MimetypesFileTypeMap().getContentType(img);
		typeFic = typeFic.split("/")[0];
		if(!typeFic.equals("image")){
			throw new IllegalArgumentException("Le fichier n'est pas une image");
		}
		
		//Traitements de la communication
		try {

			//Création de la reqête JSON qui sera envoyé dans POST['JSON']
			JSONObject requete = new JSONObject();
			requete.put("niv_1", "Image");
			requete.put("niv_2", "ajouter"+niv);
			if(params != null){
				requete.put("param", params);
			}
			else {
				requete.put("param", new JSONArray());
			}
			
			//Envoie de la requête pour ajouter un avatar à JK
			final ReponseServeur r = new ReponseServeur(false);
			params.put(img.getName());
			Thread principal = Thread.currentThread();
			Thread envoie;
			
			//envoie de la requête
			envoyerRequete(requete);
			
			synchronized (principal) {
				//Lacement du thread pour lire la réponse
				envoie = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							//Lecture de la réponse serveur
							ReponseServeur rep = new ReponseServeur(lireReponse());
							r.getCorps().put("reponse", rep.getCorps());
							r.setException(!rep.estOK());
						}
						catch (IOException e) { e.printStackTrace(); }
						catch (JSONException e){
							r.setException(true);
							try { r.getCorps().put("erreur", "La réponse n'est pas au format JSON"); }
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
				
				//Lancement de la connexion avec un nouveau socket
				Socket s = new Socket(serveur, portServeurImg);
				
				//Si la connexion a été acceptée on envoie l'image
				if(s.isConnected()){
					//Envoie de l'image
					s.setSendBufferSize(tailleBfr);
					OutputStream os = s.getOutputStream();
					os.write(buffer);
				}
				else{
					s.close();
					r.setException(true);
					r.getCorps().put("erreur", "Connection refusée");
					return r;
				}
				
				//On ferme le socket on attend la synchronisation avec le thread d'envoie et on retourne la réponse
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
	 * Permet de recevoir une image stockée sur le serveur tel un avatar de JK ou une carte d'un lieu
	 * @param niv peut prendre la valeur "Carte" (reçoit la carte d'un lieu) ou "Avatar" (reçoit l'vatar d'un JK)
	 * @param params les paramètres de la méthode à appeler. Peut être null
	 * @param img l'handler qui donnera accès à l'image reçue
	 * @param r l'objet réponse serveur qui sera renvoyé
	 * @return un objet RéponseServeur ou null en cas d'erreur
	 */
	public static File recevoirImage(NivImg niv, String path, JSONArray params, ReponseServeur r){
		
		try {
			final ReponseServeur rep = new ReponseServeur(false);
			File img = new File(path);
			
			//Envoie de la requete
			JSONObject req = new JSONObject();
			req.put("niv_1", "Image");
			req.put("niv_2", "selectionner"+((niv==NivImg.Avatar)?niv:""));
			req.put("param", (params==null)?new JSONArray():params);
			envoyerRequete(req);
			
			Thread com = Thread.currentThread();
			//Thread pour lire la réponse HTTP du serveur
			synchronized(com){
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							rep.getCorps().put("reponse", new ReponseServeur(lireReponse()).getCorps());
							synchronized (com) {
								com.notify();
							}
						} catch (IOException | JSONException e) {e.printStackTrace();}
					}
				}).start();
				
				//Lancement de la connexion
				try {
					Socket s = new Socket(serveur, portServeurImg);
					if(s.isConnected()){
						
						//Reception de l'image
						InputStream is = s.getInputStream();
						byte[] b = new byte[tailleBfr];
						FileOutputStream fos = new FileOutputStream(img);
						while(is.read(b) != -1){
							fos.write(b);
						}
						fos.close();
					}
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				com.wait();
				r = rep;
				return img;
			}
			
		} catch (JSONException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}

