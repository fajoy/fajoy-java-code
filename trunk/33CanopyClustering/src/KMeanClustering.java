
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;






public class KMeanClustering {
	public static void main(String[] args) throws IOException  {
		URL url = CanopyClustering.class.getResource("moveid.dat");
		CanopyClustering c= new CanopyClustering();
		//c.T1=0.24;
		//c.T2=0.24;
		c.parseData(url.getFile());
		//KMeanClustering means=new KMeanClustering(c);
		//c.showData();
		//c.showDistance();		
		//means.getMeanGroups();
		batchTest(c);
	}

	public KMeanClustering(CanopyClustering c){
		List<ItemModelMean> ms=c.getMeans(true);
		for(ItemModelMean m:ms){
			groups.put(m, new MeanGroup(m));
		}
		items=new LinkedHashMap<String, RowModel>(c.rows);
		for(RowModel item:items.values()){
			ItemModelMean mean=getMean(groups.keySet(), item);
			groups.get(mean).items.add(item);
		}
	}
	
	public KMeanClustering(){
		
	}
	public void addMean(String line){
		ItemModelMean m=ItemModelMean.parse(line);
		groups.put(m, new MeanGroup(m));
	}
	private static  void batchTest(CanopyClustering c){
		for(double t=0.5;t>=0;t-=0.01){
			c.T2=t;
			c.T1=t;
			c.canopys.clear();
			long time1=System.currentTimeMillis();
			c.setCanopySet();
			//c.showCanopy();
			time1=System.currentTimeMillis()-time1;
			long time2=System.currentTimeMillis();
			KMeanClustering means=new KMeanClustering(c);
			means.getMeanGroups();
			time2=System.currentTimeMillis()-time2;
			
			means.showGroup();
			System.out.format("T\tk\ttime\tkmeanrun\ttime\tgroup_count\n");
			System.out.format("%f\t%d\t%d\t%d\t%d\t%d\n",t,c.canopys.size(),time1,means.round,time2,means.group_count);
			if(c.canopys.size()==c.rows.size())
				break;
		}
	}	
	public ItemModelMean getMean(Collection<ItemModelMean> means,RowModel item){
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
	public int group_count=0;
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
		Map<ItemModelMean,MeanGroup> groups=new HashMap<ItemModelMean, MeanGroup>();
		for(MeanGroup g:old.values()){
			ItemModelMean m=new ItemModelMean(g.mean,g.items);
			groups.put(m, new MeanGroup(m));
		}
		for(RowModel item:items.values()){
			ItemModelMean mean=getMean(groups.keySet(), item);
			groups.get(mean).items.add(item);
		}
		return groups;
	}
	private boolean isDiff(Map<ItemModelMean,MeanGroup> gs1,Map<ItemModelMean,MeanGroup> gs2){
		Iterator<ItemModelMean> ig1=gs1.keySet().iterator();
		Iterator<ItemModelMean> ig2=gs2.keySet().iterator();
		
		group_count=0;
		while(ig1.hasNext()){
			ItemModelMean m1=ig1.next();
			ItemModelMean m2=ig2.next();
			if(!m1.toMeanString(true).equals(m2.toMeanString(true))){
				return true;
			}
			if(m1.itemMean.size()>0)
				group_count++;
		}
		return false;
	}
	public void showGroup(){
		System.out.format("gid:count\tid:distance\n");
		for(MeanGroup g:groups.values()){
			System.out.format("%s:%d\t",g.mean.meanId,g.items.size());
			if(g.items.size()==0){
				System.out.format("\n");
				continue;
			}
			Iterator<RowModel> i= g.items.iterator();
			RowModel row=i.next();
			//System.out.format("%s",obj.UserId);
			
			System.out.format("%s:%f",row.rowId,g.mean.getCosineDistance(row));
			int show_C=0;
			while(i.hasNext()){
				row=i.next();
				//System.out.format(",%s",obj.UserId);
				if(show_C<10){
					show_C++;
					System.out.format(",%s:%f",row.rowId,g.mean.getCosineDistance(row));
				}
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
