package pl.edu.icm.crmanager.model;

import javax.persistence.Entity;

import pl.edu.icm.common.iddict.model.ClusterSize;
import pl.edu.icm.common.iddict.model.SingleMaltCluster;

@Entity
@ClusterSize(isBig=false)
public class WorkTestEntitySingleMaltCluster extends SingleMaltCluster<WorkTestEntity>{
}
