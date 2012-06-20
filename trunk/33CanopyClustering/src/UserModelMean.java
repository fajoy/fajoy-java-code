
import java.util.*;
import java.util.Map.Entry;



public class UserModelMean {
	public String meanId="";
	Map<String, Double> MoveIDs= new HashMap<String, Double>();
	private double distanceCache=0;
	public UserModelMean(String meanId,Collection<UserModel> items){
		this.meanId=meanId;
		this.getMean(items);
	}
	private void getMean(Collection<UserModel> items) {
		if (items.size()==0)
			return;
		for (UserModel item : items) {
			for (Entry<String, Integer> entry:item.MoveIDs.entrySet()){
				String key=entry.getKey();
				double sum=0;
				if(this.MoveIDs.containsKey(key))
					sum= this.MoveIDs.get(key);
				sum+=entry.getValue();
				this.MoveIDs.put(key, sum);
			}	
		}
		int size=items.size();
		double sum=0;
		for (Entry<String, Double> entry:MoveIDs.entrySet()){
			String key=entry.getKey();
			double mean= entry.getValue()/size;
			entry.setValue(mean);
			sum+=mean*mean;
		}
		this.distanceCache=Math.pow(sum, 0.5);
	}
	public double getCosineDistance(UserModel obj){
		double same=0;
		for(Entry<String, Integer> entry:obj.MoveIDs.entrySet()){
			String key=entry.getKey();
			if(this.MoveIDs.containsKey(key)){
				same+=this.MoveIDs.get(key)*entry.getValue();
			}
		}
		
		double abs=this.distanceCache*Math.pow(obj.MoveIDs.size(), 0.5);
		double d=1f;
		d=same/abs;
		//this.cosineCache.put(obj, d);
		//obj.cosineCache.put(this, d);
		return d;
	}
	public void showData(){
		System.out.format("meanid=%s\t",meanId);
		for (Entry<String, Double> entry:MoveIDs.entrySet()){
			System.out.format(",%s:%f",entry.getKey(),entry.getValue());
		}
		//System.out.format("meanid=%s moveidsize=%d \n",meanId,this.MoveIDs.size());
	}
}
