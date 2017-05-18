package com.homeipliance.biometrics.spo2.nonin.ipod;

import java.util.Properties;

public class DataProcessor extends Thread implements Runnable
{
    private final DataQueue queue;
    private boolean processing = true;

    public DataProcessor(DataQueue dataQueue)
    {
        queue = dataQueue;
    }

    public void run()
    {
        final int frame[] = new int[125];

        while (processing)
        {
            int data = queue.remove();

            if (data == DataParser.FRAME_SYNC)
            {
                data = queue.remove();

                if ((data & (DataParser.STATUS_BIT + DataParser.FRAME_SYNC)) == (DataParser.STATUS_BIT + DataParser.FRAME_SYNC))
                {
                    frame[0] = DataParser.FRAME_SYNC;
                    frame[1] = data;

                    for (int i = 2; i < 125; i++)
                    {
                        frame[i] = queue.remove();
                    }

                    try
                    {
                        final Properties properties = new Properties();
                        DataParser.getDataFromFrame(frame, properties);
                        // int hrMsb = (Integer) properties.get("hr.display.stdAve.msb");
                        // int hrLsb = (Integer) properties.get("hr.display.stdAve.lsb");
                        // int hr = ((hrMsb & 0x00FF) & DataParser.HR_MSB)
                        // + ((hrLsb & 0x00FF) & DataParser.HR_LSB);

                        // System.out.println("SpO2: "
                        // + properties.get("spo2") + ", HR.msb: " + hrMsb + ", HR: " +
                        // hr);

                        System.out.println(properties);
                    }
                    catch (InvalidChecksumException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (InvalidStatusException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                continue;
            }
        }
    }
}
