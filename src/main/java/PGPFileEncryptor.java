import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.*;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PGPFileEncryptor {
    public static File encrypt(File inputFile,
                               HashMap<String, PGPPublicKey> keyDictionaries, String[] recipients) throws IOException, PGPException {
        File encryptedFile = File.createTempFile("encrypted",".pgpEncrypted", Paths.get(".").toFile());
        FileOutputStream fileOutputStream = new FileOutputStream(encryptedFile);
        PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(
                        SymmetricKeyAlgorithmTags.AES_256
                ).setWithIntegrityPacket(true)
        );
        cPk.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(keyDictionaries.get("User")).setProvider("BC"));
        for (String recipient : recipients) {
            PGPPublicKey publicKey = keyDictionaries.get(recipient);
            if (publicKey != null) {
                cPk.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"));
            }

        }


        OutputStream encryptedOutputStream = cPk.open(fileOutputStream, new byte[1 << 16]);


        PGPUtil.writeFileToLiteralData(
                encryptedOutputStream,
                PGPLiteralData.BINARY,
                inputFile
        );
        encryptedOutputStream.close();
        fileOutputStream.close();


        return encryptedFile;
    }
}
