# BarAPP

## 🚀 Estructura del Proyecto

El repositorio está organizado en una arquitectura de monorepo con tres componentes principales:

1.  **[server](./server/BarAPP)**: Backend desarrollado con **Spring Boot 3** y **Java 17**. Gestiona la lógica de negocio, persistencia en base de datos (PostgreSQL) y seguridad.
2.  **[mobile_client](./mobile_client/BarAPP)**: Aplicación móvil para camareros desarrollada con **Compose Multiplatform (Kotlin)**. Permite la gestión de mesas y comandas en tiempo real.
3.  **[admin-web](./admin-web)**: Panel de administración web desarrollado con **Vite** y **React/JavaScript**. Permite gestionar el menú, productos y visualizar estadísticas.

---

## 🛠️ Requisitos Previos

Antes de comenzar se necesita tener instalado:

*   **Java JDK 17** o superior.
*   **Node.js** (v18+) y **npm**.
*   **Android Studio** (para el cliente móvil).

---

## Instalación y Configuración

### 1. Servidor (Backend)
Ir a la carpeta del servidor y configura las variables de entorno:

```bash
cd server/BarAPP
```

1.  Copiar el archivo .env en el directorio server/BarAPP.

2.  Ejecuta el servidor:
```bash
./mvnw spring-boot:run
```
El servidor estará disponible en `http://localhost:8081` por defecto.

### 2. Panel de Administración (Web)
Navega a la carpeta de la web y arranca el entorno de desarrollo:

```bash
cd admin-web
npm install
npm run dev
```
La web estará disponible en `http://localhost:5173` si se usa Vite.
Tener en cuenta que también se necesita el respectivo .env en la carpeta admin-web.

### 3. Cliente Móvil (Android)
1.  Abre la carpeta `mobile_client/BarAPP` en **Android Studio**.
2.  Sincroniza el proyecto con Gradle.
3.  Configuración de IP: En `RemoteDataSourceImpl.kt` (línea 21), ajusta la `baseUrl`. Usa `10.0.2.2` para el emulador o la IP del PC para un móvil físico.
4.  Ejecuta la app en un emulador o dispositivo físico.

---


## Autor
*   **Pablo Barreiro** - *Desarrollo Integral* - [pablobarreiro131](https://github.com/pablobarreiro131)
