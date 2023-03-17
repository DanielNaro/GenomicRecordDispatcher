package es.upc.dmag.dispatcher;

import es.upc.dmag.dispatcher.parser.RulesType;

import java.util.ArrayList;
import java.util.List;

import static es.upc.dmag.dispatcher.Utils.fromDecimalListToSortedArray;
import static es.upc.dmag.dispatcher.Utils.fromIntegerListToSortedArray;

public class UtilsRules {
    public static List<Rule> decodeRules(RulesType rulesType){
        List<Rule> decodedRules = new ArrayList<>();
        if(rulesType.isMarkMapped()==Boolean.TRUE){
            decodedRules.add(new IsMappedRule());
        };
        if(rulesType.isMarkUnmapped()==Boolean.TRUE){
            decodedRules.add(new HasUnmappedRule());
        }
        if(rulesType.isMarkHasPairedEnded()==Boolean.TRUE){
            decodedRules.add(new HasPairedEndedRule());
        }
        if(rulesType.isMarkHasSingleEnded()==Boolean.TRUE){
            decodedRules.add(new HasSingleEndedRule());
        }
        if(rulesType.isMarkHasOpticalDuplicateRule()==Boolean.TRUE){
            decodedRules.add(new HasOpticalDuplicateRule());
        }
        if(rulesType.isMarkGroup()==Boolean.TRUE){
            decodedRules.add(new GroupRule());
        }

        RulesType.DeletionsLengthBins deletionsLengthBins =
                rulesType.getDeletionsLengthBins();
        if(deletionsLengthBins != null){
            decodedRules.add(new DeletionsLengthBinRule(fromIntegerListToSortedArray(deletionsLengthBins.getBoundary())));
        }

        RulesType.DeletionsLengthFractionBins deletionsLengthFractionBins = rulesType.getDeletionsLengthFractionBins();
        if(deletionsLengthFractionBins != null){
            decodedRules.add(new DeletionsLengthFractionBinRule(fromDecimalListToSortedArray(deletionsLengthFractionBins.getBoundary())));
        }

        RulesType.InsertionsLengthFractionBins insertionsLengthFractionBins =
                rulesType.getInsertionsLengthFractionBins();
        if(insertionsLengthFractionBins != null){
            decodedRules.add(new InsertionsLengthFractionBinRule(fromDecimalListToSortedArray(insertionsLengthFractionBins.getBoundary())));
        }

        RulesType.InsertionsLengthBins insertionsLengthBins =
                rulesType.getInsertionsLengthBins();
        if(insertionsLengthBins != null){
            decodedRules.add(new InsertionsLengthBinRule(fromIntegerListToSortedArray(insertionsLengthBins.getBoundary())));
        }

        RulesType.MismatchedBasesFractionBins mismatchedBasesFractionBins =
                rulesType.getMismatchedBasesFractionBins();
        if(mismatchedBasesFractionBins != null){
            decodedRules.add(new MismatchedBasesFractionRule(fromDecimalListToSortedArray(mismatchedBasesFractionBins.getBoundary())));
        }

        RulesType.MismatchedBasesBins mismatchedBasesBins =
                rulesType.getMismatchedBasesBins();
        if(mismatchedBasesBins != null){
            decodedRules.add(new MismatchedBasesBinRule(fromIntegerListToSortedArray(mismatchedBasesBins.getBoundary())));
        }

        RulesType.MismatchedLengthBins mismatchedLengthBins =
                rulesType.getMismatchedLengthBins();
        if(mismatchedLengthBins != null){
            decodedRules.add(new MismatchedLengthBinRule(fromIntegerListToSortedArray(mismatchedLengthBins.getBoundary())));
        }

        RulesType.MismatchedLengthFractionBins mismatchedLengthFractionBins =
                rulesType.getMismatchedLengthFractionBins();
        if(mismatchedLengthFractionBins != null){
            decodedRules.add(new MismatchedLengthFractionBinRule(fromDecimalListToSortedArray(mismatchedLengthFractionBins.getBoundary())));
        }

        RulesType.MaximalQualityBins maximalQualityBins =
                rulesType.getMaximalQualityBins();
        if(maximalQualityBins != null){
            decodedRules.add(new MaximalQualityBinRule(fromIntegerListToSortedArray(maximalQualityBins.getBoundary())));
        }

        RulesType.MinimalQualityBins minimalQualityBins =
                rulesType.getMinimalQualityBins();
        if(maximalQualityBins != null){
            decodedRules.add(new MinimalQualityBinRule(fromIntegerListToSortedArray(minimalQualityBins.getBoundary())));
        }

        RulesType.NumDeletionsBins numDeletionsBins =
                rulesType.getNumDeletionsBins();
        if(numDeletionsBins != null){
            decodedRules.add(new NumDeletionsBinRule(fromIntegerListToSortedArray(numDeletionsBins.getBoundary())));
        }

        RulesType.NumDeletionsFractionBins numDeletionsFractionBins =
                rulesType.getNumDeletionsFractionBins();
        if(numDeletionsFractionBins != null){
            decodedRules.add(new NumDeletionsFractionBinRule(fromDecimalListToSortedArray(numDeletionsFractionBins.getBoundary())));
        }

        RulesType.NumInsertionsBins numInsertionsBins =
                rulesType.getNumInsertionsBins();
        if(numInsertionsBins != null){
            decodedRules.add(new NumInsertionsBinRule(fromIntegerListToSortedArray(numInsertionsBins.getBoundary())));
        }

        RulesType.NumInsertionsFractionBins numInsertionsFractionBins =
                rulesType.getNumInsertionsFractionBins();
        if(numInsertionsFractionBins != null){
            decodedRules.add(new NumInsertionsFractionBinRule(fromDecimalListToSortedArray(numInsertionsFractionBins.getBoundary())));
        }

        RulesType.NumMismatchesBins numMismatchesBins =
                rulesType.getNumMismatchesBins();
        if(numMismatchesBins != null){
            decodedRules.add(new NumMismatchesBinRule(fromIntegerListToSortedArray(numMismatchesBins.getBoundary())));
        }

        RulesType.NumMismatchesFractionBins numMismatchesFractionBins =
                rulesType.getNumMismatchesFractionBins();
        if(numMismatchesFractionBins != null){
            decodedRules.add(new NumMismatchesFractionBinRule(fromDecimalListToSortedArray(numMismatchesFractionBins.getBoundary())));
        }

        RulesType.NumSplicesBins numSplicesBins = rulesType.getNumSplicesBins();
        if(numSplicesBins != null){
            decodedRules.add(new NumSplicesBinRule(fromIntegerListToSortedArray(numSplicesBins.getBoundary())));
        }

        RulesType.NumSplicesFractionBins numSplicesFractionBins =
                rulesType.getNumSplicesFractionBins();
        if(numSplicesFractionBins != null){
            decodedRules.add(new NumSplicesFractionBinRule(fromDecimalListToSortedArray(numSplicesFractionBins.getBoundary())));
        }

        RulesType.AlignmentScoreBins alignmentScoreBins =
                rulesType.getAlignmentScoreBins();
        if(alignmentScoreBins != null) {
            decodedRules.add(new AlignmentScoreBinRule(fromIntegerListToSortedArray(alignmentScoreBins.getBoundary())));
        }

        return decodedRules;
    }
}
