package imageencyptiondecryption;

public class TEA{

	protected static int DELTA = 0x9e3779b9;
	protected static int ROUNDS = 32;
	protected int sum, left, right, cipher[], block[];
		

	protected int[] key; 			// Encryption/Decryption Key
	

	public TEA(){
		key = null;
	}

	public TEA(int[] key){
		addKey(key);
	}

	public void addKey(int[] key){
		if(key.length < 4)
			System.out.println("Key is less than 128 bits");
		else if(key.length > 4)
			System.out.println("Key is more than 128 bits");
		else
			this.key = key;
	}

	public void printKey(){
		if(key == null){
			System.out.println("There's no key");
		}
		System.out.println("The Key :\n");
		for(int i=0; i < 4; i++){
			System.out.println(this.key[i]);
		}
	}

	public int[] encrypt(int[] block){

		if(key == null){
			System.out.println("PLESE ENTER KEY!");
			System.exit(0);
		}

		left = block[0];
		right = block[1];

		sum = 0;

		for(int i = 0; i < ROUNDS; i++){
			sum += DELTA;
			left += ((right << 4) + key[0]) ^ (right+sum) ^ ((right >> 5) + key[1]);
			right += ((left << 4) + key[2]) ^ (left+sum) ^ ((left >> 5) + key[3]);

		}

		cipher = new int[2];
		cipher[0] = left;
		cipher[1] = right;

		return cipher;
	}

	public int[] decrypt(int[] cipher){

		if(key == null){
			System.out.println("PLEASE ENTER KEY!");
			System.exit(0);
		}

		left = cipher[0];
		right = cipher[1];

		sum = DELTA << 5;

		for(int i = 0; i < ROUNDS; i++){
			right -= ((left << 4) + key[2]) ^ (left+sum) ^ ((left >> 5) + key[3]);
			left -= ((right << 4) + key[0]) ^ (right+sum) ^ ((right >> 5) + key[1]);
			sum -= DELTA;
		}

		block = new int[2];
		block[0] = left;
		block[1] = right;

		return block;
	}

}