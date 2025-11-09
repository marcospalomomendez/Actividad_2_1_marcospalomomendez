DROP DATABASE IF EXISTS empresa;
CREATE DATABASE empresa;
USE empresa;

CREATE TABLE empleados (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(50),
                           salario DOUBLE
);

INSERT INTO empleados (nombre, salario)
VALUES ('Ana', 25000), ('Luis', 28000), ('Marta', 32000);

-- Crea Procedimiento

DELIMITER //
CREATE PROCEDURE obtener_empleado(IN empleado_id INT)
BEGIN
SELECT * FROM empleados WHERE empleados.id = empleado_id;
END
//
DELIMITER ;