package com.homeipliance.biometrics.spo2.nonin.ipod;

import java.util.Properties;

public class DataParser
{
    public static final int BYTES_PER_SAMPLE = 5;
    public static final int SENSOR_DISCONNECTED = 0x40;
    public static final int BAD_PULSE = 0x20;
    public static final int OUT_OF_TRACK = 0x10;
    public static final int SENSOR_ALARM = 0x08;
    public static final int RED_PERFUSION = 0x04;
    public static final int GREEN_PERFUSION = 0x02;
    public static final int FRAME_SYNC = 0x01;
    public static final int STATUS_BIT = 0x80;
    public static final int HR_MSB = 0xC0;
    public static final int HR_LSB = 0x3F;
    public static final int SYNC_INDEX = 0x00;
    public static final int STATUS_INDEX = 1;
    public static final int PLETHYSMOGRAPHIC_VALUE_INDEX = 2;
    public static final int PARAM_INDEX = 3;
    public static final int CHECKSUM_INDEX = 4;

    /**
     * <code>
     * 
     * </code>
     * 
     * @param frameData
     * @return
     * @throws InvalidChecksumException
     * @throws InvalidStatusException
     */
    static Properties getDataFromFrame(final int[] frameData, final Properties properties)
            throws InvalidChecksumException, InvalidStatusException
    {
        final int[] sample = new int[BYTES_PER_SAMPLE];
        int sampleIndex = 0;
        for (int byteIndex = 1; byteIndex <= frameData.length; ++byteIndex)
        {
            sample[sampleIndex] = frameData[byteIndex - 1] & 0x00FF;

            if (byteIndex % 5 != 0)
            {
                sampleIndex++;
                continue;
            }
            else
            {
                sampleIndex = 0;
            }

            if (!hasValidChecksum(sample))
            {
                throw new InvalidChecksumException("Invalid checksum in sample: " + byteIndex
                        / 5);
            }

            if (!isValidStatus(sample))
            {
                throw new InvalidStatusException("Invalid status in sample: " + byteIndex / 5);
            }

            if ((byteIndex / 5) == 1)
            {
                final int hrMsb = ((sample[PARAM_INDEX]) & HR_MSB) & 0x00FF;
                properties.put("hr.msb", new Integer(hrMsb));
            }

            if ((byteIndex / 5) == 2)
            {
                final int hrLsb = ((sample[PARAM_INDEX]) & HR_LSB) & 0x00FF;
                properties.put("hr.lsb", new Integer(hrLsb));
            }

            if ((byteIndex / 5) == 3)
            {
                final int spo2 = ((sample[PARAM_INDEX])) & 0x00FF;
                properties.put("spo2", new Integer(spo2));
            }

            if ((byteIndex / 5) == 4)
            {
                final int rev = ((sample[PARAM_INDEX])) & 0x00FF;
                properties.put("revision", new Integer(rev));
            }

            if ((byteIndex / 5) == 9)
            {
                final int spo2DispStdAvg = ((sample[PARAM_INDEX])) & 0x00FF;
                properties.put("spo2.display.stdAvg", new Integer(spo2DispStdAvg));
            }

            if ((byteIndex / 5) == 5)
            {
                final int spo2Slew = ((sample[PARAM_INDEX])) & 0x00FF;
                properties.put("spo2.slew", new Integer(spo2Slew));
            }

            if ((byteIndex / 5) == 11)
            {
                final int spo2BeatToBeat = ((sample[PARAM_INDEX])) & 0x00FF;
                properties.put("spo2.beat2beat", new Integer(spo2BeatToBeat));
            }

            if ((byteIndex / 5) == 14)
            {
                final int hrExtAveDataMsb = ((sample[PARAM_INDEX]) & HR_MSB) & 0x00FF;
                properties.put("hr.data.extAve.msb", new Integer(hrExtAveDataMsb));
            }

            if ((byteIndex / 5) == 15)
            {
                final int hrExtAveDataLsb = ((sample[PARAM_INDEX]) & HR_LSB) & 0x00FF;
                properties.put("hr.data.extAve.lsb", new Integer(hrExtAveDataLsb));
            }

            if ((byteIndex / 5) == 16)
            {
                final int spo2ExtAveData = ((sample[PARAM_INDEX])) & 0x00FF;
                properties.put("spo2.data.extAve", new Integer(spo2ExtAveData));
            }

            if ((byteIndex / 5) == 17)
            {
                final int spo2DisplayExtAvg = ((sample[PARAM_INDEX])) & 0x00FF;
                properties.put("spo2.display.extAvg", new Integer(spo2DisplayExtAvg));
            }

            if ((byteIndex / 5) == 20)
            {
                final int hrDispStdAveMsb = ((sample[PARAM_INDEX]) & HR_MSB) & 0x00FF;
                properties.put("hr.display.stdAve.msb", new Integer(hrDispStdAveMsb));
            }

            if ((byteIndex / 5) == 21)
            {
                final int hrDispStdAveLsb = ((sample[PARAM_INDEX]) & HR_LSB) & 0x00FF;
                properties.put("hr.display.stdAve.lsb", new Integer(hrDispStdAveLsb));
            }

            if ((byteIndex / 5) == 22)
            {
                final int hrDispExtAveMsb = ((sample[PARAM_INDEX])) & 0x00FF;
                properties.put("hr.display.extAve.msb", new Integer(hrDispExtAveMsb));
            }

            if ((byteIndex / 5) == 23)
            {
                final int hrDispExtAveLsb = (( sample[PARAM_INDEX])) & 0x00FF;
                properties.put("hr.display.extAve.lsb", new Integer(hrDispExtAveLsb));
            }
        }

        return properties;
    }

    private static boolean isValidStatus(final int[] sample)
    {
        boolean sensorDisc = (sample[STATUS_INDEX] & SENSOR_DISCONNECTED) == SENSOR_DISCONNECTED;
        boolean sensorAlarm = (sample[STATUS_INDEX] & SENSOR_ALARM) == SENSOR_ALARM;
        boolean badPulse = (sample[STATUS_INDEX] & BAD_PULSE) == BAD_PULSE;
        boolean outOfTrack = (sample[STATUS_INDEX] & OUT_OF_TRACK) == OUT_OF_TRACK;
        // boolean redPerfusion = (sample[STATUS_INDEX] & RED_PERFUSION) == RED_PERFUSION;
        // boolean greenPerfusion = (sample[STATUS_INDEX] & GREEN_PERFUSION) ==
        // GREEN_PERFUSION;

        return (!sensorDisc || !sensorAlarm || !badPulse || !outOfTrack);
    }

    private static boolean hasValidChecksum(final int[] sample)
    {
        int byteSum = (sample[SYNC_INDEX] & 0x00FF); 
        byteSum += (sample[STATUS_INDEX] & 0x00FF);
        byteSum += (sample[PLETHYSMOGRAPHIC_VALUE_INDEX] & 0x00FF);
        byteSum += (sample[PARAM_INDEX] & 0x00FF);
        byteSum &= 0x00FF;
        
        return byteSum == sample[CHECKSUM_INDEX];
    }

}
