<?php


		/*********************************************
		****	GESTION GLOBALE PHP DU SITE		*****
		*********************************************/

//Variables globales d'accès au fichiers
if(!defined("ROOT")){
	define('ROOT', str_replace('index.php', '', $_SERVER['SCRIPT_FILENAME']));
	define('WEBROOT', str_replace('index.php', '', $_SERVER['SCRIPT_NAME']));
	define('CORE', "core/");
	define('AVATAR', "img/avatars/");
	define('MNGR', "core/managers/");
	define('VIEW', "web/views/");
}

//Inclusion de l'autoloader
require_once "core/autoloader.php";

echo "<pre>";
//print_r($_SERVER);
echo "</pre>";

//On lance le routeur
$route = Route::getInstance();

//S'il s'agit d'un code de confirmation alors on exécute la fonction du manager
if($route->estRequeteConfirmation()){
	JeanKevin::confirmerInscription($route->getJK(), $route->getCode());
}

//Sinon on affiche la page d'accueil
else{
?><!DOCTYPE html>

<html>

<head>
	<meta charset='utf-8'/>
	<title>Où est Jean Kévin?</title>
</head>

<body>
	<h1>Bienvenue !</h1>
	<p>Notre application est bientôt sur le Play Store</p>
</body>

</html>
<?php
}
?>