/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProgramaCliente;

import static ProgramaCliente.PrincipalCliente.ventanaCliente;
import static ProgrmaServidor.PrincipalServidor.ventanaServidor;
import java.security.Key;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Random;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author Diego
 */
public class DiffieCliente {
    
    

public Key generarKeyCliente() throws Exception {

//Diffie-CHellman Algorithm


             
                    BufferedReader inFromUser = new 
                    BufferedReader(new InputStreamReader(System.in));
                    Socket clientSocket = new Socket("localhost", 6789);
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                   // BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    DataInputStream fromServer = new DataInputStream(clientSocket.getInputStream());
                    
                    
                    BigInteger p = new BigInteger(fromServer.readUTF());
                    // receive prime number generator from server
                  
                    BigInteger g = new BigInteger(fromServer.readUTF());
                    // receive A from server
                    BigInteger A = new BigInteger(fromServer.readUTF());
                     ventanaCliente.mostrarMensaje("------------------------------------------------------------------------------------------------------------------------------------------" );
                    ventanaCliente.mostrarMensaje("primo (p) " + p.toString());
                     ventanaCliente.mostrarMensaje("------------------------------------------------------------------------------------------------------------------------------------------" );
                    ventanaCliente.mostrarMensaje("generador(g) " + g.toString());
                    ventanaCliente.mostrarMensaje("------------------------------------------------------------------------------------------------------------------------------------------" );
                  
                    // generate secret b
                    Random randomGenerator = new Random();
                    BigInteger b = new BigInteger(1024, randomGenerator); // secret key b (private) (on client)
                    
                    
                    ventanaCliente.mostrarMensaje("numero aletorio cliente(b) " + g.toString());
                    
                     ventanaCliente.mostrarMensaje("------------------------------------------------------------------------------------------------------------------------------------------" );
                    
                    // calculate public B
                    BigInteger B = g.modPow(b, p); // calculated public client key (B=g^b(modp))
                    
                    ventanaCliente.mostrarMensaje("Clave publica B  " + B.toString());
                    
                   ventanaCliente.mostrarMensaje("------------------------------------------------------------------------------------------------------------------------------------------" );
                    

                    // send B to server
                   outToServer.writeUTF(B.toString());

                    // calculate secret key
                    BigInteger decryptionKeyClient = A.modPow(b, p);

              

                    // generate AES key
                    Key Skey = generateKey(decryptionKeyClient.toByteArray());
                    
                  
                    String keyString = Base64.getEncoder().encodeToString(Skey.getEncoded());
                    
                      ventanaCliente.mostrarMensaje("Secret  Key  " + keyString);
                    
                    
                    ventanaCliente.mostrarMensaje("------------------------------------------------------------------------------------------------------------------------------------------" );
                    ventanaCliente.mostrarMensaje("Bienvenido al Chat! ya podés empezar a escribir...   " );
                    
                    
            
                    return Skey ;
            }


    // generates usable SecretKey from given value. In default, user cannot create keys.
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
