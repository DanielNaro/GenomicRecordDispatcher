
import dispatcher.dmag.upc.es.AndFilterType;
import dispatcher.dmag.upc.es.NumDeletionsFractionBin;
import dispatcher.dmag.upc.es.ObjectFactory;

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

        dispatcher.dmag.upc.es.AlignmentScoreBinFilter alignmentScoreBin = andFilterType.getAlignmentScoreBin();
        if(alignmentScoreBin != null){
            filters.add(new AlignmentScoreBinFilter(
                    alignmentScoreBin.getLowerBound().intValueExact(),
                    alignmentScoreBin.getUpperBound().intValueExact(),
                    alignmentScoreBin.isForAllReads())
            );
        }

        dispatcher.dmag.upc.es.DeletionsLengthBinFilter deletionsLengthBin = andFilterType.getDeletionsLengthBin();
        if(deletionsLengthBin != null){
            filters.add(new DeletionsLengthBinFilter(
                    deletionsLengthBin.getLowerBound().intValueExact(),
                    deletionsLengthBin.getUpperBound().intValueExact(),
                    deletionsLengthBin.isForAllReads())
            );
        }

        dispatcher.dmag.upc.es.DeletionsLengthFractionBinFilter deletionsLengthFractionBin =
                andFilterType.getDeletionsLengthFractionBin();
        if(deletionsLengthFractionBin != null){
            filters.add(new DeletionsLengthFractionBinFilter(
                    deletionsLengthFractionBin.getLowerBound().doubleValue(),
                    deletionsLengthFractionBin.getUpperBound().doubleValue(),
                    deletionsLengthFractionBin.isForAllReads())
            );
        }

        dispatcher.dmag.upc.es.HasMappedFilter hasMappedFilter = andFilterType.getHasMapped();
        if(hasMappedFilter != null){
            filters.add(new IsMappedFilter(hasMappedFilter.isValue(),
                    hasMappedFilter.isForAllReads()));
        }

        dispatcher.dmag.upc.es.HasOpticalDuplicateFilter hasOpticalDuplicateFilter = andFilterType.getHasOptical();
        if(hasOpticalDuplicateFilter != null){
            filters.add(new HasOpticalDuplicateFilter(hasOpticalDuplicateFilter.isValue(),
                    hasOpticalDuplicateFilter.isForAllReads()));
        }

        dispatcher.dmag.upc.es.HasPairedEnded hasPairedEnded = andFilterType.getHasPairedEnded();
        if(hasPairedEnded != null){
            filters.add(new HasPairedEndedFilter(hasPairedEnded.isValue(),
                    hasPairedEnded.isForAllReads()));
        }

        dispatcher.dmag.upc.es.HasSingleEnded hasSingleEnded = andFilterType.getHasSignleEnded();
        if(hasSingleEnded != null){
            filters.add(new HasSingleEndedFilter(hasSingleEnded.isValue(),
                    hasSingleEnded.isForAllReads()));
        }

        dispatcher.dmag.upc.es.HasUnmapped hasUnmapped = andFilterType.getHasUnmapped();
        if(hasUnmapped != null){
            filters.add(new HasUnmappedFilter(hasUnmapped.isValue(),
                    hasUnmapped.isForAllReads()));
        }

        dispatcher.dmag.upc.es.InsertionsLengthBin insertionsLengthBin = andFilterType.getInsertionsLengthBin();
        if(insertionsLengthBin != null){
            filters.add(new InsertionsLengthBinFilter(
                    insertionsLengthBin.getLowerBound().intValueExact(),
                    insertionsLengthBin.getUpperBound().intValueExact(),
                    insertionsLengthBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.InsertionsLengthFractionBin insertionsLengthFractionBin =
                andFilterType.getInsertionsLengthFractionBin();
        if(insertionsLengthFractionBin != null){
            filters.add(new InsertionsLengthFractionBinFilter(
                    insertionsLengthFractionBin.getLowerBound().doubleValue(),
                    insertionsLengthFractionBin.getUpperBound().doubleValue(),
                    insertionsLengthFractionBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.MaximialQualityBin maximialQualityBin = andFilterType.getMaximialQualityBin();
        if(maximialQualityBin != null){
            filters.add(new MaximalQualityBinFilter(
                    maximialQualityBin.getLowerBound().intValueExact(),
                    maximialQualityBin.getUpperBound().intValueExact(),
                    maximialQualityBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.MinimalQualityBin minimalQualityBin = andFilterType.getMinimalQualityBin();
        if(minimalQualityBin != null) {
            filters.add(new MinimalQualityBinFilter(
                    minimalQualityBin.getLowerBound().intValueExact(),
                    minimalQualityBin.getUpperBound().intValueExact(),
                    minimalQualityBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.MismatchedBasesBin mismatchedBasesBin = andFilterType.getMismatchedBasesBin();
        if(mismatchedBasesBin != null) {
            filters.add(new MismatchedBasesBinFilter(
                    mismatchedBasesBin.getLowerBound().intValueExact(),
                    mismatchedBasesBin.getUpperBound().intValueExact(),
                    mismatchedBasesBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.MismatchedBasesFractionBin mismatchedBasesFractionBin = andFilterType.getMismatchedBasesFractionBin();
        if(mismatchedBasesFractionBin != null) {
            filters.add(new MismatchedBasesFractionFilter(
                    mismatchedBasesFractionBin.getLowerBound().doubleValue(),
                    mismatchedBasesFractionBin.getLowerBound().doubleValue(),
                    mismatchedBasesFractionBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.NumDeletionsBin numDeletionsBin = andFilterType.getNumDeletionsBin();
        if(numDeletionsBin != null) {
            filters.add( new NumDeletionsBinFilter(
                    numDeletionsBin.getLowerBound().intValueExact(),
                    numDeletionsBin.getUpperBound().intValueExact(),
                    numDeletionsBin.isForAllReads()
            ));
        }

        NumDeletionsFractionBin numDeletionsFractionBin = andFilterType.getNumDeletionsFractionBin();
        if(numDeletionsFractionBin != null){
            filters.add( new NumDeletionsFractionBinFilter(
                    numDeletionsFractionBin.getLowerBound().doubleValue(),
                    numDeletionsFractionBin.getUpperBound().doubleValue(),
                    numDeletionsFractionBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.NumInsertionsBin numInsertionsBin = andFilterType.getNumInsertionsBin();
        if(numInsertionsBin != null){
            filters.add( new NumInsertionsBinFilter(
                    numInsertionsBin.getLowerBound().intValueExact(),
                    numInsertionsBin.getUpperBound().intValueExact(),
                    numInsertionsBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.NumInsertionsFractionBin numInsertionsFractionBin = andFilterType.getNumInsertionsFractionBin();
        if(numInsertionsFractionBin != null){
            filters.add( new NumInsertionsFractionBinFilter(
                    numInsertionsFractionBin.getLowerBound().doubleValue(),
                    numInsertionsFractionBin.getUpperBound().doubleValue(),
                    numInsertionsFractionBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.NumMismatchesBin numMismatchesBin = andFilterType.getNumMismatchesBin();
        if(numMismatchesBin != null){
            filters.add( new NumMismatchesBinFilter(
                    numMismatchesBin.getLowerBound().intValueExact(),
                    numMismatchesBin.getUpperBound().intValueExact(),
                    numMismatchesBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.NumMismatchesFractionBin numMismatchesFractionBin = andFilterType.getNumMismatchesFractionBin();
        if(numMismatchesFractionBin != null){
            filters.add( new NumMismatchesFractionBinFilter(
                    numMismatchesFractionBin.getLowerBound().doubleValue(),
                    numMismatchesFractionBin.getUpperBound().doubleValue(),
                    numMismatchesFractionBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.NumSplicesBin numSplicesBin = andFilterType.getNumSplicesBin();
        if(numSplicesBin != null){
            filters.add( new NumSplicesBinFilter(
                    numSplicesBin.getLowerBound().intValueExact(),
                    numSplicesBin.getUpperBound().intValueExact(),
                    numSplicesBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.NumSplicesFractionBin numSplicesFractionBin = andFilterType.getNumSplicesFractionBin();
        if(numSplicesFractionBin != null){
            filters.add( new NumSplicesFractionBinFilter(
                    numSplicesFractionBin.getLowerBound().doubleValue(),
                    numSplicesFractionBin.getUpperBound().doubleValue(),
                    numSplicesFractionBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.SplicesLengthBin splicesLengthBin = andFilterType.getSplicesLengthBin();
        if(splicesLengthBin != null) {
            filters.add( new SplicesLengthBinFilter(
                    splicesLengthBin.getLowerBound().intValueExact(),
                    splicesLengthBin.getUpperBound().intValueExact(),
                    splicesLengthBin.isForAllReads()
            ));
        }

        dispatcher.dmag.upc.es.SplicesLengthFractionBin splicesLengthFractionBin = andFilterType.getSplicesLengthFractionBin();
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
