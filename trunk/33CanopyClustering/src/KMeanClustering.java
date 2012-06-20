import java.io.IOException;
import java.net.URL;
import java.util.*;




public class KMeanClustering {
	public static void main(String[] args) throws IOException  {
		URL url = CanopyClustering.class.getResource("moveid.dat");
		CanopyClustering c= new CanopyClustering(url.getFile());
		//c.T1=0.24;
		//c.T2=0.24;
		//KMeanClustering means=new KMeanClustering(c);
		//c.showData();
		//c.showDistance();		
		//means.getMeanGroups();
		batchTest(c);
	}

	public KMeanClustering(CanopyClustering c){
		c.getCanopySet();
		List<ItemModelMean> ms=c.getMeans();
		for(ItemModelMean m:ms){
			groups.put(m, new MeanGroup(m));
		}
		items=new LinkedHashMap<String, RowModel>(c.rows);
		for(RowModel item:items.values()){
			ItemModelMean mean=getMean(groups.keySet(), item);
			groups.get(mean).items.add(item);
		}
	}
	private static  void batchTest(CanopyClustering c){
		System.out.format("T\tk\ttime\tkmeanrun\ttime\n");
		for(double t=0.5;t>=0;t-=0.01){
			c.T2=t;
			c.T1=t;
			c.canopys.clear();
			long time1=System.currentTimeMillis();
			c.getCanopySet();
			//c.showCanopy();
			time1=System.currentTimeMillis()-time1;
			long time2=System.currentTimeMillis();
			KMeanClustering means=new KMeanClustering(c);
			means.getMeanGroups();
			time2=System.currentTimeMillis()-time2;
			System.out.format("%f\t%d\t%d\t%d\t%d\n",t,c.canopys.size(),time1,means.round,time2);
			means.showGroup();
			if(c.canopys.size()==c.rows.size())
				break;
		}
	}	
	private ItemModelMean getMean(Collection<ItemModelMean> means,RowModel item){
		Iterator<ItemModelMean> i=means.iterator();
		ItemModelMean mean=i.next();
		double max=mean.getCosineDistance(item);
		while(i.hasNext()){
			ItemModelMean comp=i.next();
			double  d=comp.getCosineDistance(item);
			if(d>max){
				mean=comp;
				max=d;
			}
		}
		return mean;
	}
	public Map<String, RowModel> items=null;
	public Map<ItemModelMean,MeanGroup> groups=new LinkedHashMap<ItemModelMean, MeanGroup>();
	public int round=0;
	public void getMeanGroups(){
		round=0;
		//this.showGroup();
		//System.out.format("run %d\n",run++);
		Map<ItemModelMean,MeanGroup> g=this.getNewGroup(this.groups);
		while(isDiff(groups, g)){
			groups=g;	
			g=this.getNewGroup(this.groups);
			//this.showGroup();
			round++;
		}
		
	}
	private Map<ItemModelMean,MeanGroup> getNewGroup(Map<ItemModelMean,MeanGroup> old){
		Map<ItemModelMean,MeanGroup> groups=new LinkedHashMap<ItemModelMean, MeanGroup>();
		for(MeanGroup g:old.values()){
			ItemModelMean m=new ItemModelMean(g.mean.meanId,g.items);
			groups.put(m, new MeanGroup(m));
		}
		for(RowModel item:items.values()){
			ItemModelMean mean=getMean(groups.keySet(), item);
			groups.get(mean).items.add(item);
		}
		return groups;
	}
	private boolean isDiff(Map<ItemModelMean,MeanGroup> gs1,Map<ItemModelMean,MeanGroup> gs2){
		Iterator<MeanGroup> ig1=gs1.values().iterator();
		Iterator<MeanGroup> ig2=gs2.values().iterator();
		MeanGroup g1=null;
		MeanGroup g2=null;
		Iterator<RowModel> ri1=null;
		Iterator<RowModel> ri2=null;
		RowModel r1=null;
		RowModel r2=null;
		while(ig1.hasNext()){
			g1=ig1.next();
			g2=ig2.next();
			if(g1.items.size()!=g2.items.size())
				return true;
			ri1=g1.items.iterator();
			ri2=g2.items.iterator();
			while(ri1.hasNext()){
				r1=ri1.next();
				r2=ri2.next();
				if(r1!=r2)	return true;
			}	
		}
		return false;
	}
	public void showGroup(){
		for(MeanGroup g:groups.values()){
			System.out.format("%s:%d\t",g.mean.meanId,g.items.size());
			Iterator<RowModel> i= g.items.iterator();
			RowModel obj=i.next();
			//System.out.format("%s",obj.UserId);
			System.out.format("%s:%f",obj.rowId,g.mean.getCosineDistance(obj));
			while(i.hasNext()){
				obj=i.next();
				//System.out.format(",%s",obj.UserId);
				if(obj.rowId.length()<3)
					System.out.format(",%s:%f",obj.rowId,g.mean.getCosineDistance(obj));
			}
			System.out.format("\n");
			//g.mean.showData();
		}
	}
	public class MeanGroup{
		ItemModelMean mean;
		public List<RowModel> items=new ArrayList<RowModel>();
		public MeanGroup(ItemModelMean obj){
			this.mean=obj;
		}
	}
}
