package es.upc.dmag.dispatcher;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultToCollectionAdder {
    public static void add(
            Connection connection,
            WrittingResult writtingResult,
            FileOutputStream fileOutputStream,
            HashMap<String, PGPPublicKey> keyDictionaries
    ) throws SQLException, IOException, PGPException, InterruptedException {
        FileInputStream dataInputStream = new FileInputStream(writtingResult.getOutputFile());
        File encryptedFile = null;


        boolean encrypted = false;
        for(AbstractTag tag : writtingResult.getTags()){
            if(tag instanceof SecurityTag){
                SecurityTag securityTag = (SecurityTag)tag;

                if(securityTag.getRecipientsContains("Everybody")){
                    continue;
                }

                System.out.println("Encrypting");
                encryptedFile = PGPFileEncryptor.encrypt(
                        writtingResult.getOutputFile(),
                        keyDictionaries,
                        securityTag.getRecipients()
                );

                dataInputStream = new FileInputStream(
                        encryptedFile
                );
                encrypted = true;
                break;
            }
        }


        long startData = fileOutputStream.getChannel().position();
        append(fileOutputStream, dataInputStream);
        long endData = fileOutputStream.getChannel().position();

        writtingResult.getOutputFile().delete();
        if(encryptedFile != null){
            encryptedFile.delete();
        }

        StringBuilder insertToMain = new StringBuilder("insert into AUsMain (");
        insertToMain.append("id, startData, endData, encrypted");
        StringBuilder values = new StringBuilder(" VALUES (NULL, ");
        values.append(startData).append(", ").append(endData).append(", ").append(encrypted ? "TRUE" : "FALSE");

        List<AbstractTag> multipleOutputTags = new ArrayList<>();

        for(AbstractTag abstractTag : writtingResult.getTags()){
            Rule rule = abstractTag.getRule();
            if(rule != null){
                if(!rule.returnsMultipleTags()) {
                    int column_i=0;
                    for(String value : abstractTag.getValuesForSQL()){
                        insertToMain.append(", ").append(rule.getSQLColumnNames()[column_i]);
                        values.append(", ").append(value);

                        column_i++;
                    }
                }else{
                    multipleOutputTags.add(abstractTag);
                }
            }
        }

        insertToMain.append(") ");
        values.append(");");

        System.out.println("inserting with: "+insertToMain.toString()+values.toString());
        PreparedStatement psInsertMain = connection.prepareStatement(
                insertToMain.toString()+values.toString(),
                Statement.RETURN_GENERATED_KEYS
        );
        boolean successful = false;
        int attempts = 0;
        while (!successful) {
            try {
                psInsertMain.execute();
                successful = true;
            } catch (SQLException e) {
                attempts++;
                if(attempts == 5){
                    throw e;
                }
                Thread.sleep(1000);
            }
        }
        ResultSet resultSetInsertMain = psInsertMain.getGeneratedKeys();
        long key;
        if( resultSetInsertMain.next()){
            key = resultSetInsertMain.getLong(1);
        }else{
            throw new InternalError("No key received");
        }

        for(AbstractTag abstractTag : multipleOutputTags) {
            Rule rule = abstractTag.getRule();

            StringBuilder columnsStringBuilder = new StringBuilder();
            for (String columnName : abstractTag.getRule().getSQLColumnNames()) {
                columnsStringBuilder.append(", ").append(columnName);
            }

            StringBuilder insertCommandBuilder = new StringBuilder(
                    "insert into " + rule.getName() + "(au_id" + columnsStringBuilder + ") VALUES (" + key
            );
            for (String columnName : abstractTag.getRule().getSQLColumnNames()) {
                insertCommandBuilder.append(", ?");
            }
            insertCommandBuilder.append(")");
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(insertCommandBuilder.toString());

                for (int column_i=0 ; column_i < abstractTag.getValuesForSQL().length; column_i++) {
                    preparedStatement.setString(column_i+1, abstractTag.getValuesForSQL()[column_i]);
                }

                successful = false;
                attempts = 0;
                while (!successful) {
                    try {
                        preparedStatement.execute();
                        successful = true;
                    } catch (SQLException e) {
                        attempts++;
                        if(attempts == 5){
                            throw e;
                        }
                        Thread.sleep(1000);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                System.err.println("failed with: "+insertCommandBuilder.toString());
            }
        }
        dataInputStream.close();
    }

    private static void append(FileOutputStream outputStream, FileInputStream inputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int size;
        while ((size = inputStream.read(buffer))>0) {
            outputStream.write(buffer, 0, size);
        }
        outputStream.flush();
    }
}
