
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
    File archivoMsg = new File(archivoDestinatario, destinatario + ".txt");
    try(FileWriter fw = new FileWriter(archivoMsg, true)){
        fw.write("De: " + remitente + "\n");
        fw.write("Mensaje: " + contenido + "\n");
        fw.write("-----\n");
        escritor.println("Mensaje enviado correctamente a " + destinatario);
    } catch (IOException e){
        escritor.println("Error al enviar el mensaje a " + destinatario);
    }

}
     /*   BufferedReader teclado = new BufferedReader( new InputStreamReader(System.in));
        String entrada;
        String mensaje;
        while((entrada = lectorSocket.readLine())!= null){
            System.out.println(entrada.toUpperCase());
            mensaje = teclado.readLine();
            escritor.println(mensaje);
        }*/
        cliente.close();
        }
    }
}
