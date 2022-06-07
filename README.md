# Demo Hibernate
## Prerrequisitos
- IDE IntelliJ o Eclipse, la demo esta armada en IntelliJ
- Ultima version de la JDK disponible, como minimo la version 1.8.

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
## Configuración de Hibernate

### Creación del archivo hibernate.cfg.xml
Lo primero que tenemos que tener en cuenta es la creación de este archivo, la cual debe estar ubicada en la parte de "resources" del proyecto de modo de poder enviarle la ruta al framework y asi pueda leerse toda nuestra configuracion.

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

### Creación de HibernateUtil
La creación de esta clase es opcional, pero concretamente no existe proyecto que no tenga una clase encargada de adminsitrar la session factory de hibernate.
En nuestro caso creamos una especie de singleton de la clase SessionFactory. Con esto queremos decir que nuestra SessionFactory va a ser la misma durante toda la ejecucion de nuestro programa.
Pero veamos algo de código, la cosa quedaría asi:
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

**y con esto estamos listo para empezar a MAPEAR!!!**

## Mapeo con JPA
para el mapeo de los distintos objetos y tablas, tenemos dos formas en hibernate, una es mediante la especificacion propia de Hibernate la cual puede ser realizada con archivos XML, y la otra es mediante anotaciones definidas en las especificaciones JPA, las cuales nos permiten abstraer nuestro modelo de persistencia del ORM con el que estemos trabajando, esto quiere decir que si el dia de manana decidieramos cambiar de hibernate por spring data por ejemplo, nuestras entidades deberian quedar exactamente igual.
### DER
![alt text](https://github.com/hernancasla/Demo-hibernate-jdbc/blob/main/readme-files/der.png?raw=true)

OJO! aun no tenemos nada creado en la base de datos, vamos a trabajar solamente con hibernate

### Entities
**Vamos a crear las entidades Order, OrderDetail y Product**
Arranquemos por la clase mas simple e independiente de nuestro modelo la clase de producto
```Java
@Entity
@Table(name="PRODUCT")
public class Product {
    @Id
    @Column(name="ID")
    private int id;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="PRICE")
    private Double price;
}
```
Omitimos los getters y setters solo para no sumar código innecesario para la explicacion, pero obviamente en el codigo estan implementados.
Veamos cada Annotation de **JPA** que agregamos

`@Entity:` Definimos una nueva entidad de la base de datos que vamos a relacionar directamente con nuestra clase Product

`@Table(name="PRODUCT"):` Definimos atributos propio de la tabla que queremos mapear, en este caso solo se agrego el nombre,pero existen muchas otras propiedades para agregar.

`@Id:` es una annotation obligatoria en JPA, la cual indica el identificador unico de nuestra tabla

`@Column:`  con esta annotation mapeamos las columnas de la base con artributos de nuestra clase, en este ejemplo solo se esta mapeando el nombre pero existen mas atributos que veremos en breve.

Sigamos por la entidad ORDER

```Java
@Entity
@Table(name="ORDER_T")
public class Order {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    @Column(name="ORDER_DATE")
    @CreationTimestamp
    private Date date;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID")
    private List<OrderDetail> orderDetails;
}
```
Veamos que hay de nuevo,

`@GeneratedValue(strategy=GenerationType.AUTO):` Con esta annotation y su parametro strategy estamos diciendole a hibernate que este ID va a ser autogenerado es decir estamos delegando la responsabilidad de la generacion del ID de la tabla a hibernate, tambien podriamos configurar una secuencia propia de la base para que haga uso de ella, es bastante versatil.

`@CreationTimestamp:` con esta annotation le estamos indicando a hibernate que nuestro "date" tiene un valor por defecto, el cual es la fecha y hora del momdento de creacion de la tabla... bastante practico no?

**Llegamos a un punto importante de la especificación de JPA, las relaciones!!!**
Existen varios tipos de relaciones entre tablas de bases de datos, 1 a 1, 1 a muchos, muchos a 1. Vamos a desarrollar dos de ellos en esta demo.

`@OneToMany(cascade = CascadeType.ALL):` Con esta annotation le estamos diciendo a hibernate que nuestra clase Order tiene una lista de detalles es decir, 1 orden - muchos detalles, por eso la annotation **OneToMany**. Ademas estamos definidiendo el atributo **cascade** en ete caso estamos indicando que el cascadeo va a ser total, es decir, OrderDetail no va a existir mas que por medio de nuestra clase Order, si un detalle es borrado de una orden, este mismo va a ser eliminado de la tabla detalle, y si una orden es eliminada, entonces todos sus detalles lo seran tambien. Existen varias configuraciones de cascade, dependiendo de nuestra necesidad utilizaremos la mas nos convenga.

`@JoinColumn:` con esta annotation definimos cuáles son las columnas que relacionan las tablas.


Y por último veamos la entidad ORDER_DETAIL
```Java
@Entity
@Table(name="ORDER_DETAIL")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    @OneToOne
    @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")
    private Product product;

    @Column(name="QUANTITY")
    private int quantity;
}
```
`@OneToOne:` Como bien nuestro DER lo indica, la relacion entre detalles y productos es de 1 a 1, por eso el motivo de dicha annotation.

##### Listo todo mapeado!!!

## Creación de los DAOs
Si bien podriamos hacer uso directo de la API que nos proporciona hibernate para la ralizar nuestro CRUD, como somos gente de bien, vamos a implementar una capa de DAO (data access object) en la cual delegamos la responsabilidad de realizar cada operacion del CRUD.
ProductDao
```Java
public class ProductDao implements DAO<Product> {
    Session session = HibernateUtil.getSessionFactory().openSession();
    @Override
    public Product get(int id) {
        return  session.get(Product.class, id);
    }

    @Override
    public List<Product> getAll() {
        String hql = "FROM Product";
        Query query = session.createQuery(hql);
        return query.list();
    }

    @Override
    public int save(Product product) {
        session.getTransaction().begin();
        int id = (int) session.save(product);
        session.getTransaction().commit();
        return id;
    }

    public void update(Product product) {
        session.getTransaction().begin();
        session.saveOrUpdate(product);
        session.getTransaction().commit();

    }
    @Override
    public void delete(Product product) {
        session.getTransaction().begin();
        session.delete(product);
        session.getTransaction().commit();
    }
```
Veamos que tenemos de interesante,

`HibernateUtil.getSessionFactory().openSession()` con esto obtenemos una Session de hibernate con la cual vamos a estar interactuando en cada operacion que deseemos realizar.

`session.get(Product.class, id)` Se puede observar que obtener un producto por su ID es tan simple como pasar la clase y el correspondiente ID al metodo get del objeto session... nada mal pero sigamos.

`session.createQuery(hql)` HQL realmente merece un gran parrafo aparte, pero como la idea de esta demo es dar un pantallazo general, vamos a resumir en que HQL es el lenguaje de hibernate para realizar consultas sobre objetos, podemos joinear, sumarizar, y muchas otras cosas propias de SQL, pero siempre tener en cuenta que en HQL se trabaja con los objetos mapeados.
esto nos devuelve un objeto de tipo query que tiene varias propiedades, pero en concreto el metodo getList nos devuelve nuestra lista de productos.

`session.getTransaction().begin()` con el metodo begin damos la apertura a una nueva transaccion que va a finalizar con la llamada al metodo `commit()` o `rollback()`.

`session.save(product)` esta llamada tiene por objetivo persistir un nuevo producto, por lo que si, el ID ya se encuentra en la base de datos, va a romper, casi contrario va a persistir el producto y devolvernos el nuevo ID persistido en la base de datos.

`session.saveOrUpdate(product)` este metodo realiza una inserción si el id no existe, y un update en caso de ya encontrarse creado, el metodo es de tipo void, por lo cual no devuelve nada.


Luego pasamos a la creación del OrderDao, pero eso ya podran observarlo en el codigo, dado que no tenemos nada nuevo por exlpicar.

## Probemos nuestra solución
Creamos nuestro test unitario con JUnit e implementamos los metodos que sean necesarios, veamos alguno de ellos
```Java
 @Test
    public void testSaveProduct(){
        Product yerba = new Product();
        yerba.setDescription("YERBA");
        yerba.setPrice(200.0);

        Product azucar = new Product();
        azucar.setDescription("AZUCAR");
        azucar.setPrice(100.0);

        Product fideos = new Product();
        fideos.setDescription("FIDEOS");
        fideos.setPrice(50.5);

        ProductDao productDao = new ProductDao();
        productDao.save(yerba);
        productDao.save(azucar);
        productDao.save(fideos);

        List<Product> productList = productDao.getAll();
        productList.stream()
            .map(p ->String.format("%d: %s $%.2f",p.getId(),p.getDescription(),p.getPrice()))
            .forEach(System.out::println);
        Assert.assertFalse(productList.isEmpty());
    }
```
En este test procedimos a crear 3 productos y realizar un getAll de ellos, posteriormente imprimirlos y validar que la lista no este vacia para que el test este OK. La resupuesta:
```
1: YERBA $200.00
2: AZUCAR $100.00
3: FIDEOS $50.50
```

