-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 05-11-2026 a las 05:03:02
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `eaab_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `acuerdo_pago`
--

CREATE TABLE `acuerdo_pago` (
  `codigo` varchar(30) NOT NULL,
  `factura_numero` varchar(30) NOT NULL,
  `fecha_acuerdo` date NOT NULL,
  `numero_cuotas` int(11) NOT NULL,
  `valor_total` double NOT NULL
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `calendario_racionamiento`
--

CREATE TABLE `calendario_racionamiento` (
  `codigo` varchar(20) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `suspendido` tinyint(1) NOT NULL DEFAULT 0
) ;

--
-- Volcado de datos para la tabla `calendario_racionamiento`
--

INSERT INTO `calendario_racionamiento` (`codigo`, `fecha_inicio`, `fecha_fin`, `suspendido`) VALUES
('CAL001', '2024-01-01', '2026-12-31', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comercial`
--

CREATE TABLE `comercial` (
  `usuario_id` int(11) NOT NULL,
  `nombre_establecimiento` varchar(150) NOT NULL,
  `actividad_economica` varchar(50) NOT NULL,
  `area_operativa` double NOT NULL
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `consumo`
--

CREATE TABLE `consumo` (
  `id` int(11) NOT NULL,
  `codigo` varchar(30) NOT NULL,
  `volumen` double NOT NULL DEFAULT 0,
  `fecha_lectura` date NOT NULL,
  `tarifa_id` int(11) DEFAULT NULL,
  `tipo_lectura` enum('REAL','ESTIMADA') NOT NULL DEFAULT 'REAL',
  `usuario_id` int(11) NOT NULL
) ;

--
-- Volcado de datos para la tabla `consumo`
--

INSERT INTO `consumo` (`id`, `codigo`, `volumen`, `fecha_lectura`, `tarifa_id`, `tipo_lectura`, `usuario_id`) VALUES
(1, '123', 34, '2026-11-04', 1, 'REAL', 3),
(2, '232', 324, '2026-11-04', 1, 'REAL', 3),
(3, '8', 35, '2026-11-04', 1, 'REAL', 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cuota_acuerdo`
--

CREATE TABLE `cuota_acuerdo` (
  `acuerdo_codigo` varchar(30) NOT NULL,
  `numero` int(11) NOT NULL,
  `valor` double NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `pagada` tinyint(1) NOT NULL DEFAULT 0,
  `fecha_pago` date DEFAULT NULL
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `desincentivo`
--

CREATE TABLE `desincentivo` (
  `codigo` varchar(20) NOT NULL,
  `limite_bimestral` double NOT NULL DEFAULT 44,
  `valor_metro_cubico` double NOT NULL DEFAULT 3380.84
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `desincentivo`
--

INSERT INTO `desincentivo` (`codigo`, `limite_bimestral`, `valor_metro_cubico`) VALUES
('DES01', 44, 3380.84);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `excepcion`
--

CREATE TABLE `excepcion` (
  `codigo` varchar(20) NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `categoria_exenta` varchar(150) NOT NULL,
  `justificacion_legal` varchar(255) NOT NULL,
  `proteccion_constitucional` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `excepcion`
--

INSERT INTO `excepcion` (`codigo`, `descripcion`, `categoria_exenta`, `justificacion_legal`, `proteccion_constitucional`) VALUES
('EXC01', 'Protección del derecho a la salud', 'Hospital', 'Protección del derecho a la salud', 1),
('EXC02', 'Centros penitenciarios', 'Centro penitenciario', 'Derechos humanos de los internos', 1),
('EXC03', 'Instituciones educativas', 'Institución educativa', 'Protección de la niñez', 1),
('EXC04', 'Sujetos de especial protección', 'Sujeto especial', 'Jurisprudencia Corte Constitucional', 1),
('EXC05', 'Continuidad de servicios del Estado', 'Oficial', 'Continuidad de los servicios esenciales del Estado', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `factura`
--

CREATE TABLE `factura` (
  `numero` varchar(30) NOT NULL,
  `periodo_inicio` date NOT NULL,
  `periodo_fin` date NOT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `valor_total` double NOT NULL DEFAULT 0,
  `estado_pago` enum('PENDIENTE','PAGADA','VENCIDA','EN_ACUERDO_PAGO') NOT NULL DEFAULT 'PENDIENTE',
  `usuario_id` int(11) NOT NULL,
  `desincentivo_codigo` varchar(20) DEFAULT NULL
) ;

--
-- Volcado de datos para la tabla `factura`
--

INSERT INTO `factura` (`numero`, `periodo_inicio`, `periodo_fin`, `fecha_emision`, `fecha_vencimiento`, `valor_total`, `estado_pago`, `usuario_id`, `desincentivo_codigo`) VALUES
('1', '2026-11-04', '2027-11-04', '2026-11-04', '2027-11-04', 1393783.76, 'PENDIENTE', 3, 'DES01'),
('3', '2026-11-04', '2027-11-04', '2026-11-04', '2027-11-04', 1393783.76, 'PENDIENTE', 3, 'DES01'),
('45324', '2026-11-04', '2027-11-04', '2026-11-04', '2027-11-04', 1393783.76, 'PENDIENTE', 3, 'DES01');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `factura_consumo`
--

CREATE TABLE `factura_consumo` (
  `factura_numero` varchar(30) NOT NULL,
  `consumo_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `factura_consumo`
--

INSERT INTO `factura_consumo` (`factura_numero`, `consumo_id`) VALUES
('1', 1),
('1', 2),
('3', 1),
('3', 2),
('45324', 1),
('45324', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `factura_sancion`
--

CREATE TABLE `factura_sancion` (
  `factura_numero` varchar(30) NOT NULL,
  `sancion_codigo` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `industrial`
--

CREATE TABLE `industrial` (
  `usuario_id` int(11) NOT NULL,
  `codigo_industrial` varchar(50) NOT NULL,
  `proceso_productivo` varchar(255) DEFAULT NULL,
  `carga_contaminante` double NOT NULL DEFAULT 0
) ;

--
-- Volcado de datos para la tabla `industrial`
--

INSERT INTO `industrial` (`usuario_id`, `codigo_industrial`, `proceso_productivo`, `carga_contaminante`) VALUES
(2, '02902', 'klzxk', 24);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `notificacion`
--

CREATE TABLE `notificacion` (
  `codigo` varchar(30) NOT NULL,
  `titulo` varchar(150) NOT NULL,
  `cuerpo` text NOT NULL,
  `destinatario_id` int(11) NOT NULL,
  `canal` enum('EMAIL','SMS','PUSH','LLAMADA') NOT NULL,
  `evento` enum('EMISION_FACTURA','FACTURA_VENCIDA','SUSPENSION_INMINENTE','SUSPENSION_EFECTIVA','CORTE_PROGRAMADO','CAMBIO_CALENDARIO','SANCION_IMPUESTA','PAGO_REGISTRADO') NOT NULL,
  `fecha_creacion` date NOT NULL,
  `enviada` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `notificacion`
--

INSERT INTO `notificacion` (`codigo`, `titulo`, `cuerpo`, `destinatario_id`, `canal`, `evento`, `fecha_creacion`, `enviada`) VALUES
('NOT-00001', 'Sanción impuesta', 'Multa de $86666.0 – ldkld', 3, 'PUSH', 'SANCION_IMPUESTA', '2026-11-04', 1),
('NOT-00002', 'Factura emitida #45324', 'Se ha emitido su factura por valor de $1393783.76', 3, 'PUSH', 'EMISION_FACTURA', '2026-11-04', 1),
('NOT-00003', 'Factura emitida #3', 'Se ha emitido su factura por valor de $1393783.76', 3, 'PUSH', 'EMISION_FACTURA', '2026-11-04', 1),
('NOT-00004', 'Pago registrado', 'Pago de $30.0 registrado exitosamente.', 3, 'PUSH', 'PAGO_REGISTRADO', '2026-11-04', 1),
('NOT-00005', 'Pago registrado', 'Pago de $45.0 registrado exitosamente.', 3, 'PUSH', 'PAGO_REGISTRADO', '2026-11-04', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `oficial`
--

CREATE TABLE `oficial` (
  `usuario_id` int(11) NOT NULL,
  `entidad_publica` varchar(200) NOT NULL,
  `asignacion_presupuestal` double NOT NULL
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pago`
--

CREATE TABLE `pago` (
  `codigo` varchar(30) NOT NULL,
  `fecha` date NOT NULL,
  `valor` double NOT NULL,
  `metodo_pago` enum('TARJETA','TRANSFERENCIA','EFECTIVO','PSE') NOT NULL,
  `referencia` varchar(100) DEFAULT NULL,
  `factura_numero` varchar(30) NOT NULL
) ;

--
-- Volcado de datos para la tabla `pago`
--

INSERT INTO `pago` (`codigo`, `fecha`, `valor`, `metodo_pago`, `referencia`, `factura_numero`) VALUES
('092', '2026-11-04', 45, 'TARJETA', 'SOSOKSO', '3'),
('1234', '2026-11-04', 30, 'TARJETA', 'CKC', '3'),
('2982892', '2026-11-04', 30, 'TARJETA', 'CKC', '3'),
('3', '2026-11-04', 30, 'TARJETA', 'CKC', '3'),
('32', '2026-11-04', 30, 'TARJETA', 'CKC', '3');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona`
--

CREATE TABLE `persona` (
  `id` int(11) NOT NULL,
  `identificacion` varchar(30) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `telefono` varchar(10) DEFAULT NULL,
  `correo` varchar(150) DEFAULT NULL
) ;

--
-- Volcado de datos para la tabla `persona`
--

INSERT INTO `persona` (`id`, `identificacion`, `nombre`, `apellido`, `fecha_nacimiento`, `direccion`, `telefono`, `correo`) VALUES
(1, '4321', 'sjk', 'mk', '2026-11-04', 'lkslklslsks', '2989289282', 'jdskjd@gmail.com'),
(2, '2872872', 'luz', 'perez', '2009-11-04', 'lsjksjkajkda', '2982892', 'kj@gmail.com'),
(3, '222', 'lala', 'pepe', '2026-11-04', 'ldkdkd', '73878273', 'kd@gmail.com'),
(4, '4', 'PQPQPQ', 'MZNZ', '2026-11-04', 'LXKLX', '2222222', 'MN@GMAIL.COM');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reporte`
--

CREATE TABLE `reporte` (
  `codigo` varchar(30) NOT NULL,
  `fecha_generacion` date NOT NULL,
  `autor` varchar(100) NOT NULL,
  `ingresos_facturacion` double NOT NULL DEFAULT 0,
  `cartera_pendiente` double NOT NULL DEFAULT 0,
  `subsidios_otorgados` double NOT NULL DEFAULT 0,
  `multas_recaudadas` double NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `residencial`
--

CREATE TABLE `residencial` (
  `usuario_id` int(11) NOT NULL,
  `numero_habitantes` int(11) NOT NULL,
  `recibe_subsidio` tinyint(1) NOT NULL DEFAULT 0,
  `paga_contribucion` tinyint(1) NOT NULL DEFAULT 0,
  `porcentaje_subsidio` double NOT NULL DEFAULT 0,
  `porcentaje_contribucion` double NOT NULL DEFAULT 0
) ;

--
-- Volcado de datos para la tabla `residencial`
--

INSERT INTO `residencial` (`usuario_id`, `numero_habitantes`, `recibe_subsidio`, `paga_contribucion`, `porcentaje_subsidio`, `porcentaje_contribucion`) VALUES
(1, 22, 1, 0, 15, 0),
(3, 23, 1, 0, 70, 0),
(4, 3, 1, 0, 70, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sancion`
--

CREATE TABLE `sancion` (
  `codigo` varchar(30) NOT NULL,
  `tipo` enum('ECONOMICA','SUSPENSION','PEDAGOGICA') NOT NULL,
  `valor_multa` double NOT NULL DEFAULT 0,
  `descripcion` varchar(255) NOT NULL,
  `estado` enum('IMPUESTA','APELADA','CONFIRMADA','EJECUTADA','REVOCADA') NOT NULL DEFAULT 'IMPUESTA',
  `fecha_imposicion` date NOT NULL,
  `usuario_id` int(11) NOT NULL
) ;

--
-- Volcado de datos para la tabla `sancion`
--

INSERT INTO `sancion` (`codigo`, `tipo`, `valor_multa`, `descripcion`, `estado`, `fecha_imposicion`, `usuario_id`) VALUES
('SAN-1793847586004', 'ECONOMICA', 86666, 'ldkld', 'CONFIRMADA', '2026-11-04', 3),
('SAN-1793848808650', 'PEDAGOGICA', 0, 'KX', 'IMPUESTA', '2026-11-04', 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sector`
--

CREATE TABLE `sector` (
  `id` int(11) NOT NULL,
  `codigo` varchar(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `localidad` varchar(100) DEFAULT NULL,
  `en_racionamiento` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `sector`
--

INSERT INTO `sector` (`id`, `codigo`, `nombre`, `localidad`, `en_racionamiento`) VALUES
(1, 'SEC01', 'Chapinero', 'Chapinero', 0),
(2, 'SEC02', 'Usaquén', 'Usaquén', 0),
(3, 'SEC03', 'Kennedy', 'Kennedy', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tarifa`
--

CREATE TABLE `tarifa` (
  `id` int(11) NOT NULL,
  `codigo` varchar(30) NOT NULL,
  `cargo_fijo` double NOT NULL DEFAULT 0,
  `cargo_variable` double NOT NULL DEFAULT 0,
  `subsidio` double NOT NULL DEFAULT 0,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `tipo_usuario` enum('RESIDENCIAL','COMERCIAL','INDUSTRIAL','OFICIAL') NOT NULL
) ;

--
-- Volcado de datos para la tabla `tarifa`
--

INSERT INTO `tarifa` (`id`, `codigo`, `cargo_fijo`, `cargo_variable`, `subsidio`, `fecha_inicio`, `fecha_fin`, `tipo_usuario`) VALUES
(1, 'T-RES-2024', 10000, 3000, 40, '2024-01-01', '2026-12-31', 'RESIDENCIAL'),
(2, 'T-COM-2024', 10000, 3000, 0, '2024-01-01', '2026-12-31', 'COMERCIAL'),
(3, 'T-IND-2024', 10000, 3000, 0, '2024-01-01', '2026-12-31', 'INDUSTRIAL'),
(4, 'T-OFI-2024', 10000, 3000, 0, '2024-01-01', '2026-12-31', 'OFICIAL');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `turno_racionamiento`
--

CREATE TABLE `turno_racionamiento` (
  `id` int(11) NOT NULL,
  `codigo` varchar(20) NOT NULL,
  `fecha` date NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `sector_id` int(11) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `motivo` varchar(255) DEFAULT NULL
) ;

--
-- Volcado de datos para la tabla `turno_racionamiento`
--

INSERT INTO `turno_racionamiento` (`id`, `codigo`, `fecha`, `hora_inicio`, `hora_fin`, `sector_id`, `activo`, `motivo`) VALUES
(1, '182', '2026-11-04', '21:56:36', '23:56:00', 2, 1, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id` int(11) NOT NULL,
  `persona_id` int(11) NOT NULL,
  `codigo_suscriptor` varchar(30) NOT NULL,
  `fecha_registro` date NOT NULL,
  `tipo_usuario` enum('RESIDENCIAL','COMERCIAL','INDUSTRIAL','OFICIAL') NOT NULL,
  `estrato` int(11) NOT NULL,
  `sector_id` int(11) DEFAULT NULL,
  `canal_notificacion` enum('EMAIL','SMS','PUSH','LLAMADA') NOT NULL DEFAULT 'EMAIL',
  `estado_servicio` enum('ACTIVO','EN_DEUDA','SUSPENDIDO','CORTADO') NOT NULL DEFAULT 'ACTIVO',
  `consumo_promedio` double NOT NULL DEFAULT 0,
  `fecha_baja` date DEFAULT NULL,
  `motivo_baja` varchar(255) DEFAULT NULL,
  `excepcion_codigo` varchar(20) DEFAULT NULL
) ;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id`, `persona_id`, `codigo_suscriptor`, `fecha_registro`, `tipo_usuario`, `estrato`, `sector_id`, `canal_notificacion`, `estado_servicio`, `consumo_promedio`, `fecha_baja`, `motivo_baja`, `excepcion_codigo`) VALUES
(1, 1, '123', '2026-11-04', 'RESIDENCIAL', 3, NULL, 'LLAMADA', 'CORTADO', 0, '2026-11-04', 'lkl', NULL),
(2, 2, '222', '2026-11-04', 'INDUSTRIAL', 5, NULL, 'LLAMADA', 'ACTIVO', 0, NULL, NULL, NULL),
(3, 3, '2345', '2026-11-04', 'RESIDENCIAL', 1, NULL, 'PUSH', 'ACTIVO', 324, NULL, NULL, NULL),
(4, 4, '2', '2026-11-04', 'RESIDENCIAL', 1, NULL, 'EMAIL', 'ACTIVO', 35, NULL, NULL, NULL);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `acuerdo_pago`
--
ALTER TABLE `acuerdo_pago`
  ADD PRIMARY KEY (`codigo`),
  ADD KEY `fk_ap_factura` (`factura_numero`);

--
-- Indices de la tabla `calendario_racionamiento`
--
ALTER TABLE `calendario_racionamiento`
  ADD PRIMARY KEY (`codigo`);

--
-- Indices de la tabla `comercial`
--
ALTER TABLE `comercial`
  ADD PRIMARY KEY (`usuario_id`);

--
-- Indices de la tabla `consumo`
--
ALTER TABLE `consumo`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo` (`codigo`),
  ADD KEY `fk_consumo_tarifa` (`tarifa_id`),
  ADD KEY `fk_consumo_usuario` (`usuario_id`);

--
-- Indices de la tabla `cuota_acuerdo`
--
ALTER TABLE `cuota_acuerdo`
  ADD PRIMARY KEY (`acuerdo_codigo`,`numero`);

--
-- Indices de la tabla `desincentivo`
--
ALTER TABLE `desincentivo`
  ADD PRIMARY KEY (`codigo`);

--
-- Indices de la tabla `excepcion`
--
ALTER TABLE `excepcion`
  ADD PRIMARY KEY (`codigo`);

--
-- Indices de la tabla `factura`
--
ALTER TABLE `factura`
  ADD PRIMARY KEY (`numero`),
  ADD KEY `fk_factura_usuario` (`usuario_id`),
  ADD KEY `fk_factura_desin` (`desincentivo_codigo`);

--
-- Indices de la tabla `factura_consumo`
--
ALTER TABLE `factura_consumo`
  ADD PRIMARY KEY (`factura_numero`,`consumo_id`),
  ADD KEY `fk_fc_consumo` (`consumo_id`);

--
-- Indices de la tabla `factura_sancion`
--
ALTER TABLE `factura_sancion`
  ADD PRIMARY KEY (`factura_numero`,`sancion_codigo`),
  ADD KEY `fk_fs_sancion` (`sancion_codigo`);

--
-- Indices de la tabla `industrial`
--
ALTER TABLE `industrial`
  ADD PRIMARY KEY (`usuario_id`);

--
-- Indices de la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD PRIMARY KEY (`codigo`),
  ADD KEY `fk_notif_usuario` (`destinatario_id`);

--
-- Indices de la tabla `oficial`
--
ALTER TABLE `oficial`
  ADD PRIMARY KEY (`usuario_id`);

--
-- Indices de la tabla `pago`
--
ALTER TABLE `pago`
  ADD PRIMARY KEY (`codigo`),
  ADD KEY `fk_pago_factura` (`factura_numero`);

--
-- Indices de la tabla `persona`
--
ALTER TABLE `persona`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `identificacion` (`identificacion`);

--
-- Indices de la tabla `reporte`
--
ALTER TABLE `reporte`
  ADD PRIMARY KEY (`codigo`);

--
-- Indices de la tabla `residencial`
--
ALTER TABLE `residencial`
  ADD PRIMARY KEY (`usuario_id`);

--
-- Indices de la tabla `sancion`
--
ALTER TABLE `sancion`
  ADD PRIMARY KEY (`codigo`),
  ADD KEY `fk_sancion_usuario` (`usuario_id`);

--
-- Indices de la tabla `sector`
--
ALTER TABLE `sector`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo` (`codigo`);

--
-- Indices de la tabla `tarifa`
--
ALTER TABLE `tarifa`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo` (`codigo`);

--
-- Indices de la tabla `turno_racionamiento`
--
ALTER TABLE `turno_racionamiento`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo` (`codigo`),
  ADD KEY `fk_turno_sector` (`sector_id`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `persona_id` (`persona_id`),
  ADD UNIQUE KEY `codigo_suscriptor` (`codigo_suscriptor`),
  ADD KEY `fk_usuario_sector` (`sector_id`),
  ADD KEY `fk_usuario_excepcion` (`excepcion_codigo`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `consumo`
--
ALTER TABLE `consumo`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `persona`
--
ALTER TABLE `persona`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `sector`
--
ALTER TABLE `sector`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tarifa`
--
ALTER TABLE `tarifa`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `turno_racionamiento`
--
ALTER TABLE `turno_racionamiento`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `acuerdo_pago`
--
ALTER TABLE `acuerdo_pago`
  ADD CONSTRAINT `fk_ap_factura` FOREIGN KEY (`factura_numero`) REFERENCES `factura` (`numero`) ON DELETE CASCADE;

--
-- Filtros para la tabla `comercial`
--
ALTER TABLE `comercial`
  ADD CONSTRAINT `fk_comercial_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `consumo`
--
ALTER TABLE `consumo`
  ADD CONSTRAINT `fk_consumo_tarifa` FOREIGN KEY (`tarifa_id`) REFERENCES `tarifa` (`id`),
  ADD CONSTRAINT `fk_consumo_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `cuota_acuerdo`
--
ALTER TABLE `cuota_acuerdo`
  ADD CONSTRAINT `fk_cuota_acuerdo` FOREIGN KEY (`acuerdo_codigo`) REFERENCES `acuerdo_pago` (`codigo`) ON DELETE CASCADE;

--
-- Filtros para la tabla `factura`
--
ALTER TABLE `factura`
  ADD CONSTRAINT `fk_factura_desin` FOREIGN KEY (`desincentivo_codigo`) REFERENCES `desincentivo` (`codigo`),
  ADD CONSTRAINT `fk_factura_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `factura_consumo`
--
ALTER TABLE `factura_consumo`
  ADD CONSTRAINT `fk_fc_consumo` FOREIGN KEY (`consumo_id`) REFERENCES `consumo` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_fc_factura` FOREIGN KEY (`factura_numero`) REFERENCES `factura` (`numero`) ON DELETE CASCADE;

--
-- Filtros para la tabla `factura_sancion`
--
ALTER TABLE `factura_sancion`
  ADD CONSTRAINT `fk_fs_factura` FOREIGN KEY (`factura_numero`) REFERENCES `factura` (`numero`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_fs_sancion` FOREIGN KEY (`sancion_codigo`) REFERENCES `sancion` (`codigo`) ON DELETE CASCADE;

--
-- Filtros para la tabla `industrial`
--
ALTER TABLE `industrial`
  ADD CONSTRAINT `fk_industrial_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD CONSTRAINT `fk_notif_usuario` FOREIGN KEY (`destinatario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `oficial`
--
ALTER TABLE `oficial`
  ADD CONSTRAINT `fk_oficial_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `pago`
--
ALTER TABLE `pago`
  ADD CONSTRAINT `fk_pago_factura` FOREIGN KEY (`factura_numero`) REFERENCES `factura` (`numero`) ON DELETE CASCADE;

--
-- Filtros para la tabla `residencial`
--
ALTER TABLE `residencial`
  ADD CONSTRAINT `fk_residencial_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `sancion`
--
ALTER TABLE `sancion`
  ADD CONSTRAINT `fk_sancion_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `turno_racionamiento`
--
ALTER TABLE `turno_racionamiento`
  ADD CONSTRAINT `fk_turno_sector` FOREIGN KEY (`sector_id`) REFERENCES `sector` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `fk_usuario_excepcion` FOREIGN KEY (`excepcion_codigo`) REFERENCES `excepcion` (`codigo`),
  ADD CONSTRAINT `fk_usuario_persona` FOREIGN KEY (`persona_id`) REFERENCES `persona` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_usuario_sector` FOREIGN KEY (`sector_id`) REFERENCES `sector` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
