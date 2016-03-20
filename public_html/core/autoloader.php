<?php

/**********************************************************
***                                                     ***
***		       db                        88             ***
***		      d88b                       88             ***
***		     d8'`8b                      88             ***
***		    d8'  `8b                     88             ***
***		   d8YaaaaY8b       aaaaaaaa     88             ***
***		  d8""""""""8b      """"""""     88             ***
***		 d8'        `8b                  88             ***
***		d8'          `8b                 88888888888    ***
***                                                     ***
**********************************************************/


/**
* Fonction autoloader qui sera utilisée par le processus à chaque appel de classe,
* affiche un message d'erreur si la classe n'est pas trouvée.
* @param $name le nom de la classe appelée
*/
function autoLoader($name){
	try{
		if($name == "Route" ){
			require_once CORE."$name.php";
		}
		else{
			require_once MNGR."$name.php";
		}
	}
	catch(Exception $e){
		echo "Classe ".$name." non trouvée :\n".$e->getMessage();
	}
}

spl_autoload_register('autoLoader', false, true);

?>