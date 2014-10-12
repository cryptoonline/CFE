import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.security.SecureRandom;
import java.util.*;

/**
 * Created by naveed on 1/9/14.
 */
public class CFE {
    private int securityParameter;
    private int minPaddingSizeBytes;
    private int numBytesInSingleEncryption;

    private NMPKE nmPKE;

    public CFE(int securityParameter) {
        this.securityParameter = securityParameter;
        minPaddingSizeBytes = 44;
        numBytesInSingleEncryption = securityParameter / 8 - minPaddingSizeBytes;

        nmPKE = new NMPKE(securityParameter);
    }

    public long keygen(ArrayList<Pair<Integer, Integer>> f, Map<Integer, byte[]> rCT) {
        long garblingKey = 0;

        Map<Integer, byte[]> rPT = new HashMap<>();

        int it = 0;
        for (Map.Entry<Integer, byte[]> entry : rCT.entrySet()) {
            Integer key = entry.getKey();
            byte[] value = entry.getValue();
            rPT.put(key, nmPKE.decrypt(value));
        }


        byte[] rs_required = new byte[f.size() * 4];

        for (int i = 0; i < f.size(); i++) {
            int requiredBlock = (int) Math.floor((double) f.get(i).getL() / (double) (numBytesInSingleEncryption / 4));
            int requiredElement = f.get(i).getL() % (numBytesInSingleEncryption / 4);
            byte temp[] = rPT.get(requiredBlock);
            System.arraycopy(temp, requiredElement * 4,
                    rs_required, i * 4, 4);
        }

        int[] rs = byteArraytoIntArray(rs_required);

        for (int i = 0; i < f.size(); i++) {
            garblingKey += rs[i] * f.get(i).getR();
        }

        return garblingKey;
    }

    public Pair<byte[][], int[]> Enc(int[] PT) {
        int R[] = createR(PT.length);

        byte[] Rbytes = intArraytoByteArray(R);

        byte[][] ct1 = new byte[Rbytes.length / numBytesInSingleEncryption][securityParameter / 8];

        for (int i = 0; i < (int) Math.ceil((double) Rbytes.length / (double) numBytesInSingleEncryption); i++) {
            byte[] temp = new byte[numBytesInSingleEncryption];
            System.arraycopy(Rbytes, i * numBytesInSingleEncryption, temp, 0, numBytesInSingleEncryption);
            ct1[i] = nmPKE.encrypt(temp);
        }

        int[] ct2 = new int[PT.length];

        for (int i = 0; i < PT.length; i++) {
            ct2[i] = R[i] + PT[i];
        }

        Pair<byte[][], int[]> CT = new Pair<byte[][], int[]>();

        CT.set(ct1, ct2);

        return CT;
    }

    public long Dec(ArrayList<Pair<Integer, Integer>> f, int[] ct2, long garblingKey) {
        long output = -garblingKey;

        for (int i = 0; i < ct2.length; i++) {
            output += ct2[i] * f.get(i).getR();
        }

        return output;
    }

    private int[] createR(int size) {
        int[] R = new int[size];
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < size; i++) {
            R[i] = rand.nextInt();
        }
        return R;
    }

    private int[] byteArraytoIntArray(byte[] byteArray) {
        IntBuffer intBuf = ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        return array;
    }

    private byte[] intArraytoByteArray(int[] intArray) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(intArray);

        return byteBuffer.array();
    }
}
