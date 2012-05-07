import java.util.*;

public class CombinationSample {
	public static void main(String[] args) {
		new CombinationSample();
	}

	public CombinationSample() {
		String[] items = new String[] { "0", "1", "2", "3" };
		List<int[]> indexs = getCombinationIndex(items.length);
		for (int[] index : indexs) {

			System.out.print(String.format("%s", items[index[0]]));
			for (int i = 1; i < index.length; i++) {
				System.out.print(String.format(",%s", items[index[i]]));
			}
			System.out.print("\n");
		}
	}

	public List<int[]> getCombinationIndex(int size) {
		List<int[]> result = new ArrayList<int[]>();
		List<int[]> ln = new ArrayList<int[]>();
		int n_1 = size - 1;
		for (int i = 0; i < n_1; i++) {
			int[] items = new int[1];
			items[0] = i;
			result.add(items);
			ln.add(items);
		}
		int[] i_1 = new int[1];
		i_1[0] = n_1;
		result.add(i_1);

		int len_1 = -1;
		int len0 = 0;
		int len1 = 1;
		while (ln.size() > 0) {
			List<int[]> newln = new ArrayList<int[]>();
			len_1++;
			len0++;
			len1++;
			for (int[] comb : ln) {
				for (int p = comb[len_1] + 1; p < n_1; p++) {
					int[] items = Arrays.copyOf(comb, len1);
					items[len0] = p;
					result.add(items);
					newln.add(items);
				}
				int[] last_item = Arrays.copyOf(comb, len1);
				last_item[len0] = n_1;
				result.add(last_item);
			}
			ln = newln;
		}
		return result;
	}
}
/* 
 L1
 a:0 b:1 c:2 d:3
 
 L2
 a{b,c,d} 0 1~3
  ab 1
  ac 2
  ad 3
 b{c,d} 1 2~3
  bc 2
  bd 3
 c{d} 2 3~3
  cd 3
 
 L3
 ab{c,d} 1 2~3
  abc 2
  abd 3
 ac{d} 2 3~3
  acd 3
 bc{d} 2 3~3
  bcd 3
 
 L4
 abc{d} 2 3~3
  abcd 3

*/