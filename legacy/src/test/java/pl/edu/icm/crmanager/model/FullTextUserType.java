package pl.edu.icm.crmanager.model;

import pl.edu.icm.sedno.common.hibernate.AbstractStringPersistedUserType;

/**
 * @author bart
 */
public class FullTextUserType extends AbstractStringPersistedUserType {

	@Override
	public String writeToDatabase(Object value) {
		if (value == null)
			return null;
		return ((FullText)value).getText();
	}

	@Override
	public Object readFromDatabase(String fromPersistedString) {
		return new FullText(fromPersistedString);
	}

	@Override
	public Class returnedClass() {
		return FullText.class;
	}

	@Override
	public boolean isMutable() {
		return true;
	}

}
