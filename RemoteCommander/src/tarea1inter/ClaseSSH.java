/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tarea1inter;

import java.io.InputStream;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JOptionPane;

public class ClaseSSH {

    public JSch jsch = new JSch();
    public java.util.Properties config = new java.util.Properties();
    public static Session sesion;
    public String ServicioTexto = "";
    public String EstadoTexto = "";
    public String todtexto = "";
    public String[] nombreServicios;
    public String[] EstadoServicios;
    public static String OS;
    public static String contraseña;
    public String coman;

    public void conectar(String host, String user, String contra) {

        try {
            contraseña = contra;
            config.put("StrictHostKeyChecking", "no");
            Session session = jsch.getSession(user, host, 22); //Puerto destinado a la Escucha por Default.
            session.setPassword(contra);
            session.setConfig(config);
            sesion = session;
            sesion.connect();
            System.out.println("Conectado !");

        } catch (Exception e) {

        }

    }

    public void sistemaOP() throws JSchException, IOException {
        Channel channel = sesion.openChannel("exec");
        ((ChannelExec) channel).setCommand("dmesg | head -1");
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);

        InputStream in = channel.getInputStream();
        channel.connect();                                 //Obtenemos canal de comunicación
        byte[] tmp = new byte[1024];

        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }

                todtexto += new String(tmp, 0, i);
            }
            if (todtexto.contains("Linux")) {

                OS = "Linux";

            } else {

                OS = "Windows";
            }
            if (channel.isClosed()) {
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }
        channel.disconnect();
    }

    public void desconectar() {

        try {

            sesion.disconnect();

        } catch (Exception e) {

        }

    }

    public void mostrarServicios() throws JSchException, IOException {

        Channel channel = sesion.openChannel("exec");
        if (OS.equalsIgnoreCase("Windows")) {

            //se pide primero el nombre de los servicios
            ((ChannelExec) channel).setCommand("wmic service get name");
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();                                 //Obtenemos canal de comunicación
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    ServicioTexto += new String(tmp, 0, i); //Se guarda todo el resultado de bytes a un string
                }
                nombreServicios = null;
                nombreServicios = ServicioTexto.split("\n"); // se realiza un split para separar cada nombre de servicio
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
//fin de la llamada de servicios
//Inicio de llamda de los estados de los servicios

            channel = sesion.openChannel("exec");
            ((ChannelExec) channel).setCommand("wmic service get state");
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            in = channel.getInputStream();
            channel.connect();                                 //Obtenemos canal de comunicación
            tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    EstadoTexto += new String(tmp, 0, i); //Se guarda todo el resultado de bytes a un string
                }
                EstadoServicios=null;
                EstadoServicios = EstadoTexto.split("\n"); // se realiza un split para separar cada nombre de servicio
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();

        } else {

            ((ChannelExec) channel).setCommand("service --status-all | more");
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();                                 //Obtenemos canal de comunicación
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    ServicioTexto += new String(tmp, 0, i); //Se guarda todo el resultado de bytes a un string
                }
                nombreServicios = null;
                nombreServicios = ServicioTexto.split("\n"); // se realiza un split para separar cada nombre de servicio
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
        }

    }

    public void realizarAccion(String Comando, String servicio) throws JSchException, IOException {
        if (OS.equals("Windows")) {
            if (Comando.equals("iniciar")) {
                coman = "net start ";
            }
            if (Comando.equals("pausa")) {
                coman = "net stop ";
            }
            Channel channel = sesion.openChannel("exec");
            ((ChannelExec) channel).setCommand(coman + servicio);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();                                 //Obtenemos canal de comunicación
            byte[] tmp = new byte[1024];

            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }

                    todtexto += new String(tmp, 0, i);
                }
                System.out.print(todtexto);
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
//------------------------------------------------------------------
//Para Linux
        } else {

            if (Comando.equals("iniciar")) {

                coman = "echo " + contraseña + " | sudo -S service " + servicio + " start";
            }
            if (Comando.equals("pausa")) {
                coman = "echo " + contraseña + " | sudo -S service " + servicio + " stop";
            }

            Channel channel = sesion.openChannel("exec");
            ((ChannelExec) channel).setCommand(coman + servicio);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];

            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }

                    todtexto += new String(tmp, 0, i);
                }
                System.out.print(todtexto);
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();

        }

    }
}
