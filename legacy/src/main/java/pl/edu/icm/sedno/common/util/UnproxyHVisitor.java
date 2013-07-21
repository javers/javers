package pl.edu.icm.sedno.common.util;

import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.patterns.Visitor;
/**
 * 
 * @author bart

 */
public class UnproxyHVisitor implements Visitor<DataObject>{
    
    @Override
    public void visit(DataObject object) {
    	ReflectionUtil.unproxyHReferences(object);
    }
}    
