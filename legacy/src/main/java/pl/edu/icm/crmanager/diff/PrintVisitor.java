package pl.edu.icm.crmanager.diff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.sedno.common.dao.DataObjectDAO;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.patterns.Visitor;

/**
 * @author bart
 */
public class PrintVisitor implements Visitor<DataObject>{ 
	private static final Logger logger = LoggerFactory.getLogger(PrintVisitor.class);
	
	private DataObjectDAO dataObjectDAO;
	
	
	
	public PrintVisitor(DataObjectDAO dataObjectDAO) {
		this.dataObjectDAO = dataObjectDAO;
	}


	@Override
    public void visit(DataObject object) {
		logger.info(".. "+dataObjectDAO.getObjectShortDesc(object));
	}
}
