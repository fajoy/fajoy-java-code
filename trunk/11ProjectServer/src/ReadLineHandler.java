import java.io.IOException;


public interface ReadLineHandler<T> {
	public void action(T sender,String line);
}
