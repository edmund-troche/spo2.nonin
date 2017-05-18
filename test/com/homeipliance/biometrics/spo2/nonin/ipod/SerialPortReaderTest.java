package com.homeipliance.biometrics.spo2.nonin.ipod;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import junit.framework.TestCase;

public class SerialPortReaderTest extends TestCase
{
    private static final String COM_PORT = "/dev/cu.usbserial-000011FD";

    public SerialPortReaderTest(String name)
    {
        super(name);
    }

    public void testSerialPortReader()
    {
        boolean portFound = false;
        String defaultPort = COM_PORT;

        @SuppressWarnings("unchecked")
        final Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements())
        {
            CommPortIdentifier portId;

            portId = portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL
                    && portId.getName().equals(defaultPort))
            {
                System.out.println("Found port: " + portId.getName());
                portFound = true;

                final DataQueue dataQueue = new DataQueue();

                try
                {
                    final SerialPort serialPort = (SerialPort) portId.open("Home Heartbeat Status Reader", 2000);
                    try
                    {
                        serialPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);
                    }
                    catch (UnsupportedCommOperationException ucoExc)
                    {
                        throw new RuntimeException(ucoExc);
                    }

                    new SerialPortReader(serialPort, dataQueue);

                    SensorStatusProcessor statusProcessor = new SerialPortStatusProcessor(serialPort.getOutputStream(), dataQueue);
                    ((Thread) statusProcessor).start();
                }
                catch (PortInUseException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (TooManyListenersException e)
                {
                    e.printStackTrace();
                }
            }
        }

        if (!portFound)
        {
            System.out.println("port " + defaultPort + " not found.");
            System.exit(1);
        }

        while (true)
        {
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
