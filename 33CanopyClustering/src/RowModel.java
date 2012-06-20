import java.util.*;
public class RowModel {
	String rowId="";
	Map<String, Integer> items= new LinkedHashMap<String, Integer>();
	public double getJaccardDistance(RowModel obj){
		int same=0;
		for(String moveid:obj.items.keySet()){
			if(this.items.containsKey(moveid))
				same++;
		}
		int total=this.items.size()+obj.items.size()-same;
		double d=1f;
		d=(same+0.d)/total;
		return d;
	}
	public double getCosineDistance(RowModel obj){
		int same=0;
		for(String moveid:obj.items.keySet()){
			if(this.items.containsKey(moveid))
				same++;
		}
		
		double abs=Math.pow(this.items.size(), 0.5)*Math.pow(obj.items.size(), 0.5);
		double d=1f;
		d=same/abs;
		return d;
	}
}
