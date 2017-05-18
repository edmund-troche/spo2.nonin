package com.homeipliance.biometrics.spo2.nonin.ipod;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

public class SerialPortReader implements SerialPortEventListener
{
    private InputStream inputStream;
    private final DataQueue queue;

    public SerialPortReader(final SerialPort serialPort, final DataQueue dataQueue)
            throws PortInUseException, IOException, TooManyListenersException
    {
        if (dataQueue == null)
        {
            throw new IllegalArgumentException("queue must not be nul");
        }

        this.queue = dataQueue;

        inputStream = serialPort.getInputStream();

        serialPort.addEventListener(this);

        serialPort.notifyOnDataAvailable(true);

        try
        {
            serialPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        }
        catch (UnsupportedCommOperationException ucoExc)
        {
            throw new RuntimeException(ucoExc);
        }
    }

    public void serialEvent(final SerialPortEvent event)
    {
        switch (event.getEventType())
        {
            case SerialPortEvent.DATA_AVAILABLE:
                try
                {
                    while (inputStream.available() > 0)
                    {
                        int data = inputStream.read();

                        //System.out.println("data: " + (char) data + ",  d" + data + " " + "x"
                        //        + Integer.toHexString(data).toUpperCase());
                        queue.add(new Integer(data));
                    }
                }
                catch (IOException ioExc)
                {
                }

                break;
        }
    }
}