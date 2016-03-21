<?php

/*
	                                                                                             		      
	        "8a           88888888ba                                                             		      
	          "8a         88      "8b                             ,d                             		      
	8888888888  "8a       88      ,8P                             88                                          
	              "8a     88aaaaaa8P'  ,adPPYba,   88       88  MM88MMM  ,adPPYba,  88       88  8b,dPPYba,   
	8888888888    a8"     88""""88'   a8"     "8a  88       88    88    a8P_____88  88       88  88P'   "Y8   
	            a8"       88    `8b   8b       d8  88       88    88    8PP"""""""  88       88  88           
	          a8"         88     `8b  "8a,   ,a8"  "8a,   ,a88    88,   "8b,   ,aa  "8a,   ,a88  88           
	        a8"           88      `8b  `"YbbdP"'    `"YbbdP'Y8    "Y888  `"Ybbd8"'   `"YbbdP'Y8  88           
	                                                                                
*/



/**===========================================================================
*						ATTRIBUS DU ROUTEUR	                                ==
*-----------------------------------------------------------------------------
* ==> Classe basée sur le design pattern singleton                          ==
* Gère les URL et les requêtes addressées au site 							==
*									                                        ==
* -> @jean_kevin le jean_kevin confirmant son URL   			            ==
* -> @code le code de confirmation d'inscription                            ==
*===========================================================================*/
class Route {

	private $jean_kevin;
	private $code;
	private static $instance = null;


		/*********************
		***  CONSTRUCTEUR  ***
		**********************/
	private function __construct(){

		//Si on est en localhost il faut veiller à ne pas prendre le 'GESCABMED' de trop au début de l'url
		//if ($_SERVER['HTTP_HOST'] == 'localhost') {
			$index = 1;
		//}
		//else{
		//	$index = 0;
		//}
		//On récupère dans un tableau toutes les données passées dans l'URL 
		$url = explode('/', substr($_SERVER['REQUEST_URI'], 1));

		//Si les deux premiers paramètres de l'url ne sont pas null on parse
		if(isset($url[$index]) && $url[$index] != '' && isset($url[$index+1]) && $url[$index+1] != ''){
			$this->jean_kevin = $url[$index];
			//On récupère le code...
			$index++;
			$this->code = $url[$index];
		}

		//S'il ne s'agit pas d'un code de confirmation on redirrige sur index
		else {
			$this->jean_kevin = null;
			$this->code = null;
		}
	}


		/*********************
		*** GETTTERS & CIE ***
		**********************/
	public static function getInstance() {
		if (Route::$instance == null) {
			Route::$instance = new Route();
		}
		return Route::$instance;
	}


	public function estRequeteConfirmation(){
		return ($this->jean_kevin != null && $this->code != null && strlen($this->code)==32);
	}

	public function getJK(){
		return $this->jean_kevin;
	}


	public function getCode(){
		return $this->code;
	}

}
		

?>

