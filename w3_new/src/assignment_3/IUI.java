package assignment_3;

import javax.swing.*;
import java.beans.*;

public interface IUI extends PropertyChangeListener {
	public void run();
	
	public void addPropertyChangeListener(PropertyChangeListener pcl);

	public void removePropertyChangeListener(PropertyChangeListener pcl);
}