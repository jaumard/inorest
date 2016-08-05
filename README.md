# inorest
Android REST API

## Documentation 
La JavaDoc de cette librairie est disponible sur : http://htmlpreview.github.com/?https://github.com/jaumard/inorest/blob/master/javadoc/index.html

## Installation

Dans le `build.gradle`, rajouter dans la section `android` : 
```
    packagingOptions {
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/NOTICE'
    }
```
Puis dans vos dépendances : 
```
    compile 'com.inotekk.utils:inorest:1.0.7@aar'
    compile 'com.loopj.android:android-async-http:1.4.9'
    compile(
            [group: 'com.fasterxml.jackson.core', name: 'jackson-core', version: '2.7.0'],
            [group: 'com.fasterxml.jackson.core', name: 'jackson-annotations', version: '2.7.0'],
            [group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.7.0']
    )
```

## Utilisation 
Vous avez 2 mode d'initialisation : 

    Rest.init(getApplicationContext(), getString(R.string.root_url))

Ou lors de la première utilisation : 

```
   Rest.getSingleton(getApplicationContext(), getString(R.string.root_url)).get(R.string.sub_route, new HttpTodoHandler<MyBean, String>(){
      	@Override
        public void onSuccess(MyBean response, Header[] headers)
        {
         
        }
      	@Override
        public void onFailure(int code, String msg)
        {
          
        }
    });
```

Si besoins pour des appels concurrents, vous pouvez utiliser : 
```
Rest myRestInstance = new Rest(getApplicationContext(), getString(R.string.root_url))
```

Les prochains appel sont plus simple : 
```
   Rest.getSingleton().get(R.string.sub_route, new HttpTodoHandler<MyBean, MyErrorBean>(){
      	@Override
        public void onSuccess(MyBean response, Header[] headers)
        {
         
        }
      	@Override
        public void onFailure(int code, MyErrorBean msg)
        {
          
        }
    });
```

`MyBean` et `MyErrorBean` sont des simple classe JAVA représentant les données JSON, le JSON sera automatiquement convertie  en `MyBean` et `MyErrorBean`. Si un problème survient lors de la conversion JSON => Object, la méthode `onFailure` est appelé.
Raccourcis : `Rest.getSingleton()` peux être remplacer par `Rest.i()`.

Les méthodes REST disponibles sont get, post, put, patch, delete, et head. Les appels se font en background et les méthodes `onSuccess` et `onFailure` dans le thread principal.

Par défaut tout les cookies envoyé par le serveur sont automatiquement sauvegardé et utilisé. Cependant si besoin des cookies personnalisés peuvent être utilisés : 

    Rest.addCookie(cookie); //Ou cookie est de type BasicClientCookie http://loopj.com/android-async-http/

Si vous avez besoin de modifier les header d'une requête, procéder comme ceci :

```
    Map<String, String> headers =  Map<String, String>();
    Rest.getSingleton().post(R.string.sub_route, params, headers, new HttpTodoHandler<MyBean>(){
      	@Override
        public void onSuccess(MyBean response)
        {
         
        }
      	@Override
        public void onFailure(int code, String msg)
        {
          
        }
    });
```

Le paramètre `params` peut être n'importe quel objet JAVA, il sera convertie en JSON et envoyé. Si vous avez besoin d'envoyer en clé/valeur il faut fournir un objet de type `RequestParams` (cf http://loopj.com/android-async-http/#adding-getpost-parameters-with-requestparams).

Si vous devez récupérer autre chose que du JSON, faites comme ceci : 

```
    Rest.i().get(R.string.sub_route, new HttpPlainTodoHandler(){
      	@Override
        public void onSuccess(String response, Header[] headers)
        {
         
        }
      	@Override
        public void onFailure(int code, String msg)
        {
          
        }
    });
```

Vous recevrez les données brutes reçu en String, a vous de les parser (xml...).

## Appels synchrone
Pour faire des appels synchrone il suffit d'activer le mode synchrone comme ceci : 

    Rest.i().setSync(true)

Les signatures reste les mêmes mais maintenant les appels sont synchrone.

## Stopper des requêtes
Afin de stopper les requêtes en cours il faut faire : 

```
Rest.i().cancelRequest(myContext)
```

Toutes les requêtes associé à `myContext` seront stopper. Pour changer le context il suffit de faire `Rest.i().setContext(newContext)`.

## Support on Beerpay
Hey dude! Help me out for a couple of :beers:!

[![Beerpay](https://beerpay.io/jaumard/inorest/badge.svg?style=beer-square)](https://beerpay.io/jaumard/inorest)  [![Beerpay](https://beerpay.io/jaumard/inorest/make-wish.svg?style=flat-square)](https://beerpay.io/jaumard/inorest?focus=wish)