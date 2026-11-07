graph TD
    %% Estilos de nodos
    classDef roleStyle fill:#e1f5fe,stroke:#01579b,stroke-width:2px;
    classDef sysStyle fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px;
    classDef dbStyle fill:#fff3e0,stroke:#e65100,stroke-width:2px;

    %% Actores y Módulos
    subgraph Actores [Roles del Sistema]
        Admin[Administrador]:::roleStyle
        Recepcion[Recepcionista]:::roleStyle
        Estilista[Estilista]:::roleStyle
    end

    subgraph Sistema [Aplicación Java Swing - MVC]
        Login[Autenticación SHA-256]:::sysStyle
        Clientes[Gestión de Clientes y Mascotas]:::sysStyle
        Agenda[Control de Citas]:::sysStyle
        Ventas[Registro de Servicios y Descuento de Insumos]:::sysStyle
        Reportes[Reportes Financieros y Stock]:::sysStyle
    end

    subgraph BaseDatos [Base de Datos MySQL 8]
        DB[(bellasmascotas_db)]:::dbStyle
    end

    %% Flujos de interacción
    Admin -->|Acceso Total| Login
    Recepcion -->|Agenda y Clientes| Login
    Estilista -->|Mi Agenda y Fichas| Login

    Login -->|Valida Rol| Clientes
    Login -->|Valida Rol| Agenda
    Login -->|Valida Rol| Ventas
    Login -->|Valida Rol| Reportes

    Clientes -->|CRUD| DB
    Agenda -->|Valida Horarios| DB
    Ventas -->|Transacción ACID: <br> Registra venta, descuenta stock, <br> actualiza puntos y cita| DB
    Reportes -->|Agregaciones SUM/COUNT| DB
