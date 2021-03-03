### This repo is a customization of javers 
In a project of [CESAR](https://www.cesar.org.br/) the client has a specific rule of the database creation, so we needed to customizes the names of the entire database, so we needed to add this type of configuration to javers. So your have to add the configuration below on your application.properties

##### Table Name
> Change table name already exist
```
javers.sqlGlobalIdTableName       = my_global_id
javers.sqlCommitTableName         = my_commit
javers.sqlSnapshotTableName       = my_snapshot
javers.sqlCommitPropertyTableName = my_commit_property
```
##### Column Name
> We have add the following configurations
```
javers.sqlGlobalIdPKColumnName              = global_pk
javers.sqlGlobalIdLocalIdColumnName         = global_id_local
javers.sqlGlobalIdFragmentColumnName        = global_fragment
javers.sqlGlobalIdTypeNameColumnName        = global_type_name
javers.sqlGlobalIdOwnerIDFKColumnName       = global_id_owner_fk
javers.sqlCommitPKColumnName                = commit_pk
javers.sqlCommitAuthorColumnName            = commit_author
javers.sqlCommitCommitDateColumnName        = commit_date
javers.sqlCommitCommitDateInstantColumnName = commit_instant
javers.sqlCommitCommitIdColumName           = commit_commit_id
javers.sqlCommitPropertyCommitFKColumnName  = commit_property_id
javers.sqlCommitPropertyNameColumnName      = commit_property_name
javers.sqlCommitPropertyValueColumnName     = commit_property_value
javers.sqlSnapshotPKColumnName              = snapshot_pk
javers.sqlSnapshotCommitFKColumnName        = snapshot_commit_fk
javers.sqlSnapshotGlobalIdFKColumnName      = snapshot_global_fk
javers.sqlSnapshotTypeColumnName            = snapshot_type
javers.sqlSnapshotVersionColumnName         = snapshot_version
javers.sqlSnapshotStateColumnName           = snapshot_state
javers.sqlSnapshotChangedColumnName         = snapshot_changed
javers.sqlSnapshotManagedTypeColumnName     = snapshot_managed_type
```

##### Column Name
> add mark on constraint
javers.sqlPrimaryKeyIndicator = pk
javers.sqlForeignKeyIndicator = fk
javers.sqlSequenceIndicator   = seq
javers.sqlIndexIndicator      = idx
javers.sqlIsSuffix            = true