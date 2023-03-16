package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Arrays;

public class MinimalQualityBinRule implements BinRule<MinimalQualityBin> {
    private final int[] binMaxValue;

    public MinimalQualityBinRule(int[] binMaxValue) {
        if(binMaxValue == null){
            throw new IllegalArgumentException();
        }
        for(int i=1; i<binMaxValue.length; i++){
            if(binMaxValue[i] < binMaxValue[i-1]){
                throw new IllegalArgumentException();
            }
        }
        this.binMaxValue = binMaxValue;

        MinimalQualityBin.setRule(this);
    }

    @Override
    public MinimalQualityBin[] getTag(GlobalRecord globalRecord) {
        SAMRecord[] bestRecord = globalRecord.getBestRecords();

        MinimalQualityBin[] result = new MinimalQualityBin[bestRecord.length];
        int size = 0;
        for(int record_i=0; record_i < bestRecord.length; record_i++){
            if(bestRecord[record_i] != null){
                int numDeletions = globalRecord.getMinimalQuality();

                int bin = findBin(numDeletions, binMaxValue);
                result[size] = new MinimalQualityBin(bin);
                size++;
            }
        }
        return Arrays.copyOf(result, size);
    }

    @Override
    public String getName() {
        return "es.upc.dmag.dispatcher.MinimalQualityBin";
    }

    @Override
    public EntryType[] getTypes() {
        EntryType[] result = new EntryType[getSQLColumnNames().length];
        Arrays.fill(result, getType());
        return result;
    }

    private EntryType getType() {
        return EntryType.INTEGER;
    }

    @Override
    public String[] getValuesForSQL(int bin) {
        String[] result = new String[2];

        if(bin == 0){
            result[0] = "0";
        } else {
            result[0] = Double.toString(binMaxValue[bin-1]);
        }

        if(bin == binMaxValue.length){
            if(getType() == EntryType.INTEGER){
                result[1] = Integer.toString(Integer.MAX_VALUE);
            } else {
                result[1] = Double.toString(Double.MAX_VALUE);
            }
        } else {
            result[1] = Double.toString(binMaxValue[bin]);
        }

        return result;
    }

}