

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CanopyModel{
	public RowModel center;
	public Map<RowModel,RowModel> items=new LinkedHashMap<RowModel,RowModel>();
	public CanopyModel(RowModel center){
		this.center=center;
		items.put(center,center);
	}
	public ItemModelMean getMean(String meanId,boolean isPoint){
		if(isPoint){
			List<RowModel> centerP=new ArrayList<RowModel>();
			centerP.add(center);
			return new ItemModelMean(meanId,centerP);
		}
		else{
			return new ItemModelMean(meanId,items.values());
		}
	
	}
}