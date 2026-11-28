# Axolotl Policy

Sistema intermediario de pólizas de seguro desarrollado por el equipo **Full Metal Software** para la materia *Arquitectura de Software* de la UACM, Plantel San Lorenzo Tezonco.

La aplicación ofrece una interfaz para gestionar clientes, pólizas y beneficiarios. Consulta y actualiza los datos mediante la API REST del Sistema Dueño (`http://nachintoch.mx:8080`).

El diseño se basa en arquitectura limpia. El código incluye modelos, controladores de vista y controladores REST. La aplicación usa Spring Boot y Thymeleaf.

[Repositorio del proyecto](https://github.com/MetalCris117/Full-Metal-Software-AS)

## Requisitos

| Requisito | Detalle |
|---|---|
| JDK 25 | Comprueba la versión con `java -version`. |
| Git | Necesario si vas a clonar el repositorio. |
| Conexión a internet | Para descargar las dependencias al compilar y acceder al Sistema Dueño desde la aplicación. |
| Puerto 8081 disponible | Es el puerto configurado para la aplicación. |

El proyecto incluye Maven Wrapper (`mvnw` y `mvnw.cmd`), por lo que no hace falta instalar Maven por separado. Tampoco necesitas PostgreSQL para arrancar con la configuración incluida, ya que `Main.java` excluye la configuración automática de la fuente de datos y de Hibernate JPA.

## Instalación

```bash
git clone https://github.com/MetalCris117/Full-Metal-Software-AS.git
cd Full-Metal-Software-AS
```

## Ejecución desde el código fuente

En **Windows (PowerShell)**

```powershell
.\mvnw.cmd spring-boot:run
```

En **Linux o macOS**

```bash
./mvnw spring-boot:run
```

En VS Code, también puedes abrir `src/main/java/mx/edu/uacm/is/slt/as/axolotPolicy/Main.java` y seleccionar **Run** sobre el método `main`.

Una vez iniciada la aplicación, abre [http://localhost:8081](http://localhost:8081). Para detenerla desde la terminal, presiona `Ctrl+C`.

## Generar y ejecutar el JAR de entrega

Desde la carpeta que contiene `pom.xml`, ejecuta el comando correspondiente a tu sistema.

En **Windows (PowerShell)**

```powershell
.\mvnw.cmd clean package
```

En **Linux o macOS**

```bash
./mvnw clean package
```

Cuando Maven muestre `BUILD SUCCESS`, ubica el archivo `target/axolotPolicy-0.0.1-SNAPSHOT.jar`. Ese es el archivo que se entrega. Puedes comprobar que arranca con este comando:

```bash
java -jar target/axolotPolicy-0.0.1-SNAPSHOT.jar
```

Abre [http://localhost:8081](http://localhost:8081) para comprobar que se muestra el portal. Si la aplicación ya está en ejecución, detenla antes de iniciar el JAR.

## Uso del sistema

La página principal da acceso a las funciones de clientes, pólizas y beneficiarios.

| Pantalla o función | Caso de uso |
|---|---|
| Alta de cliente | `NRM-AC` |
| Consulta y modificación de cliente | `NRM-MC` |
| Alta de póliza | `NRM-AP` |
| Listado y búsqueda de pólizas | `PRN-GP` |
| Modificación de póliza | `NRM-MP` |
| Alta de beneficiario | `NRM-AB` |

## Problemas comunes

| Problema | Solución |
|---|---|
| `release version 25 not supported` | Instala el JDK 25 y revisa que `java -version` y `.\mvnw.cmd -v` (o `./mvnw -v`) indiquen que se está usando esa versión. Si Maven usa otra, revisa `JAVA_HOME`. |
| `Port 8081 was already in use` | Detén el proceso que usa el puerto o cambia `server.port` en `src/main/resources/application.properties`. |
| La aplicación indica que el Sistema Dueño no está disponible | Comprueba tu conexión e inténtalo más tarde. |

## Equipo de desarrollo

| Integrante | Rol |
|---|---|
| Luis Armando Aguilar Castellanos | Líder |
| Francisco Acosta Díaz | Líder |
| Francisco Aramis Alcantara Vivas | Mediador de reuniones de retrospectiva |
| Erik Cruz Roldan | Administrador de Base de Datos |
| Cristian Emmanuel Suárez García | Control de calidad |
| Brayan Pérez Nava | Moderación y fomento a la comunicación |
