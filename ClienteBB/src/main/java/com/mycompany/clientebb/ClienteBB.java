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

    while (true) {
        Object selecciona = JOptionPane.showInputDialog(
        null, "Selecciona una opcion: ", "Menu de opciones", 
                JOptionPane.QUESTION_MESSAGE, null, sign, sign[0]);
        if(selecciona != null){

            //REGISTRO DE USUARIO
            if(selecciona.equals("Registrarse")){
                String respuesta = "";
                while(respuesta == null || !respuesta.startsWith("Usuario registrado:")){
                    usuario = JOptionPane.showInputDialog("Ingrese su nombre de usuario");
                    contra = JOptionPane.showInputDialog("Ingrese su contraseña");
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
                JOptionPane.showMessageDialog(null, "Registro exitoso!");

                //INICIO DE SESION
            } else if(selecciona.equals("Iniciar sesion")){
                usuario = JOptionPane.showInputDialog("Ingrese su nombre de usuario");
                contra = JOptionPane.showInputDialog("Ingrese su contraseña");
                String respuesta = "";
   
                try (
                    Socket salida = new Socket("localhost",8080);
                    PrintWriter escritor = new PrintWriter(salida.getOutputStream(), true);
                    BufferedReader lector = new BufferedReader(new InputStreamReader(salida.getInputStream()))
            ) {
                    escritor.println("LOGIN:"+usuario+":"+contra);
                    respuesta = lector.readLine();
                    if ("LOGIN_OK".equals(respuesta)) {
                    JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso!");

                    //CREAR INTERFAZ DE USUARIO, PROFE TENGO SUEÑO
                    javax.swing.JFrame frame = new javax.swing.JFrame("Menu principal");
                    javax.swing.JButton btnVerMensajes = new javax.swing.JButton("Ver mensajes");
                    javax.swing.JButton btnEscribirMensaje = new javax.swing.JButton("Enviar mensaje");

                    btnVerMensajes.addActionListener(e -> {
                        JOptionPane.showMessageDialog(frame, "Ahorita hago este frame de mensajes.");
                    });

                    btnEscribirMensaje.addActionListener(e -> {
                        javax.swing.JFrame escribirFrame = new javax.swing.JFrame("Enviar mensaje");
                        escribirFrame.setSize(400,300);
                        escribirFrame.setLayout(new java.awt.BorderLayout());
                        javax.swing.JPanel panelSuperior = new javax.swing.JPanel(new java.awt.FlowLayout());
                        javax.swing.JLabel lblDestinatario = new javax.swing.JLabel("Para:");
                        javax.swing.JTextField txtDestinatario = new javax.swing.JTextField(20);
                        panelSuperior.add(lblDestinatario);
                        panelSuperior.add(txtDestinatario);

                        javax.swing.JTextArea areaMensaje = new javax.swing.JTextArea();
                        areaMensaje.setLineWrap(true);
                        areaMensaje.setWrapStyleWord(true);
                        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(areaMensaje);

                        javax.swing.JButton btnEnviar = new javax.swing.JButton("Enviar");

                        btnEnviar.addActionListener(ev -> {
                            String destinatario = txtDestinatario.getText().trim();
                            String mensaje = areaMensaje.getText().trim();
                            if(destinatario.isEmpty() || mensaje.isEmpty()){
                                JOptionPane.showMessageDialog(escribirFrame, "El destinatario y el mensaje no pueden estar vacíos.");
                                return;
                            } else {
                                // Aquí se enviaría el mensaje al servidor
                                JOptionPane.showMessageDialog(escribirFrame, "Mensaje enviado a " + destinatario);
                                escribirFrame.dispose();
                            }
                        });

                        escribirFrame.add(panelSuperior, java.awt.BorderLayout.NORTH);
                        escribirFrame.add(scroll, java.awt.BorderLayout.CENTER);
                        escribirFrame.add(btnEnviar, java.awt.BorderLayout.SOUTH);
                        escribirFrame.setLocationRelativeTo(null);
                        escribirFrame.setVisible(true);
                    });

                    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                    frame.setSize(300, 100);
                    frame.setLayout(new java.awt.FlowLayout());
                    frame.add(btnVerMensajes);
                    frame.add(btnEscribirMensaje);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);

                    break;
                } else if ("LOGIN_FAIL".equals(respuesta)) {
            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.");
        }
    }
            }
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
