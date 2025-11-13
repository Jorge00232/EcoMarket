# EcoMarket
Aplicación móvil desarrollada en **Android Studio** con **Kotlin** y **Jetpack Compose**.  
Incluye autenticación de usuarios, catálogo de productos, carrito de compras y un panel administrativo con CRUD.  
La aplicación utiliza **Room (SQLite)** para la persistencia de datos y aplica animaciones suaves con las APIs de Compose.

## Funcionalidades principales
- Inicio de sesión con validaciones de correo y contraseña  
- Navegación inferior (BottomBar) con secciones de Inicio y Perfil  
- Catálogo de productos con imágenes, búsqueda y categorías fijas de alimentos  
- Carrito de compras con totales dinámicos y datos persistentes  
- Panel de administrador con funciones CRUD (crear, editar y eliminar productos)  
- Validaciones en el formulario: campos obligatorios, precios numéricos y descuentos opcionales  
- Retroalimentación visual mediante loaders, mensajes y animaciones  

## Tecnologías
- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **Navigation Compose**
- **Room Database (SQLite)**
- **Coil** (carga de imágenes)
- **Compose Animation APIs**

## Nota
PARA Probar CRUD: admin@ecomarket.cl --> admin123

De momento no hay validacion en el perfil, pero si en formularios de CRUD y el primer formulario de inicio de sesion.

En caso de que la aplicación no corra correctamente, el archivo `local.properties` no se incluye en el repositorio porque es propio de cada computador (contiene la ruta local del SDK de Android).  
Debe generarse automáticamente al abrir el proyecto en Android Studio. Si no ocurre, créelo manualmente con la siguiente ruta:

```properties
sdk.dir=C:\\Users\\TuUsuario\\AppData\\Local\\Android\\Sdk
