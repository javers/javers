package pl.edu.icm.crmanager.model;

import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * 
 * @author bart
 */
@CrmStringPersistedUserType(type = "pl.edu.icm.crmanager.model.FullTextUserType")
public class FullText implements Serializable {
	private String text;
	
	public FullText() {
	}

	public FullText(String text) {
		super();
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	

	@Override
	public String toString() {
		return "FullText:"+text;
	}
	
	@Override
	public int hashCode() {
		if (this.getText() == null) return -1;
		
		return this.getText().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FullText)) {
			return false;
		}
		
		return Objects.equal(this.getText(), ((FullText)obj).getText());
	}
}
