
import java.util.*;
import java.util.Map.Entry;



public class ItemModelMean {
	public String meanId="";
	Map<String, Double> itemMean= new HashMap<String, Double>();
	private double distanceCache=0;
	public ItemModelMean(ItemModelMean old,Collection<RowModel> items){
		this.meanId=old.meanId;
		this.itemMean= old.itemMean;
		this.setMean(items);
	}
	public ItemModelMean(String meanId,Collection<RowModel> items){
		this.meanId=meanId;
		this.setMean(items);
	}
	private void setMean(Collection<RowModel> items) {
		if (items.size()==0)return;
		itemMean=new HashMap<String, Double>();
		for (RowModel item : items) {
			for (Entry<String, Integer> entry:item.items.entrySet()){
				String key=entry.getKey();
				double sum=0;
				if(itemMean.containsKey(key))
					sum= itemMean.get(key);
				sum+=entry.getValue();
				this.itemMean.put(key, sum);
			}	
		}
		int size=items.size();
		double sum=0;
		for (Entry<String, Double> entry:itemMean.entrySet()){
			String key=entry.getKey();
			double mean= entry.getValue()/size;
			entry.setValue(mean);
			sum+=mean*mean;
		}
		this.distanceCache=Math.pow(sum, 0.5);
	}
	public double getCosineDistance(RowModel row){
		if (itemMean.size()==0)return 0d;
		double same=0;
		for(Entry<String, Integer> entry:row.items.entrySet()){
			String key=entry.getKey();
			if(this.itemMean.containsKey(key)){
				same+=this.itemMean.get(key)*entry.getValue();
			}
		}
		
		double abs=this.distanceCache*Math.pow(row.items.size(), 0.5);
		double d=1f;
		d=same/abs;
		return d;
	}
	public void showData(){
		System.out.format("meanid=%s\t",meanId);
		Iterator<Entry<String, Double>> i= itemMean.entrySet().iterator();
		if(i.hasNext()){
			Entry<String, Double> entry=i.next();
			System.out.format("%s:%f",entry.getKey(),entry.getValue());
		}
		while(i.hasNext()){
			Entry<String, Double> entry=i.next();
			System.out.format(",%s:%f",entry.getKey(),entry.getValue());
		}
		System.out.println();
		//System.out.format("\nmeanid=%s moveidsize=%d \n",meanId,this.MoveIDs.size());
	}
}
