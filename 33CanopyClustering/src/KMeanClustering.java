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
			groups.put(m.meanId, new MeanGroup(m));
		}
		items=new HashMap<String, RowModel>(c.rows);
	}
	
	public KMeanClustering(){
		
	}
	public void addMean(String line){
		ItemModelMean m=ItemModelMean.parse(line);
		groups.put(m.meanId, new MeanGroup(m));
	}
	private static  void batchTest(CanopyClustering c){
		for(double t=0.0;t<=1.0;t+=0.01){
			c.T2=t;
			c.T1=t;
			c.canopys.clear();
			long time1=System.currentTimeMillis();
			c.setFastCanopySet();
			time1=System.currentTimeMillis()-time1;
			//c.showCanopy();
			//System.out.flush();
			KMeanClustering means=new KMeanClustering(c);
			long time2=System.currentTimeMillis();
			means.getMeanGroups();
			time2=System.currentTimeMillis()-time2;
			
			means.showGroup();
			//System.out.format("T\tk\ttime\tkmeanrun\ttime\tgroup_count\n");
			System.out.format("@%f\t%d\t%d\t%d\t%d\t%d\n",t,c.canopys.size(),time1,means.round,time2,means.group_count);
			if(c.canopys.size()==c.rows.size())
				break;
		}
	}	
	public ItemModelMean getCloseMean(Collection<String> means,RowModel item){
		Iterator<String> i=means.iterator();
		ItemModelMean mean=groups.get(i.next()).mean;
		double max=mean.getCosineDistance(item);
		while(i.hasNext()){
			ItemModelMean comp=groups.get(i.next()).mean;
			double  d=comp.getCosineDistance(item);
			if(d>max){
				mean=comp;
				max=d;
			}
		}
		return mean;
	}
	public HashMap<String, RowModel> items=null;
	public HashMap<String,MeanGroup> groups=new HashMap<String, MeanGroup>();
	public int round=0;
	public int group_count=0;
	public void getMeanGroups(){
		round=0;
		//this.showGroup();
		//System.out.format("run %d\n",run++);
		HashMap<String,MeanGroup> g=this.getNewGroup(this.groups);
		while(isDiff(groups, g)){
			groups=g;	
			g=this.getNewGroup(this.groups);
			//this.showGroup();
			round++;
			//showGroup();
		}
		
	}
	private HashMap<String,MeanGroup> getNewGroup(HashMap<String,MeanGroup>  old){
		HashMap<String,MeanGroup> groups=new HashMap<String,MeanGroup>();
		for(MeanGroup g:old.values()){
			ItemModelMean m=new ItemModelMean(g.mean,g.items.values());
			groups.put(m.meanId, new MeanGroup(m));
		}
		for(RowModel item:items.values()){
			ItemModelMean mean=getCloseMean(old.keySet(), item);
			groups.get(mean.meanId).items.put(item.rowId,item);
		}
		for(MeanGroup mg:groups.values()){
			mg.mean=new ItemModelMean(mg.meanId, mg.items.values());
		}
		return groups;
	}
	private boolean isDiff(HashMap<String,MeanGroup>  gs1,HashMap<String,MeanGroup>  gs2){
		
		for(String id :gs1.keySet()){
			ItemModelMean m1=gs1.get(id).mean;
			ItemModelMean m2=gs2.get(id).mean;
			if(m1.itemMean.size()!=m2.itemMean.size())
				return true;
			for(String mi: m1.itemMean.keySet()){
				if(!m2.itemMean.containsKey(mi))
					return true;
				double d1=m1.itemMean.get(mi);
				double d2=m2.itemMean.get(mi);
				if(m1.itemMean.size()!=m2.itemMean.size())
					return true;	
			}
			
			/*
			String ms1=m1.toMeanString(false);
			String ms2=m2.toMeanString(false);
			if(!ms1.equals(ms2))
				return true;
				*/
		}
		
		/*
		Iterator<MeanGroup> ig1=gs1.values().iterator();
		Iterator<MeanGroup> ig2=gs2.values().iterator();
		MeanGroup g1=null;
		MeanGroup g2=null;
		Iterator<String> ri1=null;
		Iterator<String> ri2=null;
		String r1=null;
		String r2=null;
		group_count=0;
		while(ig1.hasNext()){
			g1=ig1.next();
			g2=ig2.next();
			if(g1.items.size()!=g2.items.size())
				return true;
			ri1=g1.items.keySet().iterator();
			ri2=g2.items.keySet().iterator();
			while(ri1.hasNext()){
				r1=ri1.next();
				r2=ri2.next();
				if(r1!=r2)	return true;
			}	
			if(g1.items.size()>0)
				group_count++;
		}*/
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
			Iterator<RowModel> i= g.items.values().iterator();
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
		String meanId;
		public HashMap<String,RowModel> items=new HashMap<String,RowModel>();
		public MeanGroup(ItemModelMean obj){
			this.mean=obj;
			this.meanId=obj.meanId;
		}
	}
}
