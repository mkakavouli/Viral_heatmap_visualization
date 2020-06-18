import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;

public class RegionsComboBox extends JComboBox{
	String name;
	
	public  RegionsComboBox(Vector vector) {
		super(vector);
		setRenderer(new ComboRenderer());
		
	}
	
	public String checkedBox() {
		Object checked=getSelectedItem();
		if (checked instanceof JCheckBox) {
			JCheckBox selected =(JCheckBox)checked;
			selected.setSelected(!selected.isSelected());
			String name= selected.getText();
			return name;
		}
		return"";
		
	}

	public String getName() {
		return name;
	}
	
	
}
class ComboRenderer implements ListCellRenderer{
	private JLabel label;
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		if(value instanceof Component) {
			Component component=(Component) value;
			if(isSelected) {
				component.setBackground(list.getSelectionBackground());
				component.setForeground(list.getSelectionBackground());
			}else {
				component.setBackground(list.getBackground());
				component.setForeground(list.getForeground());
			}
			return component;
		}else {
			if(label==null) {
				label=new JLabel(value.toString());
			}else {
				label.setText(value.toString());
			}
			return label;
		}
		
		
	}
	
}
