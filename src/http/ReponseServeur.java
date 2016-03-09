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
		System.out.println(json);
		this.corps = new JSONObject(json);
		this.exception = this.corps.getBoolean("exception");
		this.corps.remove("exception");
	}
	
	
	public boolean estOK() {
		return (this.exception)?false:true;
	}

	public JSONObject getCorps() {
		return this.corps;
	}
	
	
}
