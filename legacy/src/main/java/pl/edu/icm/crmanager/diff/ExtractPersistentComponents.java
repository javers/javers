package pl.edu.icm.crmanager.diff;

import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.patterns.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback używany do diffa, wyciąga nietransientowe pod-obiekty do listy (root też)
 * @author bart
 *
 */
public class ExtractPersistentComponents  implements Visitor<DataObject>{

    private List<DataObject> result = new ArrayList<DataObject>();
    
    
    @Override
    public void visit(DataObject object) {
        if (object.isTransient())
            return;
        
        result.add(object);        
    }


    public List<DataObject> getResult() {
        return result;
    }

}
