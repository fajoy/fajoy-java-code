import java.util.*;
public class UserModel {
	String UserId="";
	Map<String, Integer> MoveIDs= new HashMap<String, Integer>();
	public double getJaccardDistance(UserModel obj){
		//if(this.jaccardCache.containsKey(obj.MoveIDs))
			//return this.jaccardCache.get(obj.MoveIDs);
		int same=0;
		for(String moveid:obj.MoveIDs.keySet()){
			if(this.MoveIDs.containsKey(moveid))
				same++;
		}
		int total=this.MoveIDs.size()+obj.MoveIDs.size()-same;
		double d=1f;
		d=(same+0.d)/total;
		return d;
	}
	public double getCosineDistance(UserModel obj){
		//if(this.cosineCache.containsKey(obj.MoveIDs))
			//return this.cosineCache.get(obj.MoveIDs);
		int same=0;
		for(String moveid:obj.MoveIDs.keySet()){
			if(this.MoveIDs.containsKey(moveid))
				same++;
		}
		
		double abs=Math.pow(this.MoveIDs.size(), 0.5)*Math.pow(obj.MoveIDs.size(), 0.5);
		double d=1f;
		d=same/abs;
		return d;
	}
}
