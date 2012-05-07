import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import fpTree.FPTree;
import fpTree.HeadTable;
public class FPTreeSample {
	public static void main(String[] args) throws IOException {
		//HeadTable table=new HeadTable("bin/test.dat");
		HeadTable table=new HeadTable("bin/test.dat");
		//table.showTable();
		FPTree fptree=table.createFPTree(3);
		
		//fptree.showTable();
		//fptree.showTree();
	}




}
