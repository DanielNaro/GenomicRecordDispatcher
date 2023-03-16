package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Arrays;

public class SplicesLengthFractionBinRule implements BinRule<SplicesLengthFractionBin> {
    private final double[] binMaxValue;

    public SplicesLengthFractionBinRule(double[] binMaxValue) {
        if(binMaxValue == null){
            throw new IllegalArgumentException();
        }
        for(int i=1; i<binMaxValue.length; i++){
            if(binMaxValue[i] < binMaxValue[i-1]){
                throw new IllegalArgumentException();
            }
        }
        this.binMaxValue = binMaxValue;
        SplicesLengthFractionBin.setRule(this);
    }

    @Override
    public SplicesLengthFractionBin[] getTag(GlobalRecord globalRecord) {
        SAMRecord[] bestRecord = globalRecord.getBestRecords();

        SplicesLengthFractionBin[] result = new SplicesLengthFractionBin[bestRecord.length];
        int size = 0;
        for(int record_i=0; record_i < bestRecord.length; record_i++){
            if(bestRecord[record_i] != null){
                int numSplicedBases = globalRecord.getTotalSizeSpliceOperations()[record_i];

                double fractionSplicedBases = (double) numSplicedBases / (double) bestRecord[record_i].getReadLength();

                int bin = findBin(fractionSplicedBases, binMaxValue);
                result[size] = new SplicesLengthFractionBin(bin);
                size++;
            }
        }
        return Arrays.copyOf(result, size);
    }

    @Override
    public String getName() {
        return "es.upc.dmag.dispatcher.SplicesLengthFractionBin";
    }

    @Override
    public EntryType[] getTypes() {
        EntryType[] result = new EntryType[getSQLColumnNames().length];
        Arrays.fill(result, getType());
        return result;
    }

    private EntryType getType() {
        return EntryType.DOUBLE;
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
