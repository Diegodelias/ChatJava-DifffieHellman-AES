/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProgrmaServidor;

import ProgramaCliente.PrincipalCliente;
import static ProgrmaServidor.PrincipalServidor.ventanaServidor;
import java.security.Key;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Diego
 */
public class DiffieServidor {

    public  BigInteger moduloPrimo;

  
    
    
    

    public  BigInteger getModuloPrimo() {
        return moduloPrimo;
    }

    public void setModuloPrimo(BigInteger moduloPrimo) {
        this.moduloPrimo = moduloPrimo;
    }
    

public Key  generarKeyServidor() throws Exception {
//public static void main(String argv[]) throws Exception {
//Diffie-CHellman Algorithm

            
             ServerSocket welcomeSocket = new ServerSocket(6789);
             Socket connectionSocket = welcomeSocket.accept();
              
            DataOutputStream toClient = new DataOutputStream(connectionSocket.getOutputStream());
            DataInputStream fromClient = new DataInputStream(connectionSocket.getInputStream());
                
            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(1024, new SecureRandom());
            AlgorithmParameters params = paramGen.generateParameters();
            DHParameterSpec dhSpec = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);
            
            Random randomGenerator = new Random();
            BigInteger a = new BigInteger(1024, randomGenerator); // secret key a (private) (on server)
            BigInteger p = dhSpec.getP(); // prime number (public) (generated on server)
            ventanaServidor.mostrarMensaje("------------------------------------------------------------------------------------------------------------------------------------------" );
            ventanaServidor.mostrarMensaje("primo (p) " + p.toString());
            
            ventanaServidor.mostrarMensaje("------------------------------------------------------------------------------------------------------------------------------------------" );
           
            
           
            BigInteger g = dhSpec.getG(); // primer number generator (public) (generated on server)
         
           
              ventanaServidor.mostrarMensaje("numero aleatorio Server(a)" + a.toString());
            BigInteger A = g.modPow(a, p); // calculated public server key (A=g^a(modp))
            
            ventanaServidor.mostrarMensaje("------------------------------------------------------------------------------------------------------------------------------------------" );
           
            
            ventanaServidor.mostrarMensaje("Clave publica “A”" + A.toString());
            
          
            //MANDA A CLIENTE
              toClient.writeUTF(p.toString());
              toClient.writeUTF(g.toString());
              toClient.writeUTF(A.toString());
            
            //rECIBE B calculada
            BigInteger B = new BigInteger(fromClient.readUTF());
            
            //calcula secret key
            BigInteger encryptionKeyServer = B.modPow(a, p);

           // System.out.println("key secreta Diffie Hellman Server : " + encryptionKeyServer);
            
            
            Key keyAes = generateKey(encryptionKeyServer.toByteArray());
            
            //PrincipalCliente.ventanaCliente.mostrarMensaje("hola gatoss");
          
            ventanaServidor.mostrarMensaje("------------------------------------------------------------------------------------------------------------------------------------------" );
             
           String keyString = Base64.getEncoder().encodeToString(keyAes.getEncoded());
                    
            ventanaServidor.mostrarMensaje("Secret  Key  " + keyString);
                    
            
            ventanaServidor.mostrarMensaje("------------------------------------------------------------------------------------------------------------------------------------------" );
            ventanaServidor.mostrarMensaje("Bienvenido al Chat! ya podés empezar a escribir...  " );
               
          //  return encryptionKeyServer;
           return keyAes;
            
            
           
            
                }
    

 private static Key generateKey(byte[] sharedKey)
    {
        // AES supports 128 bit keys. So, just take first 16 bits of DH generated key.
        byte[] byteKey = new byte[16];
        for(int i = 0; i < 16; i++) {
            byteKey[i] = sharedKey[i];
        }

        // convert given key to AES format
        try {
        
           SecretKey llave = new SecretKeySpec(byteKey, "AES");

            return llave;
        } catch(Exception e) {
            System.err.println("Error while generating key: " + e);
        }

        return null;
    }
 
 
 
    
  
}
