# Demo Hibernate JDBC
## Prerrequisitos
- IDE IntelliJ o Eclipse, la demo esta armada en IntelliJ
- Ultima version de la JDK disponible, como minimo la version 1.8.
-
## Setup
### Creamos un proyecto MAVEN
```sh
file->new->Proejct
```
![alt text](https://github.com/hernancasla/Demo-hibernate-jdbc/blob/main/readme-files/new-project.png?raw=true)
```sh
Finish
```
Y deberiamos contar con la estructura de un proyecto maven propiamente dicho
algo asi:
![alt text](https://github.com/hernancasla/Demo-hibernate-jdbc/blob/main/readme-files/maven-structure.png?raw=true)

### Dependencias
Debemos agregar las 2 dependencias necesarias en nuestro `pom.xml`

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.8.1</version>
    <scope>test</scope>
</dependency>

<dependency>
     <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.6.9.Final</version>
</dependency>
<dependency>
```
### Build
Agregamos la entrada correspondiente a nuestro Build de maven para que nuestro classpath quede correctamente configurado
```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                    <archive>
                        <index>true</index>
                        <manifest>
                            <addClasspath>true</addClasspath>
                        </manifest>
                        <manifestEntries>
                            <mode>development</mode>
                            <url>${project.url}</url>
                            <key>value</key>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
Por ultimo le damos CLEAN INSTALL en nuestra herramienta visual de maven o bien ejecutamos el comando `mvn clean install` por consola.

En la barra derecha de IntelliJ hay un panel llamado Maven ahi dentro tenemos todas las acciones que podemos ejecutar, en este caso debemos seleccionar clean + install y presionar el boton PLAY (el que esta en verde)
![alt text](https://github.com/hernancasla/Demo-hibernate-jdbc/blob/main/readme-files/clean-install.png?raw=true)

Y Listo, con todo esto tenemos todo lo necesario para empezar a trabajar!.
## Configuracion de Hibernate

### Creacion del archivo hibernate.cfg.xml
Lo primero que tenemos que tener en cuenta es la creacion de este archivo, la cual debe estar ubicada en la parte de "resources" del proyecto de modo de poder enviarle la ruta al framework y asi pueda leerse toda nuestra configuracion.

En este archivo vamos a encontrar varias entradas necesarias para poder comenzar a trabajar con nuestro modelo y algunas otras mas bien opcionales que nos facilitaran la vida.

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">org.h2.Driver</property>
        <property name="hibernate.connection.url">jdbc:h2:~/test</property>
        <property name="hibernate.connection.username">sa</property>
        <property name="hibernate.connection.password"></property>
        <property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
        <property name="show_sql">true</property>
        <property name="format_sql">true</property>
        <property name="hbm2ddl.auto">create-drop</property>
    </session-factory>
</hibernate-configuration>
```
Inicialmente este va a ser nuestra configuracion, pasemos a explicar un poco cada "property"
`hibernate.connection.driver_class:` se define el driver de la base de datos que querremos configurar, en este caso es el driver correspondiente a la base H2.
`hibernate.connection.url:` se define la url de conexion a la base, en otros casos podria definirse con el host y puerto.
`hibernate.connection.username:` se define el nombre del usuario con el que nos vamos a conectar a la BD.
`hibernate.connection.password:` se define la contraseña con la que nos vamos a conectar a la BD.
`show_sql:` este parametro es sumamente util, nos va a mostrar por consola todos los comandos SQL que se van a ir ejecutando.
`format_sql:`este parametro cuando esta en true nos formatea la consulta SQL de modo que sea mas limpia a la consulta realmente ejecutada, aunque en escencia el resultado va a ser el mismo.
`hbm2ddl.auto:` este es otro de los parametros sumamente utiles, podemos tener varias opciones, dos de ellas son
- `create-drop` en esta configuracion cada vez que levantemos la aplicacion Hibernate ejecutara los comandos de DROP de cada una de las tablas mapeadas y luego CREATE, es sumamente util cuando nos encontramos definiendo el modelo de objetos en conjunto con el modelo de datos.
- `update` en esta configuracion hibernate solo se va a dedicar a crear los objetos que no existan, normalmente mediante alters table, o agregados de constraint, secuencias, etc. Es sumamente util cuando estamos modificando nuestro modelo de objetos y queremos a la vez verlo impactado en nuestro modelo de datos.

### Creacion de HibernateUtil
La creacion de esta clase es opcional, pero concretamente no existe proyecto que no tenga una clase encargada de adminsitrar la session factory de hibernate.
En nuestro caso creamos una especie de singleton de la clase SessionFactory. Con esto queremos decir que nuestra SessionFactory va a ser la misma durante toda la ejecucion de nuestro programa.
Pero veamos algo de codigo, la cosa quedaria asi:
```java
public class HibernateUtil {
    private static SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            if (sessionFactory == null) {
                StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                        .configure("hibernate.cfg.xml").build();

                Metadata metaData = new MetadataSources(standardRegistry)
                        .getMetadataBuilder()
                        .build();

                sessionFactory = metaData.getSessionFactoryBuilder().build();
            }
            return sessionFactory;
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
```
Como podemos observar, dentro de esta clase utilitaria estamos indicandole al registry de hibernate la ubicacion de nuestro archivo hibernate.cfg.xml y creando nuestra session factory.
For production environments...

```sh
npm install --production
NODE_ENV=production node app
```

Dillinger is a cloud-enabled, mobile-ready, offline-storage compatible,
AngularJS-powered HTML5 Markdown editor.

- Type some Markdown on the left
- See HTML in the right
- ✨Magic ✨

## Features

- Import a HTML file and watch it magically convert to Markdown
- Drag and drop images (requires your Dropbox account be linked)
- Import and save files from GitHub, Dropbox, Google Drive and One Drive
- Drag and drop markdown and HTML files into Dillinger
- Export documents as Markdown, HTML and PDF

Markdown is a lightweight markup language based on the formatting conventions
that people naturally use in email.
As [John Gruber] writes on the [Markdown site][df1]

> The overriding design goal for Markdown's
> formatting syntax is to make it as readable
> as possible. The idea is that a
> Markdown-formatted document should be
> publishable as-is, as plain text, without
> looking like it's been marked up with tags
> or formatting instructions.

This text you see here is *actually- written in Markdown! To get a feel
for Markdown's syntax, type some text into the left window and
watch the results in the right.

## Tech

Dillinger uses a number of open source projects to work properly:

- [AngularJS] - HTML enhanced for web apps!
- [Ace Editor] - awesome web-based text editor
- [markdown-it] - Markdown parser done right. Fast and easy to extend.
- [Twitter Bootstrap] - great UI boilerplate for modern web apps
- [node.js] - evented I/O for the backend
- [Express] - fast node.js network app framework [@tjholowaychuk]
- [Gulp] - the streaming build system
- [Breakdance](https://breakdance.github.io/breakdance/) - HTML
  to Markdown converter
- [jQuery] - duh

And of course Dillinger itself is open source with a [public repository][dill]
on GitHub.



## Plugins

Dillinger is currently extended with the following plugins.
Instructions on how to use them in your own application are linked below.

| Plugin | README |
| ------ | ------ |
| Dropbox | [plugins/dropbox/README.md][PlDb] |
| GitHub | [plugins/github/README.md][PlGh] |
| Google Drive | [plugins/googledrive/README.md][PlGd] |
| OneDrive | [plugins/onedrive/README.md][PlOd] |
| Medium | [plugins/medium/README.md][PlMe] |
| Google Analytics | [plugins/googleanalytics/README.md][PlGa] |

## Development

Want to contribute? Great!

Dillinger uses Gulp + Webpack for fast developing.
Make a change in your file and instantaneously see your updates!

Open your favorite Terminal and run these commands.

First Tab:

```sh
node app
```

Second Tab:

```sh
gulp watch
```

(optional) Third:

```sh
karma test
```

#### Building for source

For production release:

```sh
gulp build --prod
```

Generating pre-built zip archives for distribution:

```sh
gulp build dist --prod
```

## Docker

Dillinger is very easy to install and deploy in a Docker container.

By default, the Docker will expose port 8080, so change this within the
Dockerfile if necessary. When ready, simply use the Dockerfile to
build the image.

```sh
cd dillinger
docker build -t <youruser>/dillinger:${package.json.version} .
```

This will create the dillinger image and pull in the necessary dependencies.
Be sure to swap out `${package.json.version}` with the actual
version of Dillinger.

Once done, run the Docker image and map the port to whatever you wish on
your host. In this example, we simply map port 8000 of the host to
port 8080 of the Docker (or whatever port was exposed in the Dockerfile):

```sh
docker run -d -p 8000:8080 --restart=always --cap-add=SYS_ADMIN --name=dillinger <youruser>/dillinger:${package.json.version}
```

> Note: `--capt-add=SYS-ADMIN` is required for PDF rendering.

Verify the deployment by navigating to your server address in
your preferred browser.

```sh
127.0.0.1:8000
```

## License

MIT

**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

[dill]: <https://github.com/joemccann/dillinger>
[git-repo-url]: <https://github.com/joemccann/dillinger.git>
[john gruber]: <http://daringfireball.net>
[df1]: <http://daringfireball.net/projects/markdown/>
[markdown-it]: <https://github.com/markdown-it/markdown-it>
[Ace Editor]: <http://ace.ajax.org>
[node.js]: <http://nodejs.org>
[Twitter Bootstrap]: <http://twitter.github.com/bootstrap/>
[jQuery]: <http://jquery.com>
[@tjholowaychuk]: <http://twitter.com/tjholowaychuk>
[express]: <http://expressjs.com>
[AngularJS]: <http://angularjs.org>
[Gulp]: <http://gulpjs.com>

[PlDb]: <https://github.com/joemccann/dillinger/tree/master/plugins/dropbox/README.md>
[PlGh]: <https://github.com/joemccann/dillinger/tree/master/plugins/github/README.md>
[PlGd]: <https://github.com/joemccann/dillinger/tree/master/plugins/googledrive/README.md>
[PlOd]: <https://github.com/joemccann/dillinger/tree/master/plugins/onedrive/README.md>
[PlMe]: <https://github.com/joemccann/dillinger/tree/master/plugins/medium/README.md>
[PlGa]: <https://github.com/RahulHP/dillinger/blob/master/plugins/googleanalytics/README.md>
