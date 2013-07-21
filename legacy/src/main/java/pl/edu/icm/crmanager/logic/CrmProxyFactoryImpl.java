package pl.edu.icm.crmanager.logic;

import org.springframework.stereotype.Service;

import pl.edu.icm.crmanager.model.CrmProxy;
import pl.edu.icm.crmanager.utils.CrmReflectionUtil;
import pl.edu.icm.sedno.common.model.DataObject;


/**
 * @author bart
 */
@Service("crmProxyFactory")
public class CrmProxyFactoryImpl implements CrmProxyFactory {

    private BCodeGenerator bCodeGenerator;
    
    @Override
    public CrmProxy createRedoLogProxy(DataObject instance) {
        Class proxyClass = null;
        if (CrmReflectionUtil.isCrmProxy(instance)) {
            proxyClass = instance.getClass();
        }
        else {
           // ((CrmProxy)instance).setRevision(revision);
           // return (CrmProxy)instance;

            proxyClass = bCodeGenerator.createCrmProxyClass( instance.getWrappedClass(), false );
            
            if (proxyClass == null) {
                throw new RuntimeException("createRedoLogProxy() : proxyClass == null, bCodeGenerator seems not working correctly...");
            }
            
          /*  try {
                proxyClass = Class.forName( proxyClassCt.getName());
            } catch (Exception e) {
                throw new CrmRuntimeException("error instantiating proxy for class "+instance.getClass().getSimpleName(),e);
            }*/
        }
        
        return CrmProxyUtil.getBindedProxy(instance, proxyClass, null);
        
    }
    
    public void setbCodeGenerator(BCodeGenerator bCodeGenerator) {
        this.bCodeGenerator = bCodeGenerator;
    }
}
