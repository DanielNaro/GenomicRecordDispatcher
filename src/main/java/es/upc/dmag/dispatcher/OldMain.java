package es.upc.dmag.dispatcher;

import es.upc.dmag.dispatcher.parser.ObjectFactory;
import es.upc.dmag.dispatcher.parser.RulesType;
import htsjdk.samtools.*;
import org.apache.commons.io.input.BoundedInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.Security;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OldMain {


    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, SQLException, PGPException, JAXBException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        long timeStart = System.currentTimeMillis();
        if(args[0].equalsIgnoreCase("encode")){
            //encode(args[1], args[2], args[3], args[4], args[5]);
        } else if(args[0].equalsIgnoreCase("decode")){
            //decodeCollated(args[1], args[2], args[3], args[4]);
        } else {
            /*final SamReader reader = SamReaderFactory.makeDefault().open(Paths.get(args[1]));
            SAMFileHeader outputHeader = reader.getFileHeader().clone();
            outputHeader.setSortOrder(SAMFileHeader.SortOrder.unsorted);


            SAMFileWriterFactory samFileWriterFactory = new SAMFileWriterFactory();
            samFileWriterFactory.setCreateIndex(false);
            SAMFileWriter writer =samFileWriterFactory.makeBAMWriter(
                    outputHeader,
                    false,
                    Paths.get(args[2])
            );
            SamInputResource samInputResource =
                    SamInputResource.of(new File(args[1]));
            filterBam(samInputResource, writer, AndFilterParser.parse(Paths.get(args[3])) );
            writer.close();*/
        }
        long timeEnd = System.currentTimeMillis();

        System.out.println((double)(timeEnd -timeStart)/(double)1000);


    }
}
