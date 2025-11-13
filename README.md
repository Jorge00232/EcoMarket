# 🛒 EcoMarket

Aplicación móvil desarrollada en **Android Studio**, **Kotlin** y **Jetpack Compose**, con autenticación, catálogo de productos, carrito de compras y un panel administrativo CRUD.  
La app utiliza **Room (SQLite)** para persistencia de datos y ahora integra una **API externa real** para obtener el tipo de cambio USD→CLP en tiempo real.

---

## 🚀 Funcionalidades principales
- Inicio de sesión con validaciones  
- Navegación mediante BottomBar (Inicio / Perfil)  
- Catálogo con imágenes, buscador y categorías  
- Carrito de compras persistente  
- Panel de administrador con CRUD completo  
- Validaciones de formularios (precios numéricos, campos requeridos, descuentos opcionales)  
- Animaciones suaves con Jetpack Compose  
- **Conversión de precios CLP → USD mediante API externa**

---

## 🌐 Integración con API externa (Tipo de cambio USD→CLP)

EcoMarket consume un servicio público para obtener la tasa de cambio del dólar y mostrar precios aproximados en USD dentro del catálogo.

### API utilizada
**ExchangeRate API — open.er-api.com**

### Base URL
https://open.er-api.com/


### Endpoint utilizado
https://open.er-api.com//v6/latest/USD


### Datos obtenidos
- Se extrae el valor:
rates["CLP"]

- Ejemplo real: `CLP = 934.52`

### Uso dentro de la app
- El TopBar muestra:  
**1 USD = 934 CLP**
- Cada producto muestra también su precio estimado en USD  
- La tasa se obtiene automáticamente al iniciar la app  
- Logcat registra la operación bajo la etiqueta:  
EcoMarketRate


---

## 🧱 Arquitectura de red (resumen)
- **NetworkModule**: Configura Retrofit para el backend local + API externa  
- **RateRepository**: Lógica para obtener el tipo de cambio  
- **ProductListScreen**: Muestra la tasa en el TopBar y calcula el precio en USD  
- **OkHttp Logging**: Permite depuración completa de solicitudes HTTP  

---

## 🛠 Tecnologías utilizadas
- Kotlin  
- Jetpack Compose  
- Material 3  
- Navigation Compose  
- Room Database (SQLite)  
- Retrofit + Moshi  
- OkHttp Logging Interceptor  
- Coil (carga de imágenes)  
- Coroutines  
- MVVM

---

## 🔑 Usuario administrador (para CRUD)
admin@ecomarket.cl
admin123


---

## ⚠ Nota sobre `local.properties`
Este archivo **no viene incluido en el repositorio** porque depende de cada instalación.  
Si Android Studio no lo genera, créalo manualmente:

```properties
sdk.dir=C:\\Users\\TuUsuario\\AppData\\Local\\Android\\Sdk

