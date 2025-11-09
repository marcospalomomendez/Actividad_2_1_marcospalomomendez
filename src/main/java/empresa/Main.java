package empresa;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Cargar configuración desde db.properties
        Properties props = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("No se encontró el archivo db.properties");
                return;
            }
            props.load(input);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 2. Obtener datos de conexión
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        // 3. Probar conexión
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            System.out.println("Conexión establecida con éxito a la base de datos.");

            // Mostrar metadatos
            DatabaseMetaData meta = con.getMetaData();
            System.out.println("🔹 Driver: " + meta.getDriverName());
            System.out.println("🔹 Versión del driver: " + meta.getDriverVersion());
            System.out.println("🔹 Base de datos: " + meta.getDatabaseProductName());
            System.out.println("🔹 Versión BD: " + meta.getDatabaseProductVersion());
            System.out.println("🔹 Usuario conectado: " + meta.getUserName());
            System.out.println("🔹 URL de conexión: " + meta.getURL());


            // Scanner para entradas de usuario
            Scanner sc = new Scanner(System.in);

            // 4. Listar empleados con Statement
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM empleados");

            System.out.println("\n=== EMPLEADOS ===");
            while (rs.next()) {
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                        rs.getInt("id"), rs.getString("nombre"), rs.getDouble("salario"));
            }

            // 5. Buscar empleado por ID con PreparedStatement
            System.out.print("\nIngrese el ID del empleado a buscar: ");
            int id = sc.nextInt();
            String sql = "SELECT * FROM empleados WHERE id = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, id);                 // asignar parámetro
                try (ResultSet rs2 = pst.executeQuery()) {   // ejecutar la consulta y obtener nuevo ResultSet
                    if (rs2.next()) {
                        System.out.printf("Empleado encontrado: ID: %d | Nombre: %s | Salario: %.2f €%n",
                                rs2.getInt("id"),
                                rs2.getString("nombre"),
                                rs2.getDouble("salario"));
                    } else {
                        System.out.println("Empleado no encontrado.");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error en la consulta: " + e.getMessage());
            }
            //6. Llamar a un procedimiento obtener_empleado con CallableStatement

            // 6. Llamar al procedure obtener_empleado con CallableStatement
            System.out.print("\nIngrese el ID del empleado para el procedure: ");
            int empleadoId = sc.nextInt();
            String llamadaProcedure = "{CALL obtener_empleado(?)}";
            try (CallableStatement cst = con.prepareCall(llamadaProcedure)) {
                cst.setInt(1, empleadoId);
               try(ResultSet rs3 = cst.executeQuery()){
                   if (rs3.next()) {
                       System.out.printf("Empleado encontrado: ID: %d | Nombre: %s | Salario: %.2f €%n",
                               rs3.getInt("id"),
                               rs3.getString("nombre"),
                               rs3.getDouble("salario"));
                   }else{
                          System.out.println("Empleado no encontrado.");
                   }
               }
            }catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    } catch (SQLException e) {
        System.err.println("Error al conectar a la base de datos: " + e.getMessage());
    }
}
}

