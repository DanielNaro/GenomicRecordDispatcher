package es.upc.dmag.dispatcher;

import es.upc.dmag.dispatcher.NumDeletionsFractionBin;
import es.upc.dmag.dispatcher.parser.*;
import es.upc.dmag.dispatcher.parser.HasPairedEnded;
import es.upc.dmag.dispatcher.parser.HasSingleEnded;
import es.upc.dmag.dispatcher.parser.HasUnmapped;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AndFilterParser {

    public static AndFilterCollection parse(Path inputPath) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        AndFilterType andFilterType =
                ((JAXBElement<AndFilterType>) unmarshaller.unmarshal(inputPath.toFile())).getValue();

        List<Filter> filters = new ArrayList<>();

        es.upc.dmag.dispatcher.parser.AlignmentScoreBinFilter alignmentScoreBin = andFilterType.getAlignmentScoreBin();
        if(alignmentScoreBin != null){
            filters.add(new AlignmentScoreBinFilter(
                    alignmentScoreBin.getLowerBound().intValueExact(),
                    alignmentScoreBin.getUpperBound().intValueExact(),
                    alignmentScoreBin.isForAllReads())
            );
        }

        es.upc.dmag.dispatcher.parser.DeletionsLengthBinFilter deletionsLengthBin = andFilterType.getDeletionsLengthBin();
        if(deletionsLengthBin != null){
            filters.add(new DeletionsLengthBinFilter(
                    deletionsLengthBin.getLowerBound().intValueExact(),
                    deletionsLengthBin.getUpperBound().intValueExact(),
                    deletionsLengthBin.isForAllReads())
            );
        }

        es.upc.dmag.dispatcher.parser.DeletionsLengthFractionBinFilter deletionsLengthFractionBin =
                andFilterType.getDeletionsLengthFractionBin();
        if(deletionsLengthFractionBin != null){
            filters.add(new DeletionsLengthFractionBinFilter(
                    deletionsLengthFractionBin.getLowerBound().doubleValue(),
                    deletionsLengthFractionBin.getUpperBound().doubleValue(),
                    deletionsLengthFractionBin.isForAllReads())
            );
        }

        es.upc.dmag.dispatcher.parser.HasMappedFilter hasMappedFilter = andFilterType.getHasMapped();
        if(hasMappedFilter != null){
            filters.add(new IsMappedFilter(hasMappedFilter.isValue(),
                    hasMappedFilter.isForAllReads()));
        }

        es.upc.dmag.dispatcher.parser.HasOpticalDuplicateFilter hasOpticalDuplicateFilter = andFilterType.getHasOptical();
        if(hasOpticalDuplicateFilter != null){
            filters.add(new HasOpticalDuplicateFilter(hasOpticalDuplicateFilter.isValue(),
                    hasOpticalDuplicateFilter.isForAllReads()));
        }

        HasPairedEnded hasPairedEnded = andFilterType.getHasPairedEnded();
        if(hasPairedEnded != null){
            filters.add(new HasPairedEndedFilter(hasPairedEnded.isValue(),
                    hasPairedEnded.isForAllReads()));
        }

        HasSingleEnded hasSingleEnded = andFilterType.getHasSignleEnded();
        if(hasSingleEnded != null){
            filters.add(new HasSingleEndedFilter(hasSingleEnded.isValue(),
                    hasSingleEnded.isForAllReads()));
        }

        HasUnmapped hasUnmapped = andFilterType.getHasUnmapped();
        if(hasUnmapped != null){
            filters.add(new HasUnmappedFilter(hasUnmapped.isValue(),
                    hasUnmapped.isForAllReads()));
        }

        es.upc.dmag.dispatcher.parser.InsertionsLengthBin insertionsLengthBin = andFilterType.getInsertionsLengthBin();
        if(insertionsLengthBin != null){
            filters.add(new InsertionsLengthBinFilter(
                    insertionsLengthBin.getLowerBound().intValueExact(),
                    insertionsLengthBin.getUpperBound().intValueExact(),
                    insertionsLengthBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.InsertionsLengthFractionBin insertionsLengthFractionBin =
                andFilterType.getInsertionsLengthFractionBin();
        if(insertionsLengthFractionBin != null){
            filters.add(new InsertionsLengthFractionBinFilter(
                    insertionsLengthFractionBin.getLowerBound().doubleValue(),
                    insertionsLengthFractionBin.getUpperBound().doubleValue(),
                    insertionsLengthFractionBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.MaximialQualityBin maximialQualityBin = andFilterType.getMaximialQualityBin();
        if(maximialQualityBin != null){
            filters.add(new MaximalQualityBinFilter(
                    maximialQualityBin.getLowerBound().intValueExact(),
                    maximialQualityBin.getUpperBound().intValueExact(),
                    maximialQualityBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.MinimalQualityBin minimalQualityBin = andFilterType.getMinimalQualityBin();
        if(minimalQualityBin != null) {
            filters.add(new MinimalQualityBinFilter(
                    minimalQualityBin.getLowerBound().intValueExact(),
                    minimalQualityBin.getUpperBound().intValueExact(),
                    minimalQualityBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.MismatchedBasesBin mismatchedBasesBin = andFilterType.getMismatchedBasesBin();
        if(mismatchedBasesBin != null) {
            filters.add(new MismatchedBasesBinFilter(
                    mismatchedBasesBin.getLowerBound().intValueExact(),
                    mismatchedBasesBin.getUpperBound().intValueExact(),
                    mismatchedBasesBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.MismatchedBasesFractionBin mismatchedBasesFractionBin = andFilterType.getMismatchedBasesFractionBin();
        if(mismatchedBasesFractionBin != null) {
            filters.add(new MismatchedBasesFractionFilter(
                    mismatchedBasesFractionBin.getLowerBound().doubleValue(),
                    mismatchedBasesFractionBin.getLowerBound().doubleValue(),
                    mismatchedBasesFractionBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.NumDeletionsBin numDeletionsBin = andFilterType.getNumDeletionsBin();
        if(numDeletionsBin != null) {
            filters.add( new NumDeletionsBinFilter(
                    numDeletionsBin.getLowerBound().intValueExact(),
                    numDeletionsBin.getUpperBound().intValueExact(),
                    numDeletionsBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.NumDeletionsFractionBin numDeletionsFractionBin = andFilterType.getNumDeletionsFractionBin();
        if(numDeletionsFractionBin != null){
            filters.add( new NumDeletionsFractionBinFilter(
                    numDeletionsFractionBin.getLowerBound().doubleValue(),
                    numDeletionsFractionBin.getUpperBound().doubleValue(),
                    numDeletionsFractionBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.NumInsertionsBin numInsertionsBin = andFilterType.getNumInsertionsBin();
        if(numInsertionsBin != null){
            filters.add( new NumInsertionsBinFilter(
                    numInsertionsBin.getLowerBound().intValueExact(),
                    numInsertionsBin.getUpperBound().intValueExact(),
                    numInsertionsBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.NumInsertionsFractionBin numInsertionsFractionBin = andFilterType.getNumInsertionsFractionBin();
        if(numInsertionsFractionBin != null){
            filters.add( new NumInsertionsFractionBinFilter(
                    numInsertionsFractionBin.getLowerBound().doubleValue(),
                    numInsertionsFractionBin.getUpperBound().doubleValue(),
                    numInsertionsFractionBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.NumMismatchesBin numMismatchesBin = andFilterType.getNumMismatchesBin();
        if(numMismatchesBin != null){
            filters.add( new NumMismatchesBinFilter(
                    numMismatchesBin.getLowerBound().intValueExact(),
                    numMismatchesBin.getUpperBound().intValueExact(),
                    numMismatchesBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.NumMismatchesFractionBin numMismatchesFractionBin = andFilterType.getNumMismatchesFractionBin();
        if(numMismatchesFractionBin != null){
            filters.add( new NumMismatchesFractionBinFilter(
                    numMismatchesFractionBin.getLowerBound().doubleValue(),
                    numMismatchesFractionBin.getUpperBound().doubleValue(),
                    numMismatchesFractionBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.NumSplicesBin numSplicesBin = andFilterType.getNumSplicesBin();
        if(numSplicesBin != null){
            filters.add( new NumSplicesBinFilter(
                    numSplicesBin.getLowerBound().intValueExact(),
                    numSplicesBin.getUpperBound().intValueExact(),
                    numSplicesBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.NumSplicesFractionBin numSplicesFractionBin = andFilterType.getNumSplicesFractionBin();
        if(numSplicesFractionBin != null){
            filters.add( new NumSplicesFractionBinFilter(
                    numSplicesFractionBin.getLowerBound().doubleValue(),
                    numSplicesFractionBin.getUpperBound().doubleValue(),
                    numSplicesFractionBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.SplicesLengthBin splicesLengthBin = andFilterType.getSplicesLengthBin();
        if(splicesLengthBin != null) {
            filters.add( new SplicesLengthBinFilter(
                    splicesLengthBin.getLowerBound().intValueExact(),
                    splicesLengthBin.getUpperBound().intValueExact(),
                    splicesLengthBin.isForAllReads()
            ));
        }

        es.upc.dmag.dispatcher.parser.SplicesLengthFractionBin splicesLengthFractionBin = andFilterType.getSplicesLengthFractionBin();
        if(splicesLengthFractionBin != null){
            filters.add( new SplicesLengthFractionBinFilter(
                    splicesLengthFractionBin.getLowerBound().doubleValue(),
                    splicesLengthFractionBin.getUpperBound().doubleValue(),
                    splicesLengthFractionBin.isForAllReads()
            ));
        }

        return new AndFilterCollection(filters);


    }
}
