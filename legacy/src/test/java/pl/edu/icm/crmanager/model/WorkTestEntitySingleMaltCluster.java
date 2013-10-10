package pl.edu.icm.crmanager.model;

import pl.edu.icm.common.iddict.model.ClusterSize;
import pl.edu.icm.common.iddict.model.SingleMaltCluster;

import javax.persistence.Entity;

@Entity
@ClusterSize(isBig=false)
public class WorkTestEntitySingleMaltCluster extends SingleMaltCluster<WorkTestEntity>{
}
