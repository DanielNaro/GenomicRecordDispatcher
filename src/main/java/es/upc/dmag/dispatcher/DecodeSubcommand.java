package es.upc.dmag.dispatcher;

import htsjdk.samtools.*;
import org.apache.commons.io.input.BoundedInputStream;
import org.bouncycastle.openpgp.PGPException;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Set;
import java.util.concurrent.Callable;

import static es.upc.dmag.dispatcher.Utils.extractData;
import static es.upc.dmag.dispatcher.Utils.filterBam;
import static es.upc.dmag.dispatcher.UtilsDatabase.getAvailableColumns;
import static es.upc.dmag.dispatcher.UtilsDatabase.getAvailableTables;

@Command(name = "decode")
public class DecodeSubcommand implements Callable<Integer> {
    @CommandLine.Parameters(arity = "1", paramLabel = "INPUTFILE")
    private Path collatedBAMsPath;

    @CommandLine.Parameters(arity = "1", paramLabel = "DATABASEPATH")
    private Path databasePath;

    @CommandLine.Parameters(arity = "1", paramLabel = "OUTPUTPATH")
    private Path outputPath;

    @CommandLine.Parameters(arity = "1", paramLabel = "FILTERINGPATH")
    private Path filteringConfigurationPath;


    private static void decodeCollated(
            Path databaseName,
            Path collatedBAMsPath,
            Path outputPath,
            Path filteringConfigurationPath)
            throws SQLException, IOException, PGPException, JAXBException {
        File collatedBAMs = collatedBAMsPath.toFile();

        SAMFileWriter writer = null;

        AndFilterCollection andFilterCollection = AndFilterParser.parse(filteringConfigurationPath);

        String connectionName = "jdbc:sqlite:"+databaseName;
        System.out.println("Connection to "+connectionName);
        Connection connection = DriverManager.getConnection(connectionName);
        Statement statement = connection.createStatement();


        Set<String> availableTables = getAvailableTables(statement);
        Set<String> availableColumns = getAvailableColumns(statement);


        System.out.println(andFilterCollection.generateSelect(availableTables, availableColumns));
        ResultSet resultSet =
                statement.executeQuery(andFilterCollection.generateSelect(availableTables, availableColumns));
        while(resultSet.next()){
            System.out.println("Working on "+resultSet.getLong(1));
            long start = resultSet.getLong(2);
            long end = resultSet.getLong(3);
            boolean encrypted = resultSet.getBoolean(4);

            SamInputResource samInputResource;
            File extracted =  null;
            File fileToDecode = null;

            FileInputStream fileStream;
            InputStream inputStream;

            if(encrypted) {

                extracted = extractData(collatedBAMs, start, end);
                extracted.deleteOnExit();

                fileToDecode = PGPFileDecryptor.decrypt(extracted, "secretsKeychain.asc");
                if (fileToDecode == null) {
                    throw new InternalError();
                }
                fileToDecode.deleteOnExit();


                samInputResource = SamInputResource.of(fileToDecode);
            } else {
                fileStream = new FileInputStream(collatedBAMs);
                fileStream.getChannel().position(start);
                inputStream = new BoundedInputStream(fileStream,
                        end-start);
                samInputResource = SamInputResource.of(inputStream);
            }

            if(writer == null) {
                FileInputStream fileStreamForHeader = new FileInputStream(collatedBAMs);
                fileStreamForHeader.getChannel().position(start);
                BoundedInputStream inputStreamForHeader = new BoundedInputStream(fileStreamForHeader,
                        end - start);
                SamInputResource samInputResourceForHeader = SamInputResource.of(inputStreamForHeader);

                final SamReader reader = SamReaderFactory.makeDefault().open(samInputResourceForHeader);
                SAMFileHeader outputHeader = reader.getFileHeader().clone();
                outputHeader.setSortOrder(SAMFileHeader.SortOrder.unsorted);

                SAMFileWriterFactory samFileWriterFactory = new SAMFileWriterFactory();
                samFileWriterFactory.setCreateIndex(false);
                writer =samFileWriterFactory.makeBAMWriter(
                        outputHeader,
                        false,
                        outputPath
                );
            }
            filterBam(samInputResource, writer, andFilterCollection);
            if(extracted != null) {
                if (extracted != fileToDecode) {
                    extracted.delete();
                }
                fileToDecode.delete();
            }
        }
        if(writer != null) {
            writer.close();
        }
    }

    @Override
    public Integer call() {
        try {
            decodeCollated(
                    databasePath,
                    collatedBAMsPath,
                    outputPath,
                    filteringConfigurationPath
            );
            return 0;
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

}