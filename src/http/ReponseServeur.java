package http;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author RookieRed
 *
 */
public class ReponseServeur {
	
	private boolean exception;
	private JSONObject corps;
	
	protected ReponseServeur(ArrayList<String> reponse) throws JSONException{
		String json = "";
		for (String ligne : reponse){
			json = json+ligne+"\n";
		}
		this.corps = new JSONObject(json);
		this.exception = this.corps.getBoolean("exception");
		this.corps.remove("exception");
	}
	
	protected ReponseServeur() {
		this.exception = true;
		this.corps = new JSONObject();
		try {
			this.corps.put("error", "Le serveur a rencontré un problème");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean estOK() {
		return (this.exception)?false:true;
	}

	public JSONObject getCorps() {
		return this.corps;
	}

	@Override
	public String toString() {
		return ((this.exception)?"[!] Exception levée : ":"")+corps.toString();
	}
	
	
}
