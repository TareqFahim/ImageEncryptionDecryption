package imageencyptiondecryption;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.StringJoiner;

public class ImageEncyptionDecryption {

    public static void main(String[] args) {

        Random rand = new Random();
        int[] key = new int[4];
        int skip = 10;
        int[] img = new int[2];
        int IV[] = {rand.nextInt(), rand.nextInt()};    // Random IV for CBCMode
        String image = "";
        List<String> imgsList = new ArrayList<>();

        FileInputStream imgIn;
        FileOutputStream imgOut;
        DataInputStream dataIn;
        DataOutputStream dataOut;

        Scanner s = new Scanner(System.in);
        System.out.println("Enter Four Integer Values to Use as Key\n");
        try {
            for (int i = 0; i < 4; i++) {
                key[i] = s.nextInt();
            }
        } catch (Exception ex) {
            System.out.println("Please enter valid integer values for Key parts!");
            System.exit(0);
        }
        CBCMode cbc = new CBCMode(key);

        imgsList.add("tux.bmp");
        imgsList.add("sonic.bmp");
        imgsList.add("speaker.bmp");
        imgsList.add("duck.bmp");
        imgsList.add("panda.bmp");
        imgsList.add("match.bmp");

        System.out.println("\nEnter the Number of the Image you Want :\n");
        try {
            for (int i = 0; i < imgsList.size(); i++) {
                System.out.println(i+1 + " : " + imgsList.get(i));
            }
            int index = s.nextInt();
            if(index > 6 || index < 0)
                throw new Exception();
            else
                image = imgsList.get(index - 1);
        } catch (Exception ex) {
            System.out.println("Choose a valid index!");
            System.exit(0);
        }
        try {

            /*~~~~~~~~~~~~~~~~~~~~~~~Apply Encryption ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

            imgIn = new FileInputStream("Image\\" + image);
            imgOut = new FileOutputStream("Image\\Output\\EncryptedImage.bmp");

            dataIn = new DataInputStream(imgIn);
            dataOut = new DataOutputStream(imgOut);

            for (int i = 0; i < skip; i++) {
                if (dataIn.available() > 0) {
                    img[0] = dataIn.readInt();
                    img[1] = dataIn.readInt();
                    dataOut.writeInt(img[0]);
                    dataOut.writeInt(img[1]);
                }
            }


            boolean firstBlock = true;        //to know when to apply IV or the previous encrypted block
            int cipher[] = new int[2];
            boolean check = true;            //to catch where the reading from the file is stopped
            while (dataIn.available() > 0) {
                try {
                    img[0] = dataIn.readInt();
                    check = true;
                    img[1] = dataIn.readInt();
                    if (firstBlock) {        //if true, the block is passed with IV to be encrypted by TEA algorithm
                        cipher = cbc.encrypt(img, IV);
                        firstBlock = false;        //set firstBlock to false sense IV is only encrypted in the first block
                    } else
                        cipher = cbc.encrypt(img, cipher);        //pass the block with the previous encrypted block

                    dataOut.writeInt(cipher[0]);
                    dataOut.writeInt(cipher[1]);
                    check = false;
                } catch (EOFException e) {                //excetion is thrown if the file ends and dataIn.readInt() is executed
                    if (!check) {                        //if false, it means last block were not encrypted
                        dataOut.writeInt(img[0]);
                        dataOut.writeInt(img[1]);
                    } else                            //if true, it means only last half a block is not encrypted
                        dataOut.writeInt(img[0]);
                }

            }
            dataIn.close();
            dataOut.close();

            /*~~~~~~~~~~~~~~~~~~~~~~~ Apply Decryption ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

            dataIn = new DataInputStream(new FileInputStream("Image\\Output\\EncryptedImage.bmp"));
            dataOut = new DataOutputStream(new FileOutputStream("Image\\Output\\DecryptedImage.bmp"));

            for (int i = 0; i < skip; i++) {
                if (dataIn.available() > 0) {
                    img[0] = dataIn.readInt();
                    img[1] = dataIn.readInt();
                    dataOut.writeInt(img[0]);
                    dataOut.writeInt(img[1]);
                }
            }

            int[] copyCipher = new int[2];
            firstBlock = true;
            int plain[];
            check = true;

            while (dataIn.available() > 0) {
                try {
                    img[0] = dataIn.readInt();
                    check = true;
                    img[1] = dataIn.readInt();

                    if (firstBlock) {                            //if true, the first block is passed with IV to be decrytped
                        plain = cbc.decrypt(img, IV);
                        firstBlock = false;                    //set first time to false
                    } else                                    //if false, the block is passed with the previously encrypted block
                        plain = cbc.decrypt(img, copyCipher);

                    dataOut.writeInt(plain[0]);
                    dataOut.writeInt(plain[1]);

                    copyCipher[0] = img[0];                //Save the previously encryted block in copyCipher to use it
                    copyCipher[1] = img[1];

                    check = false;
                } catch (EOFException e) {
                    if (!check) {
                        dataOut.writeInt(img[0]);
                        dataOut.writeInt(img[1]);
                    } else
                        dataOut.writeInt(img[0]);
                    ;
                }

            }
            dataIn.close();
            dataOut.close();

            imgOut.close();
            imgIn.close();
        } catch (Exception ex) {
            System.out.println("Image Not Found Exception");
        }

    }

}
