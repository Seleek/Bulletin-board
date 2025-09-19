package com.mycompany.clientebb;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

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
                    final String usuarioRemitente = usuario;

                    //CREAR INTERFAZ DE USUARIO, PROFE TENGO SUEÑO
                    javax.swing.JFrame frame = new javax.swing.JFrame("Menu principal");
                    javax.swing.JButton btnVerMensajes = new javax.swing.JButton("Ver mensajes");
                    javax.swing.JButton btnEscribirMensaje = new javax.swing.JButton("Enviar mensaje");

                    btnVerMensajes.addActionListener(e -> {
                        javax.swing.JFrame frameMensajes = new javax.swing.JFrame("Mensajes de " + usuarioRemitente);
                        frameMensajes.setSize(400,300);
                        frameMensajes.setLayout(new java.awt.BorderLayout());
                        frameMensajes.setLayout(new BorderLayout());

                        DefaultListModel<String> modeloMensajes = new DefaultListModel<>();
                        JList<String> listaMensajes = new JList<>(modeloMensajes);
                        JScrollPane scrollPane = new JScrollPane(listaMensajes);
                        JButton btnEliminar = new JButton("Eliminar mensaje");
                        
                        try (
                            Socket socket = new Socket("localhost", 8080);
                            PrintWriter escritorMensajes = new PrintWriter(socket.getOutputStream(), true);
                            BufferedReader lectorMensajes = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    ){
                            escritorMensajes.println("OBTENER_MENSAJES:" + usuarioRemitente);
                            String linea;
                            while ((linea = lectorMensajes.readLine()) != null) {
                                modeloMensajes.addElement(linea);
                            }
                           
                    }  catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameMensajes, "Error al obtener mensajes: " + ex.getMessage());
                            return;
                        }

                        btnEliminar.addActionListener(ev -> {
                            int indiceSeleccionado = listaMensajes.getSelectedIndex();
                            if (indiceSeleccionado != -1) {
                                int confirmacion = JOptionPane.showConfirmDialog(frameMensajes, "¿Estás seguro de eliminar este mensaje?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                                if (confirmacion == JOptionPane.YES_OPTION) {
                                try (
                                    Socket socketEliminar = new Socket("localhost", 8080);
                                    PrintWriter escritorEliminar = new PrintWriter(socketEliminar.getOutputStream(), true);
                                    BufferedReader lectorEliminar = new BufferedReader(new InputStreamReader(socketEliminar.getInputStream()));
                                ){
                                    escritorEliminar.println("ELIMINAR_MENSAJE:" + usuarioRemitente + ":" + indiceSeleccionado);
                                    String respuestaEliminar = lectorEliminar.readLine();
                                    if ("MENSAJE_ELIMINADO".equals(respuestaEliminar)) {
                                        modeloMensajes.remove(indiceSeleccionado);
                                        JOptionPane.showMessageDialog(frameMensajes, "Mensaje eliminado.");
                                    } else {
                                        JOptionPane.showMessageDialog(frameMensajes, "Error al eliminar el mensaje.");
                                    }
                                } catch (IOException ex) {
                                    JOptionPane.showMessageDialog(frameMensajes, "Error al eliminar el mensaje: " + ex.getMessage());
                                }
                                }
                            } else {
                                JOptionPane.showMessageDialog(frameMensajes, "Seleccione un mensaje para eliminar.");
                            }
                        });

                        frameMensajes.add(scrollPane, java.awt.BorderLayout.CENTER);
                        frameMensajes.add(btnEliminar, java.awt.BorderLayout.SOUTH);
                        frameMensajes.setLocationRelativeTo(null);
                        frameMensajes.setVisible(true);
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
                                try(
                                    Socket salidaMsg = new Socket("localhost", 8080);
                                    PrintWriter escritorMsg = new PrintWriter(salidaMsg.getOutputStream(), true);
                                    BufferedReader lectorMsg = new BufferedReader(new InputStreamReader(salidaMsg.getInputStream()));
                                 ) {
                                    escritorMsg.println("MENSAJE:" + usuarioRemitente + ":" + destinatario + ":" + mensaje);
                                    String respuestaMsg = lectorMsg.readLine();
                                    JOptionPane.showMessageDialog(escribirFrame, respuestaMsg);
                                    escribirFrame.dispose();
                                } catch (IOException ex) {
                                    JOptionPane.showMessageDialog(escribirFrame, "Error al enviar el mensaje: " + ex.getMessage());
                                 }                                
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
