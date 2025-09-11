package com.mycompany.clientebb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ClienteBB {

    public static void main(String[] args) throws IOException{
        
        
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
        Object[] sign = {"Iniciar sesion", "Registrarse"};
        String usuario;
        String contra;

        Object selecciona = JOptionPane.showInputDialog(
        null, "Selecciona una opcion: ", "Menu de opciones", 
                JOptionPane.QUESTION_MESSAGE, null, sign, sign[0]);
        if(selecciona != null){
            if(selecciona.equals("Registrarse")){
                String respuesta = "";
                while(respuesta == null || !respuesta.startsWith("Usuario registrado:")){
                    usuario = JOptionPane.showInputDialog("Ingrese su nombre de usuario");
                    contra = JOptionPane.showInputDialog("Ingrese su contraseña");
                    
                    // Crear nueva conexión para cada intento
                    try (
                        Socket salida = new Socket("localhost",8080);
                        PrintWriter escritor = new PrintWriter(salida.getOutputStream(), true);
                        BufferedReader lector = new BufferedReader(new InputStreamReader(salida.getInputStream()))
                    ) {
                        escritor.println("REGISTRO:"+usuario+":"+contra);
                        respuesta = lector.readLine();
                        System.out.println(respuesta);
                        if ("El usuario ya existe".equalsIgnoreCase(respuesta)) {
                            JOptionPane.showMessageDialog(null, "Ese usuario ya existe, intenta con otro.");
                        }
                    }
                }
 
            }else{
                System.out.println("El usuario cancelo eleccion");
        }
        /*String cadena = teclado.readLine();
        String mensaje;
        while (!cadena.equalsIgnoreCase("FIN")){
            escritor.println(cadena);
            mensaje = lector.readLine();
            System.out.println(mensaje);
            cadena = teclado.readLine();
        }*/
        //salida.close();
        
    }
}
}
