public interface BinRule<T extends AbstractTag> extends Rule<T>{
    default int findBin(int value, int[] binMaxValue) {
        int bin = 0;
        for(int bin_i = 0; bin_i < binMaxValue.length; bin_i++){
            if(value < binMaxValue[bin_i]){
                bin = bin_i;
                break;
            }
        }
        if(value >= binMaxValue[bin]){
            bin = binMaxValue.length;
        }
        return bin;
    }

    default int findBin(double value, double[] binMaxValue) {
        int bin = 0;
        for(int bin_i = 0; bin_i < binMaxValue.length; bin_i++){
            if(value < binMaxValue[bin_i]){
                bin = bin_i;
                break;
            }
        }
        if(value >= binMaxValue[bin]){
            bin = binMaxValue.length;
        }
        return bin;
    }

    default boolean returnsMultipleTags() {
        return true;
    }

    default String[] getSQLColumnNames() {
        String[] results = new String[2];
        results[0] = getName()+"_min";
        results[1] = getName()+"_max";
        return results;
    }

    String[] getValuesForSQL(int bin);

}
