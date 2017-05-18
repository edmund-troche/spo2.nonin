package com.homeipliance.biometrics.spo2.nonin.ipod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import junit.framework.TestCase;

public class DataParserTest extends TestCase
{
    public static final int FRAME_LENGTH = 125;

    public void testGetDataFromFrame()
    {
        final String userDir = System.getProperty("user.dir");
        
        FileInputStream inputStream;
        try
        {
            inputStream = new FileInputStream(userDir + File.separatorChar + "frame.txt");
        }
        catch (FileNotFoundException fnfExc)
        {
            fail("Unable to find data file: " + fnfExc.getLocalizedMessage());
            return;
        }
     
        final String line;
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            line =  br.readLine();
        } catch (IOException ioExc) {
            fail("Cound not read the fame data:" + ioExc.getLocalizedMessage());
            return;
        } finally {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        if (line == null)
        {
            fail("No data in the data file");
        }
        
        final Properties props = new Properties();
        try
        {
            final int[] frame = new int[FRAME_LENGTH];
            int frameIndex = 0;
            for (int byteIndex = 0; byteIndex < FRAME_LENGTH; ++byteIndex)
            {
                final int start = byteIndex  * 2;
                final int end = start + 2;
                final String currentByteStr = line.substring(start, end);
                final int currentByte = Integer.parseInt(currentByteStr, 16);

                frame[frameIndex] = currentByte;

                frameIndex++;
            }
            
            DataParser.getDataFromFrame(frame, props);
        }
        catch (InvalidChecksumException icExc)
        {
            icExc.printStackTrace();
            fail("Invalid checksum in data");
            return;
        }
        catch (InvalidStatusException isExc)
        {
            isExc.printStackTrace();
            fail("Invalid status in data");
            return;
        }
        
        System.out.println(props);
    }

}
