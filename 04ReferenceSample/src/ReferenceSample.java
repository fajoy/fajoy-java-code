import java.lang.reflect.*;
public class ReferenceSample {
	public static String[] args=new String[]{};
	public static void main(String[] args) throws Exception {
		ReferenceSample.args=args;
		ReferenceSample obj=new ReferenceSample();
	}
	public ReferenceSample() throws  Exception{
		show(this);
		Class newClass=Class.forName("HelloClass");
		Object obj=newClass.newInstance();
		show(obj);
		showProperties(Number.class,obj);
		run(newClass,ReferenceSample.args);
	}
	public void show(Object obj)throws  Exception{
		Class objType=obj.getClass();
		System.out.println(String.format("class name:%s",objType.getName()));
		for(Method method: objType.getDeclaredMethods()){
			String methodName=method.getName();
			System.out.println(String.format(" method name:%s",methodName));
			for(Class parameterType: method.getParameterTypes()){
				String parameterTypeName=parameterType.getName();
				System.out.println(String.format("  parameter class:%s",parameterTypeName));	
			}
			String returnClassName=method.getReturnType().getName();
			System.out.println(String.format(" return type:%s",returnClassName));	
		}
		
		for(Field field: objType.getFields()){
			String className=field.getType().getName();
			String name=field.getName();
			Object value=field.get(obj);
			System.out.println(String.format(" field type:%s",className));
			System.out.println(String.format("       name:%s",name));
			System.out.println(String.format("       value:%s",value));
		}
	}
	public void showProperties(Class propertiesType,Object obj) throws Exception{
		Class objType=obj.getClass();
		String propertiesTypeName=propertiesType.getName();
		System.out.println(String.format("show %s properties which type is %s",objType.getName(),propertiesTypeName));
		for(Field field: objType.getFields()){
			String className=field.getType().getName();
			String name=field.getName();
			Object value=field.get(obj);
			if(propertiesType.isInstance(value)){
				System.out.println(String.format(" %s %s = %s",className,name,value));
			}
		}
	}
	public void run(Class mainClass,String[] args){
		try{
			Method method=mainClass.getMethod("main", new Class[]{String[].class});
			method.invoke(null,new Object[]{ReferenceSample.args});
		}catch (Exception e) {e.printStackTrace();}
	}	
	
}
