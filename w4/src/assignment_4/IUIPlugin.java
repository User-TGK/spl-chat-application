package assignment_4;

import java.beans.*;

public interface IUIPlugin extends PropertyChangeListener {
	public void run(boolean authenticationEnabled);
	
	public void addPropertyChangeListener(PropertyChangeListener pcl);

	public void removePropertyChangeListener(PropertyChangeListener pcl);
}