package pl.edu.icm.crmanager.logic;
/**
 * Typy persistent getterów rozróżniane przez CRM
 * 
 * @author bart
 */
public enum MethodType {
    simpleValue, dataObjectValue, dataObjectCollection, transparentProxy, CrmProxy_Impl, CrmExcluded, dictionaryReference
}