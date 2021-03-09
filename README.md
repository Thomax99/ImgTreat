# Modèle client - serveur

Ce README présente les tds 4-5 du cours projet de développement logiciel sur l'architecture client-serveur.

## Partie Serveur

Pour le développement du serveur, il n'y a pas grand chose à ajouter. Nous avons fait un choix d'implémentation, qui est pour le get sur une liste d'image, nous renvoyons un json contenant plusieurs informations sur l'image. Parmi celles-ci, nous avons le nom et l'id de l'image, mais aussi la taille de l'image, chose pratique pour gérer l'affichage correctement. Pour faciliter ceci, nous avons géré la chose avec la création d'une classe ImageInfos, que nous serialisons en json , au lieu de renvoyer des images directement.

Pour faciliter l'intégration de la partie traitement d'image, nous avons mis en place une méthode get sur images/infos/id, qui permet d'obtenir les métadonnées correspondant à une image, celle en id. Cela permettra, par exemple, lorsque l'on veut connaitre la taille d'une seule image à partir de son id, ou de son type, de ne pas être obligé de déployer l'entiereté de la liste des images.

Nous pouvons ajouter quelque chose sur les tests effectués. Il est bien évidemment fondamental de tester son projet de manière propre, ce qui a été bien entendu fait. Néanmoins, et particulièrement un test, a à mon avis, assez peu de pertinence : deleteImageShouldReturnBadRequest ne joue pas sur un comportement que nous avons décidé, mais sur le fait que spring-boot va retourner une bad request dans un certain cas. Ainsi, nous ne testons pas notre projet mais plus à mon avis le fait que spring-boot "marche comme prévu".

## Partie client

De la même manière, peu de chose est à ajouter sur la partie client. Nous allons néanmoins détailler le projet car celui-ci est librement inspiré des demandes

### Détail du projet

Dans cette partie, nous allons détailler l'idée du projet.
C'est une page monofichier correspondant au composant Main.vue.
Celui-ci est composé d'une partie haute et de deux autres composants, Image et Gallery.

La partie haute contient un titre, un formulaire ainsi qu'un bouton. Ce formulaire et ce bouton permette de soumettre au serveur un nouveau fichier.

Image contient une image affichée en grand et Gallery contient toutes les images stockées sur le serveur.
Ces deux composants ne peuvent être en même temps, c'est à dire que l'on a d'affiché soit Image, soit Gallery.

Au départ, Gallery est affiché, et le click sur une image de Gallery engendre l'apparition de la même image mais en plus grand.
Il y a dans Gallery un bouton de raffraichissement des images. Il faut savoir que la soumission d'une image par le client engendre un rafraichissement automatique des images. Pour ne pas rompre l'affichage, toutes les images de Gallery ont une hauteur fixe.

Lorsque l'on clique sur une image, elle est affichée en grand. Deux autres boutons apparaissent, les deux permettant de repasser en mode Gallery. Néanmoins, le premier ne fait que repasser en mode Gallery (Changer l'image) alors que le deuxième (Supprimer l'image) engendre la suppression de l'image sur le serveur.

### Questions soulevées

La création de ce client a posé quelques questions sur la partie implémentation des méthodes : certaines sont passées en proprietés, d'autres sont directement dans le code du composant. L'idée de ceci est la suivante : quand la méthode est sensée effectuer une action simplement sur le composant, alors cette méthode est interne au composant. Quand par contre la méthode va engendrer une modification plus globale, alors elle sera passée en tant que callback.

### Tests

Nous avons mis en place quelques tests dans cette application, mais la finalité de ceux-ci n'est pas de tester réellement l'application mais plutôt simplement d'apprendre à faire des tests. Ainsi ceux-ci passent sans avoir un quelconque intérêt.
# ImgTreat
