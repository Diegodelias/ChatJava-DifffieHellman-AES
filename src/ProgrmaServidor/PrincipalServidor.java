package ProgrmaServidor;

import ProgramaArchivos.EnviarArchivo;
import ProgramaArchivos.RecibirArchivo;
import java.awt.*;
import java.security.Key;
import java.awt.event.*;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Base64;
import java.util.logging.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;

public class PrincipalServidor extends JFrame {

    public JTextField ingresoMensaje;
    public JTextArea pantallaChat;
    public JMenuItem adjuntar;
    public JMenuItem desencriptar;
    private static ServerSocket servidor;
    private static Socket cliente;
    private static String ipCliente;// = "10.0.0.4";
    public static String usuario;
    public static PrincipalServidor ventanaServidor;
    
   

  

    //Creamos la ventana del chat del servidor
    public PrincipalServidor() {
        super("Chat");
        //Campo de Texto en la parte inferior
        ingresoMensaje = new JTextField();
        ingresoMensaje.setEditable(false);
        add(ingresoMensaje, BorderLayout.SOUTH);

        //Hoja del chat centrado
        pantallaChat = new JTextArea();
        pantallaChat.setEditable(false);
        add(new JScrollPane(pantallaChat), BorderLayout.CENTER);
        pantallaChat.setBackground(Color.white);
        pantallaChat.setForeground(Color.black);
        ingresoMensaje.setForeground(Color.gray);

        //Crea opciones de Salir y Adjuntar Archivos
        desencriptar = new JMenuItem("Descencriptar");
        adjuntar = new JMenuItem("Adjuntar Archivo");
        adjuntar.setEnabled(false);
        JMenuBar barra = new JMenuBar();
        setJMenuBar(barra);
        barra.add(desencriptar);
       // barra.add(adjuntar);
       desencriptar.setHorizontalAlignment(SwingConstants.RIGHT);
       

        //Accion que se realiza Adjuntar Archivo
        adjuntar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                javax.swing.JFileChooser ventanaEscojer = new javax.swing.JFileChooser();
                int seleccion = ventanaEscojer.showOpenDialog(ventanaEscojer);// Se abre el cuadro para escoger el archivo
                String path = ventanaEscojer.getSelectedFile().getAbsolutePath();//Se obtiene la direccion completa del archivo

                //bucle para realizar la comparacion del archivo y poderlo enviar
                if (seleccion == JFileChooser.APPROVE_OPTION) {
                    ventanaServidor.mostrarMensaje("Enviando Archivo...");
                    RecibirArchivo recibirArchivo = new RecibirArchivo(path, usuario, 35557, "localhost");
                    recibirArchivo.start();
                    EnviarArchivo enviarArchivo = new EnviarArchivo(ipCliente, path);
                    enviarArchivo.start();
                    ventanaServidor.mostrarMensaje("Archivo Enviado Existosamente");
//                    PrincipalCliente.ventanaCliente.recibirArchivo(path,11112);
                }
            }
        });
        ipCliente = JOptionPane.showInputDialog(null, "Introduzca numero IP del Cliente: ");
        setSize(500, 500);//tamano de la ventana del chat
        setVisible(true); //hace visible a la ventana
      

    }

    public static void main(String[] args) throws Exception {
        ventanaServidor = new PrincipalServidor();
      //  ventanaServidor.setLocationRelativeTo(null);
        ventanaServidor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        usuario = JOptionPane.showInputDialog(null, "Introduzca su nombre: ");// intrduce el nombre del usuario o el nick

        try {
            //Crear el socket Servidor
            servidor = new ServerSocket(11111, 100);
            DiffieServidor generarKeyDiffie = new DiffieServidor();
          //  String primoServer = generarKeyDiffie.getModuloPrimo().toString();
            
           // ventanaServidor.mostrarMensaje("primoServer: " + primoServer );
            ventanaServidor.mostrarMensaje("Esperando Cliente ...");
           // ventanaServidor.mostrarMensaje("primoServer: " + primoServer );
            //Bucle infinito para esperar conexiones de los clientes
            
            while (true) {
                try {
                    //Coneccion con el cliente
                    cliente = servidor.accept();
                    ventanaServidor.mostrarMensaje("Conectado a : " + cliente.getInetAddress().getHostName());
                    ventanaServidor.habilitar(true);
                 
                     Key  clave = generarKeyDiffie.generarKeyServidor();//genera clave con diffieHellman
                    //Correr los hilos de enviar y recibir
                    HiloEnviar hiloEnviarServidor = new HiloEnviar(cliente, ventanaServidor);
                    hiloEnviarServidor.setKeyEncriptaconSrv(clave); 
                   
                    hiloEnviarServidor.start();
                        String key = Base64.getEncoder().encodeToString(hiloEnviarServidor.getKeyEncriptaconSrv().getEncoded());
                     // ventanaServidor.mostrarMensaje("clave diffie hellman" + hiloEnviarServidor.getKeyEncriptaconSrv());
                    
                    HiloRecibir hiloRecibirServidor = new HiloRecibir(cliente, ventanaServidor);
                    hiloRecibirServidor.setKeyEncriptaconSrv(clave); //genera clave con diffieHellman
                    hiloRecibirServidor.start();
               //    ventanaServidor.mostrarMensaje("primoServer: " + key);
                    
                } catch (IOException ex) {
                    Logger.getLogger(PrincipalServidor.class.getName()).log(Level.SEVERE, null, ex);
                    ventanaServidor.mostrarMensaje("No se puede conectar con el cliente");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PrincipalServidor.class.getName()).log(Level.SEVERE, null, ex);
            ventanaServidor.mostrarMensaje("No se encuentra IP del Servidor");
        }
    }

    public void mostrarMensaje(String mensaje) {
        pantallaChat.append(mensaje + "\n");
    }

    public void habilitar(boolean editable) {
        ingresoMensaje.setEditable(editable);
        adjuntar.setEnabled(editable);
        desencriptar.setEnabled(editable);
    }
}
