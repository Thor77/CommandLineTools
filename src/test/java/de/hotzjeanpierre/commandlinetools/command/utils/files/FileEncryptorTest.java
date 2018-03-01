/*
 *     Copyright 2018 Jean-Pierre Hotz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hotzjeanpierre.commandlinetools.command.utils.files;

import de.hotzjeanpierre.commandlinetools.command.testutilities.ByteArrayProcessor;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

public class FileEncryptorTest {

    private static final String TESTREADFILE_TOWRITE = "This is some weird kind of text.\nIt is used to test whether the class FileEncryptor actually correctly reads data from files.";
    private static final byte[] TESTREADFILE_EXPECTED = TESTREADFILE_TOWRITE.getBytes();

    @Test
    public void testReadFile() throws IOException {
        File toRead = new File(System.getProperty("user.home"), "sometestfile.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(toRead))) {
            writer.write(TESTREADFILE_TOWRITE);
        } catch (IOException e) {
            throw new AssertionError(
                    "Couldn't test since there was an error setting up the test environment.", e
            );
        }

        assertThat(
                FileEncryptor.readFile(toRead),
                is(TESTREADFILE_EXPECTED)
        );

        toRead.delete();
    }

    private static final String TESTWRITEFILE_TOWRITE = "This is some different but also weird kind of text.\nIt is used to test whether the class FileEncryptor actually correctly writes data to files.";
    private static final byte[] TESTWRITEFILE_EXPECTED = TESTWRITEFILE_TOWRITE.getBytes();

    @Test
    public void testWriteFile() throws IOException {
        File toWrite = new File(System.getProperty("user.home"), "sometestfile.txt");

        FileEncryptor.writeFile(toWrite, TESTWRITEFILE_EXPECTED);

        byte[] readData = new byte[(int) toWrite.length()];

        try (FileInputStream stream = new FileInputStream(toWrite)) {
            if(stream.read(readData) != readData.length) {
                throw new IOException("Length of file does not match the detected data.");
            }
        } catch (IOException e) {
            throw new AssertionError(
                    "Couldn't test since there was an error setting up the test environment.", e
            );
        }

        assertThat(
                readData,
                is(TESTWRITEFILE_EXPECTED)
        );

        toWrite.delete();
    }

    /**
     * The password we're using to determine the integrity of the method FileEncryptor#createPrivateKey(String).
     */
    public static String TESTCREATEPRIVATEKEYVALID_USEDPASSWORD = "asdf1234";
    /**
     * The hash (312433c28349f63c4f387953ff337046e794bea0f9b9ebfcb08e90046ded9c76) has been determined
     * using https://www.tools4noobs.com/online_tools/hash/ with SHA-256 and the text "asdf1234".
     * Since AES uses keys of 16 bytes length we'll also have to trim the hash to said length´.
     */
    public static byte[] TESTCREATEPRIVATEKEYVALID_EXPECTED =
            ByteArrayProcessor.bringToLength(
                    ByteArrayProcessor.parseFromHexString(
                            "312433c28349f63c4f387953ff337046e794bea0f9b9ebfcb08e90046ded9c76"
                    ),
                    16
            );

    @Test
    public void testCreatePrivateKeyValid() {
        assertThat(
                FileEncryptor.createPrivateKey(TESTCREATEPRIVATEKEYVALID_USEDPASSWORD).getSecretKey().getEncoded(),
                is(TESTCREATEPRIVATEKEYVALID_EXPECTED)
        );
    }

    @Test
    public void testCreatePrivateKeyInvalid() {
        assertThat(
                FileEncryptor.createPrivateKey(null).isSuccess(),
                is(false)
        );
    }

    private static final String TESTENCRYPT_USEDPASSWORD = "asdf1234";
    private static final String TESTENCRYPT_USEDDATATEXT = "This is some text the FileEncryptor-class is supposed to encrypt with the password 'asdf1234'.\nIf this fails... well something's just wrong :D";
    private static final byte[] TESTENCRYPT_USEDDATA = TESTENCRYPT_USEDDATATEXT.getBytes(Charset.forName("UTF-8"));

    /**
     * This byte array has been determined by some straight-forward but rather long Kotlin-code.
     * Thus this test is rather a mock than a real test, but it's better than nothing.
     * Here's the actual code that determined the result:
     *
     *
     import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing
     import java.security.MessageDigest
     import javax.crypto.Cipher
     import javax.crypto.spec.SecretKeySpec

     fun main(args: Array<String>) {
         val password = "asdf1234"
         val toEncrypt = "This is some text the FileEncryptor-class is supposed to encrypt with the password 'asdf1234'.\nIf this fails... well something's just wrong :D"

         var keyByte = password.toByteArray()
         val sha256 = MessageDigest.getInstance("SHA-256")

         keyByte = sha256.digest(keyByte).copyOfRange(0, 16)

         println(keyByte.size)

         val key = SecretKeySpec(keyByte, "AES")

         var dataEncrypted = toEncrypt.toByteArray()
         val aesEnc = Cipher.getInstance("AES")

         aesEnc.init(Cipher.ENCRYPT_MODE, key)

         dataEncrypted = aesEnc.doFinal(dataEncrypted)

         println(String(dataEncrypted))
         // this print statement actually prints the expected result:
         println(dataEncrypted.ctoString())
     }

     fun ByteArray.ctoString() : String {
         val resultBuilder = StringBuilder()

         for(i in 0 until this.size) {
             resultBuilder.append(StringProcessing.zeroPadding(this[i].toInt() and 0xFF, 2, 16))
         }

         return resultBuilder.toString()
     }
     */
    private static final byte[] TESTENCRYPT_EXPECTED =
            ByteArrayProcessor.parseFromHexString(
                    "dec622de4f00bd6aefa3d73c9a61e3475774860554d217f0159d8393d02aa6dffc404b3a167d0e0772d56f5ce0eaa34dafc79fe99be81cdc38528378c9d30f5add4101aa8d961b56eb7caedce4f9c80a54c104f2467e0d204796124eff94f9a164534f2e6ae709e2781b372dbc47ee9e21e1161a71de9c2f78d0a4ecab50af995075a0d1124d966f3e4876dcd6d94ddb"
            );

    @Test
    public void testEncrypt() throws GeneralSecurityException {
        assertThat(
                FileEncryptor.encrypt(
                        TESTENCRYPT_USEDDATA,
                        FileEncryptor.createPrivateKey(TESTENCRYPT_USEDPASSWORD).getSecretKey()
                ),
                is(TESTENCRYPT_EXPECTED)
        );
    }

    @Test
    public void testDecrypt() throws GeneralSecurityException {
        assertThat(
                new String(FileEncryptor.decrypt(
                        TESTENCRYPT_EXPECTED,
                        FileEncryptor.createPrivateKey(TESTENCRYPT_USEDPASSWORD).getSecretKey()
                )),
                is(TESTENCRYPT_USEDDATATEXT)
        );
    }

    @Test
    public void testEncryptFileNull() {
        assertThat(
                FileEncryptor.encrypt(
                        FileEncryptor.createPrivateKey("asdf1234").getSecretKey(),
                        null,
                        new File(System.getProperty("user.home"))
                ).isSuccess(),
                is(false)
        );
    }

    @Test
    public void testEncryptFileNonExisting() {
        assertThat(
                FileEncryptor.encrypt(
                        FileEncryptor.createPrivateKey("asdf1234").getSecretKey(),
                        new File(System.getProperty("user.home"), "somenonexistingfile.txt"),
                        new File(System.getProperty("user.home"))
                ).isSuccess(),
                is(false)
        );
    }
}
