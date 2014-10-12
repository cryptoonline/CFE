import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by naveed on 1/24/14.
 */
public class Test {

    public static void main(String args[]) {

//            final int securityParameter = 2048 * pa;
//            final int size = 40000000;
//            final int functionSize = 4000000;
//            final int iterations = 15;

        final int size = Integer.parseInt(args[0]);
        final int functionSize = Integer.parseInt(args[1]);
        final int securityParameter = Integer.parseInt(args[2]);
        final int iterations = Integer.parseInt(args[3]);

    	final int minPaddingSizeBytes = 44;
        final int numBytesInSingleEncryption = securityParameter / 8 - minPaddingSizeBytes;

            System.out.println("Format for excel output is: " +
                    "plaintext size, " +
                    "encryption time, " +
                    "ciphertext size, ratio of ciphertext to plaintext size, " +
                    "function size, " +
                    "key generation time, " +
                    "size of the message sent to authority, " +
                    "decryption time (ns)" +
                    "decryptiom time (ms)");

            int n = 0;
            for (int i = 0; n <= size + minPaddingSizeBytes / 4; i++) {
                n += numBytesInSingleEncryption / 4;
            }


            CFE fe = new CFE(securityParameter);

            int[] PT = new int[n];

            Random rand1 = new Random();
            for (int i = 0; i < n; i++) {
                PT[i] = rand1.nextInt();
            }


            double encryptionStartTime = System.currentTimeMillis();
            Pair<byte[][], int[]> CT = fe.Enc(PT);
            double encryptionTime = System.currentTimeMillis() - encryptionStartTime;
            long encryptionSize = CT.getR().length * 4 + CT.getL().length * CT.getL()[0].length;

            ArrayList<Pair<Integer, Integer>> f = new ArrayList<Pair<Integer, Integer>>();


            Random rand2 = new Random();
            Random rand3 = new Random();
            for (int i = 0; i < functionSize; i++) {
                Pair<Integer, Integer> pair = new Pair<Integer, Integer>();
                int val = rand3.nextInt(n);
                pair.set(val, rand2.nextInt());

                f.add(pair);
            }


            for (int iter = 0; iter < iterations; iter++) {
                double keygenStartTime = System.currentTimeMillis();
                int[] partial_rCT2 = new int[f.size()];

                Map<Integer, byte[]> partial_rCT1 = new HashMap<>();

                Set<Integer> isBlockAdded = new HashSet<Integer>();
                int j = 0;
                for (int i = 0; i < f.size(); i++) {
                    int requiredBlock = (int) Math.floor((double) f.get(i).getL() / (double) (numBytesInSingleEncryption / 4));
                    if (!isBlockAdded.contains(requiredBlock)) {
                        isBlockAdded.add(requiredBlock);
                        partial_rCT1.put(requiredBlock, CT.getL()[requiredBlock]);
                        j++;
                    }
                    partial_rCT2[i] = CT.getR()[f.get(i).getL()];
                }

                long garblingKey = fe.keygen(f, partial_rCT1);
                double keygenTime = System.currentTimeMillis() - keygenStartTime;
                long messageClientToAuthoritySize = f.size() * 8 + partial_rCT1.size() * securityParameter / 8;

                double decryptionStartTime = System.nanoTime();
                double decryptionStartTimems = System.currentTimeMillis();
                long output = fe.Dec(f, partial_rCT2, garblingKey);
                double decryptionTime = (System.nanoTime() - decryptionStartTime) / 1000000;
                double decryptionTimems = System.currentTimeMillis() - decryptionStartTimems;

                System.out.print("Iteration " + iter + ": ");
                String result =
                        size + ", " +
                                encryptionTime + ", " +
                                encryptionSize + ", " +
                                f.size() * 8 + ", " +
                                keygenTime + ", " +
                                messageClientToAuthoritySize + ", " +
                                decryptionTime + ", " +
                                decryptionTimems;
                store(size + "_" + functionSize + "_" + securityParameter, result);
                System.out.println(n * 4 + ", " + encryptionTime + ", " + encryptionSize + ", " + f.size() * 8 + ", " + keygenTime + ", " + messageClientToAuthoritySize + ", " + decryptionTime + ", " + decryptionTimems);
        }
    }

    public static void store(String filename, String text) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)))) {
            out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
