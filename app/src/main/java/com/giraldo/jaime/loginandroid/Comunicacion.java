package com.giraldo.jaime.loginandroid;

import android.provider.ContactsContract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;

/**
 * Created by jaime on 24/02/2017.
 */

public class Comunicacion extends Observable implements Runnable {

    private static Comunicacion ref;
    public static final String android = "10.0.2.2";
    public static final int PORT = 6000;

    private DatagramSocket socket;
    private boolean corre;
    private boolean conectado;
    private boolean reset;

    private Comunicacion() {
        corre = true;
        conectado = true;
        reset = true;
    }

    public static Comunicacion instancia() {
        if (ref == null) {
            ref = new Comunicacion();
            new Thread(ref).start();
        }
        return ref;
    }

    public boolean intento() {
        try {
            socket = new DatagramSocket();
            setChanged();
            notifyObservers();
            clearChanged();
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void run() {
        if (corre) {
            if (conectado) {
                if (reset) {
                    socket.close();
                }
                reset = false;
            }
            conectado = !intento();
        } else {
            if (socket != null) {
                DatagramPacket packet = recibir();
                Object objetoRecibido = desSerializar(packet.getData());
                if (objetoRecibido instanceof Usuario) {
                    Usuario usuario = (Usuario) objetoRecibido;
                    System.out.println("NO LLEGÓ");
                    setChanged();
                    notifyObservers(usuario);
                    clearChanged();
                }
            }
        }
    }

    public DatagramPacket recibir() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            return packet;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void enviar(final byte[] pack, final String destino, final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket != null) {
                    try {
                        InetAddress ipEnvio = null;
                        ipEnvio = InetAddress.getByName(destino);
                        DatagramPacket paqueteEnviar = new DatagramPacket(pack, pack.length, ipEnvio, port);
                        socket.send(paqueteEnviar);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private byte[] serializar(Object data) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(data);
            bytes = baos.toByteArray();
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private Object desSerializar(byte[] bytes) {
        Object data = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            data = ois.readObject();
            ois.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }
}

