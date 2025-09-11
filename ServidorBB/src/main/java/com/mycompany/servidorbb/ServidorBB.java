
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
