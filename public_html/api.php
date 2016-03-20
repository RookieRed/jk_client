<?php 



		/*********************************************
		****	GESTION GLOBALE PHP DU SITE		*****
		*********************************************/

//Variables globales d'accès au fichiers
define('ROOT', str_replace('api.php', '', $_SERVER['SCRIPT_FILENAME']));
define('WEBROOT', str_replace('api.php', '', $_SERVER['SCRIPT_NAME']));
define('CORE', "core/");
define('AVATAR', "img/avatars/");
define('CARTE', "img/cartes/");
define('MNGR', "core/managers/");
define('VIEW', "web/views/");

require "core/autoloader.php";
header('Content-Type: text/html; charset=utf-8');

session_start();

//Desactive les erreurs
//error_reporting(0);

if(isset($_POST['JSON'])){

	//Decodage de la requête JSON => objet
	$request = json_decode($_POST['JSON']);
	$answer = new stdClass();

	if(isset($request->niv_1)){
		
		//On teste d'abord si le niveau 1 existe
		if(class_exists($request->niv_1)){

			//On teste ensuite si le niveau 2 est un nom de méthode valide
			if(isset($request->niv_2) && method_exists(new $request->niv_1(), $request->niv_2)){
				$answer = call_user_func_array("$request->niv_1::".$request->niv_2, $request->param);
				$answer->exception = false;
			}

			//S'il n'y a pas de niveau 2
			else {
				$answer->exception = true;
				$answer->error = "Niveau 2 non reconnu";
			}
		}

		//s'il n'y a pas de rang 1
		else{
			$answer->exception = true;
			$answer->error = "Niveau 1 non reconnu";
		}
	}
	
	//affichage de la réponse
	echo json_encode($answer);

}

//Si le client n'est pas l'application
else {
	//include 'index.php';
	echo "<pre>";
	print_r(Image::selectionnerNoms("jk1"));
	echo "</pre>";
}

?>