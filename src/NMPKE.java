import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;

/**
 * Created by naveed on 1/31/14.
 */
public class NMPKE {
    private KeyPair keyPair;
    private Key publicKey;
    private Key privateKey;
    private Cipher cipher;
    private SecureRandom rand;

    public NMPKE(){
        Security.addProvider(new BouncyCastleProvider());
    }

    public NMPKE(int keysize){
        Security.addProvider(new BouncyCastleProvider());
        generateKeys(keysize);
        try {
            cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");
//            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    private void generateKeys(int keysize){
        rand = new SecureRandom();

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
            generator.initialize(keysize, rand);
            keyPair = generator.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }


    }

    public byte[] encrypt(byte[] plaintext){
        byte[] ciphertext = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, rand);
            ciphertext = cipher.doFinal(plaintext);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext){
        byte[] plaintext = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            plaintext = cipher.doFinal(ciphertext);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return plaintext;
    }

    public void storekey(String filename){
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(filename + ".publickey"));
            objectOutputStream.writeObject(publicKey);
            objectOutputStream.flush();
            objectOutputStream.close();

            objectOutputStream = new ObjectOutputStream(new FileOutputStream(filename + ".privatekey"));
            objectOutputStream.writeObject(privateKey);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadkey(String filename){

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filename + ".pulickey"));
            publicKey = (Key) objectInputStream.readObject();
            objectInputStream.close();

            objectInputStream = new ObjectInputStream(new FileInputStream(filename + ".privatekey"));
            privateKey = (Key) objectInputStream.readObject();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
