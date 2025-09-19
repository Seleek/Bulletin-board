
package com.mycompany.servidorbb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorBB {

    public static void main(String[] args) throws IOException{
         try {
      File archivo = new File("usuarios.txt");

      if (archivo.createNewFile()) {
        System.out.println("El archivo fue creado");
      } else {
        System.out.println("El archivo ya existe");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
        ServerSocket socketEspecial = new ServerSocket(8080);

        while (true){
        Socket cliente = socketEspecial.accept();

        PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
        BufferedReader lectorSocket = new BufferedReader(new InputStreamReader(
                cliente.getInputStream()));
        String mensaje = lectorSocket.readLine();

        //REVISAR EL REGISTRO
        if(mensaje != null && mensaje.startsWith("REGISTRO:")){
            String[] partes = mensaje.split(":");
            String usuario = partes[1];
            String contra = partes[2];
            boolean existe = false;
            try (BufferedReader br = new BufferedReader(new FileReader("usuarios.txt"))){
                String renglon;
                while((renglon = br.readLine()) != null){
                    String[] datos = renglon.split(",");
                    if(datos[0].equals(usuario)){
                        existe = true;
                        escritor.println("El usuario ya existe");
                        break;
                    }
                }
            }

            if(!existe){
            try(FileWriter fw = new FileWriter("usuarios.txt", true)){
                fw.write(usuario + "," + contra + "\n");
                escritor.println("Usuario registrado: " + usuario);
            }

            File carpetaUsuarios = new File("usuarios");
            if(!carpetaUsuarios.exists()){
                carpetaUsuarios.mkdir();
            }
            File archivoUsuario = new File (carpetaUsuarios, usuario + ".txt");
            if(archivoUsuario.createNewFile()){
                System.out.println("Archivo creado para "+ usuario);
            } else{
                System.out.println("El archivo ya existe para "+ usuario);
            }
            }
        }

        //REVISAR EL LOGIN
        else if(mensaje != null && mensaje.startsWith("LOGIN:")){
            String[] partes = mensaje.split(":");
            String usuario = partes[1];
            String contra = partes[2];
            boolean loginOk = false;
        try (BufferedReader br = new BufferedReader(new FileReader("usuarios.txt"))){
                String renglon;
            while((renglon = br.readLine()) != null){
                String[] datos = renglon.split(",");
            if(datos[0].equals(usuario) && datos[1].equals(contra)){
                loginOk = true;
                break;
            }
        }
    }
        if(loginOk){
            escritor.println("LOGIN_OK");
    } else {
            escritor.println("LOGIN_FAIL");
    }
}   else if(mensaje != null && mensaje.startsWith("MENSAJE:")){
    String[] partes = mensaje.split(":", 4);
    String remitente = partes[1];
    String destinatario = partes[2];
    String contenido = partes[3];

    File archivoDestinatario = new File("usuarios",destinatario + ".txt");
    if(!archivoDestinatario.exists()){
        archivoDestinatario.mkdir();
    }
    try(FileWriter fw = new FileWriter(archivoDestinatario, true)){
        fw.write("De: " + remitente + "\n");
        fw.write("Mensaje: " + contenido + "\n");
        fw.write("-----\n");
        escritor.println("Mensaje enviado correctamente a " + destinatario);
    } catch (IOException e){
        escritor.println("Error al enviar el mensaje a " + destinatario);
    
}

}else if(mensaje != null && mensaje.startsWith("OBTENER_MENSAJES:")){
    String[] partes = mensaje.split(":");
    String usuario = partes[1];

    File archivoUsuario = new File("usuarios", usuario + ".txt");
    if(!archivoUsuario.exists()){
        try(BufferedReader br = new BufferedReader(new FileReader(archivoUsuario))){
            String linea;
            while((linea = br.readLine()) != null){
                escritor.println(linea);
            }
        } catch (IOException e){
            escritor.println("Error al obtener mensajes para " + usuario);
        }
    } 
}else if(mensaje != null && mensaje.startsWith("ELIMINAR_MENSAJES:")){
    String[] partes = mensaje.split(":");
    String usuario = partes[1];
    int index;
    try {
        index = Integer.parseInt(partes[2]);
    } catch (NumberFormatException e) {
        escritor.println("Índice inválido para eliminar mensaje.");
        return;
    }

    File archivoUsuario = new File("usuarios", usuario + ".txt");
    if(!archivoUsuario.exists()){
        escritor.println("No hay mensajes para eliminar para " + usuario);
        return;
    } else{
        try {
            BufferedReader br = new BufferedReader (new FileReader(archivoUsuario));
            java.util.List<String> lineas = new java.util.ArrayList<>();
            String linea;
            while((linea = br.readLine()) != null){
                lineas.add(linea);
            }
            br.close();

            int inicio = index * 3;
            if(inicio >=0 && inicio + 2 < lineas.size()){
                lineas.remove(inicio); // "De: ..."
                lineas.remove(inicio); // "Mensaje: ..."
                lineas.remove(inicio); // "-----"
                
                FileWriter fw = new FileWriter(archivoUsuario, false);
                for(String l : lineas){
                    fw.write(l + "\n");
                }
                fw.close();
                escritor.println("Mensaje eliminado correctamente.");
            } else{
                escritor.println("Índice fuera de rango para eliminar mensaje.");
            }
        } catch (Exception e) {
            escritor.println("Error al eliminar mensaje para " + usuario);
        }
    }
}
        cliente.close();
        }
    }
}
