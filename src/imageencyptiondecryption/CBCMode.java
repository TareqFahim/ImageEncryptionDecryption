package imageencyptiondecryption;

public class CBCMode extends TEA{

	public CBCMode(){
		this.key = null;
	}

	public CBCMode(int[] key){
		super.addKey(key);
	}

	public int[] encrypt(int[] currentPlainBlock, int[] previousCipherBlock){

		if(key == null){
			System.out.println("PLESE ENTER KEY!");
			System.exit(0);
		}
		
		int[] block = new int[2];
		block[0] = currentPlainBlock[0] ^ previousCipherBlock[0];
		block[1] = currentPlainBlock[1] ^ previousCipherBlock[1];

		int[] cipher = super.encrypt(block);

		return cipher;
	}

	public int[] decrypt(int[] currentCipherBlock, int previousCipherBlock[]){

		if(key == null){
			System.out.println("PLEASE ENTER KEY!");
			System.exit(0);
		}

		int block[] = super.decrypt(currentCipherBlock);
		block[0] = block[0] ^ previousCipherBlock[0];
		block[1] = block[1] ^ previousCipherBlock[1];
		
		return block;
	}

}