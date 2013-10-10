package pl.edu.icm.crmanager.logic;

import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.model.CrmProxy;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.model.DataObject;

import java.util.Collection;

/**
 * 
 * @author bart
 */
public abstract class CrmCollectionWrapper {
    
    protected   Revision revision;
    protected   DataObject node; //collection owner
    protected   String getterName;
    protected   Collection<DataObject>  instance;        
    
    protected   Class proxyClass; //klasa obiektow w proxyMirror 
    
    public CrmCollectionWrapper (Revision revision, DataObject node, String getterName, Collection instance, Class proxyClass) {
        
        this.instance = instance;
        this.revision = revision;   
        this.node = node;
        this.getterName = getterName;
        this.proxyClass = proxyClass;
        
        if (instance == null) {
            throw new CrmRuntimeException("CrmSetWrapper: proxiedInstance is null");
        }
        initProxyMirror();
        
        checkType(instance);
        //redoRoot.registerColProxy(this);
    }
    
    public Collection getWrappedInstance() {
        return instance;
    }
    
    public String getShortDesc() {      
        return this.getClass().getSimpleName() +"->"+ node.getGlobalId()+"."+getterName+"(), size:"+
               instance.size()+", revision:"+revision.getIdRevision();
    }
    
    public String printToString() {
        StringBuffer buf = new StringBuffer();
        
        buf.append("-- "+ this.getClass().getName()+" hash:"+ System.identityHashCode(this)+" --\n");
        buf.append(".. getterName: "+ getterName+"\n");
        buf.append(".. node:       "+ node.getGlobalId()+"\n");
        buf.append(".. revision:   "+ revision.getIdRevision()+"\n");
        buf.append(".. proxyClass: "+proxyClass.getName()+"\n");

        buf.append(".. instance:   "+"\n");
        int i=0;
        for (Object o : instance) {
            buf.append(".. .. ["+i+"]:   "+((DataObject)o).getGlobalId()+"\n");
            i++;        
        }
        
        buf.append(".. proxyMirror:"+"\n");

        i=0;
        for (Object o : getProxyMirrorValues()) {
            buf.append(".. .. ["+i+"]:   "+o.toString() +" -> "+ ((CrmProxy)o).getInstance().getGlobalId()+"\n");
            i++;        
        }
        
        return buf.toString();
    }
    
    protected CrmProxy createProxy(DataObject o) {
        return  CrmProxyUtil.getBindedProxy(o, proxyClass, null);
    }
       
    protected abstract void checkType(Collection instance);
    
    protected abstract void initProxyMirror(); 
    
    protected abstract Collection getProxyMirrorValues();
         
    
    protected void registerAdd(DataObject dataObject) {        
        if (dataObject != null) {
            revision.registerChildAdd(node, getterName, (DataObject)dataObject);
         }
    }
    
    protected void registerRemove(DataObject o, CrmProxy proxyToDetach) {
        if (o != null) {    
            revision.registerChildRemove(node, getterName, o );
            
            if (proxyToDetach != null) {
                CrmProxyUtil.detachProxy(proxyToDetach);
            } 
        }
    }
    
    public boolean isEmpty() {
        return instance.isEmpty();
    }

    public int size() {
        return instance.size();
    }

    public Object[] toArray() {
        return getProxyMirrorValues().toArray();
    }

    public Object[] toArray(Object[] a) {
        return getProxyMirrorValues().toArray(a);
    }
}
