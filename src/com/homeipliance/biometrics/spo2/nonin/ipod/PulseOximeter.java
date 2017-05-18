package com.homeipliance.biometrics.spo2.nonin.ipod;

//
// PulseOximeter.java
// PulseOximeter
//
// Created by Edmund Troche on 4/5/09.
// Copyright (c) 2009 Home iPpliance. All rights reserved.
//
//
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

public class PulseOximeter
{

    static class SerialPortReader implements SerialPortEventListener
    {
        InputStream inputStream;
        Thread readThread;
        final DataQueue queue;

        public SerialPortReader(final CommPortIdentifier portId, final DataQueue dataQueue)
                throws PortInUseException, IOException, TooManyListenersException
        {
            final SerialPort serialPort;

            if (portId == null)
            {
                throw new IllegalArgumentException("portId must not be null");
            }

            if (dataQueue == null)
            {
                throw new IllegalArgumentException("queue must not be nul");
            }

            this.queue = dataQueue;

            try
            {
                serialPort = (SerialPort) portId.open("Nonin Pulse Oximeter", 2000);
            }
            catch (PortInUseException piuExc)
            {
                System.out.println("Port in use: " + piuExc.getMessage());
                throw piuExc;
            }

            inputStream = serialPort.getInputStream();

            try
            {
                serialPort.addEventListener(this);
            }
            catch (TooManyListenersException e)
            {
                throw e;
            }

            serialPort.notifyOnDataAvailable(true);

            try
            {
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            }
            catch (UnsupportedCommOperationException ucoExc)
            {
                throw new RuntimeException(ucoExc);
            }
        }

        /**
         * Method declaration
         * 
         * 
         * @see
         */
        public void run()
        {
            try
            {
                Thread.sleep(20000);
            }
            catch (InterruptedException e)
            {
            }
        }

        /**
         * Method declaration
         * 
         * 
         * @param event
         * 
         * @see
         */
        public void serialEvent(SerialPortEvent event)
        {
            switch (event.getEventType())
            {
            case SerialPortEvent.BI:

            case SerialPortEvent.OE:

            case SerialPortEvent.FE:

            case SerialPortEvent.PE:

            case SerialPortEvent.CD:

            case SerialPortEvent.CTS:

            case SerialPortEvent.DSR:

            case SerialPortEvent.RI:

            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;

            case SerialPortEvent.DATA_AVAILABLE:

                // byte[] readBuffer = new byte[375];
                // try
                // {
                // while (inputStream.available() > 0)
                // {
                // int numBytes = 0;
                // int bytesRead = inputStream.read(readBuffer, numBytes,
                // 375 - numBytes);
                //
                // numBytes += bytesRead;
                // if (numBytes >= 375)
                // {
                // System.out.print(byteArrayToHexString(readBuffer));
                // numBytes = 0;
                // System.out.println();
                // }
                // }
                // }
                // catch (IOException e)
                // {
                // System.out.println("IOException");
                // }
                try
                {
                    while (inputStream.available() > 0)
                    {
                        int data = inputStream.read();

                        queue.add(new Integer(data));
                    }
                }
                catch (IOException ioExc)
                {
                }

                break;
            }
        }

        static String byteArrayToHexString(byte in[])
        {
            byte ch = 0x00;
            int i = 0;

            if (in == null || in.length <= 0)
            {
                return null;
            }

            String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                    "B", "C", "D", "E", "F" };

            StringBuffer out = new StringBuffer(in.length * 2);

            while (i < in.length)
            {
                ch = (byte) (in[i] & 0xF0); // Strip off high nibble
                ch = (byte) (ch >>> 4); // shift the bits down
                ch = (byte) (ch & 0x0F); // must do this is high order bit is
                // on!
                out.append(pseudo[(int) ch]); // convert the nibble to a
                // String Character
                ch = (byte) (in[i] & 0x0F); // Strip off low nibble
                out.append(pseudo[(int) ch]); // convert the nibble to a
                // String Character

                i++;
            }

            return new String(out);
        }
    }

    /**
     * Method declaration
     * 
     * 
     * @param args
     * @throws IOException
     * @throws PortInUseException
     * @throws TooManyListenersException
     * 
     * @see
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws PortInUseException, IOException,
            TooManyListenersException
    {
        boolean portFound = false;
        String defaultPort = "COM8";
        Enumeration<CommPortIdentifier> portList;
        CommPortIdentifier portId;
        DataQueue dataQueue = new DataQueue();

        if (args.length > 0)
        {
            defaultPort = args[0];
        }

        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements())
        {
            portId = portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL
                    && portId.getName().equals(defaultPort))
            {
                System.out.println("Found port: " + defaultPort);
                portFound = true;
                new SerialPortReader(portId, dataQueue);

                DataProcessor dataProcessor = new DataProcessor(dataQueue);
                dataProcessor.start();
            }
        }

        if (!portFound)
        {
            System.out.println("port " + defaultPort + " not found.");
        }

    }

}