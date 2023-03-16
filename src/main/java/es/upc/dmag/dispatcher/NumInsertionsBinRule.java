package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Arrays;

public class NumInsertionsBinRule implements BinRule<NumInsertionsBin> {
    private final int[] binMaxValue;

    public NumInsertionsBinRule(int[] binMaxValue) {
        if(binMaxValue == null){
            throw new IllegalArgumentException();
        }
        for(int i=1; i<binMaxValue.length; i++){
            if(binMaxValue[i] < binMaxValue[i-1]){
                throw new IllegalArgumentException();
            }
        }
        this.binMaxValue = binMaxValue;

        NumInsertionsBin.setRule(this);
    }

    @Override
    public boolean returnsMultipleTags() {
        return true;
    }

    @Override
    public NumInsertionsBin[] getTag(GlobalRecord globalRecord) {
        SAMRecord[] bestRecord = globalRecord.getBestRecords();

        NumInsertionsBin[] result = new NumInsertionsBin[bestRecord.length];
        int size = 0;
        for(int record_i=0; record_i < bestRecord.length; record_i++){
            if(bestRecord[record_i] != null){
                int numInsertions = globalRecord.getNumInsertionOperations()[record_i];

                int bin = findBin(numInsertions, binMaxValue);
                result[size] = new NumInsertionsBin(bin);
                size++;
            }
        }
        return Arrays.copyOf(result, size);
    }

    @Override
    public String getName() {
        return "es.upc.dmag.dispatcher.NumInsertionsBin";
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