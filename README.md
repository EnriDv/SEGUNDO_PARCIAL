Documentación de Patrones y Arquitectura

Este documento justifica las decisiones técnicas, patrones de diseño y elecciones de arquitectura utilizadas en los tres ejercicios de desarrollo Android con Jetpack Compose.

1. Componente UserCard (Tarjeta de Usuario Compleja)

Objetivo

Crear un componente de UI visualmente rico, autocontenido pero capaz de comunicarse con el exterior.

Patrones y Decisiones Técnicas

A. Patrón State (Estado Interno vs. Hoisting)

Implementación: Se utilizó var isFollowing by remember { mutableStateOf(false) } dentro del componente.

Justificación:

Optimismo en UI: Para un botón de "Seguir", el usuario espera retroalimentación instantánea. Manejar el estado visual internamente permite que el botón cambie de color inmediatamente sin esperar una respuesta de red.

Encapsulamiento: El componente sabe cómo dibujarse a sí mismo en ambos estados (siguiendo/no siguiendo), reduciendo la complejidad del padre.

B. Callback Pattern (Lambda Hoisting)

Implementación: onFollowClick: (Boolean) -> Unit.

Justificación: Aunque el componente maneja su estado visual, necesita notificar al "mundo exterior" (API, Base de Datos) que ocurrió una acción. Al pasar el nuevo estado en el callback, desacoplamos la UI de la lógica de negocio.

C. Composable Reusability (Parametrización)

Implementación: Parámetros explícitos para datos (nombre, fotoUrl) y modificadores.

Justificación: Permite que la misma tarjeta se use en listas, perfiles o diálogos sin modificar su código interno, cumpliendo el principio de responsabilidad única (SRP).

2. Gestor de Permisos (Permissions Manager)

Objetivo

Manejar el flujo complejo de permisos de Android (solicitud, denegación, "no volver a preguntar" y recuperación desde configuración).

Patrones y Decisiones Técnicas

A. Observer Pattern (Lifecycle Awareness)

Implementación: LifecycleEventObserver escuchando ON_RESUME.

Justificación:

Problema: Cuando un usuario niega un permiso permanentemente, debe ir a la configuración de Android para activarlo. Al regresar a la app, la UI no se entera automáticamente de ese cambio externo.

Solución: El observador detecta cuando la app vuelve al primer plano y fuerza una re-verificación de permisos, actualizando los iconos verdes/rojos automáticamente sin intervención del usuario.

B. Componentización Modular

Implementación: Separación de PermissionItem como entidad individual manejada por una lista mutable mutableStateListOf.

Justificación:

Escalabilidad: Agregar un nuevo permiso (ej. Cámara) solo requiere agregar una línea a la lista de datos. No es necesario copiar y pegar lógica de botones o verificaciones.

Aislamiento: Cada permiso gestiona su propio launcher y estado de denegación independientemente de los demás.

C. Logic Branching (Manejo de Rationale)

Implementación: Uso de shouldShowRequestPermissionRationale.

Justificación: Es crucial para la UX distinguir entre "El usuario dijo no por ahora" (mostrar botón de solicitar de nuevo) y "El usuario bloqueó el permiso" (mostrar botón para ir a Configuración). Sin esto, la app parecería rota al presionar un botón que no hace nada.

3. Generador Fibonacci (Arquitectura MVVM)

Objetivo

Calcular una serie matemática pesada de forma segura, separando la lógica de la vista.

Patrones y Decisiones Técnicas

A. Arquitectura MVVM (Model-View-ViewModel)

Implementación: Separación estricta entre MainActivity (Vista) y FibonacciViewModel (Lógica).

Justificación:

Supervivencia a Cambios de Configuración: Si el usuario rota la pantalla mientras se calcula la serie, el ViewModel retiene los datos. Si la lógica estuviera en la Activity, el cálculo se perdería o se reiniciaría.

Testabilidad: La lógica de validación y cálculo puede probarse unitariamente sin necesidad de un emulador Android.

B. Unidirectional Data Flow (UDF)

Implementación: StateFlow (_fibonacciState) expuesto como inmutable a la UI.

Justificación: La UI nunca modifica los datos directamente; solo envía "intenciones" (eventos de clic). El ViewModel procesa y emite un nuevo estado. Esto elimina inconsistencias de estado difíciles de depurar.

C. Concurrencia Estructurada (Corrutinas)

Implementación: withContext(Dispatchers.Default) dentro de viewModelScope.

Justificación:

Performance: El cálculo recursivo de Fibonacci es exponencial ($O(2^n)$). Si se ejecutara en el Main Thread (hilo de UI), la app se congelaría (ANR) con N > 30.

Safety: Dispatchers.Default utiliza un pool de hilos optimizado para tareas intensivas de CPU, manteniendo la UI fluida (el loader sigue girando) mientras se calcula.

D. Channel Pattern para Side-Effects

Implementación: Channel<String> para los mensajes del Snackbar.

Justificación:

Problema con StateFlow: Si usáramos un StateFlow para mostrar errores, al rotar la pantalla el error volvería a aparecer (porque el estado persiste).

Solución: Los Channels envían eventos "calientes" que se consumen una sola vez. Esto garantiza que el Snackbar de error aparezca solo cuando ocurre el error y no reaparezca mágicamente al cambiar la configuración.
