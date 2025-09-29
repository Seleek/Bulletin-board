package com.mycompany.clientebb;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ClienteBB {


    public static void main(String[] args) throws IOException{
        
        
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
        Object[] sign = {"Iniciar sesion", "Registrarse"};
        String usuario;
        String contra = "";

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
                String respuesta = "";
                while (true) { 
                    usuario = JOptionPane.showInputDialog("Ingrese su nombre de usuario");
                    if(usuario == null) break;
                    usuario = usuario.trim();
                    if(usuario.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El nombre de usuario no puede estar vacío.");
                        continue;
                    }
                    contra = JOptionPane.showInputDialog("Ingrese su contraseña");
                    if(contra == null) break;
                    contra = contra.trim();
                    if(contra.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "La contraseña no puede estar vacía.");
                        continue;
                    }
                    break;
                }
                if(usuario == null || contra == null ){
                    
                    continue;
                }
                if(usuario.isEmpty() || contra.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Inicio de sesión cancelado o datos inválidos.");
                    continue;
                }
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
                    javax.swing.JButton btnCerrarSesion = new javax.swing.JButton("Cerrar sesión");
                    JButton btnBloquearUsuarios = new JButton("Bloquear usuarios");
                    frame.add(btnBloquearUsuarios);

                    btnCerrarSesion.addActionListener(e -> {
                        frame.dispose();
                    });

                    btnVerMensajes.addActionListener(e -> {
                        
                        javax.swing.JFrame frameMensajes = new javax.swing.JFrame("Mensajes de " + usuarioRemitente);
                        frameMensajes.setSize(400,300);
                        frameMensajes.setLayout(new java.awt.BorderLayout());
                        frameMensajes.setLayout(new BorderLayout());

                        DefaultListModel<String> modeloMensajes = new DefaultListModel<>();
                        JList<String> listaMensajes = new JList<>(modeloMensajes);
                        JScrollPane scrollPane = new JScrollPane(listaMensajes);
                        JButton btnEliminar = new JButton("Eliminar mensaje");

                        JTextArea areaMensajes = new JTextArea();
                        areaMensajes.setEditable(false);
                        areaMensajes.setLineWrap(true);
                        areaMensajes.setWrapStyleWord(true);
                        JScrollPane scrollArea = new JScrollPane(areaMensajes);
                        
                        listaMensajes.addListSelectionListener(e2 -> {
                            int indice = listaMensajes.getSelectedIndex();
                            if (indice != -1) {
                                areaMensajes.setText(modeloMensajes.getElementAt(indice));
                            }
                        });


                        try (
                            Socket socket = new Socket("localhost", 8080);
                            PrintWriter escritorMensajes = new PrintWriter(socket.getOutputStream(), true);
                            BufferedReader lectorMensajes = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    ){
                            escritorMensajes.println("OBTENER_MENSAJES:" + usuarioRemitente);
                            String linea;
                            StringBuilder bloque = new StringBuilder();
                            while ((linea = lectorMensajes.readLine()) != null) {
                                bloque.append(linea).append("\n");
                                if (linea.equals("-----")) {
                                    modeloMensajes.addElement(bloque.toString());
                                    bloque.setLength(0);
                                }
                            }
                            if(bloque.length() > 0){
                                modeloMensajes.addElement(bloque.toString());
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
                                        areaMensajes.setText("");
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

                        JButton btnVolver = new JButton("Volver");
                        btnVolver.addActionListener(ev -> frameMensajes.dispose());
                        javax.swing.JPanel panelBotones = new javax.swing.JPanel(new java.awt.FlowLayout());
                        panelBotones.add(btnEliminar);
                        panelBotones.add(btnVolver);

                        frameMensajes.add(scrollPane, java.awt.BorderLayout.CENTER);
                        frameMensajes.add(scrollArea, java.awt.BorderLayout.EAST); 
                        frameMensajes.add(panelBotones, java.awt.BorderLayout.SOUTH);
                        frameMensajes.setLocationRelativeTo(null);
                        frameMensajes.setVisible(true);
                    });

                    btnBloquearUsuarios.addActionListener(e -> {
                        javax.swing.JFrame frameBloquear = new javax.swing.JFrame("Bloquear usuarios");
                        frameBloquear.setSize(400,300);
                        frameBloquear.setLayout(new java.awt.BorderLayout());

                        DefaultListModel<String> modeloUsuarios = new DefaultListModel<>();
                        JList<String> listaUsuarios = new JList<>(modeloUsuarios);
                        listaUsuarios.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                        JScrollPane scrollPane = new JScrollPane(listaUsuarios);

                        java.util.Set<String> bloqueados = new java.util.HashSet<>();
                        java.io.File archivoBloqueados = new java.io.File("bloqueados_" + usuarioRemitente + ".txt");
                        if (archivoBloqueados.exists()) {
                            try(java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(archivoBloqueados))) {
                                String linea;
                                while ((linea = br.readLine()) != null) {
                                    bloqueados.add(linea.trim());
                                }
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(frameBloquear, "Error al leer el archivo de bloqueados: " + ex.getMessage());
                            }
                        }

                        listaUsuarios.setCellRenderer(new javax.swing.DefaultListCellRenderer(){
                            @Override
                            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                                String usuario = value.toString();
                                if(bloqueados.contains(usuario)){
                                    c.setBackground(java.awt.Color.RED);
                                }else{
                                    c.setBackground(java.awt.Color.WHITE);
                                }
                                return c;
                            }
                        });

                        try (
                            Socket socketUsuarios = new Socket("localhost", 8080);
                            PrintWriter escritorUsuarios = new PrintWriter(socketUsuarios.getOutputStream(), true);
                            BufferedReader lectorUsuarios = new BufferedReader(new InputStreamReader(socketUsuarios.getInputStream()));
                    ){
                            escritorUsuarios.println("LISTA_USUARIOS:" + usuarioRemitente);
                            String linea;
                            while ((linea = lectorUsuarios.readLine()) != null) {
                                if(linea.equals("__END__")) break;
                                    modeloUsuarios.addElement(linea);                               
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(frameBloquear, "Error al obtener la lista de usuarios: " + ex.getMessage());
                        }


                        for(int i=0; i<modeloUsuarios.size(); i++){
                            if(bloqueados.contains(modeloUsuarios.getElementAt(i))){
                                listaUsuarios.addSelectionInterval(i, i);
                            }
                        }
                        JButton btnGuardar = new JButton("Guardar usuarios bloqueados");
                        btnGuardar.addActionListener(ev ->{
                            java.util.List<String> seleccionados = listaUsuarios.getSelectedValuesList();
                            bloqueados.clear();
                            bloqueados.addAll(seleccionados);
                            try(java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(archivoBloqueados, false))) {
                                for(String u : bloqueados){
                                    pw.println(u);
                                }
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(frameBloquear, "Error al guardar el archivo de bloqueados: " + ex.getMessage());
                                return;
                            }
                            JOptionPane.showMessageDialog(frameBloquear, "Usuarios bloqueados actualizados.");
                            frameBloquear.dispose();

                        });
                        JButton btnVolver = new JButton("Volver");
                        btnVolver.addActionListener(ev -> frameBloquear.dispose());
                        JPanel panelBotones = new javax.swing.JPanel(new java.awt.FlowLayout());
                        panelBotones.add(btnGuardar);
                        panelBotones.add(btnVolver);
                        frameBloquear.add(new JScrollPane(listaUsuarios), BorderLayout.CENTER);
                        frameBloquear.add(panelBotones, BorderLayout.SOUTH);
                        frameBloquear.setLocationRelativeTo(null);
                        frameBloquear.setVisible(true);
                    });

                    btnEscribirMensaje.addActionListener(evt -> {
                        javax.swing.JFrame escribirFrame = new javax.swing.JFrame("Enviar mensaje");
                        escribirFrame.setSize(400,300);
                        escribirFrame.setLayout(new java.awt.BorderLayout());
                        javax.swing.JPanel panelSuperior = new javax.swing.JPanel(new java.awt.FlowLayout());
                        javax.swing.JLabel lblDestinatario = new javax.swing.JLabel("Para:");
                        DefaultComboBoxModel<String> modeloUsuariosCombo = new DefaultComboBoxModel<>();
                        try (
                            Socket socketUsuarios = new Socket("localhost", 8080);
                            PrintWriter escritorUsuarios = new PrintWriter(socketUsuarios.getOutputStream(), true);
                            BufferedReader lectorUsuarios = new BufferedReader(new InputStreamReader(socketUsuarios.getInputStream()));
                    ){
                            escritorUsuarios.println("LISTA_USUARIOS");
                            String linea;
                            while ((linea = lectorUsuarios.readLine()) != null) {
                                if(linea.equals("__END__")) break;
                                if (!linea.equals(usuarioRemitente)) {
                                    modeloUsuariosCombo.addElement(linea);
                                }
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(escribirFrame, "Error al obtener la lista de usuarios: " + ex.getMessage());
                        }
                        javax.swing.JComboBox<String> comboDestinatarios = new javax.swing.JComboBox<>(modeloUsuariosCombo);
                        comboDestinatarios.setEditable(true);
                        panelSuperior.add(lblDestinatario);
                        panelSuperior.add(comboDestinatarios);

                        javax.swing.JTextArea areaMensaje = new javax.swing.JTextArea();
                        areaMensaje.setLineWrap(true);
                        areaMensaje.setWrapStyleWord(true);
                        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(areaMensaje);

                        javax.swing.JButton btnEnviar = new javax.swing.JButton("Enviar");
                        btnEnviar.addActionListener(ev -> {
                            String destinatario = (String) comboDestinatarios.getSelectedItem();
                            if(destinatario == null) destinatario = "";
                            destinatario = destinatario.trim();
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
                        javax.swing.JButton btnvolver = new javax.swing.JButton("Volver");
                        btnvolver.addActionListener(ev -> escribirFrame.dispose());

                        javax.swing.JPanel panelBotones = new javax.swing.JPanel(new java.awt.FlowLayout());
                        panelBotones.add(btnEnviar);
                        panelBotones.add(btnvolver);

                        escribirFrame.add(panelBotones, java.awt.BorderLayout.SOUTH);
                        escribirFrame.add(panelSuperior, java.awt.BorderLayout.NORTH);
                        escribirFrame.add(scroll, java.awt.BorderLayout.CENTER);
                        escribirFrame.setLocationRelativeTo(null);
                        escribirFrame.setVisible(true);
                    });

                    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                    frame.setSize(300, 100);
                    frame.setLayout(new java.awt.FlowLayout());
                    frame.add(btnVerMensajes);
                    frame.add(btnEscribirMensaje);
                    frame.add(btnCerrarSesion);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);

                    while(frame.isVisible()){
                        try {Thread.sleep(100);} catch (InterruptedException ex) {}
                    }
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
